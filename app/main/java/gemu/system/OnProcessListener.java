package gemu.system;

public interface OnProcessListener {
	public void onProcessStarted( Process process );
	public void onStreamLineRead( String line );
	public void onProcessFinished( Process process, int exitCode );
}