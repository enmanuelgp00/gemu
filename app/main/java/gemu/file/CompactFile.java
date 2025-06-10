package gemu.file;

import gemu.system.Shell;
import java.util.List;
import java.util.ArrayList;

public class CompactFile extends File {

	public CompactFile ( String name ) {
		super( name );
	}
	
	public CompactFile ( File file ) {
		super( file.getAbsolutePath() );
	}
	
	public static boolean isFileCompact( File file ) {
		CompactFile compactFile = new CompactFile( file.getAbsolutePath() );
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
			Shell.exec( new Shell.OnProcessListener() {
				String path;
				@Override
				public void onProcessStarted( Process process ) { }
				@Override
				public void onStreamLineRead( String line ) {
					String pathSign = "Path = ";
					String fileSign = "Attributes = A";
					if ( line.contains( pathSign )) {
						path = line.substring( pathSign.length() );
						
					} else if ( line.contains( fileSign ) ) {
						File file = new File( getAbsolutePath() + "/" + path );
						lsCompactFile.add( new CompactFile( file ) );           
					}
					 
				}
				@Override
				public void onProcessFinished( Process process, int exitCode ) {}
			} , "7z", "l", "-slt", getAbsolutePath() );
			return lsCompactFile.toArray( new CompactFile[ lsCompactFile.size() ] );
		}
		
		return new CompactFile[]{};
	}
	
	public CompactFile getParentRootFile() {
		CompactFile file = new CompactFile( getAbsolutePath() ) ;
		
		while( ( file.getParentFile() ) != null ) {
		
			if ( file.isRootFile()) {
				return file;
			}
			file = new CompactFile( file.getParentFile().getAbsolutePath() );
		}
		return null;
	}
	
	
}