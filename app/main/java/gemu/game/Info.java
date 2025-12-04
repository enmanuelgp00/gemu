package gemu.game;

import java.util.*;
import java.io.*;
import gemu.io.*;      
import gemu.util.*;

public class Info {
	HashMap<Key, Set<String>> hashMap = new HashMap<>();
	public final static String FILE_EXTENSION = ".gemuinfo";
	final static Key COVER_IMAGE = new Key("cover_image");    
	final static Key LAUNCHER = new Key("launcher");
	final static Key TITLE = new Key("title");
	final static Key COVER_XVIEWPORT = new Key("cover_xviewport");
	final static Key EXECUTABLES = new Key("executables");
	
	final static HashSet<Key> KEYS = new HashSet<>( Arrays.<Key>asList(
		COVER_IMAGE,
		LAUNCHER,
		TITLE,
		COVER_XVIEWPORT,
		EXECUTABLES
	));
	
	File file;
	
	private Info() {
		defaultConstructor();
	}
	
	public static Info createInfo( ZipFile file ) throws Exception {
		ArrayList<Executable> executables = new ArrayList<Executable>();
		for ( ZipFile f : file.listFiles() ) {
			if ( Games.isLauncher(f) ) {
				executables.add( new Executable(f) );
			}
		}
		return createInfo( executables.toArray( new Executable[ executables.size() ] ) );
	}
	
	public static Info createInfo( Executable... executables ) throws Exception {
		if ( executables.length == 0 ) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					System.out.println("No executables found");
					super.printStackTrace();
				}
			};
		}
		Info info = new Info();
		Executable ref = executables[0];
		
		File parent;
		if ( !ZipFiles.isZipFile( ref ) ) {
			parent = new File( ref.getParentFile().getCanonicalPath());			
		} else {                                  
			parent = new File( ZipFiles.get( ref ).getRootZipFile().getParentFile().getCanonicalPath() );			
		}
		
		info.file = new File( parent + "\\" + parent.getName() + FILE_EXTENSION );
												
		if ( info.file.exists() ) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					System.out.println("File already exist : " + info.file  + "");
					super.printStackTrace();
				}
			};
		}
		for ( Executable executable : executables ) {
			String path = executable.getAbsolutePath();
			//info.add( EXECUTABLES, path.substring( parent.getAbsolutePath().length() , path.length() ) );	 
			info.add( EXECUTABLES, FileNames.relativePath( parent, executable ));
		}
		
		if ( executables.length == 1 ) {
			String path = ref.getAbsolutePath();
			info.set( LAUNCHER, path.substring( parent.getAbsolutePath().length() , path.length() ) );		
		}
		
		return info;
	}
	
	public static Info parseInfo( File f ) throws Exception {
		Info info = new Info();
		info.file = f;
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(f), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			int code;
			char ch;
			boolean insideQuotes = false;
			Key key = null;
			
			while( (code = reader.read()) != -1 ) {
				ch = (char)code;
				switch(ch) {
					case '"':
						insideQuotes = !insideQuotes;
					break;
					
					case '{':
						String keyname = sb.toString();   
						sb.setLength(0);
						
						boolean found = false;
						for ( Key k : info.hashMap.keySet() ) {
							if (k.name.equals(keyname) ) {
								key = k;
								found = true;
								break;
							}
						}
						if ( !found ) {
							throw new Exception() {
								@Override
								public void printStackTrace() {
									try {                    
										System.out.println("Parse exception, key name : \""+ keyname +"\" was not found in : " + f.getCanonicalPath());									
									} catch( Exception e ) {}
									super.printStackTrace();
								}
							};
						}  
					break;
					
						
					case '\n':
						if ( insideQuotes ) {
							throw new Exception() {
								@Override
								public void printStackTrace() {
									try {                    
										System.out.println("Parse exception, no quotes clapsule in : " + f.getCanonicalPath());										
									} catch( Exception e ) {}
									super.printStackTrace();
								}
							};
						}
						if ( key != null && sb.length() > 0 ) {
							info.hashMap.get(key).add(sb.toString());
							sb.setLength(0);
						}
					break;
					
					case '}':     
						key = null; 
					break;
					case ' ':
					case '\t':
						if ( insideQuotes ) {
							sb.append(ch);
						}
					break;
					default:
						sb.append(ch);
				}
			}
			
			reader.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return info;
	}
	
	private void defaultConstructor() {
		for ( Key key : KEYS ) {
			hashMap.put( key, new TreeSet<String>() );
		}
	}
	//end constructor
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
		commit();
	}
	
	public void add( Key key, String value) {
		hashMap.get(key).add( value );
		commit();
	}
	
	public File getFile() {
		return file;
	}
	
	public void commit() {
		StringBuilder sb = new StringBuilder();
		for ( Key key : hashMap.keySet() ) {
			sb.append( key.name + " {\n" );
			for ( String s : hashMap.get( key ) ) {
				sb.append("\t\"" + s + "\"\n");
			}
			sb.append("}\n\n");
		}
		
		try {
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( getFile()) , "UTF-8"));
			writer.write( sb.toString( ));
			writer.close();
		} catch( Exception e ) {}
	}
	
	static class Key {
		final String name;
		Key ( String name ) {
			this.name = name;
		}
	}

}