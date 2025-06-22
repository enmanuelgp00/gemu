package gemu.util;

public final class Texts {
	
	public static String bytesToHumanVerbose( long l ) {
		int scale = 1024;
		int pow = 1;
		double res;
		String[] names = new String[]{ "b", "kib", "mib" ,"gib","tib" };
		while( ( res = l / Math.pow( scale, pow ) ) > scale ) {
			pow ++;
		}
		
		return String.format( "%.2f %s", res , names[ pow ] );
	}
	
	public static String inBrace( String key ) {
		return "[ " + key + " ]";
	}
	
	public static String inCurlyBrace( String key ) {
		return "{ " + key + " }";
	}
	
	public static boolean isWordDelimiter( char ch ) {
		char[] delims = new char[] { ' ', '\n', '\t', '{', '}', '"', ( char ) 13 };
		for ( char c : delims ) {
			if ( c == ch ) {
				return true;
			}
		}
		
		return false;
	}
}