package gemu;

import gemu.system.*;
import gemu.system.event.*;
import gemu.io.*;
import gemu.game.*;
import gemu.util.*;
import gemu.frame.main.MainFrame;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

class Gemu {
	
	Scanner scan = new Scanner( System.in );
	List<Game> gamels = new ArrayList<Game>();
	
	Gemu( File file ) {
		
		findGames( file );	
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() { 
				new MainFrame( gamels );
			}
		});
		
	}
	
	void consoleInterface() {
		while( true ) {
			StringBuilder sb = new StringBuilder();
			
			for ( int i = 0; i < gamels.size() ; i++ ) {
				Game game = gamels.get( i );
				sb.append("\n[" + i + "] " + game.getName() + " { compressed : " + game.isCompressed() + " }" );		
			}
			System.out.println("\n" + sb.toString() );
			
			Game game = gamels.get( scan.nextInt() );
			System.out.println("\n" + game.getName() );
			System.out.println("[ 0 ] play\n[ 1 ] compress/extract ");
			switch( scan.nextInt() ) {
				case 0:
					game.play();
				break;
				case 1:
					if( game.isCompressed() ) {
						game.extract( new OnProcessAdapter() {} );
					} else {
						game.compress( new OnProcessAdapter() {} );
					}
				break;
			}
		}
	}
	
	void findGames( File file ) {
		Games.findPossibleGames( file, new Games.OnGameFoundListener() {
					@Override
					public void onGameFound( Game game ) {
						gamels.add( game );
					}
					@Override
					public void onLaunchersFound( Launcher[] launchers ) {
						handleMultipleLaunchers( launchers );					
					}
					@Override
					public void onCompactLaunchersFound( CompactLauncher[] launchers ) {
						handleMultipleLaunchers( launchers );					
					}
				}
			);
		}

	private void handleMultipleLaunchers( CompactLauncher[] compactLaunchers ) {
		Launcher[] launchers = new Launcher[ compactLaunchers.length ];
		
		for ( int i = 0; i < compactLaunchers.length; i++ ) {
			try {
				launchers[ i ] = new Launcher( compactLaunchers[ i ] );			
			} catch ( Exception e ) {
				Log.error( e.getMessage() );
			}
		}
		
		handleMultipleLaunchers( launchers );
	}
	
	private void handleMultipleLaunchers( Launcher[] launchers ) {
		File container;
		if ( CompactFiles.isCompactFile( launchers[ 0 ] ) ) {
			container = new CompactFile( launchers[ 0 ] ).getParentRootFile();
		} else {
			container = launchers[ 0 ].getParentFile();
		}
		
		System.out.println( "\n[More than a single launcher found in \"" + container + "\"]"); 
		System.out.println("Would you like to define \"" + container + "\" as a game ? [ y / n]");		
		if ( positiveAnswer() ) {
			selectMainLauncher( launchers );
			
		} else {
			System.out.println("\nWould you like to create a ignore file?");
			if ( positiveAnswer() ) {
				try {
					File folder = container;
					if ( CompactFiles.isCompactFile( container ) ) {
						folder = container.getParentFile();
					}
					new File( folder.getAbsolutePath() + "/" + Games.FILE_NAME_IGNORE ).createNewFile();
				} catch ( Exception e ) {
					Log.error( e.getMessage() );
				}
			}
		}
		
		
		
	}	
	
	private void selectMainLauncher( Launcher[] launchers ) {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < launchers.length; i++ ) {
			sb.append( String.format( "[ %2d] %s\n", i, launchers[i].getAbsolutePath()) );
		}
		int answer = -2;
		do {
		
			System.out.println("\nPlease select the main launcher");
			System.out.println("[ -1] quit");
			System.out.println( sb.toString());
			
			try {
				answer = Integer.parseInt( scan.nextLine() );
			} catch ( Exception e ) {
			
			}
			
		
		} while( !( -2 < answer && answer < launchers.length ) );
		if ( answer != -1 ) {
			gamels.add( new Game( launchers[ answer ]));
		}
	}
	
	private boolean positiveAnswer() {
		String answer = null;
		do {
			try {
				answer = scan.nextLine();
				
			} catch ( Exception e ) {
			
			}
			
		} while ( !isValidAnswer( answer ) );
		
		switch( answer.toLowerCase() ) {
			case "y":                   
			case "yes":
				return true;
		}
		
		return false;
	}
	
	private boolean isValidAnswer( String answer ) {
		String[] possibles = new String[] {"yes","no","n","y" };
		for ( String p : possibles ) {
			if ( p.equals( answer.toLowerCase() )) {
				return true;
			}
		}
		return false;
	}
	
	
	
	public static void main( String[] args ) {
		File file;
		if ( args.length > 0 ) {
			file = new File( args[0] );
			if ( !file.isDirectory() ) {
				Log.error("Parameter most be a directory");
				System.exit( 1 );
			}
		} else {
			file = new File(".");
		}
		new Gemu( file );
	}
}