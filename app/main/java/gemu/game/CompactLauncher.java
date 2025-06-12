package gemu.game;

import gemu.file.*;

public class CompactLauncher extends CompactFile {
	public CompactLauncher( String name ) {
		super( name );
	}
	public CompactLauncher( CompactFile file ) {
		super( file );
	}
	public CompactLauncher( Launcher launcher ) {
		super( launcher );
	}
}