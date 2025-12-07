package gemu.io;

import java.io.*;
import java.util.*;
import gemu.shell.*;

public final class ZipFiles {
	public static final String[] PASSWORDS = findPasswords();
	private static String[] findPasswords() {
		ArrayList<String> passwordList = new ArrayList<>();
		try {
		
			File passwordFile = new File("./passwords.txt");
			if ( !passwordFile.exists() ) {
				System.out.println("passwords.txt not found");
				passwordList.add("");
				return passwordList.toArray( new String[ passwordList.size() ] );
			}
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream( passwordFile ), "UTF-8" ));
			String line;
			while( ( line = reader.readLine() ) != null ) {
			
				passwordList.add( line );
			}
			reader.close();
		} catch( Exception e ) {} 
		if ( passwordList.size() == 0 ) {
			passwordList.add("");		
		}
		return passwordList.toArray( new String[ passwordList.size() ] );
	}
	public static final HashSet<String> EXTENSIONS = new HashSet<>( Arrays.<String>asList(
		".rar",
		".zip",
		".7zip",
		".7z"
	) );
	
	public static void pack( OnZipProcessListener listener, File dir, File file, File... ignoredFiles )  {
		File archive = new File( dir + "\\" + file.getName() + ".7z");
		
		ArrayList<String> cmd = new ArrayList<>();
		cmd.add("7z");
		for ( File f : ignoredFiles ) {  
			if ( f != null ) {
				cmd.add("-x!" + f.getName());		
			}
		}
		cmd.add("-sdel");
		cmd.add("-t7z");
		cmd.add("-mmt8");
		cmd.add("-mx9");
		cmd.add("-bsp1");
		cmd.add("a");
		cmd.add(archive.getName());
		cmd.add(file.getAbsolutePath() + "\\.");
		
		Shell.run( new OnProcessListener() {
		
			@Override
			public void processStarted( Process p) {
				listener.processStarted( p );
			}    
			@Override
			public void streamLineRead( Process p, String line ) {
				System.out.println( line );
				listener.streamLineRead( p, line );
			}   
			@Override
			public void processFinished( Process p, int exitCode ) {
				if ( exitCode != 0 ) {
					listener.processFinished( p, exitCode, null );
					return;
				}                     
				listener.processFinished( p, exitCode, archive );
			}
			
		}, dir, cmd.toArray( new String[ cmd.size() ]) );
	}
	
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