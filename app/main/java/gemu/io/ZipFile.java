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
	
	public void unzip( OnZipProcessListener listener ) {
		File root = getRootZipFile();
		File dir = root.getParentFile();
		int zipFileCount = 0;
		int foldersCount = 0;
		
		for ( File f : dir.listFiles() ) {
			if ( f.isDirectory() ) {
				foldersCount++;
			} else if ( ZipFiles.isZipFile(f)) {
				zipFileCount++;
			}
		}
		
		if ( zipFileCount > 1 || foldersCount != 0 ) {
			dir = new File( dir.getAbsolutePath() + "\\" + FileNames.getBaseName( root ));
		}
		unzip( listener, dir, Arrays.<String>asList( ZipFiles.PASSWORDS ).iterator() );	
	}
	
	private void unzip( OnZipProcessListener listener, File dir, Iterator<String> passwordIterator )  {
		Shell.run( new OnProcessListener() {
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
					listener.processFinished( p, exitCode, dir );
					return;
				}
				
				if ( !passwordIterator.hasNext() ) {
					listener.processFinished( p, exitCode, null );
					throw new RuntimeException () {
						@Override
						public void printStackTrace() {
							System.out.println( "Password not found : " + ZipFile.this );
							super.printStackTrace();
						}
					};
				}
				
				unzip( listener, dir, passwordIterator );
				
			}
		}, null, "7z",  "-o" + dir.getAbsolutePath() ,"-bsp1", "-p" + passwordIterator.next() , "x", getRootZipFile().getAbsolutePath() );
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
		Shell.run( new OnProcessListener() {
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