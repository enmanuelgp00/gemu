package gemu.io;

import java.io.*;
import java.util.*;
import gemu.shell.*;

public class ZipFile extends File {
	protected ZipFile( String name ) {
		super( name );
	}
	
	public ZipFile getRootParent() {
		return new ZipFile("");
	}
	
	@Override
	public ZipFile[] listFiles() {
		ArrayList<ZipFile> list = new ArrayList<>();
		return list.toArray( new ZipFile[ list.size() ] );
	}
}