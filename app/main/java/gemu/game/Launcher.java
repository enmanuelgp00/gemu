package gemu.game;

import gemu.system.Shell;
import gemu.file.*;

public class Launcher extends File {

	public Launcher( String name ) {
		super(name);
		check();
	}
	public Launcher( File file ) {
		super( file.getAbsolutePath() );
		check();
	}
	
	public static boolean isFileLauncher( File file ) {
		if ( file.hasExtension("exe") ) {
			String[] exceptions = new String[] { "setting", "crash", "helper", "setup" };		
			String name = file.getName();
			
			for (String e : exceptions ) {
				if (name.toLowerCase().contains( e ) ) {
						return false;
				}
			}
			return true;
		}		
		return false;
	}
	
	
	public void run () {
		if ( !CompactFile.isFileCompact( this ) ) {
			Thread ht = new Thread( new Runnable() {
				@Override
				public void run() {
					Shell.exec( new Shell.OnProcessListener() {
						@Override
						public void onProcessStarted( Process process ) {
							System.out.println( getAbsolutePath() );
						}
						@Override
						public void onStreamLineRead( String line ) {
							System.out.println( line );
						}
						@Override
						public void onProcessFinished( Process process, int exitCode ) {
							System.out.println("");
						}
					}, new Shell.Command( getParentFolder() , getAbsolutePath() ) );
				}
			} );
			ht.start();
		} else {
			System.out.println("First, you need to decompress its container, launcher : [ " + getAbsolutePath() + " ]" );
		}
	}
	
	private void check() {
		 try {
			
			if ( !exists() && !CompactFile.isFileCompact( this ) ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						super.printStackTrace();
						System.out.println( "Launcher does not exists : " + getAbsolutePath() );
					}
				};
			}
			
			if ( !Launcher.isFileLauncher( this ) ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						super.printStackTrace();
						System.out.println( "Is not a valid launcher name : " + getName() );
					}
				};
			}
		} catch(Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}