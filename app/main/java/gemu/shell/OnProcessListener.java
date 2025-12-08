package gemu.shell;

public abstract class OnProcessListener {                     

	public void processStarted( long id ) { }
	public void streamLineRead( long id, String line ) { }
	public void processFinished( long id, int exitCode ) {	}
}