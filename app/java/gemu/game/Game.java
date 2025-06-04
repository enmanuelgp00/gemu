package gemu.game;

import java.io.File;
import java.util.*;

public class Game {

	GameInfo info;
	String dir;
	
	public Game ( File launcher ) {	
		dir = launcher.getParentFile().getAbsolutePath();
		info = new GameInfo( new File ( dir + "/" + GameInfo.FILE_NAME) );
		setLauncher( launcher );
		String fileName = launcher.getName();
		String name = fileName.substring( 0, fileName.lastIndexOf('.') );
		setName( name );
		info.commit();
	}
		
	public Game ( GameInfo info ) {
		this.info = info;
	}
	public void setLauncher( File file ) {
		info.set( GameInfo.KEY_LAUNCHER , file.getName());
	}  
	public File getLauncher() {
		return new File( dir + "/" + info.get("launcher") );
	}
	public void setName( String name ) {
		info.set( GameInfo.KEY_NAME, name );
	}
	
	public String getName() {
		return info.get( GameInfo.KEY_NAME );
	}
	
	public void addSite( String site ) {
		info.getList( GameInfo.KEY_SITES ).add(site);
	}
	public List<String> getSites() {
		return info.getList( GameInfo.KEY_SITES );
	}
	public void addTag( String tag ) {
		info.getList( GameInfo.KEY_TAGS ).add(tag); 		
	}
	public List<String> getTags() {
		return info.getList( GameInfo.KEY_TAGS );
	}
	
	public GameInfo getInfo() {
		return info;
	}	
}