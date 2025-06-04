package gemu;

import gemu.game.*;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Gemu {

	List<Game> gameList;
	
	Gemu( File file ) {
		gameList = new ArrayList<Game>();
		findGames( file );
		gameList.get(0).play();
	}
	
	public void findGames( File file ) {
		 if ( file.isDirectory() ) {
			if ( !hasPossibleGame( file, 
				new OnPossibleGameFound() {
					@Override
					public void gameFound( Game game ) {
						//System.out.println( "game with source found " + game.getName());
						gameList.add( game );
					}
					@Override
					public void launchersFound( List<File> launchers ) {
						gameList.add( new Game( launchers.get(0)));
					}
				}
				)){			
				for (File f : file.listFiles()) {
					findGames(f);
				}
			}
		 }
	}
	
	public boolean hasPossibleGame( File file, OnPossibleGameFound listener ) {
		if ( !file.isDirectory()) {
			return false;
		}
		List<File> launchers = new ArrayList<File>();
		Game game;
		
		for ( File f : file.listFiles() ) {
			if (isLauncher(f)) {
				launchers.add( f );
				
			} else if ( isGameInfo( f ) ) {
				game = new Game( GameInfo.parse( f ) );
				listener.gameFound( game );
				return true;
			}
		}
		
		if ( launchers.size() > 0 ) {
			listener.launchersFound( launchers );
			return true;
		}
		return false;
		
	}
	public boolean isLauncher( File file ) {
		String[] exceptions = new String[] {"helper", "crash" };
		String name = file.getName();
		for ( String exception : exceptions ) {
			if ( name.toLowerCase().contains(exception)) {
				return false;
			}
		}
		int dotIndex = name.lastIndexOf('.');
		String extension = name.substring( dotIndex + 1 );
		return extension.equals("exe");
	}       
	public boolean isGameInfo( File file ) {
		return file.getName().equals(GameInfo.FILE_NAME);
	}	
	
	public interface OnPossibleGameFound {
		public void gameFound( Game game );
		public void launchersFound( List<File> launchers );
	}
	public static void main( String[] args ) {
		new Gemu( new File(args[0]));
	}
}