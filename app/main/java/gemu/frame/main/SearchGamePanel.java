package gemu.frame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import gemu.game.Game;
import gemu.game.Games;                     
import gemu.frame.main.gameshelf.GameShelf;
import gemu.system.event.*;

class SearchGamePanel extends JPanel {
	GameShelf gameShelf;
	List<Game> gamels;
	JTextField field;
	JButton screenshotButton;
	
	SearchGamePanel( List<Game> gamels ) {
		super( new BorderLayout() );
		this.gameShelf = new GameShelf( gamels );
		this.gamels = gamels;
		
		field = new JTextField();
		screenshotButton = new JButton("Take Screenshot Open Games");
		screenshotButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				for ( Game runningGame : gameShelf.getRunningGames() ) {
					runningGame.takeScreenshot( new OnProcessListener() {
						@Override
						public void onStreamLineRead( String line ) {}
						@Override
						public void onProcessStarted( Process p ) {}
						@Override
						public void onProcessFinished( Process p, int exitcode ) {
							System.out.println( "exit_code : " + String.valueOf(exitcode));
						}
					});
				}
			}
		});
		field.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
		field.addKeyListener( searchOnTyping );
		
		add( field , BorderLayout.NORTH );
		add( screenshotButton, BorderLayout.SOUTH);
		add( gameShelf, BorderLayout.CENTER );
		
	}
	

	KeyListener searchOnTyping = new KeyAdapter() {
		@Override
		public void keyReleased( KeyEvent ev ) {
			if ((char)ev.getKeyCode() == '\n' ) {
			
				if ( field.getText().length() == 0 ) {
					Games.sort( gamels );
					gameShelf.setGameList( gamels );
				} else {
					
					Set<Game> gameSearch = new HashSet<Game>();
					
					for ( Game game : gamels ) {
						if ( game.getName().toLowerCase().contains( field.getText() ) ) {
							gameSearch.add( game );
						} else {
							for ( String tag : game.getTags() ) {
								if ( tag.equals( field.getText() ) ) { 
									gameSearch.add( game );
								}
							}
						}
					}
					gameShelf.setGameList( gameSearch );
				}
			}
		}
	};
}
