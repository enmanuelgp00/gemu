package gemu.util;

public final class Texts {
	public static String bytesToHumanVerbose( long bytes ) {
		double result = 0;
		int p = 1;
		int scale = 1024;
		String type;
			
		while ( result < 1 || result > scale ) {
			result = bytes / Math.pow( scale, p ) ;
			p ++ ;
		}
		
		String[] binary = new String[]{ "bytes", "Kib", "Mib","Gib", "Tib" };
		
		return String.format("%.2f %s", result , binary[ p - 1 ] );
	}
}