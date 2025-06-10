package gemu;

import gemu.system.Shell;
import gemu.game.*; 
import gemu.file.*;

import java.util.List;
import java.util.ArrayList;

public class Gemu {
	private List<Game> gameLs = new ArrayList<Game>();
	
	Gemu( Folder folder ) {		
		findGames( folder );
		
		for ( int i = 0; i < 5; i++ ) {
			Game game = gameLs.get( i );
			if ( game.isCompressed() ) {
				game.decompress();
			} else {                    
				game.compress();			
			}
		}
				
		
	}
	public void open( Game game ) {
		System.out.println("Openning Game : " + game.getFolder().getAbsolutePath() );
		game.play();
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
			gameLs.add(game);
		}
		@Override
		public void onLauncherListFound( List<Launcher> launchers ) {
			System.out.println("[ " + launchers.size() + " ] Launchers Found");			
		}
	};
	
	public static void main ( String[] args ) {		
		new Gemu( new Folder(args[0]) );
	}
}