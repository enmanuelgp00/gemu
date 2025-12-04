package gemu.game;

import java.io.*;
import gemu.util.*;

public class Executable extends File{
	public Executable ( File f ) throws Exception {
		super(f.getAbsolutePath());
		if ( !FileNames.getExtension(f).equals(".exe") ) {
			throw new Exception();
		}
	}
	
	public Executable ( String path ) throws Exception {
		super( path );
		if ( !FileNames.getExtension(this).equals(".exe") ) {
			throw new Exception();
		}
	}
}