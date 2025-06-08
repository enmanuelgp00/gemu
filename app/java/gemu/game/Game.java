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
		File launch = new File( folder.getAbsolutePath() + "/" + info.get(GameInfo.KEY_LAUNCHER).get(0) );
		return launch;
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
	public GameInfo removeScreenshot( File file ) {
		if ( info.get(GameInfo.KEY_SCREENSHOTS).remove( file.getName() ) ) {
			System.out.println( file.getName() + " was remove from screenshots in game : " + getName() );
		}
		if ( file.exists() ) {
			file.delete();
		}
		return info;
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
		return !getLauncher().exists();
		/*
		if ( getCompression() != null )  {
			 return true;
		}
		return false;
		*/
	}
	public void openFolder( Shell.OnProcessListener listener ) {
		Shell.run( listener, "explorer.exe", folder.getAbsolutePath() );
	}
	public void openFolder() {
		Shell.run("explorer.exe", folder.getAbsolutePath() );
	}
	public void compress( Shell.OnProcessListener listener ) {
		Thread t = new Thread( new Runnable() {
			@Override
			public void run () {
				if ( !isCompressed() ) {
					Shell.run( listener , "7z", "a","-sdel","-mmt8", "-mx9", "-x!" + GameInfo.FILE_NAME , folder.getAbsolutePath() + "/"+ getName() + ".7z", folder.getAbsolutePath() + "/." );		
				} else {
					System.out.println( getName() + " is already compressed");
				}							
			}
		});
		t.start();
	}
	
	public File getCompression() {
		List<File> compressions = new ArrayList<File>();
		for ( File f : folder.listFiles() ) {
			 if (f.getName().contains(".7z")) {
				compressions.add(f);
			 }
		}
		if (compressions.size() > 1 ) {
			System.out.println("More than one compression file found in " + folder.getAbsolutePath() + "\n game: " + getName() );
			System.exit( 1 );
		} else if (compressions.size() == 0 ) {
			return null;
		}
		return compressions.get(0);
	}
	public void decompress( Shell.OnProcessListener listener ) {
		Thread t = new Thread( new Runnable() {
			@Override
			public void run () {
				File compression = null; 
				if ( ( compression = getCompression()) != null ) {
						Shell.run( listener, "7z", "x", "-o"+ folder.getAbsolutePath(), compression.getAbsolutePath() );
						compression.delete();		
				} else {
					System.out.println( getName() + " no compressed file found ");
				}		
			}
		});
		t.start();
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