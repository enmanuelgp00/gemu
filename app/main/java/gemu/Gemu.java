package gemu;

import gemu.frame.main.MainFrame;
import java.io.*;       
import java.util.*;
import gemu.game.*;  
import gemu.util.*;

public class Gemu {
	ArrayList<Game> games = new ArrayList<>();
	
	Gemu( File f ) {
		findGames(f);
	}
	public void findGames( File file ) {
		if ( file.isDirectory() ) {
			ArrayList<Launcher> launchers = new ArrayList<>();  
			ArrayList<Info> infos = new ArrayList<>();
			ArrayList<File> compactFiles = new ArrayList<>();
			
			for ( File f : file.listFiles() ) {
				
				if ( Games.isInfoFile(f) ) {
					try {
						Info inf = Info.parseInfo(f);
						infos.add(inf);					
					} catch ( Exception e ) {
						e.printStackTrace();
					}
					
				} else if ( Games.isLauncher(f) ) {
					launchers.add( new Launcher(f));
					
				} else if ( Zipper.isCompact(f) ) {
					compactFiles.add(f);
				}
			}
			
			if ( infos.size() > 0 ) {			
				for ( Info info : infos ) {
					games.add( Game.from(info));
				}
			} else {
				for ( Launcher l : launchers ) {
					try {
						Game game = new Game( l );
						games.add( game );
					} catch ( Exception e ) {}
				}
			}
						
			
			if ( launchers.size() == 0 ) {
				for ( File f : file.listFiles() ) {
					findGames(f);
				}
			} 
			/*
			System.out.println("launchers : " + launchers.size());
			System.out.println("infos : " + infos.size());
			System.out.println("compactfiles : " + compactFiles.size());
			*/
		}
	}
	public static void main( String[] args ) {
		Gemu g = new Gemu( new File( args[0]) );
		new MainFrame( g.games.toArray( new Game[0] ) );
	}
}