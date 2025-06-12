package gemu.game;

import gemu.system.*;
import gemu.file.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;      
import java.util.HashSet;

public class Game {
	private GameInfo info;
	private static String[] screenshotNames = new String[]{ "screenshot", "capture" };
	public static final int COMPRESSION_STATE_FREE = 0;
	public static final int COMPRESSION_STATE_COMPRESSING = 1;
	public static final int COMPRESSION_STATE_DECOMPRESSING = -1;
	
	public Game( Launcher launcher ) {
		if ( CompactFile.isCompactFile( launcher )) {
			this.info = new GameInfo( new CompactLauncher( launcher ).getParentRootFile().getParentFolder() );
		} else {
			this.info = new GameInfo( launcher.getParentFolder() );
		}
		setLauncher( launcher );
	}
	
	public Game( CompactLauncher launcher ) {		
		this.info = new GameInfo( launcher.getParentRootFile().getParentFolder() );
		setLauncher( launcher );
	}
	
	public Game( GameInfo info ) {
		 this.info = info;
	}
	
	
	public static boolean isScreenshot( File f ) {
		for ( String n : screenshotNames ) {
			if ( f.getName().contains( n ) ) {
				return true;
			}
		}
		return false;
	}
	
	public Game setLauncher( Launcher launcher ) {
		String path = launcher.getAbsolutePath().substring( getFolder().getAbsolutePath().length() );
		info.set( GameInfo.Key.launcher, path );
		info.commit();
		return this;
	}
	
	public Game setLauncher( CompactLauncher launcher ) {
		String path = launcher.getAbsolutePath().substring( getFolder().getAbsolutePath().length() );
		info.set( GameInfo.Key.launcher, path );
		info.commit();		
		return this;
	}
	
	public Launcher getLauncher() {
		return new Launcher ( info.getFolder().getAbsolutePath() + "/" + info.get( GameInfo.Key.launcher )[0] );
	}	
	
	public Game setName( String name ) {
		info.set( GameInfo.Key.name, name );
		info.commit();
		return this;
	}
	
	public String getName() {
		String[] names = info.get( GameInfo.Key.name );
		if ( names.length > 0 ) { 
			return names[0];
		}		
		return getFolder().getName();
	}
	
	public Game addTags( String tag ) {
		info.add( GameInfo.Key.tags, tag );
		info.commit();
		return this;
	}
	
	public String[] getTags() {
		return info.get( GameInfo.Key.tags );
	}
	
	public void play() {
		getLauncher().run();
	}
	public void setFolder( Folder folder ) {
		info.setFolder( folder );
	}
	public Folder getFolder() {
		return info.getFolder();
	}
	
	public void addScreenshot( File file ) {
		if ( file.exists() ) {
			info.modif( GameInfo.Key.screenshots ).add( file.getName() );
			info.commit();		
		}
	}
	
	public File[] getScreenshots() {
		String[] names = info.get( GameInfo.Key.screenshots );
		File[] screenshots = new File[ names.length ];
		
		for ( int i = 0; i < names.length; i++ ) {
			screenshots[i] = new File( getFolder() + "\\" + names[i] );
		}
		
		return screenshots;
	}
	public long length() {
		return getFolder().length();
	}
	
	public boolean isCompressed() {
		return CompactFile.isCompactFile( getLauncher() );
	}
	
	public void openFolder() {
		Shell.exec( new Shell.Command("explorer", getFolder().getAbsolutePath() ) );
	}
	
