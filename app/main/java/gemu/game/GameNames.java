package gemu.game;

public final class GameNames {	
	
	private static boolean isEndWord( char ch ) {
		char[] sign = new char[] {' ', '_', '-' };
		return false;
	}	
		
	private static Site[] sites = new Site[] { };
	
	private static String getSite( String str ) {
		for ( Site site : sites ) {
			if ( str.toLowerCase().contains( site.ref ) ) {
				return site.name;
			} 			
		}
		return null;
	}
	
	public static void interpret( Game game, InterpreterListener listener ) {
		String name = game.getName();
		
	}
	
	public interface InterpreterListener {
		public void onVersionInterpreted( String v );
		public void onNameInterpreted( String name );
		public void onSitesInterpreted( String[] sites );
	}
	
	
	private class Site {
		final String name;
		final String ref;
		
		Site( String name, String ref ) {
			this.name = name;
			this.ref = ref;
		}
	}
} 