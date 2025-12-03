package gemu.io;

import java.io.*;
import java.util.*;

public final class ZipFiles {
	public static final HashSet<String> EXTENSIONS = new HashSet<>( Arrays.<String>asList(
		".rar",
		".zip",
		".7zip",
		".7z"
	) );
	
	public static boolean isCompact( File f ) {
		ZipFile z = new ZipFile(f);
		return z.isRootZipFile() || z.getRootZipFile() != null ;
	}
	
	public static ZipFile get( File f ) throws Exception {
		ZipFile z = new ZipFile(f);
		
		if ( !isCompact( f ) ) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					super.printStackTrace();
				}
			};
		}
		
		return  z;
	}
}