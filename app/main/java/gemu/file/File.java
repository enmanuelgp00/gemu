package gemu.file;

import gemu.system.*;
import java.util.List;
import java.util.ArrayList;

public class File extends java.io.File {
	public File( String name ) {
		super( name );
	}  
	
	public boolean matchesPath( File file ) {
		return this.getAbsolutePath().equals( file.getAbsolutePath() );
	}
	
	public boolean hasSameName( File file ) {
		return this.getName().toLowerCase().equals( file.getName().toLowerCase() );
	}
	
	public boolean hasExtension( String e ) {
		if (!isDirectory()) {       		
			String name = getName();
			if( name.contains(".")) {
				String extension = name.substring( name.lastIndexOf('.') + 1 );
				return extension.equals( e );                                  			
			}
			return false;
		}
		return false;
	}
	
	public boolean hasExtension( String... arr ) {
		for (String e : arr ) {
			if ( hasExtension(e) ) {
				return true;
			}
		}
		return false;
	}
	public String getExtension() {
		if( !isDirectory() ) {
			return getNamedExtension();
		}
		return null;
	}
	public String getNamedExtension() {
		String name = getName();
		System.out.println( isDirectory() );
		if ( name.contains(".")) {
			String extension = name.substring( name.lastIndexOf('.') + 1 );
			return extension;
		}
		return null;
	}
	public String getBaseName() {
		return getName().substring( 0, getName().lastIndexOf('.'));
	}
	@Override
	public File[] listFiles() {
		java.io.File[] pfiles = super.listFiles();
		if ( pfiles == null ) {
			return null;
		}
		File[] files = new File[ pfiles.length ];
		for ( int i = 0; i < pfiles.length; i++ ) {
			files[i] = new File( pfiles[i].getAbsolutePath() );
		};
		return files;
	}
	
	@Override
	public File getParentFile() {
		java.io.File parent =  super.getParentFile();
		if ( parent != null ) {	
			return new File( parent.getAbsolutePath() );		
		}
		return null;
	}
	
	public Folder getParentFolder() {
		return new Folder( new File( super.getParentFile().getAbsolutePath() ) );
	}
	
}
