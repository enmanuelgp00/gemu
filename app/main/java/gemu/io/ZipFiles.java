package gemu.io;

import java.io.*;
import java.util.*;
import gemu.util.*;

public final class ZipFiles {
	public static final HashSet<String> EXTENSIONS = new HashSet<>( Arrays.<String>asList(
		".rar",
		".zip",
		".7zip",
		".7z"
	) );
	public static boolean isCompact( File f ) {
		return EXTENSIONS.contains( FileNames.getExtension(f) );
	}
	
	public static ZipFile get( File f ) throws Exception {
		if ( !isCompact( f ) || !f.exists() ) {
			throw new Exception();
		}
		
		return new ZipFile( f.getAbsolutePath() );
	}
}