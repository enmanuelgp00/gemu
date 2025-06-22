package gemu.system;

import java.io.*;
import gemu.system.event.*;

public final class Shell {
	public static void exec( Command  command ) {
		try {
			ProcessBuilder pb = new ProcessBuilder( command.command );
			if ( command.directory != null ) {
				pb.directory( command.directory );
			}
			
			Process process = pb.start();
			command.listener.onProcessStarted( process );
			
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() , "Shift-JIS") );
			String line;
			while ( ( line = reader.readLine() ) != null ) {
				command.listener.onStreamLineRead( line );
			}
			int exitCode = process.waitFor();
			command.listener.onProcessFinished( process, exitCode );
			
		} catch ( Exception e ) {
			e.printStackTrace();
			Log.error( e.getMessage() );
		}
		
	}
	
	
	
	public static class Command {
	
		File directory;
		OnProcessListener listener;
		String[] command;
		
		public Command ( File file, OnProcessListener listener, String... command ) {
			this.listener = listener;
			this.command = command;
			this.directory = file;
		}
	} 
}
