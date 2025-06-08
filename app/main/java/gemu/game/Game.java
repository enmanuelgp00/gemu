package gemu.game;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public final class Game {
	private Info info;
	
	private Game( Launcher launcher ) {
		this.info = new Info( Folder.from( launcher ));
		
		setLauncher( launcher ).
		setName( "New Game" );
		
	}
	
	private Game( Info info ) {
		 this.info = info;
	}
	
	public static Game from( File file ) {
		Launcher launcher = new Launcher( file.getAbsolutePath() );
		return new Game( launcher );
	}
	
	public Game setLauncher( Launcher launcher ) {
		info.set( Info.Key.LAUNCHER, launcher.getName() );	
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