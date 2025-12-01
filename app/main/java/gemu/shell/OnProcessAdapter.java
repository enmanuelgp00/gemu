package gemu.shell;

public abstract class OnProcessAdapter {                     

	public void processStarted( Process p) { }
	public void streamLineRead( Process p, String line ) { }
	public void processFinished( Process p, int exitCode ) {	}
}