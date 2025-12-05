package gemu.io;

import java.io.*;

public abstract class OnZipProcessListener {                     

	public void processStarted( Process p) { }
	public void streamLineRead( Process p, String line ) { }
	public void processFinished( Process p, int exitCode, File f ) {	}
}