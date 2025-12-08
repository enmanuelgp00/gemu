package gemu.game;

import java.io.*;
import java.nio.*;    
import java.util.*;                   
import java.util.stream.Collectors;
import gemu.io.*;
import gemu.shell.*;                  
import gemu.util.*;                


public class Game {
	Info info;
	long processId = -1;
	long zippingProcessId = -1;
	
	private Game() {
		
	}
	
	public Game( Executable... executables) throws Exception {
		info = Info.createInfo( executables );
		checkLength();
	}
													  
		
	public static Game inZip( ZipFile f ) throws Exception {
		Game game = new Game();
		game.info = Info.createInfo(f);
		game.checkLength(); 
		return game;
		
	}               
	
	public static Game from( Info info ) {
		Game game = new Game();
		game.info = info;
		if ( game.getCoverImage() == null ) {
			game.findCoverImage();		
		}
		return game;
	}
	
	public File getInfoFile() {
		return info.getFile();
	}	
	
	//play
	public void play( OnProcessListener adapter ) {
		Thread th = new Thread(() -> {
			String[] cmd;
			
			if ( needsAdmin() ) {
				cmd = new String[] {"powershell", "$process = start-process -PassThru -verb runas -filePath '" + getLauncher().getAbsolutePath() + "';",
				"write-host $process.id;" };
			} else {
				cmd = new String[] { getLauncher().getAbsolutePath() };
			}
			
			Shell.run( new OnProcessListener() {
				@Override
				public void processStarted( long processId ){   
					setLastTimePlayed( System.currentTimeMillis() );
					
					if ( !needsAdmin() ) {
						setProcessId( processId );
						adapter.processStarted( processId );
					}
					
				}
				@Override
				public void streamLineRead( long processId, String line ) {
					if ( needsAdmin() ) {
						try {
							long id = Long.parseLong( line );
							setProcessId( id );
						} catch( Exception e ) {}
					} 
					adapter.streamLineRead( processId, line );
				} 
				@Override
				public void processFinished( long processId, int exitCode ) {
					if ( needsAdmin() ) {
						adapter.processStarted( getProcessId() ); 
						Shell.waitProcess( getProcessId() );
					}
					setPlayingTime( getPlayingTime() + System.currentTimeMillis() - getLastTimePlayed() ); 
					adapter.processFinished( getProcessId() , exitCode );
					setProcessId( -1L );     
					if ( getCoverImage() == null ) {
						findCoverImage();		
					}					
				}
			}, getDirectory(), cmd );		
		});
		
		th.start();
	}
	
	public void stop() {
		if ( isRunning() ) {
			Thread th = new Thread(()->{
				Shell.run( null, null, "taskkill", "/pid", String.valueOf( getProcessId() ));
			});
			th.start();
		}
	}
	
	//playing time
	private void setPlayingTime( long l ) {
		info.set( Info.PLAYING_TIME, String.valueOf(l) );
		info.commit();
	}
	
	public long getPlayingTime() {
		try {
			return Long.parseLong(  info.get( Info.PLAYING_TIME ) );
		} catch( Exception e ) {}
		return 0;
	}
	
	private void setLastTimePlayed( long l ) {
		info.set( Info.LAST_TIME_PLAYED, String.valueOf(l) );
		info.commit();
	}
	
	public Long getLastTimePlayed() {
		try {                               
			return Long.parseLong(  info.get( Info.LAST_TIME_PLAYED ) );
		} catch( Exception e ) { }
		return null;
	}
	
	//states
	private void setZippingProcessId( Long processId ) {
		zippingProcessId = processId;
	}
	
	public boolean isInZippingProcess() {
		return zippingProcessId != -1;
	}
	
	public boolean isRunning() {
		return processId != -1;
	}
	
	public boolean isStandby() {
		return !isRunning() && !isInZip() && !isDeleted() && !isInZippingProcess();
	}
	
	public boolean isInZip() {
		return ZipFiles.isZipFile( getExecutables()[0] ) && !isInZippingProcess() ;
	}
	
