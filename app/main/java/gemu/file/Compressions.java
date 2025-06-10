package gemu.file;

import gemu.system.Shell;
import java.util.List;
import java.util.ArrayList; 
import java.util.Arrays;

public final class Compressions {
	
	private static List<CProcess> cProcessesLs = new ArrayList<CProcess>();
	private static boolean compressing = false;

	public static void add( CProcess cprocess ) {
		cProcessesLs.add( cprocess );
		if ( !isCompressing() ) {
			setCompressingState( true );
			
			new Thread( new Runnable() {
				@Override
				public void run() {
					while( cProcessesLs.size() > 0 ) {						
						cProcessesLs.get(0).start();
						cProcessesLs.remove(0);
						try {
							Thread.sleep( 300 );
						} catch ( Exception e ) {
							e.printStackTrace();
						}
					}
					setCompressingState( false );				
				}
			}).start();
				
		}
	}		
	private static boolean isCompressing() {
		return compressing;
	}
	private static void setCompressingState( boolean state ) {
		compressing = state;
	}
	
	
	public static class CompressProcess extends CProcess {
		final String[] ignorels;
		public CompressProcess( File file, Shell.OnProcessListener listener, String... ignorels ) {
			super( file, listener );
			this.ignorels = ignorels;
		} 
		@Override
		void checkAnonymous() {} 		
		@Override
		public void start() {
			File file = getFile();
			String name = "/" + file.getName();
			StringBuilder path = new StringBuilder();
			path.append( file.getAbsolutePath() );
			File src = null;
			while( new File ( path.toString() ).exists() ) {
				src = new File( path.toString() );
				path.append( name );
			}
			
			StringBuilder cmd =  new StringBuilder();
			cmd.append( "7z a " );
			for ( String s : this.ignorels ) {
				cmd.append(" -x!" + s);
			}
			cmd.append(" -sdel -mmt8 -mx9 -t7z");
			List<String> cmdls = new ArrayList<String>( Arrays.<String>asList( cmd.toString().split("\\s+")) );
			cmdls.add( file.getAbsolutePath() + "\\" + file.getName() + ".7z" );
			cmdls.add( src + "\\." );
			Shell.exec( getListener(), new Shell.Command( null, cmdls.toArray( new String[cmdls.size()]) ));
			src.delete();
		}
	}       
	
	public static class DecompressProcess extends CProcess {
		public DecompressProcess( CompactFile file, Shell.OnProcessListener listener ) {
			super( file.getParentRootFile(), listener );
		}
		@Override
		void checkAnonymous() {}
		@Override
		public void start() {
			File file = getFile();
			Shell.exec( getListener(), new Shell.Command( "7z", "x", "-o" + file.getParentFile().getAbsolutePath() , file.getAbsolutePath() ) );
			file.delete();
		}
	}
	
	public static abstract class CProcess {
		File file;
		Shell.OnProcessListener listener;
		
		public CProcess( File file, Shell.OnProcessListener listener ) {
			this.file = file;
			this.listener = listener;
			checkAnonymous();
		}
		void checkAnonymous() {
			try {
				if ( getClass().isAnonymousClass() ) {
					throw new IllegalStateException() {
						@Override
						public void printStackTrace() {
							System.out.println("\n[ " + getClass() + " can not be innitialize or anonymous ] ");
							super.printStackTrace();
						}
					};
				}
			
			} catch (IllegalStateException e) {
				e.printStackTrace();
				System.exit( 1 );
			}
			
		}
		public File getFile() {
			return file;
		}
		public Shell.OnProcessListener getListener() {
			return listener;
		}
		public void start() { } ;
	}
}