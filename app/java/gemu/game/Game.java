package gemu.game;

import java.io.*;
import java.util.*;

public class Game {

	GameInfo info;
	String path;
	
	public Game ( File launcher ) {	
		path = launcher.getParentFile().getAbsolutePath();
		info = new GameInfo( new File ( path + "/" + GameInfo.FILE_NAME) );
		setLauncher( launcher );
		String fileName = launcher.getName();
		String name = fileName.substring( 0, fileName.lastIndexOf('.') );
		setName( name );
		info.commit();
	}
		
	public Game ( GameInfo info ) {
		this.info = info;
		path = info.getFile().getParentFile().getAbsolutePath();
	}
	public void setLauncher( File file ) {
		info.set( GameInfo.KEY_LAUNCHER , file.getName());
	}  
	public File getLauncher() {
		return new File( path + "/" + info.get(GameInfo.KEY_LAUNCHER) );
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
		
	public void play() {
		try {
			ProcessBuilder builder = new ProcessBuilder(getLauncher().getAbsolutePath());
			Process process = builder.start();
			BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()));
			String line;
			while ( (line = reader.readLine()) != null ) {
				System.out.println(line);
			}
			
			int exitCode = process.waitFor();
			System.out.println("Exit code : " + exitCode);		
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
}