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
	
	@Override
	public long length() {
		long length = 0;
		for ( File f : listFiles() ) {
			if( f.isDirectory() ) {
				length += new Folder( f ).length();
			} else {
				length += f.length();
			}         			
		}
		return length;
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