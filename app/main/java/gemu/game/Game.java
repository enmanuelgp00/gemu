package gemu.game;

import java.util.List;
import java.util.ArrayList;

public class Game {
	private Info info;
	
	public Game( Launcher launcher ) {
		this.info = new Info( launcher.getParentFolderZip() );
		setLauncher( launcher );
		
	}
	
	public Game( CompressedLauncher launcher ) {		
		this.info = new Info( launcher.getRootFile().getParentFolderZip() );
		setLauncher( launcher.getFile() );
	}
	
	public Game( Info info ) {
		 this.info = info;
	}
	
	public Game setLauncher( Launcher launcher ) {
		String path = launcher.getAbsolutePath().substring( info.getFolder().getAbsolutePath().length() );
		info.set( Info.Key.LAUNCHER, path );	
		return this;
	}	
	
	public Launcher getLauncher() {
		return new Launcher ( info.getFolder().getAbsolutePath() + "/" + info.get( Info.Key.LAUNCHER ).get(0) );
	}
	
	public Game setName( String name ) {
		info.set( Info.Key.NAME, name );
		return this;
	}
	
	public String getName() {
		return info.get( Info.Key.NAME ).get(0);
	}
	
	public Game addTags( String tag ) {
		info.add( Info.Key.TAGS, tag );
		return this;
	}
	
	public List<String> getTags() {
		return info.get( Info.Key.TAGS );
	}
	
}