	public int getCompressionState() {
		for ( Compressions.CProcess p : Compressions.getList() ) {
			File f = p.getFile();
			File tmp = null;
			if ( f.matchesPath( getFolder() ) ) {
				return COMPRESSION_STATE_COMPRESSING;
			} else if ( ( tmp = new CompactFile( getLauncher() ).getParentRootFile() ) != null ) {
				if ( tmp.matchesPath( f ) ) {
					return COMPRESSION_STATE_DECOMPRESSING;				
				}
			}
		}
		
		return COMPRESSION_STATE_FREE;
		
	}
	
	
	public void compress( OnSuccessListener listener ) {
		if ( getCompressionState() == COMPRESSION_STATE_FREE ) {
		
			if ( !isCompressed() ) {  			
				File folder = getFolder();
				Launcher launcher = getLauncher();
				
				Compressions.add( new Compressions.CompressProcess( folder, new Compressions.OnCompressListener() {
					@Override
					public void onStart() {
						System.out.println("\n[ Compressing : " + getName() + " ]"); 
						listener.onStart();
					}
					@Override
					public void onError() {
						listener.onError(); 
					}
					@Override
					public void onSuccess( CompactFile file ) {
						String absolutePath = launcher.getAbsolutePath();
						String parentAbsPath = folder.getAbsolutePath();
						String path = absolutePath.substring( parentAbsPath.length() );
						setLauncher( new CompactLauncher( file.getAbsolutePath() + "/" + path ));
						listener.onSuccess();
					}
					
				}, "screenshot.jpg", GameInfo.NAME_FILE ) );
			
				
			} else {
				System.out.println( "[ " + getName() + " ] : is already compressed");
			}  		
		}      		
	}
	
	public void decompress( OnSuccessListener listener ) {
		if ( getCompressionState() == COMPRESSION_STATE_FREE ) {
		
			if ( isCompressed() ) { 
			
				CompactFile compression = new CompactFile( getLauncher() );
				
				Compressions.add( new Compressions.DecompressProcess( compression , new Compressions.OnDecompressListener() {
					@Override
					public void onStart() {					
						System.out.println("\n[ Decompressing : " + getName() + " ]" );
						listener.onStart();
					}
					@Override
					public void onError() {
						listener.onError();
					}
					@Override
					public void onSuccess( Folder folder ) {						
						String oldPath = compression.getAbsolutePath();
						String root = compression.getParentRootFile().getAbsolutePath();
						String newPath = oldPath.substring( root.length() );
						
						if ( !getFolder().matchesPath( folder ) ) {
							setFolder( folder );
						}
						
						setLauncher( new Launcher( getFolder().getAbsolutePath() + "/" + newPath ) );
						listener.onSuccess();
					}
				}));
			
			} else {
				System.out.println( "[ " + getName() + " ] : is not compressed");
			}
		}
		
	}
	
	
	public static boolean hasPossibleGame( File file, OnPossibleGameFoundListener listener ) {
	
		List<Launcher> launcherLs = new ArrayList<Launcher>();
		List<CompactLauncher> compactLauncherList = new ArrayList<CompactLauncher>();
		Game game = null;
		
		if ( CompactFile.isCompactFile( file ) ) {
			CompactFile compactFile = new CompactFile( file );
			
			if ( compactFile.isRootFile() ) {
			
				for ( CompactFile f : compactFile.listFiles() ) {
				
					if ( Launcher.isLauncherFile( f ) ) {
					
						launcherLs.add( new Launcher( f ) );
					}
				} 			
			}
			
		} else {
			for ( File f : file.listFiles() ) {
			
				if ( GameInfo.isIgnoreFile( f ) ) {
					listener.onIgnoreFileFound( f );
					return false;
					
				} else if ( GameInfo.isGameInfoFile( f ) ) {
					game = new Game( GameInfo.parse( f ) );
				
				} else if ( Launcher.isLauncherFile( f ) ) {
					launcherLs.add( new Launcher( f ) );
					
				} else  if ( Game.isScreenshot( f ) ) {					
					
					if ( game != null ) {
					
						if ( !new HashSet<File>( Arrays.<File>asList( game.getScreenshots() )  ).contains(f)) {
							System.out.println("New screenshot in game : [ " + game.getName() + " ] ");
							game.addScreenshot( f );
						}					
					}
				}
			}
		}
		
		if ( game != null ) {
			listener.onGameFound( game );
			return true;
		}
		
		if ( launcherLs.size() > 0 ) {
			if ( launcherLs.size() == 1 ) {
				listener.onGameFound( new Game( launcherLs.get( 0 ) ) );
				return true;
			}
			
			listener.onLauncherListFound( launcherLs );
			return true;
		}
		
		return false;
	}
	
	public interface OnPossibleGameFoundListener {
		public void onGameFound( Game game );
		public void onLauncherListFound( List<Launcher> launchers );
		public void onIgnoreFileFound( File file );
	}
}