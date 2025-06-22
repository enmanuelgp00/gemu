package gemu.io;

public class File extends java.io.File {

	public File( String path ) {
		super( path );
	}
	
	public File( File file ) {
		super( file.getAbsolutePath() );
	}
	
	public boolean matchesPath( File file ) {
		return file.getAbsolutePath().equals( this.getAbsolutePath() );
	}
	
	public String getBaseName() {
		if ( isDirectory() ) {
			return getName();
		}
		String name = getName();
		return name.substring( 0, name.lastIndexOf('.') );
	}
	
	@Override
	public File getParentFile() {
		java.io.File parent = super.getParentFile();
		if ( parent != null ) {
			return new File( parent.getAbsolutePath() );		
		}
		return null;
	}
	
	@Override
	public File[] listFiles() {
		java.io.File[] files = super.listFiles();
		File[] fl = new File[ files.length ];
		
		for ( int i = 0; i < files.length; i ++) {
			fl[i] = new File( files[i].getAbsolutePath() );
		}
		
		return fl;
	}
	
	public boolean hasExtension( String ex ) {
		if ( isDirectory() ) {
			return false;
		}
		String name = getName();
		String extension = name.substring( name.lastIndexOf('.') + 1 );
		return ex.equals( extension );
	}
	
	public boolean hasExtension( String... ex ) {
		for ( String x : ex )  {
			if ( hasExtension(x) ) {
				return true;
			}
		}
		return false;
	}
}