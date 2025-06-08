package gemu;

import java.io.*;

public final class Shell {
	public static void run( String... command ) {
		try {
			ProcessBuilder builder = new ProcessBuilder( command );
			builder.redirectErrorStream(true); // errors appears in getInputStream()
			Process process = builder.start(); 
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream()));
			String line = null;
			while ( ( line = reader.readLine()) != null ) {
				System.out.println(line);
			} 	
			/*
			int exitCode = process.waitFor();
			
			*/
		} catch(Exception e ) {
			e.printStackTrace();
		}
	
	}
	
	public static void run ( OnProcessListener listener, String... command ) {
		try {
			ProcessBuilder builder = new ProcessBuilder( command );
			builder.redirectErrorStream(true);
			Process process = builder.start();
			listener.onProcessStarted( process );
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream()) );
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				listener.onStreamLineRead( line );
			}
			
			int exitCode = process.waitFor();     
			listener.onProcessFinished( process, exitCode );
			
		} catch ( Exception e ) {
		
		}
		
	}
	
	public interface OnProcessListener {
		public void onProcessStarted( Process process );
		public void onStreamLineRead( String line );
		public void onProcessFinished( Process process, int exitCode );
	}
}