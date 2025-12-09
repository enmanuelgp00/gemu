package gemu;

import gemu.frame.main.MainFrame;
import java.io.*;       
import java.util.*;  
import java.util.stream.Collectors;  
import gemu.game.*;  
import gemu.util.*;  
import gemu.io.*;       

public class Gemu {
	ArrayList<Game> games = new ArrayList<>();
	
	Gemu( File f ) {
		findGames(f);
	
	}
	
	public boolean isIgnoreFile( File file ) {
		return file.getName().equals(".gemuignore");
	}
	
	public void findGames( File file ) {
		if ( file.isDirectory() ) {
			ArrayList<Executable> executables = new ArrayList<>();  
			ArrayList<Info> infos = new ArrayList<>();
			ArrayList<File> compactFiles = new ArrayList<>();
			
			for ( File f : file.listFiles() ) {
				if ( isIgnoreFile(f) ) {
					return;
				}
				if ( Games.isInfoFile(f) ) {
					try {
						Info inf = Info.parseInfo(f);
						infos.add(inf);					
					} catch ( Exception e ) {
						e.printStackTrace();
					}
					
				} else if ( Games.isLauncher(f) ) {
					try {
						executables.add( new Executable(f));					
					} catch ( Exception e ) {}
					
				} else if ( ZipFiles.isZipFile(f) ) {
					compactFiles.add(f);
				}
			}
			
			if ( infos.size() > 0 ) {			
				for ( Info info : infos ) {
					Game game = Game.from( info );
					handleMultipleExecutables( game );
				}
			} else {
				try {
					if ( executables.size() > 0 ) {
						Game game = new Game( executables.toArray( new Executable[ executables.size() ] ) );
						handleMultipleExecutables( game );
					}
				
				} catch( Exception e ) {
					e.printStackTrace();
				}
			}
			
			if ( compactFiles.size() > 0 ) {
				for ( File f : compactFiles ) {					
					boolean gamefound = false ;
					String infoName;
					String fileName;
					for ( Game game : games ) {
						infoName = FileNames.getBaseName( game.getInfoFile() );
						fileName = FileNames.getBaseName(f);
						if ( infoName.equals(fileName)) {
							gamefound = true;
							if ( game.isDeleted() ) {
								System.out.println("Zip file found : " + fileName + " but game state is \"DELETED\" : " + infoName );							
								System.out.println("In folder : " + game.getDirectory() );
							}
							break;
						}
					}
					
					if ( !gamefound) {
						try {  
							Game game = Game.inZip( ZipFiles.get( f ) ); 
							handleMultipleExecutables( game );
						} catch( Exception e ) {
							e.printStackTrace();
						}
						
					}
				}
			}
			
			
			
			if ( executables.size() == 0 ) {
				for ( File f : file.listFiles() ) {
					findGames(f);
				}
			} 
			
		}
	}
	
	public void handleMultipleExecutables( Game game ) {
		if ( game.getLauncher() != null || game.isDeleted() ) {
			if ( game.getCoverImage() == null ) {
				game.findCoverImage();
			}               
			games.add( game );			
			return;
		}
		
		Executable[] executables = game.getExecutables();  
		Scanner scanner = new Scanner( System.in );
		System.out.println( executables.length + " executables found for in folder :" + game.getDirectory() );
		System.out.println("Please define one: ");
		int user = -2;
		int count = 0;
		while ( user < -1 || user > executables.length - 1 ) {
			System.out.println( "[ " + count++ + " ] " + "[IGNORE]" );
			for ( Executable exe : executables ) {
				System.out.println( "[ " + count++ + " ] " + exe.getName() );
			}
			count = 1;     
			System.out.println(""); 
			try {              
				user = Integer.parseInt(scanner.nextLine()) - 1;				
			} catch( Exception e ) {}
		}
		if ( user != -2 ) {
			try {
				game.setLauncher( executables[user] );
				games.add( game );
			} catch ( Exception e ) {}
		}
		
		
	}
	
	public static void main( String[] args ) {
		
		Gemu g = new Gemu( new File( ( args.length > 0 ) ? args[0] : "."  ) );
		ArrayList<Game> gameList = new ArrayList<>();
		List<List<Game>> states = new ArrayList<>();
		states.add(g.games.stream().filter(Game::isStandby).collect( Collectors.toList() ) );     
		states.add(g.games.stream().filter(Game::isInZip).collect( Collectors.toList() ) );
		states.add(g.games.stream().filter(Game::isDeleted).collect( Collectors.toList() ) );
							 
		List<Game> ls = states.get(0);
		int control = 0;
		int size = ls.size();
		Game g0;
		Game g1;
		Long ltp0;
		Long ltp1;               
		
		while ( control < size ) {    			   
			g0 = ls.get( control );
									
			for ( int i = control + 1; i < size; i++ ) {
				g1 = ls.get(i);            
				
				ltp0 = g0.getLastTimePlayed();
				ltp1 = g1.getLastTimePlayed();
				if ( ltp0 == null ) ltp0 = 0L;
				if ( ltp1 == null ) ltp1 = 0L;
				
				if ( ltp1 > ltp0 ) {             
					ls.set( control, g1 );
					ls.set( i, g0 );
					control--;
					break;
				}
			}
			
			control++;
		}
		for ( List<Game> l : states ) {
			for ( Game game : l ) {
				gameList.add( game );
			}
		}
		
		System.out.println("Games found : " + g.games.size() );
		new MainFrame( gameList.toArray( new Game[gameList.size()] ) );
	}
}