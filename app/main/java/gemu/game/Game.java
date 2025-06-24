package gemu.game;

import gemu.system.Compressions;
import gemu.system.event.*;
import gemu.io.*;
import gemu.system.*;
import gemu.system.event.*; 
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class Game {
	private long length = 0;
	GameInfo info;
	
	public Game( GameInfo gameInfo ) {
		this.info = gameInfo;
		int state = getState();
		if ( state != Games.STATE_DELETED ) {
			if ( state != Games.STATE_STANDBY ) {
				setState( Games.STATE_STANDBY );
			}
		}
	}
	
	public Game( Launcher launcher ) {
		this.info = new GameInfo( launcher );
	}
	
	public Game( CompactLauncher compactLauncher ) {
		this.info = new GameInfo( compactLauncher );
	}
	
	public void play( OnProcessListener listener ) {
		if ( getState() == Games.STATE_STANDBY ) {
			Thread th = new Thread( new Runnable() {
				@Override
				public void run() {
					try {
						setState( Games.STATE_RUNNING );
						getLauncher().run( needsAdmin(), listener);   						
						setState( Games.STATE_STANDBY );
					} catch ( Exception e ) {
						Log.error( e.getMessage() );
					}				
				}
			});
			th.start();		
		}
	}	
	
	public boolean needsAdmin() {
		String[] names = info.get( GameInfo.Key.admin );
		if ( names.length > 0 ) {
			try {
				return Boolean.parseBoolean( names[0] );
			} catch( Exception e ) {
			}
		}
		info.set( GameInfo.Key.admin , "false");
		info.commit();
		return false;
	}
	
	public String getName() {
		String[] names = info.get( GameInfo.Key.name );
		if ( names.length > 0 ) {
			return names[0];			
		}
		
		if ( getState() == Games.STATE_DELETED ) {
			return getFolder().getName();
		}
		Launcher launcher = getLauncher();
		if ( CompactFiles.isCompactFile( launcher ) ) {
			return new CompactLauncher( launcher ).getParentRootFile().getBaseName();
		}
		return getFolder().getName();
		
	};
	
	private void setLauncher( Launcher launcher ) {
		String name = launcher.getAbsolutePath();
		name = name.substring( getFolder().getAbsolutePath().length() );		
		
		info.set( GameInfo.Key.launcher, name );	
		info.commit();
	}
	
	public Launcher getLauncher() {
		if ( !( getState() == Games.STATE_DELETED ) ) { 
			return new Launcher ( getFolder() + info.get( GameInfo.Key.launcher )[0] );	
				
		} else {
			return null;
		}	
	} 
	
	public File getFolder() {
		return info.getFolder();
	}
	
	private void setFolder( File folder ) {
		for ( File f : getScreenshots() ) {
			f.renameTo( new File( folder.getAbsolutePath() + "/" + f.getName()) );
		}
		info.setFolder( folder );
	}
	
	public boolean isFavorite() {
		String[] values = info.get( GameInfo.Key.favorite );
		if ( values.length > 0 ) {
			return Boolean.parseBoolean( values[0] );
		}
		setFavorite( false );
		return false;
	}
	
	public void setFavorite( boolean value ) {
		info.set( GameInfo.Key.favorite, String.valueOf( value ));
		info.commit();
	}
	
	public String[] getTags() {
		return info.get( GameInfo.Key.tags );
	}
	
	public void addTag( String tag ) {
		Set<String> list = info.modif( GameInfo.Key.tags );
		if ( !list.contains( tag ) ) {
			list.add( tag );		
			info.commit();
		}
		
	}
	
	public void removeTag( String tag ) {
		info.modif( GameInfo.Key.tags).remove( tag );
		info.commit();
	}
	
	public File[] getScreenshots() {
		String[] names = info.get( GameInfo.Key.screenshots );
		if ( names.length == 0 ) {
			return new File[0];
		}
		File[] screenshots = new File[ names.length ];
		for ( int i = 0; i < names.length; i ++ ) {
			screenshots[ i ] = new File( getFolder().getAbsolutePath() + "/" + names[ i ] );
		}
		return screenshots;
	}
	
	public void addScreenshot( File file ) {
		String name = file.getAbsolutePath();
		name = name.substring( getFolder().getAbsolutePath().length() );
		info.modif( GameInfo.Key.screenshots ).add( name );
		info.commit();
	}
	
	public void findNewScreenshots() {
		Set<File> screenshots = new HashSet<File>( Arrays.<File>asList( getScreenshots() ));
		for ( File f : getFolder().listFiles() ) {
			if ( Games.isScreenshot( f ) ) {
				if ( !screenshots.contains(f) ) {
					Log.info( getName() + ", New screenshot : " + f.getName() );
					addScreenshot( f );				
				}
			}
		}
	}
	
	public int getState() {
		String[] names = info.get( GameInfo.Key.state );
		if ( names.length > 0 ) {
			try {
				return Integer.parseInt( names[0] );
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit( 1 );
			}
		}
		setState( Games.STATE_STANDBY );
		return Games.STATE_STANDBY;
	}
	public void setState( int state ) {
		info.set( GameInfo.Key.state, String.valueOf( state ) );
		info.commit();
	}
	
	public long length() {
		if ( getState() == Games.STATE_DELETED ) {
			return 0l;
		}
		Launcher launcher = getLauncher();
		if ( CompactFiles.isCompactFile( launcher ) ) {
			length = new CompactFile( launcher ).getParentRootFile().length();
		} else {
			length = 0;
			calculateLength( getFolder() );
		}
		
		return length;
	}
	
	private void calculateLength( File file ) {
		if ( file.isDirectory() ) {
			for ( File f : file.listFiles() ) {
				calculateLength( f );
			}
		} else {
			length += file.length();
		}
	}
	
	public void openFolder() {
		Shell.exec( new Shell.Command( null, new OnProcessAdapter() {}, "explorer", getFolder().getAbsolutePath() ));
	}
	
	public String getVersion() {
		String[] names = info.get( GameInfo.Key.version );
		if ( names.length > 0 ) {
			return names[0];
		}
		return "";
	}
	
	public boolean isCompressed() {
		if ( getState() == Games.STATE_DELETED ) {
			return false;
		}
		return CompactFiles.isCompactFile( getLauncher() );
	}
	
	public void compress( OnProcessListener listener ) {
		if ( !isCompressed() && getState() == Games.STATE_STANDBY ) { 
			setState( Games.STATE_COMPRESSING );
			String name = getName() + ".7z";
			String launcherName = getLauncher().getName();
			
			Compressions.carryOut( new Compressions.CompressProcess( 
				name, 
				getFolder(), 
				new OnProcessAdapter() {
					@Override
					public void onProcessStarted( Process process ) {
						Log.info("Compressing : " + getName()); 
						listener.onProcessStarted( process );
					}
					@Override
					public void onStreamLineRead( String line ) {
						System.out.println( line ); 
						listener.onStreamLineRead( line );
					}
					@Override
					public void onProcessFinished( Process process, int exitCode ) {
						if ( exitCode == 0 ) {
							CompactFile compactFile = new CompactFile( getFolder() + "/" + name );
							CompactFile c = Files.find( compactFile, launcherName );
							System.out.println( c );
							setLauncher( new Launcher( c ) );
							setState( Games.STATE_STANDBY );
						} 
						listener.onProcessFinished( process, exitCode );
					}
				}, 
				new String[] { "*.jpg", "*.png", "*.gemu" } ) 
			);
		} else {
			Log.error( getName() + " : is already compressed");
		}
	}  	
	
	public void extract( OnProcessListener listener ) {
		if ( isCompressed() && getState() == Games.STATE_STANDBY ) { 
			setState( Games.STATE_EXTRACTING );
			CompactFile compression = new CompactFile( getLauncher()).getParentRootFile();
			String wrapperName = compression.getWrapperNameInside();
			String folderPath = getFolder().getAbsolutePath();
			
			File extractionFolder;
			File gameContainer;
			
			boolean hasDirectories = false;
			int compactFilesCount = 0;
			for ( File f : getFolder().listFiles() ) {
				if ( f.isDirectory() ) {
					hasDirectories = true;
					
				} else {
					if ( CompactFiles.isCompactFile( f ) ) {
						compactFilesCount ++;
					}
				}
				
				if ( compactFilesCount > 1 && hasDirectories ) {
					break;
				}
			}
			
			boolean hasWrapper = ( wrapperName != null );
			if (!hasWrapper ) {
				wrapperName = compression.getBaseName();
			}
			
			
			if ( hasDirectories || compactFilesCount > 1 ) {
			
				extractionFolder = new File( folderPath + "/" + wrapperName );
				gameContainer = extractionFolder;
				
				if ( !extractionFolder.exists() ) {
					extractionFolder.mkdir();      
				}
				
			} else {
				extractionFolder = getFolder();
				if ( hasWrapper ) {
					gameContainer = new File( extractionFolder + "/" + wrapperName );				
				} else {
					gameContainer = extractionFolder;
				}
			}
			
			Compressions.carryOut( new Compressions.ExtractProcess(  extractionFolder, compression, new OnProcessAdapter() {			
				@Override
				public void onProcessStarted( Process process ) {
					Log.info("Extracting : " + getName());
					listener.onProcessStarted( process );
				}
				@Override
				public void onStreamLineRead( String line ) {
					System.out.println( line );
					listener.onStreamLineRead( line );
				}
				@Override
				public void onProcessFinished( Process process, int exitCode ) {
					if ( exitCode == 0 ) {
											
						String name = getLauncher().getName();						
						if ( !getFolder().matchesPath( gameContainer ) ) {
							setFolder( gameContainer );
						}
						File l = Files.find( gameContainer, name ); 
						System.out.println( name );
						System.out.println( gameContainer );
						System.out.println( l );
						setLauncher( new Launcher( l ));	
						compression.delete();  
						setState( Games.STATE_STANDBY );
					} 
					
					listener.onProcessFinished( process, exitCode );
					
				}
			} ) );
			
		} else {
			Log.error( getName() + " : is already extracted");
		}
	}
	
	public File getGameContainer() {
		Launcher launcher = getLauncher();
		if ( CompactFiles.isCompactFile( launcher )) {
			return new CompactFile( launcher ).getParentRootFile();
		} else {
			return getFolder();
		}
	}
	
	public void delete() {
		if ( getState() == Games.STATE_STANDBY ) {	
			File container = getGameContainer();
			if ( CompactFiles.isCompactFile( container )) {
				System.out.println( container + " has been deleted ");
				container.delete();
			} else {
				for ( File f : container.listFiles() ) {
					if ( !Games.isScreenshot( f ) && !Games.isGameInfo( f ) ) {
						delete( f );
					} 
				}
			}		
			setState( Games.STATE_DELETED );
		}
	}
	
	private void delete( File file ) {
		if ( file.isDirectory() ) {
			for ( File f : file.listFiles() ) {
				delete( f );
			}
		}
		file.delete();
	}
}