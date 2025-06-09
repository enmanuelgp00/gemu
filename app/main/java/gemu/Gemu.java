package gemu;

import gemu.sys.Shell;
import gemu.game.*;

import java.util.List;
import java.util.ArrayList;

public class Gemu {
	private List<Game> lsGames = new ArrayList<Game>();
	
	Gemu( FolderZip folder ) {
		findGames( folder );
		for ( Game g : lsGames ) {
			System.out.println( g.getLauncher().getAbsolutePath() );
		}
	}
	
	private void findGames( File file ) {		
		if ( file.isDirectory() || file.isCompressed() ) {
			if( !new FolderZip(file).hasPossibleGame( onPossibleGameFoundListener ) ) {
				if ( file.isDirectory() ) {
					for ( File f : file.listFiles() ) {
						findGames( f );
					}				
				}			
			}				
		}
	}
	
	FolderZip.OnPossibleGameFoundListener onPossibleGameFoundListener = new FolderZip.OnPossibleGameFoundListener() {
		@Override
		public void onGameFound( Game game ) {
			lsGames.add(game);
		}
		@Override
		public void onLauncherListFound( List<Launcher> launchers ) {
			System.out.println("[ " + launchers.size() + " ] Launchers Found");			
		}
	};
	
	public static void main ( String[] args ) {		
		new Gemu( new FolderZip(args[0]) );
	}
}