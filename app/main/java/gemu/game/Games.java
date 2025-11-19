package gemu.game;
				   
import gemu.system.Log;
import gemu.io.*;
import java.util.*;

public final class Games {

	public static Set<String> tagsCollection = new TreeSet<String>();
	public static HashMap<Integer , Game> runningGamesIds = new HashMap<>();
	public static final int STATE_RUNNING = 1;
	public static final int STATE_COMPRESSING = 2;
	public static final int STATE_EXTRACTING = 3;
	public static final int STATE_STANDBY = 0;
	public static final int STATE_DELETED = -1;
	
	public static String FILE_NAME_IGNORE = ".gemuignore";  
	public static String FILE_NAME_MORE = ".more";
	
	public static void sort( List<Game> games ) {
		
		
		Set<Game> ready = new HashSet<Game>();
		Set<Game> fav = new HashSet<Game>();
		Set<Game> compressed = new HashSet<Game>();
		Set<Game> deleted = new HashSet<Game>();
		
		List< Set<Game> > setList = new ArrayList< Set<Game> > ();
		setList.add( ready );
		setList.add( fav );
		setList.add( compressed);
		setList.add( deleted );
		
		for ( Game game :  games ) {
			if ( game.getState() == STATE_DELETED ) {
				deleted.add( game );
			} else if( game.isCompressed() ) {
				if ( game.isFavorite() ) {
					fav.add( game );
				} else {
					compressed.add( game );
				}
			} else {
				ready.add( game );
			}
		}
		
		games.clear();
		for ( Set<Game> set : setList ) {
			for ( Game game : set ) {
				games.add( game );
			}
		}
	}
	
	public static boolean isCompactLauncher( File file ) {
		return isLauncher( file ) && CompactFiles.isCompactFile( file );
	}
	
	public static boolean isGameInfo( File file ) {
		return file.hasExtension( GameInfo.EXTENSION );
	}
	
	public static boolean isLauncher( File file ) {
		if ( !file.isDirectory() && file.hasExtension("exe") ) {			
			String name = file.getName().toLowerCase();
			String[] exceptions = new String[] {"crash", "unins", "setting", "config", "helper"};
			boolean found = false;
			for ( String exception : exceptions ) {
				if ( name.contains( exception ) ) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public static boolean isMoreFile( File file ) {
		return !file.isDirectory() && file.getName().equals( FILE_NAME_MORE );
	}
	
	public static boolean isGameContainer( File file ) {
		return file.isDirectory() || CompactFiles.isCompactFile( file );
	}
	
	public static void findPossibleGames( File file, OnGameFoundListener listener ) {
		if ( isGameContainer( file ) && !containsIgnoreFile( file ) ) {
			boolean hasMore = false;
			Set<File> folderSet = new HashSet<File>();
			Set<Game> gameSet = new HashSet<Game>();
			List<Launcher> launcherls = new ArrayList<Launcher>();
			List<CompactFile> compactFilels = new ArrayList<CompactFile>();
			List<CompactFile> compactGameRootFiles = new ArrayList<CompactFile>();

			for ( File f : file.listFiles() ) {
				if ( isMoreFile( f ) ) {
					hasMore = true;
					
				} else if ( isLauncher( f ) ) {
					try {
						launcherls.add( new Launcher( f ));					
					} catch ( Exception e ) {
						e.printStackTrace();
						Log.error( e.getMessage() );
					}
					
				} else if ( CompactFiles.isCompactFile( f ) ) { 
					compactFilels.add( new CompactFile( f ) );
				
				} else if ( f.isDirectory() ) {
					folderSet.add( f );
					
				} else if ( isGameInfo( f )) {
					Game game = new Game( GameInfo.parse( f ) );
					gameSet.add( game );
					for ( String tag : game.getTags() ) {
						tagsCollection.add( tag );
					}
					if ( game.isCompressed() ) {
						compactGameRootFiles.add( new CompactFile ( game.getLauncher()).getParentRootFile() );
					}
				}
			}
			
			if ( launcherls.size() > 0 ) {
				if ( launcherls.size() == 1 ) {
					
					boolean gameFound = false;
					Launcher launcher = launcherls.get( 0 );
					
					for ( Game game : gameSet ) {
						if ( game.getState() != STATE_DELETED ) {						
							if ( game.getLauncher().matchesPath( launcher ) ) {
								gameFound = true;
								break;
							}
						}
					}
					
					if ( !gameFound ) {
						gameSet.add( new Game( launcher ) );
					}
				} else {
					boolean found = false;
					for ( Game game : gameSet ) {
						for ( Launcher launcher : launcherls ) {
							if ( game.getLauncher().matchesPath( launcher ) ) {
								found = true;
								break;
							}
						}
					}
					if ( !found ) {
						listener.onLaunchersFound( launcherls.toArray( new Launcher[ launcherls.size() ]) );					
					}
				}
			}
			
			for ( CompactFile compactFile : compactFilels ) {
				if ( !compactGameRootFiles.contains( compactFile ) ) {
					List<CompactLauncher> compactLaunchers = new ArrayList<CompactLauncher>();
					for ( CompactFile cf : compactFile.listFiles() ) {
						if ( isCompactLauncher( cf ) ) {
							compactLaunchers.add( new CompactLauncher( cf ) );						
						}
					}
					if ( compactLaunchers.size() > 0 ) {					  
						if ( compactLaunchers.size() == 1 ) {
							listener.onGameFound( new Game( compactLaunchers.get(0) ) );
						} else {
							listener.onCompactLaunchersFound( compactLaunchers.toArray( new CompactLauncher[ compactLaunchers.size() ] ) );
						}
					} 
				}
				
			}
			
			for ( Game g : gameSet ) {
				g.findNewScreenshots();
				listener.onGameFound( g );
			}
			
			if ( launcherls.size() == 0 ) {
				for ( File folder : folderSet ) {
					findPossibleGames( folder, listener );
				}
			}
		
		}
		
		
		
		
	}
	
	public static boolean containsIgnoreFile( File file ) {
		if ( file.isDirectory() ) {			
			for ( File f : file.listFiles() ) {
				if ( isIgnoreFile( f ) ) {
					Log.info( file.getAbsolutePath() + " : Ignored");
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean isScreenshot( File file ) {
		return file.hasExtension("jpg", "jpeg", "png");
	}
	
	public static boolean isIgnoreFile( File file ) {
		return !file.isDirectory() && file.getName().equals(FILE_NAME_IGNORE);
	}
	
	public interface OnGameFoundListener {
		public void onGameFound( Game game );
		public void onLaunchersFound( Launcher[] launcherls);
		public void onCompactLaunchersFound( CompactLauncher[] launcherls);
		
	}
}