package gemu.util;

import java.text.SimpleDateFormat;
import java.util.*;

public final class HumanVerbose {
	public static ByteStructure DECIMAL_BYTES = new ByteStructure( new String[]{"b ", "kb", "mb", "gb", "tb"}, 1000 );
	public static ByteStructure BINARY_BYTES = new ByteStructure( new String[]{"b ", "kib", "mib", "gib", "tib"}, 1024 );
	
	public static String bytes( Long l, ByteStructure byteStructure ) {
		String[] names = byteStructure.names; 
		
		if (l == null ) {
			return "??";
		}                            
		
		int scale = byteStructure.scale;		
		int shiftCount = 0;
		double value = (double)l;
		while( value > scale ) {
			value /= scale;
			shiftCount++;
		}
		return String.format("%.1f %s", value, names[shiftCount]);
	}
	
	public static String hours( Long l ) {
		if ( l != 0 ) {
			String[] names = new String[]{ "sec", "min", "h" };
			
			int[] val = new int[] { 1000, 60, 60 };
			double result = (double)l;
			for ( int i = 0; i < names.length; i++ ) {
				result /= val[i];             
				int test = i + 1;
				
				if ( test < names.length ) {
					if ( result / val[test] < 1 )  {
						return String.format("%.0f %s", result, names[i]);
					}
				}
			}
		}
		return "?";
	}
	
	public static String date( Long l ) {
		if ( l != null ) {			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(l);
			SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM YYYY", Locale.US ); 
			SimpleDateFormat hourFormat = new SimpleDateFormat("hh:mm a", Locale.US );
			String played = dateFormat.format( calendar.getTime() );
			String current = dateFormat.format( Calendar.getInstance().getTime() );
			if ( played.equals(current)) {
				return "Today " + hourFormat.format( calendar.getTime() );
			}
			return dateFormat.format( calendar.getTime());
			
		}
		return "?";
	}
	
	private static class ByteStructure {
		String[] names;
		int scale;
		ByteStructure( String[] names, int scale ) {
			this.names = names;
			this.scale = scale;
		}
	}
	
} 