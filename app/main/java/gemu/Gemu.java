package gemu;

import gemu.frame.main.MainFrame;
import java.io.*;       
import java.util.*;
import gemu.game.*;

public class Gemu {
	ArrayList<Game> games = new ArrayList<>();
	
	Gemu( File f ) {
		findGames(f);
	}
	public void findGames( File file ) {
		if ( file.isDirectory() ) {            
			boolean gamefound = false;
			for ( File f : file.listFiles() ) {
				if ( gamefound ) {
					break;
				}
				
				findGames(f);
			}
		}
		
		if ( Games.isLauncher(file) ) {
			games.add( new Game( new Launcher(file)));
		}
	}
	public static void main( String[] args ) {
		Gemu g = new Gemu( new File( args[0]) );
		new MainFrame( g.games.toArray( new Game[0] ) );
	}
}