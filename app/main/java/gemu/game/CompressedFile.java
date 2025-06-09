package gemu.game;

class CompressedFile {
	File root;
	File file;
	CompressedFile( File root, File file ) {
		this.root = root;
		this.file = file;
	}
	File getFile() {
		return file;
	}
	File getRootFile() {
		return root;
	}
}