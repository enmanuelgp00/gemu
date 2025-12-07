package gemu.util;

public final class HumanVerbose {
	public static String bytes( Long l ) {
		if (l == null ) {
			return "??kb";
		}
		String[] names = new String[]{"b ", "kb", "mb", "gb", "tb"};
		int scale = 1000;
		int shiftCount = 0;
		double value = (double)l;
		while( value > scale ) {
			value /= scale;
			shiftCount++;
		}
		return String.format("%.1f%s", value, names[shiftCount]);
	}
}