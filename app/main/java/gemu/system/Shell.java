package gemu.system;

import gemu.file.Folder;
import java.io.*;

public final class Shell {
	public static int exec ( OnProcessListener listener, Command command ) {		
		
		try {
			ProcessBuilder builder = new ProcessBuilder( command.name );
			builder.redirectErrorStream( true );
			if ( command.folder != null ) {
				builder.directory( command.folder );
			}
			Process process = builder.start();
			if ( listener != null ) {
				listener.onProcessStarted( process );												 
			}
			
			String decoder = "Shift_JIS";
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream(), decoder) );
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				listener.onStreamLineRead( line );
			}
			int exitCode = process.waitFor();
			listener.onProcessFinished( process, exitCode );
			
			reader.close();
			return exitCode;
		} catch (Exception e ) {
			e.printStackTrace();
			return 1;
		}
	}
	public static int exec ( Command command ) {
		return Shell.exec( new OnProcessListener() {		  
			@Override
			public void onProcessStarted( Process process ) {
			
			}
			@Override
			public void onStreamLineRead( String line ) {
				System.out.println( line );
			}
			@Override
			public void onProcessFinished( Process process, int exitCode ) {
			
			}
		}, command );
	}
	public static class Command {
		String[] name;
		Folder folder;
		public Command( Folder folder, String... name ) {
			this.folder = folder;
			this.name = name;
		}
		
		public Command( String... name ) {
			this.name = name;
		}
		
	}
}

