package gemu;

import javax.swing.SwingUtilities;
import gemu.game.*;
import gemu.frame.main.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Gemu {
	public static final String IGNORE = "gemu.ignore";
	List<Game> gameList;
	
	Gemu( File file ) {
		gameList = new ArrayList<Game>();
		findGames( file );
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run() {
				MainFrame mainFrame = new MainFrame( gameList );			
			}
		} );
	}
	
	public void findGames( File file ) {
		 if ( file.isDirectory() ) {
			if ( !hasPossibleGame( file, 
				new OnPossibleGameFound() {
					@Override
					public void gameFound( Game game ) {
						gameList.add( game );
					}
					@Override
					public void launchersFound( List<File> launchers ) {
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
				listener.gameFound( game );
				return true;
			}
		}
		
		if ( launchers.size() > 0 ) {
			listener.launchersFound( launchers );
			return true;
		}
		return false;
		
	}
	public boolean isLauncher( File file ) {
		String[] exceptions = new String[] {"helper", "crash", "update", "install", "legui" };
		String name = file.getName();
		for ( String exception : exceptions ) {
			if ( name.toLowerCase().contains(exception)) {
				return false;
			}
		}
		int dotIndex = name.lastIndexOf('.');
		String extension = name.substring( dotIndex + 1 );
		return extension.equals("exe");
	}
	
	
	public boolean isGameInfo( File file ) {
		return file.getName().equals(GameInfo.FILE_NAME);
	}	
	
	public interface OnPossibleGameFound {
		public void gameFound( Game game );
		public void launchersFound( List<File> launchers );
	}
	public static void main( String[] args ) {
		new Gemu( new File(args[0]));
	}
}