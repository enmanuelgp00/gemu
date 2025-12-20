package gemu.util;

import java.io.*;

public final class FileNames {
	public static final String[] IMAGE_EXTENSIONS = new String[] { ".jpg",".jpeg",".png",".webp"};

	public static boolean hasExtension( File f, String extension ) {
		return getExtension(f).equals(extension);
	}
	public static boolean hasImageExtension( File f ) {
		for ( String extension : IMAGE_EXTENSIONS ) {
			if (  hasExtension( f, extension ) ) {
				return true;
			}
		}
		return false;
	}
	public static String getExtension( File f ) {
		if ( f.isDirectory() ) {
			return "";
		}
		String extension = f.getName();
		int dotIndex = extension.lastIndexOf("."); 
		if ( dotIndex == -1 ) {
			return "";
		}
		return extension = extension.substring( dotIndex ).toLowerCase();
	}
	
	public static String getBaseName( File f ) {
		String name = f.getName();
		if ( !name.contains(".") ) {
			return f.getName();
		}
		name = name.substring( 0, name.lastIndexOf(".")  ); 
		return name;
	}
	
	public static String relativePath( File origin, File file ) {
		try {
			String path = file.getCanonicalPath();
			path = path.substring( origin.getCanonicalPath().length(), path.length() );
			
			return path;
		
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
}