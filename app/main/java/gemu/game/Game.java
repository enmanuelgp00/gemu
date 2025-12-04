package gemu.game;

import java.io.*;
import java.nio.*;    
import java.util.*;
import gemu.io.*;
import gemu.shell.*;                  
import gemu.util.*;


public class Game {
	String COVER_NAME = "main_screenshot.jpg";
	Info info;
	Process process = null;
	
	private Game() {
		
	}
	
	public Game( Executable... executables) throws Exception {
		info = Info.createInfo( executables );
	}
													  
		
	public static Game inZip( ZipFile f ) throws Exception {
		Game game = new Game();
		game.info = Info.createInfo(f);
		return game;
		
	}               
	
	public static Game from( Info info ) {
		Game game = new Game();
		game.info = info;
		return game;
	}
	
	public File getInfoFile() {
		return info.getFile();
	}
	
	private void defaultConstructor() {
	
	}
	
	//play
	public void play( OnProcessAdapter adapter ) {
		Thread th = new Thread(() -> { 
			Shell.run( new OnProcessAdapter() {
				@Override
				public void processStarted( Process process ){
					setProcess( process );
					adapter.processStarted( process );
				}
				@Override
				public void streamLineRead( Process process, String line ) {
					adapter.streamLineRead( process, line );
				} 
				@Override
				public void processFinished( Process process, int exitCode ) {                  
					adapter.processFinished( process, exitCode );
					setProcess( null );                        
				}
			}, getDirectory(), getLauncher().getAbsolutePath() );		
		});
		
		th.start();
	}
	//states
	public boolean isRunning() {
		return process != null;
	}
	
	public boolean isStandby() {
		return !isRunning() && !isInZip() && !isDeleted();
	}
	
	public boolean isInZip() {
		return ZipFiles.isZipFile( getLauncher() );
	}
	
	public boolean isDeleted() {
		return getLauncher() == null;
	}
	
	private void setProcess( Process process ) {
		this.process = process;		
	}
	
	public Process getProcess() {
		return process;
	}
	
	public void stop() {
		if ( isRunning() ) {
			Thread th = new Thread(()->{
				Shell.run( null, null, "taskkill", "/pid", String.valueOf(getProcess().pid()));
			});
			th.start();
		}
	}
	
	// end play
	
	//title
	public void setTitle( String title ) {
		info.set( Info.TITLE, title );
	}
	public String getTitle() {
		String title = info.get( Info.TITLE );
		if ( title != null ) {
			return title;
		}
		if ( isInZip() ) {
			try {
				return ZipFiles.get(getExecutables()[0]).getRootZipFile().getName();			
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
	}
	
	public void setCoverImage( File cover ) {
		info.set( Info.COVER_IMAGE, cover.getName() );
	}
	
	public File getCoverImage() {
		return getKeyAsFile( Info.COVER_IMAGE);
	}
	
	public void findCoverImage() {
		for ( File f : getDirectory().listFiles() ) {
			if ( f.getName().equals(COVER_NAME)) {
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