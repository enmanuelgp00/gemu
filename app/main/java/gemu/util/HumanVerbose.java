package gemu.util;

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
		return String.format("%.1f%s", value, names[shiftCount]);
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