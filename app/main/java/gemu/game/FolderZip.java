package gemu.game;

import java.util.List;
import java.util.ArrayList;

public class FolderZip extends File {
	public FolderZip( String name ) {
		super( name );
		check();
	}
	
	public FolderZip( File file ) {
		super( file.getAbsolutePath() );
		check();
	}
	
	public boolean hasPossibleGame( OnPossibleGameFoundListener listener ) {
		List<Launcher> launcherLs = new ArrayList<Launcher>();
		if ( isCompressed() ) {
			for ( CompressedFile f : listCompressedFiles() ) {
				if ( Launcher.isFileLauncher( f.getFile() ) ) {
					listener.onGameFound( new Game( new CompressedLauncher( f.getRootFile(), f.getFile() ) ) );
					return true;
				}
			}
		} else {
			for ( File f : this.listFiles() ) {			
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
	
	private void check() {
		try {
		
			if ( !isDirectory() && !isCompressed() ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						System.out.println("\n[ The name \"" + getName() + "\" most be a directory or a compressed file]");
						super.printStackTrace();
					}
				} ;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}