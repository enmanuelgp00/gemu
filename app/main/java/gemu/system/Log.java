package gemu.system;

public final class Log {
	public static void info( String message ) {
		print( tag("INFO"), message );
	}
	
	public static void warring( String message ) {
		print( tag("WARRING"), message );
	}
	
	public static void error( String message ) {
		print( tag("ERROR"), message );
	}
	
	private static String tag( String key) {
		return String.format("[ %s ]", key );
	}
	private static void print( String key, String message ) {
		System.out.println( key + " " + message );
	}
	
}