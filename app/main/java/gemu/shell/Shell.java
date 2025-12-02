package gemu.shell;

import java.io.*;

public final class Shell {
	public static void run( OnProcessAdapter listener, File dir, String... cmd) {
		OnProcessAdapter adapter = new OnProcessAdapter() { };
		
		if ( listener != null ) {
			adapter = listener;
		}
		
		try {
			ProcessBuilder pb = new ProcessBuilder( cmd );
			pb.inheritIO();
			if ( dir != null ) {
				pb.directory( dir );			
			}
			pb.redirectErrorStream();
			
			Process process = pb.start();
			adapter.processStarted( process );
			
			/*
			Thread th = new Thread(()->{
				try {                    
					BufferedReader reader = new BufferedReader( new InputStreamReader( process.getErrorStream(), "UTF-8" ));
					String line;
					while( (line = reader.readLine()) != null ) {
						System.out.println(line);
					}
					reader.close();
				
				} catch( Exception e ) {}
			});
			th.start();
			*/
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream(), "UTF-8" ));
			String line;
			while( (line = reader.readLine()) != null ) {
				adapter.streamLineRead( process, line );
			}
			int exitCode = process.waitFor();
			adapter.processFinished( process, exitCode );
			reader.close();
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
}