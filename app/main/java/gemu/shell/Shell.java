package gemu.shell;

import java.io.*;

public final class Shell {
	public static void run( OnProcessListener listener, File dir, String... cmd) {
		OnProcessListener adapter = new OnProcessListener() { };
		
		if ( listener != null ) {
			adapter = listener;
		}
		
		try {
			ProcessBuilder pb = new ProcessBuilder( cmd );
			//pb.inheritIO();
			/*
			pb.redirectInput( ProcessBuilder.Redirect.INHERIT);
			pb.redirectOutput( ProcessBuilder.Redirect.INHERIT );      
			pb.redirectError( ProcessBuilder.Redirect.INHERIT );
			*/
			if ( dir != null ) {
				pb.directory( dir );			
			}
			
			Process process = pb.start();
			adapter.processStarted( process );
			
			
			Thread th = new Thread(()->{
				try {                    
					BufferedReader reader = new BufferedReader( new InputStreamReader( process.getErrorStream(), "SHIFT-JIS" ));
					String line;
					while( (line = reader.readLine()) != null ) {
						//System.out.println(line);
					}
					reader.close();
				
				} catch( Exception e ) {}
			});
			th.start();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream(), "SHIFT-JIS" ));
			String line;
			while( (line = reader.readLine()) != null ) {
				adapter.streamLineRead( process, line );
			}                                            
			reader.close();
			int exitCode = process.waitFor();
			adapter.processFinished( process, exitCode );
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
}