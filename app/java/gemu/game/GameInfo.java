package gemu.game;

import java.io.*;
import java.util.*;

public class GameInfo {  
	File file;
	public static final String FILE_NAME = "gemu.info";
	static final String KEY_NAME = "name";
	static final String KEY_LAUNCHER = "exe";
	static final String KEY_SITES = "sites";
	static final String KEY_TAGS = "tags";
	final String[] keys = new String[] { KEY_NAME, KEY_LAUNCHER, KEY_SITES, KEY_TAGS };
	
	HashMap<String, List<String>> info;
	
	public GameInfo ( File file ) { 
		this.file = file;                                     		
		info = new HashMap<String, List<String>>();
		for ( String k : keys ) {
			info.put(k, new ArrayList<String>());
		}
	}
	
	public static GameInfo parse( File file ) {
		GameInfo info = new GameInfo( file );
		try {
			BufferedReader reader = new BufferedReader( new FileReader( file ));
			StringBuilder str = new StringBuilder();
			String key = null;
			String itm = null;
			int code;
			boolean isInCurlyBrace = false;
			boolean isInQuotes = false;
			while ( ( code = reader.read() ) != -1 )  {
				char ch = (char) code;
				
				switch(ch) {
					case '{':
						isInCurlyBrace = true;
					break;  
					case '}':                   
						isInCurlyBrace = false;
					break;
					case '"':
						isInQuotes = !isInQuotes;
					break;
				}
				
				if ( isInQuotes ) {
					if (ch != '"') str.append(ch); 					
				} else {
				
					if ( isEndWordSign(ch) ) {
						if ( str.length() > 0 ) { 
							if ( isInCurlyBrace ) {
								itm = str.toString();
								info.getMap().get( key ).add( itm );
								str.setLength(0);
							} else {                            
								key = str.toString();
								str.setLength(0);
							}       						
						}
					} else {
						str.append(ch);
					}	
				}
				
				
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return info;
	}
	private static boolean isEndWordSign( char ch ) {
		char[] signs = new char[] { '"',' ', '{', '}', '\t', '\n', (char)13 };
		for ( char s : signs ) {
			if ( s == ch ) {
				return true;
			}
		}
		return false;
	}
	public List<String> getList( String key ) {
		return info.get( key );
	}
	public String get( String key ) {
		return info.get( key ).get(0);
	}
	public void set( String key, String value ) {
		List<String> list = info.get( key );
		if ( list.size() > 0 ) {
			list.set( 0 , value );		
		} else {
			list.add(value);
		}
	}
	public File getFile() {
		return this.file;
	}
	public HashMap<String, List<String>> getMap() {
		return info;
	}
	public void commit() {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( file ));
			writer.write( this.toString());
			writer.close();
		} catch ( IOException e ) {
			System.out.println( e.getMessage() );
		}
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		for ( String k : keys ) {
			str.append( k + " {\n");
			for ( String value : info.get(k) ) {
				str.append("\t\"" + value + "\"\n");
			}
			str.append("\n}\n\n");
		}
		return str.toString();
	}
}