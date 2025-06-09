package gemu.game;

import gemu.sys.Shell;
import java.util.List;
import java.util.ArrayList;

public class File extends java.io.File {
	public File( String name ) {
		super( name );
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
	public CompressedFile[] listCompressedFiles() {
		if ( isCompressed() ) {
			List<CompressedFile> cmfiles = new ArrayList<CompressedFile>();
			Shell.run( new Shell.OnProcessListener() {
				String path;
				@Override
				public void onProcessStarted( Process process ) {
				}
				@Override
				public void onStreamLineRead( String line ) {
					String pathSign = "Path = ";
					String fileSign = "Attributes = A";
					if ( line.contains( pathSign )) {
						path = line.substring( pathSign.length() );
						
					} else if ( line.contains( fileSign ) ) {
						File file = new File( getParentFile().getAbsolutePath() + "/" + path );
						
						cmfiles.add( new CompressedFile( File.this , file ) );           
					}
					 
				}
				@Override
				public void onProcessFinished( Process process, int exitCode ) {}
			} , "7z", "l", "-slt", getAbsolutePath() );
			return cmfiles.toArray( new CompressedFile[ cmfiles.size() ] );
		}
		return new CompressedFile[]{};
	}
	@Override
	public File[] listFiles() {
		java.io.File[] pfiles = super.listFiles();
		File[] files = new File[ pfiles.length ];
		for ( int i = 0; i < pfiles.length; i++ ) {
			files[i] = new File( pfiles[i].getAbsolutePath() );
		};
		return files;
	}
	
	
	@Override
	public File getParentFile() {
		return new File( super.getParentFile().getAbsolutePath() );
	}
	public FolderZip getParentFolderZip() {
		return new FolderZip( new File( super.getParentFile().getAbsolutePath() ) );
	}
	
	public boolean isCompressed() {
		return hasExtension("7z", "zip", "rar");
	}
}