package gemu.game;

import java.io.*;
import java.nio.*;
import gemu.shell.*;


public class Game {
	String COVER_NAME = "main_screenshot.jpg";
	Info info;
	Process process = null;
	
	public Game( Launcher launcher ) {
		info = new Info( launcher );
		findCover();
	}
	
	public Game( ZipLauncher f ) {
	
	}
	
	public Game( Info info ) {
	
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
	
	public boolean isRunning() {
		return process != null;
	}
	
	private void setProcess( Process process ) {
		this.process = process;		
	}
	
	public Process getProcess() {
		return process;
	}
	
	public String getTitle() {
		String title = info.get( Info.TITLE );
		if ( title != null ) {
			return title;
		}
		return getLauncher().getName();
	}
	
	public void stop() {
		if ( isRunning() ) {
			Thread th = new Thread(()->{
				Shell.run( new OnProcessAdapter() { 
					@Override
					public void streamLineRead( Process process, String line ){
						System.out.println( line );
					}
				}, null, "taskkill", "/pid", String.valueOf(getProcess().pid()));
			});
			th.start();
		}
	}
	
	//launcher
	public void setLauncher( Launcher launcher) {
		info.set( Info.COVER, launcher.getName() );
		
	}
	
	public File getLauncher() {
		return getKeyAsFile( Info.LAUNCHER );
	}
	
	//directory	
	public File getDirectory() {
		return info.getFile().getParentFile();
	} 
	
	public void openDirectory() {
		Shell.run( null, null, new String[]{ "explorer", getDirectory().getAbsolutePath() });
	}
	
	
	// cover
	public void setCover( File cover ) {
		info.set( Info.COVER, cover.getName() );
	}
	
	public File getCover() {
		return getKeyAsFile( Info.COVER);
	}
	
	public void findCover() {
		for ( File f : getDirectory().listFiles() ) {
			if ( f.getName().equals(COVER_NAME)) {
				setCover( f );
				break;
			}
		}
	} 
	//extra
	private File getKeyAsFile( Info.Key key ) {
		String name = info.get( key );
		if ( name != null ) {
			File f = new File( getDirectory() + "/" + name );
			if ( f.exists() ) {
				return f;
			}
		}
		return null;
		
	}
	
}