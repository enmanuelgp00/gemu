package gemu.io;

import java.io.*;
import java.util.*;
import gemu.shell.*;
import gemu.util.*;

public class ZipFile extends File {
	protected ZipFile( String name ) {
		super( name );
	}
	
	protected ZipFile( File file ) {
		super( file.getAbsolutePath() );
	}
	
	
	public ZipFile getRootZipFile() {
		File parent = getParentFile();
		System.out.println( parent );
		while( parent != null ) {
			if( parent.exists() ) { 
				if ( ZipFiles.EXTENSIONS.contains( FileNames.getExtension(parent) ) ) {
					return new ZipFile( parent.getAbsolutePath() );
				
				} else {
					return null;
				}
			} 
			parent = getParentFile();
		}
		return null;
	}
	
	public boolean isRootZipFile() {
		File parent = getParentFile();
		return parent.isDirectory() && parent.exists();
	}
	
	@Override
	public ZipFile[] listFiles() {
		if ( !isRootZipFile() ) {
			return null;
		}
		ArrayList<ZipFile> list = new ArrayList<>();
		Shell.run( new OnProcessAdapter() {
			String path;
			@Override
			public void streamLineRead( Process p, String line ) {
				if ( line.contains("Path = ")) {
					path = line.substring( "Path = ".length(), line.length() );
				} else if ( line.contains("Attributes =")) {
					if ( !line.contains("D") ) {
						try {
							list.add( new ZipFile( getCanonicalPath() + "\\" + path ));
						} catch( Exception e ) {}
					}
				}
				
			}
		}, null , "7z", "-pkimochi.info", "l", "-slt", "-ba", getAbsolutePath() );
		return list.toArray( new ZipFile[ list.size() ] );
	}
}