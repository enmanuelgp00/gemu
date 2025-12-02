package gemu.game;
import java.io.*;
import gemu.util.*;

public final class Games {
	
	public static boolean isLauncher( File f) {
		if (f.isDirectory()) {
			return false;
		}
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
	
	
	public static boolean isInfoFile( File f ) {
		if (f.isDirectory()) {
			return false;
		}
		
		return FileNames.getExtension(f).equals( Info.FILE_EXTENSION );
	}
}