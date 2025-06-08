package gemu.game;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Folder extends File {
	public Folder( String name ) {
		super( name );
		try {
			if ( !isDirectory() ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						System.out.println("\n[ The name \"" + name + "\" most be a directory ]");
						super.printStackTrace();
					}
				} ;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
		
	}
	
	public static Folder from( File file ) {
		return new Folder( file.getAbsolutePath() );
	}
	
	public boolean hasPossibleGame( OnPossibleGameFoundListener listener ) {
		listener.onCompressedGameFound( new CompressedGame() );
		return true;
	}
	
	public interface OnPossibleGameFoundListener {
		public void onGameFound( Game game );
		public void onLauncherListFound( List<Launcher> launchers );
		public void onCompressedGameFound( CompressedGame compressedGame );
	}
}