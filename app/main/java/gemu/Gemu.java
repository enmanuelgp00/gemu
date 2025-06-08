package gemu;

import gemu.game.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class Gemu {
	private List<Game> lsGames = new ArrayList<Game>();
	
	Gemu( Folder folder ) {
		findGames( folder );
		for ( Game g : lsGames ) {
			System.out.println( g.getLauncher().getAbsolutePath() );
		}
	}
	
	private void findGames( File file ) {		
		if ( file.isDirectory() ) {
			if( !Folder.from(file).hasPossibleGame( onPossibleGameFoundListener ) ) {  				
				for ( File f : file.listFiles() ) {
					findGames( f );
				}			
			}				
		} 
	}
	
	Folder.OnPossibleGameFoundListener onPossibleGameFoundListener = new Folder.OnPossibleGameFoundListener() {
		@Override
		public void onGameFound( Game game ) {
			lsGames.add(game);
		}
		@Override
		public void onLauncherListFound( List<Launcher> launchers ) {
			
		}
		@Override
		public void onCompressedGameFound( CompressedGame compressedGame ) {
			System.out.println("Hello from listener");
		}
	};
	
	public static void main ( String[] args ) {		
		new Gemu( new Folder(args[0]) );
	}
}