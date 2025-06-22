package gemu.io;

import gemu.system.*;
import gemu.system.event.*;
import java.util.Set;
import java.util.HashSet;

public class CompactFile extends File {
	public CompactFile( String path ) {
		super( path );
	}
	
	public CompactFile( File file ) {
		super( file );
	}
	
	public CompactFile getParentRootFile() {
		File parent =  getParentFile();
		
		if ( parent != null ) {	
		CompactFile file = new CompactFile( parent );
			if ( file.isRootFile() ) {
				return file;
			}
			return file.getParentRootFile();
		}
		
		return null;
		
	}
	
	public boolean isRootFile() {
		return !isDirectory() && hasExtension( CompactFiles.EXTENSIONS );
	}
	
	public String getWrapperNameInside() {
		if ( !isRootFile() ) {
			return null;
		}
		CompactFile[] files = listFiles();
		String folderIndicator = getFolderIndicator( files[0].getAbsolutePath() );
		
		for ( CompactFile compactFile : files ) {
			String name = compactFile.getAbsolutePath();
			name = name.substring( getAbsolutePath().length() + 1 );
			if ( !name.contains( folderIndicator ) ) {
				return null;
			}
		}
		String name = files[0].getAbsolutePath();
		name = name.substring( getAbsolutePath().length() + 1 );
		name = name.substring( 0, name.indexOf( folderIndicator ) );
		return name;
	}
	
	private String getFolderIndicator( String path ) {
		String[] folderIndicators = new String[] {"/", "\\"};
		for ( String indicator : folderIndicators ) {
			if ( path.contains( indicator )) {
				return indicator;
			}
		}
		return null;
	}
	
	@Override
	public CompactFile[] listFiles() {
		if( !isRootFile() ) {
			return null;		
		}
		Set<CompactFile> compactFileSet = new HashSet<CompactFile>();
		
		String[] command = new String[] {"7z", "l", "-slt", getAbsolutePath() };
		Shell.exec(  new Shell.Command( null, new OnProcessAdapter() {
		
			String pathsignal = "Path = ";
			String attrsignal = "Attributes = ";
			String dirAttr = "D";
			String pth;
			String attr;
			
			@Override
			public void onStreamLineRead( String line ) {
				if ( line.contains( pathsignal ) ) {
					pth = line.substring( pathsignal.length() );
					
				} else if ( line.contains( attrsignal ) ) {
					attr = line.substring( attrsignal.length() );
					if ( !attr.contains( dirAttr )) {
						compactFileSet.add( new CompactFile( getAbsolutePath() + "/" + pth ) );
					}
				}
				
				
			}
		}, command ) );
		
		return compactFileSet.toArray( new CompactFile[ compactFileSet.size() ] );
	}
}