package gemu.io; 
  
import gemu.io.*;

public final class CompactFiles {

	public static final String[] EXTENSIONS = new String[] { "7z", "zip", "rar" };
	
	public static boolean isCompactFile( File file ) {
		CompactFile cf = new CompactFile( file );
		if ( cf.isRootFile() ) {
			return true;
		}
		return cf.getParentRootFile() != null;
	}
}