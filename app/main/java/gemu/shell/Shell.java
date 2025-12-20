package gemu.shell;

import java.lang.ref.WeakReference;
import java.util.*;
import java.io.*;

public final class Shell {
	private static class PowerCfgScheme {
	
		String scheme_guid = "";
		String subgroup_guid = "";
		Setting	minimum_setting = new Setting();      
		Setting maximum_setting = new Setting();
		
		private static class Setting {
			String guid = "";
			int ac = -1;      
			int dc = -1;
		}
	}
	
	private static PowerCfgScheme initialPowerCfgScheme = getCurrentPowerCfgScheme();
	
	public static void setLowClockPerformance() {
	
		for ( PowerCfgScheme scheme : listPowerCfgSchemes() ) {
			if ( scheme.maximum_setting.ac < 31 ) {
				setActivePowerCfgScheme( scheme );
				break;
			}
			/*
			System.out.println(scheme.scheme_guid);  
			System.out.println(scheme.subgroup_guid);    
			System.out.println(scheme.maximum_setting.guid);     
			System.out.println("\t" + scheme.maximum_setting.ac);   
			System.out.println("\t" + scheme.maximum_setting.dc);          
			System.out.println(scheme.minimum_setting.guid);          
			System.out.println("\t" + scheme.minimum_setting.ac);      
			System.out.println("\t" + scheme.minimum_setting.dc);
			*/
		}
	};
	public static void setActiveInitialPowerCfgScheme() {
		setActivePowerCfgScheme( initialPowerCfgScheme );
	}
	public static void setActivePowerCfgScheme( PowerCfgScheme scheme ) {
		System.out.println( "Setting power scheme : " + scheme.scheme_guid );
		run( new OnProcessListener(){}, null, "powercfg", "/setActive", scheme.scheme_guid );
	}
	public static PowerCfgScheme[] listPowerCfgSchemes() {
		ArrayList<PowerCfgScheme> list = new ArrayList<>();
		ArrayList<String> guids = new ArrayList<>();
		run( new OnProcessListener(){
			@Override
			public void streamLineRead( long processId, String line ) {
				if ( line.startsWith("Power") ) {
					guids.add( line.split("\\s+")[3]);
				}
			}
		}, null, "powercfg", "/list");
		guids.forEach(( guid )->{
			list.add( getPowerCfgScheme( guid ) );
		});
		return list.toArray( new PowerCfgScheme[ list.size() ]);
	}
	public static PowerCfgScheme getPowerCfgScheme( String scheme_guid ) {
		
		PowerCfgScheme scheme = new PowerCfgScheme();
		scheme.scheme_guid = scheme_guid;
		run( new OnProcessListener() {
			boolean isReadingParam = false;
			String param = "";
			@Override
			public void streamLineRead( long processId, String line ) {
				if ( line.contains("Processor power management")) {
					scheme.subgroup_guid = line.trim().split("\\s+")[2];
					return;
				}
				
				if ( line.contains("Maximum processor state")) {               
					scheme.maximum_setting.guid = line.trim().split("\\s+")[3];
					isReadingParam = true;
					param = "Maximum";
					return;
					
				}
				
				if ( line.contains("Minimum processor state")) {
					scheme.minimum_setting.guid = line.trim().split("\\s+")[3];
					isReadingParam = true;
					param = "Minimum";
					return;
					
				}
				
				if ( isReadingParam ) {
					if ( param.equals("Maximum") ) {
						if ( line.contains("Current AC Power Setting Index") ) {
							try {                                             
								scheme.maximum_setting.ac = Integer.parseInt( line.trim().split("\\s+")[5].substring(2), 16 );
							} catch( Exception e ) {}
						} else if ( line.contains("Current DC Power Setting Index") ) {
							try {                                                                          
								scheme.maximum_setting.dc = Integer.parseInt( line.trim().split("\\s+")[5].substring(2), 16);
							} catch( Exception e ) {}
							
							isReadingParam = false;
						}
						return;
					}
					
					if ( param.equals("Minimum") ) {
						if ( line.contains("Current AC Power Setting Index") ) {
							try {                                                  
								scheme.minimum_setting.ac = Integer.parseInt( line.trim().split("\\s+")[5].substring(2), 16 );
							} catch( Exception e ) {}
						} else if ( line.contains("Current DC Power Setting Index") ) {
							try {                                                                      
								scheme.minimum_setting.dc = Integer.parseInt( line.trim().split("\\s+")[5].substring(2), 16 );
							} catch( Exception e ) {}
														  
							isReadingParam = false;
						}
						return;
					}
				}
				
				
			}
		}, null, "powercfg", "/query", scheme_guid );
		
		return scheme;
	}
	
	public static PowerCfgScheme getCurrentPowerCfgScheme() {
		System.out.println("getCurrentPowerCfgScheme");
		PowerCfgScheme tmp = new PowerCfgScheme();
		run( new OnProcessListener(){
			@Override
			public void streamLineRead( long processId, String line ) {
				String[] lineSplit = line.split("\\s+");
				tmp.scheme_guid = lineSplit[3];
			}
		}, null, "powercfg", "/getActiveScheme");
		return getPowerCfgScheme( tmp.scheme_guid );
		
	}
	
	public static void waitProcess( long processId ) {
		new ProcessWaiter( processId );
	}
	
	
	public static void run( OnProcessListener adapter, File dir, String... cmd) {
		
		if ( adapter == null ) {
			adapter = new OnProcessListener() { };
		}  
		WeakReference<OnProcessListener> weakReference = new WeakReference<>( adapter );
		
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