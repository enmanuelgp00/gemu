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
		if ( isRootZipFile() ) {
			return this;
		}
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
	
	public void unzip( OnProcessAdapter listener ) {
		unzip( listener, Arrays.<String>asList( ZipFiles.PASSWORDS ).iterator() );	
	}
	
	private void unzip( OnProcessAdapter listener, Iterator<String> passwordIterator )  {
		Shell.run( new OnProcessAdapter() {
			@Override
			public void processStarted( Process p ) {
				listener.processStarted( p );
			}
			@Override
			public void streamLineRead( Process p, String line ) {
				listener.streamLineRead( p, line );
			}
			@Override
			public void processFinished( Process p, int exitCode ) {
				if ( exitCode == 0 ) {
					listener.processFinished( p, exitCode );
					return;
				}
				
				if ( !passwordIterator.hasNext() ) {
					listener.processFinished( p, exitCode );
					throw new RuntimeException () {
						@Override
						public void printStackTrace() {
							System.out.println( "Password not found : " + ZipFile.this );
							super.printStackTrace();
						}
					};
				}
				
				unzip( listener, passwordIterator );
				
			}
		}, getRootZipFile().getParentFile(), "7z", "-p" + passwordIterator.next() , "x", getRootZipFile().getAbsolutePath() );
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