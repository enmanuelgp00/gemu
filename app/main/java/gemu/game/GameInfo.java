package gemu.game;    

import gemu.system.Log;  
import gemu.io.File;
import gemu.util.Texts;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;  
import java.io.FileOutputStream;
import java.io.IOException;


public class GameInfo {
	File file;
	
	HashMap< Key, Set<String> > map = new HashMap< Key, Set<String> >();
	public static String EXTENSION = "gemu";
	
	private GameInfo( File file ) {
		this.file = new File( file );
		loadkeys();
	}
	
	public GameInfo( Launcher launcher ) {
		File folder = launcher.getParentFile();
		String name = folder.getAbsolutePath() + "/" + folder.getName() + "." + EXTENSION ;
		this.file = new File( name );
		errorIfExists(); 
		loadkeys();
		String relative = launcher.getAbsolutePath().substring( folder.getAbsolutePath().length() );
		map.get( Key.launcher ).add( relative );
		map.get( Key.favorite ).add( "false" );
		commit();
	}
	
	public GameInfo( CompactLauncher launcher ) {
		File rootfile = launcher.getParentRootFile();
		File folder = rootfile.getParentFile();
		String name = folder.getAbsolutePath() + "/" + rootfile.getBaseName() + "." + EXTENSION ;
		this.file = new File( name );
		errorIfExists(); 
		loadkeys();
		String relative = launcher.getAbsolutePath().substring( folder.getAbsolutePath().length() );
		map.get( Key.launcher ).add( relative );
		map.get( Key.favorite ).add( "false" );
		commit();
	}
	
	public void set( Key k, String value ) {
		Set<String> list = map.get( k );
		list.clear();
		list.add( value );
	}
	
	Set<String> modif( Key k ) {
		return map.get( k );
	}
	
	public File getFile() {
		return file;
	}
	
	public File getFolder() {
		return file.getParentFile();
	}
	
	public void setFolder( File folder ) {
		File n = new File ( folder.getAbsolutePath() + "/" + file.getName() );
		file.renameTo( n );
		file = n;
	}
	
	public String[] get( Key k ) {
		Set<String> list = map.get( k );
		return list.toArray( new String[ list.size() ] );
	}
	
	public void commit() {
		try {
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), "utf-8" ) );
			writer.write( toString() );
			writer.close();
		} catch ( Exception e ) {
			e.printStackTrace();
			Log.error( e.getMessage() );
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( Key k : map.keySet() ) {
			sb.append( k.value + " {\n" );
			Set<String> items = map.get( k );
			if ( items.size() > 0 ) {				
				for ( String item : map.get( k ) ) {
					sb.append("\t\"" + item + "\"\n");
				}  
			} else {
				sb.append("\n");
			}
			sb.append( "}\n\n" );
		}
		return sb.toString();
	}
	
	public static GameInfo parse( File file ) {
		parseFileVerification( file );
		GameInfo info = new GameInfo( file );
		
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ), "utf-8" ) );
			
			StringBuilder sb = new StringBuilder();
			String k = null;
			int code;                   
			boolean inCurlyBrace = false;
			boolean inQuotes = false;
			
			while ( ( code = reader.read() ) != -1 ) {
				char ch = ( char ) code;
				switch ( ch ) {
					case '{': 
						inCurlyBrace = true ;
					break;					
					case '}': 
						inCurlyBrace = false ;
					break;					
					case '"': inQuotes = !inQuotes ;
					break;
				}
				if ( inQuotes ) {
					if ( ch != '"') {
						sb.append( ch ); 					
					}
				} else {					
					if ( Texts.isWordDelimiter( ch ) ) {
						if ( sb.length() > 0 ) {						
							if ( inCurlyBrace ) {
								try {
									info.modif( Key.get( k ) ).add( sb.toString() ) ;								
								} catch ( Exception e ) {
									e.printStackTrace();
									Log.error( "Error parsing : " + info.getFile() );
									System.exit( 1 );
								}
								sb.setLength( 0 );
							} else {
								k = sb.toString();
								sb.setLength( 0 );
							}
						}
					} else {
						sb.append( ch );
					}
				}
			}
			reader.close();
		} catch ( Exception e ) {
			Log.error( e.getMessage() );
			e.printStackTrace();
			System.exit( 1 );
		}
		return info;
		
	}
	
	
	private static void parseFileVerification( File file ) {
		try {
			if ( !file.exists() ) {
				throw new IOException() {
					@Override
					public String getMessage() {
						return file + " : file most exits for parsing ";
					}
				};
			}
			
			if ( !Games.isGameInfo( file ) ) {
				throw new IOException() {
					@Override
					public String getMessage() {
						return file + " : file most have extension " + Texts.inBrace( EXTENSION ) ;
					}
				};
			}
			
		} catch ( Exception e ) {  
			e.printStackTrace();
			Log.error( e.getMessage() );
			System.exit( 1 );
		}
	}
	private void errorIfExists() {
		try {
			if ( this.file.exists() ) {
				throw new IOException() {
					@Override
					public String getMessage() {
						return file + " : File already exists ";
					}
				};
			}
		} catch ( Exception exc ) {
			exc.printStackTrace();
			Log.error( exc.getMessage() );
			System.exit( 1 );
		}
	}
	
	private void loadkeys() {		
		for ( Key k : GameInfo.Key.set ) {
			map.put( k , new TreeSet<String>() );
		}
	}
	public static final class Key {
	
		public String value;
		private Key( String n ) {
			this.value = n;
		}
		
		public static Key get( String n ) throws Exception {
			for (Key k : Key.set ) {
				if ( k.value.equals( n ) ) {
					return k;
				}	
			}
			throw new Exception () {
				@Override
				public String getMessage() {
					return " key with value : " + n + " not found";
				}
			};
		}
		
		public static Key name;
		public static Key launcher;
		public static Key tags;
		public static Key screenshots;
		public static Key sites;      
		public static Key version;
		public static Key favorite;
		public static Key state; 
		public static Key admin;
		public static Key length;   
		public static Key compactLength;
		
		static Key[] set = new Key[] {
			name = new Key("name"),
			launcher = new Key("exe"),
			tags = new Key("tags"),
			screenshots = new Key("screenshots"),
			sites = new Key("sites"),      
			version = new Key("version"),
			favorite = new Key("star"),
			state = new Key("state"),
			admin = new Key("admin"),
			length = new Key("length"), 
			compactLength = new Key("compactLength")
		};
		
	}
}