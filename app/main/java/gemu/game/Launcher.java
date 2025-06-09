package gemu.game;

public class Launcher extends File {

	public Launcher( String name ) {
		super(name);
		check();
	}
	public Launcher( File file ) {
		super( file.getAbsolutePath() );
		check();
	}
	
	public static boolean isFileLauncher( File file ) {
		if ( file.hasExtension("exe") ) {
			String[] exceptions = new String[] { "setting", "crash", "helper", "setup" };		
			String name = file.getName();
			
			for (String e : exceptions ) {
				if (name.toLowerCase().contains( e ) ) {
						return false;
				}
			}
			return true;
		}		
		return false;
	}
	
	private void check() {
		 try {
			if ( !Launcher.isFileLauncher( this ) ) {
				throw new Exception() {
					@Override
					public void printStackTrace() {
						super.printStackTrace();
						System.out.println( getName() );
					}
				};
			}
		} catch(Exception e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
}