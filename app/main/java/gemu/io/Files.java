package gemu.io;

public final class Files {
	private static File f = null;
	
	public static File find( File file, String name ) {
		f = null;
		fnd( file, name );	
		return f;
	}
	
	public static CompactFile find( CompactFile file, String name ) {
		if ( file.isRootFile() ) {
			for( CompactFile f : file.listFiles() ) {
				if( f.getName().equals( name ))
				return f;
			}
		}
		return null;
	}
	
	private static void fnd( File file, String name ) {
		if ( file.isDirectory() ) {
			for( File f : file.listFiles() ) {
				fnd( f, name );
			}
		} else {
			if ( file.getName().equals( name )) {
				f = file;
			}
		}
	}
}