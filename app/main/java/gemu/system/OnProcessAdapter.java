package gemu.system;

public abstract class OnProcessAdapter implements OnProcessListener {
	public void onProcessStarted( Process process ) { }
	public void onStreamLineRead( String line ) { }
	public void onProcessFinished( Process process, int exitCode ) { }
	
	public void onStart() { }
	public void onError() { }
	public void onSuccess() { }
}