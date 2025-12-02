package gemu.util;

import java.io.*;
import java.util.*;

public final class Zipper {
	public static final HashSet<String> EXTENSIONS = new HashSet<>( Arrays.<String>asList(
		".rar",
		".zip",
		".7zip",
		".7z"
	) );
	public static boolean isCompact( File f ) {
		return EXTENSIONS.contains( FileNames.getExtension(f) );
	}
}