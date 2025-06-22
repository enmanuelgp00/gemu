package gemu.system.event;

public interface OnProcessListener {
	public void onProcessStarted( Process p );
	public void onProcessFinished( Process p, int exitCode );
	public void onStreamLineRead( String line );
}