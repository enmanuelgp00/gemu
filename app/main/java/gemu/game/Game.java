package gemu.game;

import gemu.system.Compressions;
import gemu.system.event.*;
import gemu.io.*;
import gemu.system.*;
import gemu.system.event.*;
import java.util.Set;

public class Game {
	private long length = 0;
	private int state = Games.STATE_FREE;
	GameInfo info;
	
	public Game( GameInfo gameInfo ) {
		this.info = gameInfo;
	}
	
	public Game( Launcher launcher ) {
		this.info = new GameInfo( launcher );
	}
	
	public Game( CompactLauncher compactLauncher ) {
		this.info = new GameInfo( compactLauncher );
	}
	
	public void play() {
		try {
			getLauncher().run();		
		} catch ( Exception e ) {
			Log.error( e.getMessage() );
		}
	}
	
	public String getName() {
		String[] names = info.get( GameInfo.Key.name );
		if ( names.length > 0 ) {
			return names[0];			
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
		try {
			return new Launcher ( getFolder() + info.get( GameInfo.Key.launcher )[0] );		
		} catch( Exception e ) {
			Log.error( e );
			e.printStackTrace();
		}
		return null;
	} 
	
	public File getFolder() {
		return info.getFolder();
	}
	
	private void setFolder( File folder ) {
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
	
	public int getState() {
		return state;
	}
	public void setState( int state ) {
		this.state = state;
	}
	
	public long length() {
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
		return CompactFiles.isCompactFile( getLauncher() );
	}
	
	public void compress( OnProcessListener listener ) {
		if ( !isCompressed() && getState() == Games.STATE_FREE ) { 
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
							setState( Games.STATE_FREE );
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
		if ( isCompressed() && getState() == Games.STATE_FREE ) { 
			setState( Games.STATE_EXTRACTING );
			CompactFile compression = new CompactFile( getLauncher()).getParentRootFile();
			String name = compression.getWrapperNameInside();
			String folderPath = getFolder().getAbsolutePath();
			
			File extractionFolder;
			
			if ( name == null ) {
				name = compression.getBaseName();
				extractionFolder = new File( folderPath + "/" + name );
				if ( !extractionFolder.exists() ) {
					extractionFolder.mkdir();      
				}
			} else {
				extractionFolder = getFolder();
			}
			
			File gameContainer = new File( folderPath + "/" + name );
			
			
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
						setLauncher( new Launcher( Files.find( getFolder(), name ) ) );	
						compression.delete();  
						setState( Games.STATE_FREE );
					} 
					
					listener.onProcessFinished( process, exitCode );
					
				}
			} ) );
			
		} else {
			Log.error( getName() + " : is already extracted");
		}
	}
	
	public void delete() {
	
	}
}