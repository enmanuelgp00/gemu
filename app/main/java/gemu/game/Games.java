package gemu.game;

import gemu.system.*;
import gemu.file.*;
import java.io.IOException;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;     
import java.util.TreeSet;     
import java.util.HashSet;     
import java.util.HashMap;


public final class Games {

	private static String[] screenshotNames = new String[]{ "jpg", "png" };	
	public static String MORE_FILE_NAME = ".more";
	
	public static File defineMoreFileIn( Folder folder ) {
		return new File( folder.getAbsolutePath() + "\\" + MORE_FILE_NAME );
	}
	public static boolean isScreenshot( File f ) {
		for ( String n : screenshotNames ) {
			if ( f.hasExtension( n ) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isMoreFile( File file ) {
		return file.getName().equals( MORE_FILE_NAME );
	}
	
	public static boolean hasPossibleGame( File file, OnPossibleGameFoundListener listener ) {
		boolean value = false;
		boolean hasDirectories = false;
		
		List<File> screenshots = new ArrayList<File>();
		List<Launcher> launcherLs = new ArrayList<Launcher>();
		List<Game> games = new ArrayList<Game>();
		List<CompactFile> compactGames = new ArrayList<CompactFile>();
		HashMap< CompactFile, List<CompactLauncher>> compactLaunchersMap = new HashMap<CompactFile , List<CompactLauncher>>();
		
		for ( File f : file.listFiles() ) {
		
			if ( GameInfo.isIgnoreFile( f ) ) {
				listener.onIgnoreFileFound( f );
				return false;
				
			} else if ( CompactFile.isCompactFile( f ) ) {
				CompactFile compactFile = new CompactFile( f );
				
				if ( compactFile.isRootFile() ) {
					List<CompactLauncher> compactLaunchers = new ArrayList<CompactLauncher>();				
					for ( CompactFile cf : compactFile.listFiles() ) {
						if ( Launcher.isLauncherFile( cf ) ) {					
							compactLaunchers.add( new CompactLauncher( cf ) );
						}
					}
					if ( compactLaunchers.size() > 0 ) {
						compactLaunchersMap.put( compactFile , compactLaunchers );					
					}
				}
			
			} else if ( GameInfo.isGameInfoFile( f ) ) {
				Game game = new Game( GameInfo.parse( f ) );
				games.add( game );
				
				if ( CompactFile.isCompactFile( game.getLauncher() ) ) {
					compactGames.add( new CompactFile( game.getLauncher()).getParentRootFile() );
				}
					
			
			} else if ( Launcher.isLauncherFile( f ) ) {
				launcherLs.add( new Launcher( f ) );
				
			} else if ( Games.isScreenshot( f ) ) {
				screenshots.add( f );
			} else if ( Games.isMoreFile( f ) ) {
				listener.onMoreFileFound( f );
			} else if ( f.isDirectory() ) {
				hasDirectories = true;
			}
		}
		
		if ( hasDirectories && compactGames.size() > 2 ) {
			File more = Games.defineMoreFileIn( new Folder ( file ) );
			if ( !more.exists() ) {
				try {
					more.createNewFile();
				} catch ( Exception e ) {
					Log.error( e.getMessage() );
				}
			}
		}
		
		if ( games.size() > 0 ) {
			if ( games.size() == 1 ) {
				Game game = games.get( 0 );
				Set<File> gameScreenshots = new HashSet<File>( Arrays.<File>asList( game.getScreenshots() ) );
				for ( File f : screenshots ) {				
					if ( !gameScreenshots.contains(f)) {
						System.out.println("New screenshot in game : [ " + game.getName() + " ] ");
						game.addScreenshot( f );
					}
				}
				
				listener.onGameFound( game );
				value = true;
			} else {				 
				for ( Game game : games ) {
					listener.onGameFound( game );
				}
			}
			value = true;
		}
		
		if ( launcherLs.size() > 0 ) { 
			if ( launcherLs.size() == 1 ) {
				Launcher l = launcherLs.get( 0 ); 
				boolean gameFound = false;
				
				for ( Game g : games ) {
					if ( g.getLauncher().matchesPath( l ) ) {
						gameFound = true;
						break;
					}
				}
				
				if ( !gameFound ) {
					Game game = new Game( l );
					listener.onGameFound( game ); 
					Log.info( game.getName() + " : New game found");
				}
				return true;
			}
			
			listener.onLauncherListFound( launcherLs );
			return true;
		}
		
		if ( compactLaunchersMap.keySet().size() > 0 ) {
			for ( CompactFile key : compactLaunchersMap.keySet() ) { 
				
				if ( !compactGames.contains( key ) ) {
					List<CompactLauncher> launchers = compactLaunchersMap.get( key );
					if ( launchers.size() == 1 ) {
						Game game = new Game( launchers.get(0) );
						listener.onGameFound( game ); 
						Log.info( game.getName() + " : New game found");
					} else {
						listener.onCompactLauncherListFound( launchers );
					}
				}
				
			}
			return true;
		}
		
		return value;
	}
	
	public interface OnPossibleGameFoundListener {
		public void onGameFound( Game game );
		public void onLauncherListFound( List<Launcher> launchers );
		public void onCompactLauncherListFound( List<CompactLauncher> launchers );
		public void onIgnoreFileFound( File file );                                 
		public void onMoreFileFound( File file );
	}
	
	
}


