package gemu.game;

import java.io.*;
import gemu.util.*;

public class Executable extends File{
	public Executable ( File f ) throws Exception {
		super(f.getAbsolutePath());
		if ( !FileNames.hasExtension( f, ".exe") ) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					System.out.println("Wrong name : " + f );
					super.printStackTrace();
				}
			};
		}
	}
	
	public Executable ( String path ) throws Exception {
		super( path );
		if ( !FileNames.hasExtension( this, ".exe") ) {
			throw new Exception();
		}
	}
}