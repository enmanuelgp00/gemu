package gemu.file;

public final class Files {
	private static File f = null;
	public static File findExactNameFile( File file, String name ) {
		clear();
		find( file, name );
		return f;
	}
																	
	private static void find(File file, String name ) {
		 if ( file.isDirectory() ) { 
			for ( File f : file.listFiles() ) {
				find( f, name );
			}
		} else {		
			if ( file.getName().equals( name ) ) {
				f = file;
			}
		}
	} 
	
	private static void clear() {
		f = null;
	}
}