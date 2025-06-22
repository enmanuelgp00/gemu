package gemu.game;

import gemu.io.*;

public class CompactLauncher extends CompactFile implements Launch {
	public CompactLauncher( String name ) {
		super( name );
	}
	
	public CompactLauncher( File file ) {
		super( file );
	}
	
}