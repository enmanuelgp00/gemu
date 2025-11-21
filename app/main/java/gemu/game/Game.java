package gemu.game;

import gemu.system.Compressions;
import gemu.system.event.*;
import gemu.io.*;
import gemu.system.*;
import gemu.system.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader; 
import java.io.FileInputStream;
import java.util.Arrays;             
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import gemu.frame.main.gamepanel.GamePanel;
import java.util.Calendar;

public class Game {
	private boolean childrenProcessOpened = false;
	private long length = 0;
	private int processId = -1;
	GameInfo info;
	GamePanel gamePanel;
	
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
			Thread gamePlay = new Thread( new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println( "Current game id :" + getProcessId() );
						setState( Games.STATE_RUNNING );
						getLauncher().run( needsAdmin(), listener);
						
						if ( hasChildrenProcessOpened() ) {
							System.out.println("Children process found");
							waitForChildrenProcessToFinish( listener );
						} else {
							System.out.println("no process children found");
							setClosedGameState();
							
						}
					} catch ( Exception e ) {
						Log.error( e.getMessage() );
					}				
				}
			});
						
			gamePlay.start();
			checkProcessId( getProcessId() );
		}
	}	
	
	public void setClosedGameState() {
		setState( Games.STATE_STANDBY );						
		Games.runningGamesIds.remove( getProcessId() );
		setProcessId( -1 );
	}
	
	private void checkChildrenProcessOpenedState() {
		Shell.exec( new Shell.Command( null, new OnProcessAdapter() {
			@Override
			public void onStreamLineRead( String info ) {
				try {
					String[] values = info.split("\\s+");
					if ( values.length > 1 ) {
						setProcessId( Integer.parseInt( values[1]) );
					}                                                
					System.out.println("processId : " + processId );
					System.out.println("checkChildrenProcessOpenedState : " + values[0] );
					setChildrenProcessOpenedState( Boolean.parseBoolean( values[0] ) );
				} catch( Exception e ) { }
			}
		}, "powershell", "$p = Get-WmiObject Win32_Process | Where-Object { $_.ParentProcessId -like '" + processId + "'}; write-host $( $p -ne $null ) $p.ProcessId"));
	}
	
	private void setChildrenProcessOpenedState( boolean state ) {
		childrenProcessOpened = state;
	}
	
	public boolean hasChildrenProcessOpened() {
		checkChildrenProcessOpenedState();
		return childrenProcessOpened;
	}
	
	private void waitForChildrenProcessToFinish( OnProcessListener listener ) {
		Shell.exec( new Shell.Command( null, new OnProcessAdapter(){
			@Override
			public void onProcessStarted( Process process ) {
				System.out.println( "Waiting for process : " + processId + " to finish ");
			}
			@Override
			public void onProcessFinished( Process p, int exitCode ) {
				System.out.println("Process finshed");
				setClosedGameState();
				listener.onProcessFinished( p, exitCode );
			}
		}, "powershell", "Wait-Process -Id " + processId ) );
	}
	
	public void setGamePanel( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
	}
	
	public GamePanel getGamePanel() {
		return gamePanel;
	}
	
	public void checkProcessId( int parentId ) {
		Thread checkProcessIdThread = new Thread( new Runnable() {
			@Override
			public void run() {
				String processName = getLauncher().getName();
				StringBuilder scriptContent = new StringBuilder();
				scriptContent.append( "$processName = '" + processName + "'\n"); 
				scriptContent.append( "$parentId = '" + String.valueOf(parentId) + "'\n");
				try {
				
					String jarLocation = Shell.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
					String path = jarLocation.substring( 0, jarLocation.lastIndexOf('/'));
					BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( path + "\\" + "id_check_script.ps1" )));
					
					Thread.sleep(500);	
					int code;
					char ch;
					while( ( code = reader.read() ) != -1 ) {
						ch = (char) code;
						if (ch == '"') {    
							scriptContent.append( '\\' );  							
						}                                    
						//System.out.print(ch);
						scriptContent.append( ch );
					}
					
					ProcessBuilder powershellGetsId = new ProcessBuilder( new String[] { "powershell", scriptContent.toString() });
					Process process = powershellGetsId.start();
					
					Thread errorThread = new Thread(() -> {
						try {
							BufferedReader errorReader = new BufferedReader( new InputStreamReader( process.getErrorStream(), "Shift-JIS" ));
							String l;
							while (( l = errorReader.readLine()) != null ) {
								Log.error(l);
							}
						} catch( Exception e ) { }
					});	
					errorThread.start();
					
					String line;
					reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
					
					boolean hasMainWindowHandle = false;;
					
					Log.info( "Checking for id : " + processName );
					while( ( line = reader.readLine() ) != null ) {
						System.out.println( line );
						if ( Character.isDigit( line.charAt(0) ) ) {
							try {
								String[] values = line.split("\\s+");
								
								int id = Integer.parseInt( values[0] );
								hasMainWindowHandle = Boolean.parseBoolean(values[1]);
								
								setProcessId( id );
								
								if ( hasMainWindowHandle && !Games.runningGamesIds.keySet().contains(id)) {
									Games.runningGamesIds.put( id , Game.this);
								}  
							} catch (Exception e) {
								e.printStackTrace();
							}						
						} 					
					}
					
					if ( !hasMainWindowHandle && isRunning() ) { 
						checkProcessId( getProcessId() );
					} else {								
						Log.info( getName() + " has started with id: " + processId );
					
						Log.info("Current running games : [" + Games.runningGamesIds.size() + "]");
						for ( int id : Games.runningGamesIds.keySet()) {
							System.out.println( "[id] " + String.valueOf(id) + " = " + Games.runningGamesIds.get(id).getName() );
						}
					}
					reader.close();
				} catch( Exception e ) {
					e.printStackTrace();
				}
					
				
			}
		});
		checkProcessIdThread.start();
	}
	
	private void setProcessId( int id ) {
		processId = id;
	}
	
	private int getProcessId() {
		return processId;
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
	
	public boolean removeScreenshot( File file ) { 
		if ( file.exists() ) {
			if ( !file.delete() ) {
				Log.error( " Counld not delete screenshot file: " + file.getAbsolutePath() );
				return false;
			}
		}
		
		info.modif( GameInfo.Key.screenshots ).remove( "\\" + file.getName() );
		info.commit();
		return true;
	}
	
	public void setLastTimePlayed( Calendar calendar ) {
		info.set( GameInfo.Key.lastTimePlayed , String.valueOf( calendar.getTimeInMillis() ));
		info.commit();
	}
	
	public void setLastTimePlayed( Long millis ) {
		info.set( GameInfo.Key.lastTimePlayed , String.valueOf( millis ));
		info.commit();
	}
	
	public Calendar getLastTimePlayed() {
		Calendar calendar = Calendar.getInstance();
		long millis = System.currentTimeMillis();
		try {
			String[] data = info.get( GameInfo.Key.lastTimePlayed );
			if ( data.length > 0 ) {
				millis = Long.parseLong( data[0] );								
			} else {
				return null;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		calendar.setTimeInMillis( millis );
		return calendar;
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
		String[] names = info.get( GameInfo.Key.length );
		long currentLength = currentLength();
		long value = 0;
		
		if ( names.length == 1 ) {
			try {
				value = Long.parseLong( names[0] );
			} catch ( Exception e ) { }
		}
			
		if ( currentLength > value ) {
			info.set( GameInfo.Key.length, String.valueOf( currentLength ));
			return currentLength;
		} else {
			return value;
		}
		
	}
	
	public long compactLength() {
		String[] names = info.get( GameInfo.Key.compactLength );
		long currentLength = currentLength();
		long value = 0;
		
		if ( names.length == 1 ) {
			try {
				value = Long.parseLong( names[0] );
			} catch ( Exception e ) { }
		}
			
		if ( currentLength != value && isCompressed() && getState() == Games.STATE_STANDBY  ) {
			info.set( GameInfo.Key.compactLength, String.valueOf( currentLength ));  
			info.commit();
			return currentLength;
		} else {
			return value;
		}
		
	}
	
	public long currentLength() {
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


	public boolean isRunning() {
		return getState() == Games.STATE_RUNNING;
	}


}