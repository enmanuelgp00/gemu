package gemu.game;

import java.io.*;
import java.nio.*;


public class Game {
	String COVER_NAME = "main_screenshot.jpg";
	Info info;
	
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
	
	public void play() {
		System.out.println("playing " + getTitle());
	}
	
	public String getTitle() {
		String title = info.get( Info.TITLE );
		if ( title != null ) {
			return title;
		}
		return getLauncher().getName();
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