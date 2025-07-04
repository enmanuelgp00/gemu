package gemu.game;

import gemu.io.File;
import gemu.file.*;
import java.io.IOException;
	
public class InfoFile	{ 

	Folder folder;
	File file;
	
	static final String EXTENSION = "gemu"; 

	InfoFile( File file ) { 	
		this.folder = file.getParentFolder();
		if ( file.hasExtension( EXTENSION )) {
			this.file = new File( folder.getAbsolutePath() + "\\" + file.getName() );		
		}  else {
			this.file = new File( folder.getAbsolutePath() + "\\" + file.getBaseName() + "." + EXTENSION );
		}
		//System.out.println( getFile() );
	}	
	
	public void setFolder( Folder folder ) {;
		getFolder().renameTo( folder );    
		getFile().renameTo( new File ( folder.getAbsolutePath() + "\\" + file.getName()) );
	}
	private void setFile( File file) {
		this.file = file;
	}
	public File getFile() {
		return file;
	}
	public Folder getFolder() {
		return folder;
	}
}