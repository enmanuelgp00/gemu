package gemu.system;

import gemu.file.Folder;
import java.io.*;

public final class Shell {
	public static void exec ( OnProcessListener listener, Command command ) {		
		
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
			
			String japaneseDecoder = "shift_jis";
			BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream(), japaneseDecoder) );
			String line = null;
			while ( ( line = reader.readLine() ) != null ) {
				listener.onStreamLineRead( line );
			}
			int exitCode = process.waitFor();
			listener.onProcessFinished( process, exitCode );
			
			reader.close();
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}
	public static void exec ( Command command ) {
		Shell.exec( new OnProcessListener() {		  
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
	public interface OnProcessListener {
		public void onProcessStarted( Process process );
		public void onStreamLineRead( String line );
		public void onProcessFinished( Process process, int exitCode );
	}
}

