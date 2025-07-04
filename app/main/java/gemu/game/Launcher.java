package gemu.game;
  
import gemu.io.*;
import gemu.system.Shell;
import gemu.system.event.OnProcessListener;
import java.io.IOException;
import java.util.Arrays;

public class Launcher extends File implements Launch {
	public Launcher ( String name ) {
		super( name );
		errorIfNotExists();
	}
	
	public Launcher( File file ) {
		super( file );		
		errorIfNotExists();
	}
	
	public void run( boolean asAdmin , OnProcessListener listener ) {
		errorIfNotExists();
		try {    		 
			if ( !CompactFiles.isCompactFile( this ) ) {			
				String[] command;
				if ( asAdmin ) {
					command = new String[] { "powershell", "start-process", "-verb", "runas", "-wait", "-filepath", "'"+ getAbsolutePath() + "'"};
				} else {
					command = new String[] { getAbsolutePath() };
				}				
				Shell.exec( new Shell.Command( getParentFile(), listener , command ) );
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