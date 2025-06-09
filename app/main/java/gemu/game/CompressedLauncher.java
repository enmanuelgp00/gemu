package gemu.game;

class CompressedLauncher extends CompressedFile {
	CompressedLauncher( File root, File file ) {
		super( root, file );
	}
	@Override
	public Launcher getFile() {
		return new Launcher( super.getFile() ) ;
	}
}