	public boolean isDeleted() {
		File sample = getExecutables()[0];
		if ( ZipFiles.isZipFile( sample ) ) {
			try {
				return !ZipFiles.get( sample ).getRootZipFile().exists();  
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
		return !sample.exists();
	}
	
	public boolean needsAdmin() {
		try {
			return Boolean.parseBoolean(info.get( Info.NEEDS_ADMIN ));
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	private void setNeedsAdmin( boolean val ) {
		info.set( Info.NEEDS_ADMIN, String.valueOf(val) );
		info.commit();
	}
	
	private void setProcessId( Long processId ) {
		this.processId = processId;		
	}
	
	public Long getProcessId() {
		return processId;
	}
	
	//length
	private void checkLength() {
		if ( isStandby() ) {
			setLength( getDirectoryLength( getDirectory() ) );
		} else if ( isInZip() ) {
			try {
				setZipLength( ZipFiles.get( getExecutables()[0] ).getRootZipFile().length() );
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	private void setLength( long l ) { 
		info.set( Info.LENGTH, String.valueOf(l) );
		info.commit();
	}
	
	private void setZipLength( long l ) {
		info.set( Info.ZIP_LENGTH, String.valueOf(l) );
		info.commit();
	}
	
	public Long getLength() {
		try {
			return Long.parseLong( info.get( Info.LENGTH ) );
		} catch( Exception e ) {}
		return null;
	}
	
	public Long getZipLength() {
		try {
			return Long.parseLong( info.get( Info.ZIP_LENGTH ) );
		} catch( Exception e ) {}
		return null;
	
	}	
	
	public Long getCurrentLength() {
		return getDirectoryLength( getDirectory() );
	}
	
	private Long getDirectoryLength( File file ) {
		long length = 0;
		for ( File f : file.listFiles() ) {
			if ( f.isDirectory() ) {
				length += getDirectoryLength(f);
			} else {
				length += f.length();
			}
		}
		return length;
	}
	
	//title
	public void setTitle( String title ) {
		info.set( Info.TITLE, title ); 
		info.commit();
	}
	public String getTitle() {
		String title = info.get( Info.TITLE );
		if ( title != null ) {
			return title;
		}
		if ( isInZip() ) {
			try {
				return FileNames.getBaseName( ZipFiles.get(getExecutables()[0]).getRootZipFile() );			
			} catch ( Exception e ) {}
		}
		return getDirectory().getName();
	}
	
	//launcher
	public void setLauncher( Executable executable ) throws Exception {
		HashSet<Executable> executables = new HashSet<>( Arrays.<Executable>asList( getExecutables() ));
		if ( !executables.contains(executable) ) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					System.out.println( "Game : \"" + getTitle() + "\" does not contains executable : \"" + executable + "\"");
				}
			};		
		}                                                 
		info.set( Info.LAUNCHER, FileNames.relativePath( getDirectory(), executable ) ); 
		info.commit();
	}
	
	public File getLauncher() {
		return getKeyAsFile( Info.LAUNCHER );
	}
	
	public Executable[] getExecutables() {
		String[] relative = info.list( Info.EXECUTABLES );
		Executable[] executables = new Executable[ relative.length ];
		try {
			for ( int i = 0; i < relative.length; i++ ) {
				executables[i] = new Executable( likeAbsolutePath(relative[i]) );
				
			}
		} catch ( Exception e ) {}
		return executables;
	}
	
	//directory	
	public File getDirectory() {
		return info.getFile().getParentFile();
	} 
	
	public void openDirectory() {
		try {
			String path = getDirectory().getCanonicalPath();      
			Shell.run( null, null, new String[]{ "explorer", path });
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	//zip
	public void unzip( OnProcessListener listener ) {
		if ( isInZip() ) {
			try {
			
				ZipFile zipLauncher = ZipFiles.get( getLauncher() );
				ZipFile rootZipFile = zipLauncher.getRootZipFile();
				
				rootZipFile.unzip( new OnZipProcessListener() {
					@Override
					public void processStarted( long processId ) {
						setZippingProcessId( processId );
						listener.processStarted( processId );
					}
					@Override
					public void streamLineRead( long processId, String line ) {
						listener.streamLineRead( processId, line );
					
					}
					@Override
					public void processFinished( long processId, int exitCode, File dir ) {
						if ( exitCode == 0 ) {
							if ( !getDirectory().getAbsolutePath().equals( dir.getAbsolutePath() ) ) {
								info.setFile( new File( dir + "\\" + info.file.getName() ));						
								File oldGameRootDir = new File( likeAbsolutePath( FileNames.relativePath( rootZipFile, zipLauncher ) ) ).getParentFile();
								
								for ( File f : oldGameRootDir.listFiles() ) {
									f.renameTo( new File( dir + "\\" + f.getName() ) );
								}
								
								if ( oldGameRootDir.listFiles().length == 0 ) {
									if ( oldGameRootDir.delete() ) {
										System.out.println("Could not delete old root game : " + oldGameRootDir );
									}
								}
													
							}
							
							Executable[] executables = getExecutables();
							info.clear( Info.EXECUTABLES );
							for ( Executable executable : executables ) {
								info.add( Info.EXECUTABLES, executable.getName() );
								if ( executable.getName().equals( zipLauncher.getName()) ) {
									info.set( Info.LAUNCHER, executable.getName() );							
								} 
							}
							
							info.commit();
							
							if ( getCoverImage() == null ) {
								findCoverImage();		
							}
							
							if ( !rootZipFile.delete() ) {
								throw new RuntimeException() {
									@Override
									public void printStackTrace() {
										System.out.println("Could not delete " + rootZipFile + " after extraction ");
										super.printStackTrace();
									}
								};
							};  
						}
						
						
						setZippingProcessId( -1L );  
						checkLength();
						listener.processFinished( processId, exitCode);
					
					}
				} ) ;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	public void pack( OnProcessListener listener ) {
		if ( !isInZip() ) {
			File dir = getDirectory();
			Executable[] executables = getExecutables();
			File launcher = getLauncher();
			ZipFiles.pack( new OnZipProcessListener() {

				public void processStarted( long processId) {
					setZippingProcessId( processId );
					listener.processStarted( processId );
				}
				public void streamLineRead( long processId, String line ) {
					listener.streamLineRead( processId, line );
				}
				public void processFinished( long processId, int exitCode, File outf ) {
					if ( exitCode == 0 ) {           
						info.clear( Info.EXECUTABLES );   
						String path;
						for ( Executable executable : executables ) {
							path =  "\\" + outf.getName() + "\\" + executable.getName();
							info.add( Info.EXECUTABLES, path );
							if ( launcher.getName().equals( executable.getName() ) ) {
								info.set( Info.LAUNCHER, path );
							}
						}
						info.commit();
						String infoFileBaseName = FileNames.getBaseName( info.getFile() );
						String outfBaseName = FileNames.getBaseName( outf );
						if ( !infoFileBaseName.equals( outfBaseName ) ) {
							info.setFile( new File( getDirectory() + "\\" + outfBaseName + Info.FILE_EXTENSION ) );
						}
					} 
					
					setZippingProcessId( -1L );
					checkLength();
					listener.processFinished( processId, exitCode );
				}
			
			}, dir, dir, getInfoFile(), getCoverImage() );
		}
	}
	
	// cover
	public int getCoverXViewport() {
		String value = info.get( Info.COVER_XVIEWPORT );
		if ( value != null ) {  
			try {
				return Integer.parseInt(value);
			} catch ( Exception e ) {}
			setCoverXViewport(0);
			return 0;
		}   
		setCoverXViewport(0);
		return 0;
	}
	
	public void setCoverXViewport( int i ) {
		info.set( Info.COVER_XVIEWPORT, String.valueOf(i) );
		info.commit();
	}
	
	public void setCoverImage( File cover ) {
		info.set( Info.COVER_IMAGE, cover.getName() ); 
		info.commit();
	}
	
	public File getCoverImage() {
		return getKeyAsFile( Info.COVER_IMAGE);
	}
	
	public void findCoverImage() {
		for ( File f : getDirectory().listFiles() ) {
			if ( f.getName().equals(Games.COVER_NAME)) {
				setCoverImage( f );
				break;
			}
		}
	} 
	//extra
	private File getKeyAsFile( Info.Key key ) {
		String relative = info.get( key );
		if ( relative != null ) {
			File f = new File( likeAbsolutePath( relative ) );
			if ( f.exists() || ZipFiles.isZipFile(f) ) {
				return f;
			}
		}
		return null;
		
	}
	
	public String likeAbsolutePath( String r ) {
		try {
			return getDirectory().getCanonicalPath() + "\\" + r ;		
		} catch( Exception e ) {}
		return null;
	}
	
}