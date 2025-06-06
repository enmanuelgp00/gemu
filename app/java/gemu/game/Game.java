package gemu.game;

import gemu.Shell;
import java.io.*;
import java.util.*;

public class Game {
	private static File localEmulator = null;
	
	public static boolean isLocalEmulator( File file ) {
		return file.getName().toLowerCase().contains("leproc.exe");
	}
	
	public static File getLocalEmulator() {
		return Game.localEmulator;
	}
	
	public static void setLocalEmulator( File file ) {
		Game.localEmulator = file;
	}
	
	GameInfo info;
	File folder;
	
	public Game ( File launcher ) {	
		folder = launcher.getParentFile();
		info = new GameInfo( new File ( folder.getAbsolutePath() + "/" + GameInfo.FILE_NAME) );
		setLauncher( launcher );
		String fileName = launcher.getName();
		String name = fileName.substring( 0, fileName.lastIndexOf('.') );
		setName( name );
		info.commit();
	}
	
	public Game ( GameInfo info ) {
		this.info = info;
		folder = info.getFile().getParentFile();
	}
	public void setLauncher( File file ) {
		info.set( GameInfo.KEY_LAUNCHER , file.getName());
	}  
	public File getLauncher() {
		return new File( folder.getAbsolutePath() + "/" + info.get(GameInfo.KEY_LAUNCHER).get(0) );
	}
	public void setName( String name ) {
		info.set( GameInfo.KEY_NAME, name );
	}
	
	public String getName() {
		return info.get( GameInfo.KEY_NAME ).get(0);
	}
	
	public GameInfo addSite( String site ) {
		info.get( GameInfo.KEY_SITES ).add(site);
		return info;
	}
	public List<String> getSites() {
		return info.get( GameInfo.KEY_SITES );
	}
	public GameInfo addTag( String tag ) {
		info.get( GameInfo.KEY_TAGS ).add(tag);
		return info; 		
	}
	public List<String> getTags() {
		return info.get( GameInfo.KEY_TAGS );
	}
	public String getVersion() {
		return info.get(GameInfo.KEY_VERSION).get(0);
	}
	public GameInfo addScreenshot( File file ) {
		if ( file.exists() ) {
			if ( file.getParentFile().getAbsolutePath().equals( folder.getAbsolutePath()) ) {
				info.get(GameInfo.KEY_SCREENSHOTS).add(file.getName());
			}
		}
		return info;
	}
	public List<File> getScreenshots() {
		List<File> screenshots = new ArrayList<File>();
		List<String> names = info.get(GameInfo.KEY_SCREENSHOTS);
		if ( names.size() > 0 ) {
			for (String name : names) {
				screenshots.add( new File( folder.getAbsolutePath() + "/" + name ));
			} 
			return screenshots;
		}
		return null;
	}
	public GameInfo getInfo() {
		return info;
	}
	public boolean isCompressed() {
		if ( getLauncher().exists() )  {
			 return false;
		}
		return true;
	}
	public void openFolder() {
		Shell.run("explorer.exe", folder.getAbsolutePath() );
	}
	public void compress() {
		if ( !isCompressed() ) {
			System.out.println("Compressing : \"" + getName() + "\"");
			Shell.run( "7z", "a","-sdel","-mmt8", "-mx9", "-x!" + GameInfo.FILE_NAME , folder.getAbsolutePath() + "/"+ getName() + ".7z", folder.getAbsolutePath() + "/." );		
		}
	}
	
	public void decompress() {
		if (isCompressed()) {
			System.out.println("Decompressing :" + getName());
			File compressedFile = new File( folder.getAbsolutePath() + "/" + getName() + ".7z" );
			Shell.run("7z", "x", "-o"+folder.getAbsolutePath(), compressedFile.getAbsolutePath());
			compressedFile.delete();
			
		}
	}
	public boolean isFavorite() {
		if ( info.get(GameInfo.KEY_FAVORITE).size() > 0 ) {
			return Boolean.parseBoolean( info.get(GameInfo.KEY_FAVORITE).get(0) );
		}
		return false;
	}
	
	public void setFavorite( boolean value ) {
		info.set(GameInfo.KEY_FAVORITE , String.valueOf( value ));
	} 
	
	public File getFolder() {
		return folder;
	}
	
	public boolean needsEmulator() {
		if ( info.get(GameInfo.KEY_EMULATOR).size() > 0 ) {
			return Boolean.parseBoolean( info.get(GameInfo.KEY_EMULATOR).get(0) );
		}
		return false;
	}
	
	public void play() {
		if ( !isCompressed() ) {			
			Thread thread = new Thread( new Runnable(){
				@Override
				public void run() {
				
					List<String> command = new ArrayList<String>();
					
					if ( needsEmulator() ) {
					   command.add( Game.getLocalEmulator().getAbsolutePath() );					   
					}
					command.add( getLauncher().getAbsolutePath() );
					
					Shell.run( command.toArray( new String[command.size()]) );
				}
			}); 
			thread.start();
		}
		
	}
	
	
}