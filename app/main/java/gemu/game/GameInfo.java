package gemu.game;

import java.util.*;
import gemu.file.File;
import gemu.file.*;
import java.io.*;

public class GameInfo extends InfoFile {

	static final String IGNORE = ".gemuignore";
	
	private HashMap<String, List<String>> map = new HashMap<String, List<String>>();
	
	Folder folder;
	File file;
	
	private GameInfo( File file ) {  
		super( file );
		for (Key k : Key.set ) {
			map.put( k.value, new ArrayList<String>() );
		}	
	}
	
	GameInfo( Launcher launcher ) {
		super( launcher );
		try {
			if ( getFile().exists() ) {
				 throw new IOException();
			}
			for (Key k : Key.set ) {
				map.put( k.value, new ArrayList<String>() );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	GameInfo( CompactLauncher compactLauncher ) {
		super( compactLauncher.getParentRootFile() );
		try {
			if ( getFile().exists() ) {
				 throw new IOException();
			}
			for (Key k : Key.set ) {
				map.put( k.value, new ArrayList<String>() );
			}
		} catch ( Exception e ) {
			System.out.println( compactLauncher );
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	
	public static GameInfo parse( File file ) {
		try {			
			if ( !GameInfo.isGameInfoFile( file ) && !file.exists() ) {
				throw new Exception();
			}
		} catch ( Exception e ) {
			System.out.println( file + " is not a valid info file ");
			e.printStackTrace();
			System.exit( 1 );		
		}
		GameInfo info = new GameInfo( file );
		try {
			BufferedReader reader = new BufferedReader( new FileReader( file ) );
			int charCode;
			String key = null;
			String value = null;
			StringBuilder str = new StringBuilder();
			
			boolean inCurlyBrace = false;
			boolean inQuotes = false;
			while( ( charCode = reader.read()) != -1 ) {
				char ch = (char) charCode;
				switch( ch ) {
					case '{':
						inCurlyBrace = true;
					break;    
					case '}':               
						inCurlyBrace = false;
					break;
					case '"':
						inQuotes = !inQuotes;
					break;
				}
				
				if ( inQuotes ) {
					if ( ch != '"') { 
						str.append( ch ); 
					} 
				} else {					
					if ( isWordDelimiter( ch ) ) {						
						if ( str.length() > 0 ) {							
							if ( inCurlyBrace ) {
								value = str.toString();
								info.modif( GameInfo.Key.get( key ) ).add( value );
								str.setLength( 0 );
							} else {
								key = str.toString();
								str.setLength( 0 );
							}
						}
					} else {
						str.append( ch );
					}
				}
				
			}
			reader.close();
		} catch ( Exception e ) {
			e.printStackTrace();
			System.out.println( file );
			System.exit( 1 );
		}
		return info;
	}
	
	List<String> modif( Key key ) {
		return map.get( key.value );
	}
	
	private static boolean isWordDelimiter( char ch ) {
		char[] delims = new char[] {'{','}',' ', '\n', '"', (char)13, '\t'};	
		for ( char c : delims ) {
			if ( c == ch ) {
				return true;
			}
		}
		return false;
	}
	public static void createIgnoreFileIn( Folder folder ) throws IOException {
		File ignoreFile = new File( folder.getAbsolutePath() + "\\" + IGNORE );
		boolean isCreated = ignoreFile.createNewFile();
		if ( !isCreated ) {
			throw new IOException();
		}		
	}
	public static boolean hasIgnoreFile( File file ) {
		for ( File f : file.listFiles() ) {
			if ( GameInfo.isIgnoreFile(f)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isIgnoreFile( File file ) {
		return file.getName().equals( IGNORE );
	}
	public static boolean isGameInfoFile( File file ) {
		return file.hasExtension( EXTENSION ); //getName().equals( NAME_FILE );
	}
	
	void set( Key key, String value ) {
		List<String> list = map.get( key.value );
		if ( list.size() > 0 ) {
			list.set( 0, value);		
		} else {
			list.add( value );
		}
		
	}
	
	String[] get( Key key ) {
		return map.get( key.value ).toArray( new String[ 0 ] );
	}
	
	void add( Key key, String value ) {
		map.get(key.value ).add(value);
	}
	
	public void commit() {
		try {
			BufferedWriter writer = new BufferedWriter( new FileWriter( getFile() , false ) );
			writer.write( toString() );
			writer.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String k : map.keySet() ) {
			str.append( k + " {\n");
			for ( String v : map.get(k)) {
				str.append( "\t\"" + v + "\"\n" );
			}
			str.append( "\n}\n\n");
		}
		return str.toString();
	}
	
	final static class Key {
		final String value;
		private Key ( String value ) {
			this.value = value;
		}
		
		public static final Key name; 
		public static final Key launcher;
		public static final Key version;
		public static final Key tags;
		public static final Key screenshots;
		public static final Key favorite;
		
		public static final Set<Key> set = new HashSet<Key>( Arrays.asList( new Key[] {
			name = new Key("name"),
			launcher = new Key("exe"),
			version = new Key("version"),
			tags = new Key("tags"),
			screenshots = new Key("screenshots"),
			favorite = new Key("star")
		}));
		
		public static Key get( String name ) throws Exception {
			for ( Key k : Key.set ) {
				if ( k.value.equals( name ) ) {
					return k;
				}
			}
			
			throw new Exception() {
				@Override
				public void printStackTrace() {
					System.out.println( "\n[ \"" + name + "\" is not a valid key name ]");
					super.printStackTrace();
				}
			};
		}
		
	}
}