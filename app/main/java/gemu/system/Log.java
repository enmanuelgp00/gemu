package gemu.system;

import gemu.util.Texts;

public final class Log {
	public static void error( Object... message ) {
		print( Texts.inBrace("ERR"), message );
	}
	
	public static void info( Object... message ) {
		print( Texts.inBrace("INF"), message );
	}
	
	private static void print( String key, Object[] objs ) {
		StringBuilder str = new StringBuilder();
		for ( Object ob : objs ) {
			str.append( ob.toString());
		}
		System.out.println( key + " " + str.toString() );
	}
}