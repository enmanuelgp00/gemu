package gemu.game;

import gemu.system.*;
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
	
	public static boolean isLauncherFile( File file ) {
		if ( file.hasExtension("exe") ) {
			String[] exceptions = new String[] { "config", "setting", "crash", "helper", "setup", "unins", "update" };		
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
		if ( !CompactFile.isCompactFile( this ) ) {
			Thread ht = new Thread( new Runnable() {
				@Override
				public void run() {
					Shell.exec( new OnProcessListener() {
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
			
			if ( !Launcher.isLauncherFile( this ) ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						super.printStackTrace();
						Log.error( getName() + "Is not a valid launcher name : " );
					}
				};
			}
			
			if ( CompactFile.isCompactFile( this ) ) {
				if ( !new CompactLauncher( this ).exists() ) {
					throw new Exception() {
						@Override
						public void printStackTrace() {
							super.printStackTrace();
							Log.error( getAbsolutePath() + " : this launcher does not exists ");
						}
					};
				}
			} else {
				if ( !exists() ) {
					throw new Exception() {
						@Override
						public void printStackTrace() {
							super.printStackTrace();
							Log.error( getAbsolutePath() + " : this launcher does not exists ");
						}
					};
				}
			}
			
		} catch(Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}