package gemu.util;

public final class HumanVerbose {
	public static String bytes( Long l ) {
		if (l == null ) {
			return "??kb";
		}
		return "100gb";
	}
}