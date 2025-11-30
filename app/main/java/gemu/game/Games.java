package gemu.game;
import java.io.*;

public final class Games {
	
	public static boolean isLauncher( File f) {
		String name = f.getName();
		if ( name.contains(".exe") )  {
			String[] exceptions = new String[]{
				"crash", "unins", "setting", "config", "helper", "setup"
			};
			for ( String exception : exceptions ) {
				if ( name.toLowerCase().contains(exception)) {
					return false;
				}
			}
			return true;
			
		}
		return false;
	}
}