package gemu.shell;

import java.io.*;

public final class Shell {
	
	public static void waitProcess( long processId ) {
		new ProcessWaiter( processId );
	}
	
	
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
			long processId = process.pid();
			adapter.processStarted( processId );
			
			
			Thread th = new Thread(()->{
				try {                    
					BufferedReader reader = new BufferedReader( new InputStreamReader( process.getErrorStream(), "SHIFT-JIS" ));
					String line;
					while( (line = reader.readLine()) != null ) {
						System.out.println(line);
					}
					reader.close();
				
				} catch( Exception e ) {}
			});
			th.start();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream(), "SHIFT-JIS" ));
			String line;
			while( (line = reader.readLine()) != null ) {
				adapter.streamLineRead( processId, line );
			}                                            
			reader.close();
			int exitCode = process.waitFor();
			adapter.processFinished( processId, exitCode );
			
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private static class ProcessWaiter {
		private boolean processFound = false;
		
		private ProcessWaiter( long processId ) {
			setProcessFound( true );
			while( isProcessFound() ) {
				System.out.println("Waiting for process : " + processId );
				try {
					Thread.sleep( 300 );
				} catch( Exception e ) {}
				
				Shell.run( new OnProcessListener(){
					int lineCount = 0;
					@Override
					public void streamLineRead( long processId, String line ) {
						lineCount++;                                                      
					}
					@Override
					public void processFinished( long processId, int exitCode ) {
						if ( lineCount < 6 ) {
							setProcessFound( false );
						}
					}
				}, null, "wmic", "process", "where", "processId=" + processId, "get", "name");
			}
		}
		
		private boolean isProcessFound() {
			return processFound;
		}
		
		private void setProcessFound( boolean b ) {
			processFound = b;
		}
	}
	
}