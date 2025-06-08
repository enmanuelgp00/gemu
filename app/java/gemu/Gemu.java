package gemu;

import javax.swing.SwingUtilities;
import gemu.game.*;
import gemu.frame.main.*;
import java.io.File;
import java.util.*;

public class Gemu {
	public static final String IGNORE = "gemu.ignore";
	List<Game> gameList;
	
	Gemu( File file ) {
		gameList = new ArrayList<Game>();
		findGames( file );
		gameList = sort( gameList );              
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MainFrame mainFrame = new MainFrame( gameList );			
			}
		} );
	}
	
	public ArrayList<Game> sort( List<Game> list ) {
	
		Set<Game> gms = new HashSet<Game>();
		Set<Game> cGms = new HashSet<Game>();
		
		for ( Game game : list ) {
			if ( game.isCompressed() ) {
				cGms.add( game );			
			} else {
				gms.add( game );
			}
		}
		
		ArrayList<Game> l = new ArrayList<Game>();		
		for ( Game game : gms ) {
			l.add(game);
		}
		for ( Game game : cGms ) {
			l.add(game);
		}
		
		return l;
		
	}
	public void findGames( File file ) {
		 if ( file.isDirectory() ) {
			if ( !hasPossibleGame( file, 
				new OnPossibleGameFound() {
					@Override
					public void onGameFound( Game game ) {
						gameList.add( game );
					}
					@Override
					public void onLaunchersFound( List<File> launchers ) {
						String folder = launchers.get(0).getParentFile().getAbsolutePath();	 
						int index = 0;
						
						if ( launchers.size() > 1 ) {
							System.out.println("\nMutiple launchers were found in the folder :" + folder + "\n");	
							for ( File f : launchers ) {
								System.out.println(f.getName());
							} 
							System.out.println("");
						
							Scanner scan = new Scanner( System.in );
							while ( index < 1 || index > launchers.size() ) {
								System.out.println("Choose from 1 to " + ( launchers.size() ) );
								index = scan.nextInt();
							}
							--index;
						}
						Game game = new Game( launchers.get(index));
						System.out.println("New game found : { " + game.getName() +" }");
						System.out.println( game.getFolder() );
						gameList.add( game );
					}
				}
				)){			
				for (File f : file.listFiles()) {
					findGames(f);
				}
			}
		 }
	}
	
	public boolean hasPossibleGame( File file, OnPossibleGameFound listener ) {
		if ( !file.isDirectory()) {
			return false;
		}
		List<File> launchers = new ArrayList<File>();
		Game game;
		
		for ( File f : file.listFiles() ) {
			if ( isLauncher(f) ) {
				if ( Game.isLocalEmulator(f) ) {
					Game.setLocalEmulator( f );
				} else {
					launchers.add( f );				
				}
				
			} else if ( isGameInfo( f ) ) {
				game = new Game( GameInfo.parse( f ) );
				if ( game.getScreenshots() == null ) {
					File possibleScreenshot = new File ( game.getFolder() + "/screenshot.jpg");
					if ( possibleScreenshot.exists()) {
						game.addScreenshot( possibleScreenshot ).commit(); 
						System.out.println("New Screenshot for " + game.getName() );
					}
				}
				listener.onGameFound( game );
				return true;
			} else if ( is7zFile( f ) ) {
			
				Shell.run( new Shell.OnProcessListener() {
					@Override
					public void onProcessStarted( Process p ) { }      
					@Override
					public void onProcessFinished( Process p, int code ) { }
					@Override
					public void onStreamLineRead( String line ) {
						if ( line.length() > 53 ) {
							String pth = line.substring( 53 );
							String name = pth.substring( pth.lastIndexOf('\\') + 1 );
							if ( hasExtension( name, "exe" ) ) {  
								if ( isLauncherName( name ) ) {								
									launchers.add( new File( f.getParentFile().getAbsolutePath() + "/" + name ) );
								} 
							}
						}
					}
					
				} , "7z", "l", f.getAbsolutePath() );
			}
		}
		
		if ( launchers.size() > 0 ) {
			listener.onLaunchersFound( launchers );
			return true;
		}
		
		return false;
		
	}
	public boolean isLauncher( File file ) {
		String name = file.getName();
		if ( hasExtension( name, "exe" ) ) {
			return isLauncherName( name );		
		}
		return false;
	}
	public boolean isLauncherName( String name ) {
		String[] exceptions = new String[] {"helper", "crash", "update", "install", "gui", "config", "setting", "utility", "setup"};
		for ( String exception : exceptions ) {
			if ( name.toLowerCase().contains(exception)) {
				return false;
			}
		}
		return true;
	}
	public boolean hasExtension( File file, String extension ) {
		String name = file.getName();
		return hasExtension( name, extension );
	}
	public boolean hasExtension( String name, String extension ) {
		String flExtension = name.substring( name.lastIndexOf('.') + 1 );
		return flExtension.equals( extension );		
	}
	
	public boolean is7zFile( File file ) {
		return hasExtension( file, "7z" );
	}
	
	public boolean isGameInfo( File file ) {
		return file.getName().equals(GameInfo.FILE_NAME);
	}	
	
	public interface OnPossibleGameFound {
		public void onGameFound( Game game );
		public void onLaunchersFound( List<File> launchers );
	}
	public static void main( String[] args ) {
		new Gemu( new File(args[0]));
	}
}