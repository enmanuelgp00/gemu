package gemu.sys;

import java.io.*;

public final class Shell {
	public static void run ( OnProcessListener listener, String... command ) {
		Thread th = new Thread( new Runnable() {
			@Override
			public void run () {
				try {
					ProcessBuilder builder = new ProcessBuilder( command );
					builder.redirectStreamError( true );
					Process process = builder.start();
					listener.onProcessStarted( process );
					BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
					String line = null;
					while ( ( line = reader.readLine() ) != null ) {
						listener.onStreamLineReaded( line );
					}
					int exitCode = process.waitFor();
					listener.onProcessFinished( process, exitCode );
					
					reader.close();
				} catch (Exception e ) {
					e.printStackTrace();
				}				
			}		
		});
		
	}
	public interface OnProcessListener {
		public void onProcessStarted( Process process );
		public void onStreamLineReaded( String line );
		public void onProcessFinished( Process process, int exitCode );
	}
}

