package gemu.system;

import gemu.io.CompactFile;
import gemu.io.File;
import gemu.system.event.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public final class Compressions {

	private static boolean running = false;
	private static List<Process> processList = new ArrayList<Process>();

	public static void carryOut( Process process ) {
		processList.add( process );
		if ( !running ) {
			Thread th = new Thread( new Runnable() {
				@Override
				public void run() {
					running = true;
					Process process = null;
					while( processList.size() > 0 ) {
						process = processList.get( 0 ); 
						process.run();
						processList.remove( process );
					}
					running = false;
				}
			} );
			th.start();
		}
	}
	
	public static class CompressProcess implements Process {
		
		String archiveName;
		String[] exceptions;
		File source;
		OnProcessListener listener;
		
		public CompressProcess( String archiveName , File source , OnProcessListener listener, String... exceptions ) {
			this.archiveName = archiveName;
			this.source = source;
			this.listener = listener;
			this.exceptions = exceptions;
		}
		
		@Override
		public void run() {
			List<String> command = new ArrayList<String>( Arrays.<String>asList("7z","a","-bsp1","-sdel", "-mmt8", "-mx9", "-t7z"));
			for ( String exception : exceptions ) {
				command.add("-x!" + exception );
			}
			command.add( archiveName );
			command.add( source.getAbsolutePath() + "/.");
			
			Shell.exec( new Shell.Command( source, listener , command.toArray( new String[ command.size() ] ) ) );
		}
	}
	
	public static class ExtractProcess implements Process {
		
		CompactFile compactFile;
		OnProcessListener listener;
		File folder;
		
		public ExtractProcess( File file, CompactFile compactFile, OnProcessListener listener ) {	
			this.folder = file;
			this.compactFile = compactFile;
			this.listener = listener;
		}
				
		public void run() {
			String[] command = new String[]{ "7z","-bsp1", "x", "-y", "-o" + folder.getAbsolutePath() , compactFile.getAbsolutePath() };
			Shell.exec( new Shell.Command( null, listener, command ));
		}
	}
	
	public interface Process {
		public void run();
	}
}