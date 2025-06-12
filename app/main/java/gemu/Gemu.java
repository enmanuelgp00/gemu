package gemu;

import gemu.system.*;
import gemu.game.*; 
import gemu.file.*;
import gemu.frame.main.MainFrame;
import java.io.IOException;


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
		if ( file.isDirectory() || CompactFile.isCompactFile( file ) ) {
			if( !Game.hasPossibleGame( file , onPossibleGameFoundListener ) ) {
				if ( file.isDirectory() && !GameInfo.hasIgnoreFile( file ) ) {
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
			Scanner scan = new Scanner( System.in );
			String answer = null;
			File container = null;
			if ( CompactFile.isCompactFile( launchers.get(0))) {
				container = new CompactFile( launchers.get(0)).getParentRootFile();
			} else {
				container =  launchers.get(0).getParentFile();
			}
			System.out.println("[ " + launchers.size() + " ] Launchers Found in : " + container );
			System.out.println("Would you like to define this folder as a Game ? [ y / n ]");
			if( ( answer = scan.nextLine() ).equals("y") ) {
				int index = -1;
				while( 0 > index || index > launchers.size() ) {
					int i = 0;
					System.out.println("Please, select which is the main launcher");
					for ( Launcher l : launchers ) {
						System.out.println("[" + i + "] " + l );
						i++;
					}
					try {
						index = scan.nextInt();
					} catch ( Exception e ) {}
				}
				
				gamels.add( new Game( launchers.get(index)));
			} else {
				System.out.println("Would you like to create an ignore file ? [ y / n ]");
				if ( ( answer = scan.nextLine() ).equals("y") ) {
					if ( CompactFile.isCompactFile ( container ) ) {
						container = container.getParentFile();						
						
						try {                                                         						
							GameInfo.createIgnoreFileIn( new Folder ( container ) );
							System.out.println("An ignore file was created in folder : " + container );	
						
						} catch ( IOException e ) {
							System.out.println( e.getMessage() );
							System.out.println("Error: counld not create a ignore file");
						}
					}
					
				}
			}
			
			
		}
	};
	
	public static void main ( String[] args ) {		
		new Gemu( new Folder(args[0]) );
	}
}