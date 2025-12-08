package gemu.io;

import java.io.*;

public abstract class OnZipProcessListener {                     

	public void processStarted( long processId) { }
	public void streamLineRead( long processId, String line ) { }
	public void processFinished( long processId, int exitCode, File f ) {	}
}