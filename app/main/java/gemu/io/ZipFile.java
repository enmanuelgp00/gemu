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
		while( parent != null ) {
			if( parent.exists() ) { 
				if ( ZipFiles.EXTENSIONS.contains( FileNames.getExtension(parent) ) ) {
					return new ZipFile( parent.getAbsolutePath() );
				
				} else {
					return null;
				}
			} 
			parent = parent.getParentFile();
		}
		return null;
	}
	
	public boolean isRootZipFile() {
		File parent = getParentFile();
		return ZipFiles.EXTENSIONS.contains( FileNames.getExtension( this ) ) &&
		exists() &&
		!isDirectory();
	}
	
	@Override
	public ZipFile[] listFiles() {
		if ( !isRootZipFile() ) {
			return null;
		}
		ArrayList<ZipFile> list = new ArrayList<>();
		putListedFilesInList( list, Arrays.<String>asList( ZipFiles.PASSWORDS ).iterator() );
		return list.toArray( new ZipFile[ list.size() ] );
	}
	
	private void putListedFilesInList( ArrayList<ZipFile> list, Iterator<String> passwordIterator ) {
		Shell.run( new OnProcessAdapter() {
			String path;
			@Override
			public void streamLineRead( Process p, String line ){
				if ( line.contains("Path = ")) {
					path = line.substring( "Path = ".length(), line.length() );
				} else if ( line.contains("Attributes =")) {
					if ( !line.contains("D") ) {
						try {
							ZipFile z = new ZipFile( getAbsolutePath() + "\\" + path );
							list.add( z );
						} catch( Exception e ) {
							e.printStackTrace();
							}
					}
				}
				
			}
			
			public void processFinished( Process p, int exitCode ) {
				if ( exitCode != 0 ) {
					if ( !passwordIterator.hasNext() ) {
						throw new RuntimeException () {
							@Override
							public void printStackTrace() {
								System.out.println( "Password not found : " + ZipFile.this );
								super.printStackTrace();
							}
						};
					}
					putListedFilesInList( list, passwordIterator );
				}
			}
			
		}, null , "7z","-p" + passwordIterator.next(), "l", "-slt", "-ba", getAbsolutePath() );
	}
}