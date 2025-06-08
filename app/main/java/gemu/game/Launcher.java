package gemu.game;

import java.io.File;

public class Launcher extends File {

	public Launcher( String name ) {
		super(name);
		try {
			if ( isDirectory() || !Launcher.isLauncher( this ) ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						System.out.println( name );
					}
				};
			}
		} catch(Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	
	public static boolean isLauncher( File file ) {
		String[] exceptions = new String[] { "setting", "crash", "helper", "setup" };		
		String name = file.getName();
		
		for (String e : exceptions ) {
			if (name.toLowerCase().contains( e ) ) {
					return false;
			}
		}
		String extension = name.substring( name.lastIndexOf('.') + 1 );
		return extension.equals("exe");
	}
}