package gemu.system;

import gemu.system.event.*;
import java.io.*;

public final class Shell {
	public static void exec( Command  command ) {
		try {
			ProcessBuilder pb = new ProcessBuilder( command.command );
			if ( command.directory != null ) {
				pb.directory( command.directory );
			}
			
			Process process = pb.start();
			command.listener.onProcessStarted( process );
			
			Thread errorThread = new Thread( new Runnable() {
				public void run() {
					try( BufferedReader errorReader = new BufferedReader( new InputStreamReader( process.getErrorStream(),"Shift-JIS") )) {
						String line; 
						while( ( line = errorReader.readLine()) != null ) {
							Log.error( line );
						}
					} catch (Exception e) {
						
					}
				}
			});
			errorThread.start();
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

	public static void takeScreenshot( OnProcessListener listener, String name, int processId , File location ) {
		/*
		int copies = 0;
		String filename = name + "_screenshot_";
		File screenshot;

		while ( ( screenshot = new File( location + "/" + filename + String.valueOf(copies))).exists() ) {
			copies++;
		}

		filename = filename + String.valueOf(copies);
		*/
		try {

			String jarPath = Shell.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			String path = jarPath.substring( 0, jarPath.lastIndexOf('/'));
			File screenshotScript = new File ( path + "/screenshot_script.ps1");
			StringBuilder scriptContent = new StringBuilder();

			String[] scriptParams = new String[]{
				"$id = \"" + String.valueOf(processId) + "\""
			};
			for ( String s : scriptParams ) {
				scriptContent.append(s + "\n");
			}
			char ch;
			int code;
			BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(screenshotScript), "Shift-JIS"));
			while ( ( code = reader.read() ) != -1 ) {
				ch = (char) code;
				if ( ch == '"' ) {
					scriptContent.append('\\');
				}
				scriptContent.append(ch);
			}
			
			reader.close();

			exec( new Command( location, listener, "powershell", scriptContent.toString() ));
			
			
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		


		
	}
}
