package gemu;

import gemu.frame.main.MainFrame;
import java.io.*;       
import java.util.*;
import gemu.game.*;  
import gemu.util.*;

public class Gemu {
	ArrayList<Game> games = new ArrayList<>();
	
	Gemu( File f ) {
		findGames(f);
	}
	public void findGames( File file ) {
		if ( file.isDirectory() ) {
			ArrayList<Executable> executables = new ArrayList<>();  
			ArrayList<Info> infos = new ArrayList<>();
			ArrayList<File> compactFiles = new ArrayList<>();
			
			for ( File f : file.listFiles() ) {
				
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
					
				} else if ( Zipper.isCompact(f) ) {
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
			
			for ( File f : compactFiles ) {
				boolean gamefound = false ;
				String infoName;
				String fileName;
				for ( Game game : games ) {
					infoName = FileNames.getBaseName( game.getInfoFile() );
					fileName = FileNames.getBaseName(f);
					if ( infoName.equals(fileName)) {
						gamefound = true;
						break;
					}
				}
				
				if ( !gamefound) {
					/*
					try {
						Game game = Game.inZip( f );
						games.add( game );
					} catch( Exception e ) {}
					*/
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
		if ( game.getLauncher() != null ) {
			games.add( game );			
			return;
		}
		Executable[] executables = game.getExecutables();
		Scanner scanner = new Scanner( System.in );
		System.out.println("More than one executable found for in folder :" + game.getDirectory() );
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
		Gemu g = new Gemu( new File( args[0]) );
		new MainFrame( g.games.toArray( new Game[0] ) );
	}
}