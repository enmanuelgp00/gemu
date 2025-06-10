package gemu;

import gemu.system.Shell;
import gemu.game.*; 
import gemu.file.*;
import gemu.frame.main.MainFrame;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Gemu {
	private List<Game> gamels = new ArrayList<Game>();
	
	Gemu( Folder folder ) {		
		findGames( folder );
		new MainFrame( gamels );
	}	
	
	private void findGames( File file ) {		
		if ( file.isDirectory() || CompactFile.isFileCompact( file ) ) {
			if( !Game.hasFilePossibleGame( file , onPossibleGameFoundListener ) ) {
				if ( file.isDirectory() ) {
					for ( File f : file.listFiles() ) {
						findGames( f );
					}				
				}			
			}				
		} 
	}
	
	Game.OnPossibleGameFoundListener onPossibleGameFoundListener = new Game.OnPossibleGameFoundListener() {
		@Override
		public void onGameFound( Game game ) {
			gamels.add(game);
		}
		@Override
		public void onLauncherListFound( List<Launcher> launchers ) {
			System.out.println("[ " + launchers.size() + " ] Launchers Found");
			gamels.add( new Game( launchers.get(0)));
		}
	};
	
	public static void main ( String[] args ) {		
		new Gemu( new Folder(args[0]) );
	}
}