package gemu.file;

import gemu.game.*;
import java.util.List;
import java.util.ArrayList;

public class Folder extends File {
	public Folder( String name ) {
		super( name );
		check();
	}
	
	public Folder( File file ) {
		super( file.getAbsolutePath() );
		check();
	}
	
	
	
	private void check() {
		try {
		
			if ( !isDirectory() ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						System.out.println("\n[ The name \"" + getName() + "\" most be a directory ]");
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