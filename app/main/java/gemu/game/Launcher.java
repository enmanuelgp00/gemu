package gemu.game;
  
import gemu.io.*;
import gemu.system.Shell;
import gemu.system.event.OnProcessAdapter;
import java.io.IOException;

public class Launcher extends File implements Launch {
	public Launcher ( String name ) throws IOException {
		super( name );
		errorIfNotExists();
	}
	
	public Launcher( File file ) {
		super( file );		
		errorIfNotExists();
	}
	
	public void run() {
		errorIfNotExists();
		try {    		 
			if ( !CompactFiles.isCompactFile( this ) ) {			
				Shell.exec( new Shell.Command( getParentFile(), new OnProcessAdapter() { }, getAbsolutePath() ) );
			} else {
				throw new Exception() {
					@Override
					public String getMessage() {
						return Launcher.this + " : the launcher is compressed";
					}
				};
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	private void errorIfNotExists() {
		try {
			File f = this;
			if ( CompactFiles.isCompactFile( f ) ) {
				f = new CompactFile( f ).getParentRootFile();
			}
			
			if ( !f.exists() ) {
				throw new IOException () {
					@Override
					public String getMessage() {
						return Launcher.this + " does not exists";
					}
				};
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
		
	}
	
	
	
}