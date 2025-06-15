package gemu.file;

import java.io.IOException;
import gemu.system.*;
import java.util.List;
import java.util.ArrayList;

public class CompactFile extends File {

	public CompactFile ( String name ) {
		super( name );
		check();
	}
	
	public CompactFile ( File file ) {
		super( file.getAbsolutePath() );
		check();
	}
	
	public static final boolean isCompactFile( File file ) {
		if ( file.isDirectory() ) {
			return false;
		}
		CompactFile compactFile = new CompactFile( file );
		if ( !compactFile.isRootFile() ) {			
			return compactFile.getParentRootFile() != null;
		}
		
		return true;
	}
	
	public boolean isRootFile() {
		return hasExtension("7z", "rar", "zip");
	}
	
	@Override
	public CompactFile[] listFiles() {
		if ( isRootFile() ) {
			List<CompactFile> lsCompactFile = new ArrayList<CompactFile>();
			Shell.exec( new OnProcessListener() {
				String path;
				@Override
				public void onProcessStarted( Process process ) { }
				@Override
				public void onStreamLineRead( String line ) {
					String pathSign = "Path = ";
					String attributeSign = "Attributes = ";
					if ( line.contains( pathSign )) {
						path = line.substring( pathSign.length() );
						
					} else if ( line.contains( attributeSign ) ) {
						String attributes = line.substring( attributeSign.length() );
						String archiveAttribute = "A";
						
						if ( attributes.contains( archiveAttribute ) ) {
							File file = new File( getAbsolutePath() + "/" + path );
							lsCompactFile.add( new CompactFile( file ) );          
						}
					}
					 
				}
				@Override
				public void onProcessFinished( Process process, int exitCode ) {}
			} , new Shell.Command( "7z", "l", "-slt", getAbsolutePath()));
			
			return lsCompactFile.toArray( new CompactFile[ lsCompactFile.size() ] );
		}
		
		return new CompactFile[]{};
	}
	@Override
	public boolean exists() {
		if ( isRootFile() ) {
			return super.exists();
		}
		CompactFile parent = getParentRootFile();
		if ( parent.exists() ) {
			for ( CompactFile cf : parent.listFiles() ) {
				if (cf.matchesPath( this )) {
					return true;
				};
			}
		}
		return false;
	}
	public CompactFile getParentRootFile() {
		CompactFile compactFile = new CompactFile( getAbsolutePath() ) ;
		File parent;
		while( ( compactFile.getParentFile() ) != null ) {
		
			if ( compactFile.isRootFile()) {
				return compactFile;
			}
			parent = compactFile.getParentFile();
			
			if ( parent.isDirectory() ) {
				return null;
			}
			
			compactFile = new CompactFile( parent );
		}
		return null;
	}
	private void check() {
		try {
			if ( isDirectory() ) {
				throw new IOException() {
					@Override
					public String getMessage()  {
						return "compact file most not be a directory";
					}
				};
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			Log.error(this + " : " + e.getMessage());
			System.exit( 1 );
		}
	}
}