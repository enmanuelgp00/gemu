package gemu.game;

import java.util.*;
import java.io.*;

class Info {
	HashMap<Key, Set<String>> hashMap = new HashMap<>();
	private final String INFO_FILE_NAME = "gemuinfo";
	final static Key COVER = new Key("cover");    
	final static Key LAUNCHER = new Key("launcher");
	final static Key TITLE = new Key("title");
	
	final static Key[] KEYS = new Key[] {                                    
		COVER,
		LAUNCHER,
		TITLE
	};
	
	File file;
	
	Info() {
		defaultConstructor();
	}
	
	Info( Launcher launcher ) {
		defaultConstructor();
		file = new File( launcher.getParentFile() + "/" + INFO_FILE_NAME );
		set( LAUNCHER, launcher.getName() );
	}
	
	Info parseFile( File f ) {
		Info info = new Info();
		return info;
	}
	
	private void defaultConstructor() {
		for ( Key key : KEYS ) {
			hashMap.put( key, new TreeSet<String>() );
		}
	}
	
	public String get( Key key ) {
		TreeSet<String> set = (TreeSet<String>)hashMap.get( key );
		if ( set.size() > 0 ) {
			return set.first();
		}
		return null;
	}
	
	public String[] list( Key key ) {
		return hashMap.get( key ).toArray( new String[0] );
	}
	
	public void set( Key key, String value) {
		Set<String> set = hashMap.get(key);
		set.clear();
		set.add( value );
		
	}
	
	public void add( Key key, String value) {
		
	}
	
	public File getFile() {
		return file;
	}
	
	static class Key {
		final String name;
		Key ( String name ) {
			this.name = name;
		}
	}

}