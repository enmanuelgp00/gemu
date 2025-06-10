package gemu.file;

import java.util.List;
import java.util.ArrayList;

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
	
	
	public static abstract class CompressProcess extends CProcess {
		public CompressProcess( File file ) {
			super( file );
		} 
		@Override
		void checkAnonymous() {} 		
		@Override
		public void start() {
			System.out.println("Compressing : " + getFile() );
		}
	}       
	
	public static abstract class DecompressProcess extends CProcess {
		public DecompressProcess( CompactFile file ) {
			super( file.getParentRootFile());
		}
		@Override
		void checkAnonymous() {}
		@Override
		public void start() {
			System.out.println("Decompressing : " + getFile() );
		
		}
	}
	
	public static abstract class CProcess {
		File file;
		public CProcess( File file) {
			this.file = file;
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
		
		public void start() { } ;
		public abstract void onProcessStarted( Process process );
	}
}