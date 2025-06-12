package gemu.file;

import gemu.system.*;
import java.util.List;
import java.util.ArrayList; 
import java.util.Arrays;

public final class Compressions {
	
	private static List<CProcess> processls = new ArrayList<CProcess>();
	private static boolean compressing = false;

	public static void add( CProcess cprocess ) {
		processls.add( cprocess );
		if ( !isCompressing() ) {
			setCompressingState( true );
			
			new Thread( new Runnable() {
				@Override
				public void run() {
					while( processls.size() > 0 ) {						
						processls.get(0).start();
						processls.remove(0);
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
	
	public static CProcess[] getList() {
		return processls.toArray( new CProcess[processls.size()] );
	}
	
	public static class CompressProcess extends CProcess {
		final String[] ignorels;
		public CompressProcess( File file, OnCompressListener listener, String... ignorels ) {
			super( file, listener );
			this.ignorels = ignorels;
		} 
		@Override
		void checkAnonymous() {} 		
		@Override
		public void start() {
			OnCompressListener listener = (OnCompressListener) getListener();
			listener.onStart();
			
			File file = getFile();
			String name = "/" + file.getName();
			
			StringBuilder path = new StringBuilder();
			path.append( file.getAbsolutePath() );
			File src = new File( path.toString() );
			while( new File ( path.toString() ).exists() ) {
				src = new File( path.toString() );
				path.append( name );
			}
			
			StringBuilder cmd =  new StringBuilder();
			File archive = new File( file.getAbsolutePath() + "\\" + file.getName() + ".7z" );
			
			if ( archive.exists() ) {
				System.out.println("[ ERROR ] Compression process : \"" + archive + "\" already exists ");
				listener.onError();
				
			} else {
			
				cmd.append( "7z a -y" );
				for ( String s : this.ignorels ) {
					cmd.append(" -x!" + s);
				}
				cmd.append(" -sdel -mmt8 -mx9 -t7z");
				List<String> cmdls = new ArrayList<String>( Arrays.<String>asList( cmd.toString().split("\\s+")) );
				cmdls.add( archive.getAbsolutePath() );
				cmdls.add( src + "\\." );
				
				if ( Shell.exec( new Shell.Command( null, cmdls.toArray( new String[cmdls.size()]) )) == 0 ) {	
					listener.onSuccess( new CompactFile( archive ) );
					src.delete();
				}  else {
					listener.onError();
				}
			
			}
			
			
		}
	}       
	
	public static class DecompressProcess extends CProcess {
		public DecompressProcess( CompactFile file, OnDecompressListener listener ) {
			super( file.getParentRootFile(), listener );
		}
		@Override
		void checkAnonymous() {}
		@Override
		public void start() { 
			OnDecompressListener listener = (OnDecompressListener) getListener();
			listener.onStart();
			
			File file = getFile(); // this is 7z 
			Folder folder = file.getParentFolder();
			
			while( ( folder.hasSameName( folder.getParentFile() ) )) {			
				folder = folder.getParentFolder();
			}
			
			if ( Shell.exec( new Shell.Command( "7z", "x", "-y", "-o" + folder.getAbsolutePath() , file.getAbsolutePath() ) ) == 0 ) {
				listener.onSuccess( folder );
				
				if ( file.delete() ) {
					System.out.println( "[ Source file deleted : " + file + " ]" );
				} else {
					System.out.println("[ There was an error trying to delete : " + file + " after decompression ]");
				}
				
			} else {
				listener.onError();
			};
		}
	}
	
	public static abstract class CProcess {
		File file;
		OnSuccessListener listener;
		
		public CProcess( File file, OnSuccessListener listener ) {
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
		public OnSuccessListener getListener() {
			return listener;
		}
		public void start() { } ;
	}
	
	public static abstract class OnCompressListener extends OnSuccessListener {
		public abstract void onSuccess( CompactFile file );
	}
	
	public static abstract class OnDecompressListener extends OnSuccessListener {
		public abstract void onSuccess( Folder folder );
	}
}