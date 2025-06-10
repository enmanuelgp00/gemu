package gemu.game;

import gemu.system.Shell;
import gemu.file.*;
import java.util.List;
import java.util.ArrayList;

public class Game {
	private Info info;
	
	public Game( Launcher launcher ) {
		this.info = new Info( launcher.getParentFolder() );
		setLauncher( launcher );
		
	}
	
	public Game( CompactLauncher launcher ) {		
		this.info = new Info( launcher.getParentRootFile().getParentFolder() );
		setLauncher( launcher );
	}
	
	public Game( Info info ) {
		 this.info = info;
	}
	
	public Game setLauncher( Launcher launcher ) {
		String path = launcher.getAbsolutePath().substring( info.getFolder().getAbsolutePath().length() );
		info.set( Info.Key.LAUNCHER, path );	
		return this;
	}
	public Game setLauncher( CompactLauncher launcher ) {
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
		List<String> names = info.get( Info.Key.NAME );
		if ( names.size() > 0 ) { 
			return names.get(0);
		}		
		return getFolder().getName();
	}
	
	public Game addTags( String tag ) {
		info.add( Info.Key.TAGS, tag );
		return this;
	}
	
	public List<String> getTags() {
		return info.get( Info.Key.TAGS );
	}
	
	public void play() {
		getLauncher().run();
	}
	
	public Folder getFolder() {
		return info.getFolder();
	}
	
	public boolean isCompressed() {
		return CompactFile.isFileCompact( getLauncher() );
	}
	
	public void openFolder() {
		Shell.exec( new Shell.Command("explorer", getFolder().getAbsolutePath() ) );
	}
	
	public void compress() { 
		if ( !isCompressed() ) {
			
			File file = info.getFolder();		
			Compressions.add( new Compressions.CompressProcess( file, new Shell.OnProcessListener() {
				@Override
				public void onProcessStarted( Process process ) {
					System.out.println("\n[ Compressing : " + getName() + " ]");
				}
				@Override
				public void onStreamLineRead( String line ) { 
					System.out.println( line );
				}
				@Override
				public void onProcessFinished( Process process, int exitCode ) {
					//setLauncher( new Launcher( new CompactFile() );
				}
			}, "screenshot.jpg", Info.NAME_FILE ) );
			
			
		} else {
			System.out.println( "[ " + getName() + " ] : is already compressed");
		}
	}
	
	public void decompress() {
		if ( isCompressed() ) { 
			CompactFile compression = new CompactFile( getLauncher() );
			Compressions.add( new Compressions.DecompressProcess( compression, new Shell.OnProcessListener() {
				@Override
				public void onProcessStarted( Process process ) {
					System.out.println("\n[ Decompressing : " + getName() + " ]" );
				}
				@Override
				public void onStreamLineRead( String line ) {
					System.out.println( line );
				}
				@Override
				public void onProcessFinished( Process process, int exitCode ) {
					String oldPath = compression.getAbsolutePath();
					String root = compression.getParentRootFile().getAbsolutePath();
					String newPath = oldPath.substring( root.length() );
					setLauncher( new Launcher( getFolder().getAbsolutePath() + "/" + newPath ) );
				}
			}));
		
		} else {
			System.out.println( "[ " + getName() + " ] : is not compressed");
		}
	}
	
	
	public static boolean hasFilePossibleGame( File file, OnPossibleGameFoundListener listener ) {
		List<Launcher> launcherLs = new ArrayList<Launcher>();
		if ( CompactFile.isFileCompact( file ) ) {
			CompactFile compactFile = new CompactFile( file );
			if ( compactFile.isRootFile() ) {
				for ( CompactFile f : compactFile.listFiles() ) {
					if ( Launcher.isFileLauncher( f ) ) {
						listener.onGameFound( new Game( new CompactLauncher( f ) ) );
						return true;
					}
				} 			
			}
			
		} else {
			for ( File f : file.listFiles() ) {			
				if ( Info.isFileInfo( f ) ) {
					listener.onGameFound( new Game( Info.parse( f ) ) );
					return true;
				
				} else if ( Launcher.isFileLauncher( f ) ) {
					launcherLs.add( new Launcher( f ) );
				}
			}
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
	}
}