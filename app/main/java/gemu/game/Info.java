package gemu.game;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import gemu.file.*;

public class Info {

	static final String NAME_FILE = "gm.inf";
	
	HashMap<String, List<String>> map = new HashMap<String, List<String>>();
	
	Folder folder;
	
	Info( Folder folder ) {
		this.folder = folder;
		for (Key k : Key.SET ) {
			map.put( k.name, new ArrayList<String>() );
		}
	}
	
	public static Info parse( File file ) {
		Info info = new Info( file.getParentFolder() );
		return info;
	}
	
	public static boolean isFileInfo( File file ) {
		return file.getName().equals( NAME_FILE );
	}
	
	public Folder getFolder() {
		return folder;
	}
	
	void set( Key key, String value ) {
		List<String> list = map.get( key.name );
		if ( list.size() > 0 ) {
			list.set( 0, value);		
		} else {
			list.add( value );
		}
		
	}
	
	List<String> get( Key key ) {
		return map.get( key.name );
	}
	
	void add( Key key, String value ) {
		map.get(key.name ).add(value);
	}
	
	
	final static class Key {
		final String name;
		Key ( String name ) {
			this.name = name;
		}
		
		public static final Key NAME; 
		public static final Key LAUNCHER;
		public static final Key VERSION;
		public static final Key TAGS;
		public static final Key SCREENSHOTS;
		
		public static final Set<Key> SET = new HashSet<Key>( Arrays.asList( new Key[] {
			NAME = new Key("name"),
			LAUNCHER = new Key("exe"),
			VERSION = new Key("version"),
			TAGS = new Key("tags"),
			SCREENSHOTS = new Key("screenshots")
		}));
		
	}
}