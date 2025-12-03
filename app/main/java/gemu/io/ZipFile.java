package gemu.io;

import java.io.*;
import java.util.*;
import gemu.shell.*;

public class ZipFile extends File {
	protected ZipFile( String name ) {
		super( name );
	}
	
	public ZipFile getRootParentFile() {
		File parent = getParentFile();
		while( parent != null ) {
			if( parent.exists() && ZipFiles.isCompact( parent ) ) {
				return new ZipFile( parent.getAbsolutePath() );
			}
			parent = getParentFile();
		}
		return null;
	}
	
	public boolean isRootParentFile() {
		File parent = getParentFile();
		return parent.isDirectory() && parent.exists();
	}
	
	@Override
	public ZipFile[] listFiles() {
		if ( !isRootParentFile() ) {
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