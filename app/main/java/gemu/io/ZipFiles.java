package gemu.io;

import java.io.*;
import java.util.*;
import gemu.shell.*;

public final class ZipFiles {
	public static final String[] PASSWORDS = findPasswords();
	private static String[] findPasswords() {
		ArrayList<String> passwordList = new ArrayList<>();
		passwordList.add("");
		try {
		
			File passwordFile = new File("./passwords.txt");
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( passwordFile ), "UTF-8" ));
			String line;
			while( ( line = reader.readLine() ) != null ) {
			
				passwordList.add( line );
			}
			reader.close();
		} catch( Exception e ) {}
		return passwordList.toArray( new String[ passwordList.size() ] );
	}
	public static final HashSet<String> EXTENSIONS = new HashSet<>( Arrays.<String>asList(
		".rar",
		".zip",
		".7zip",
		".7z"
	) );
	
	public static boolean hasZipFiles( File file ) {
		for ( File f : file.listFiles() ) {
			if ( isZipFile(f) ) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isZipFile( File f ) {
		ZipFile z = new ZipFile(f);
		if ( z.isRootZipFile() ) {
			return true;
		}                         
		return z.getRootZipFile() != null ;
	}
	
	public static ZipFile get( File f ) throws Exception {
		ZipFile z = new ZipFile(f);
		
		if ( !isZipFile( f ) ) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					super.printStackTrace();
				}
			};
		}
		
		return  z;
	}
}