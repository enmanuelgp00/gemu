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
	
	public static boolean isZipFile( File f ) {
		ZipFile z = new ZipFile(f);
		if ( z.isRootZipFile() ) {
			return true;
		}                         
		return z.getRootZipFile() != null ;
	}
	
	public static ZipFile get( File f ) throws Exception {
		ZipFile z = new ZipFile(f);
		
		if ( !isZipFile( f ) ) {
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