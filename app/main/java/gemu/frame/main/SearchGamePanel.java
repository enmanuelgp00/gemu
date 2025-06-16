package gemu.frame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import gemu.game.Game;                     
import gemu.frame.main.gameshelf.GameShelf;

class SearchGamePanel extends JPanel {
	GameShelf gameShelf;
	List<Game> gamels;
	JTextField field;
	
	SearchGamePanel( List<Game> gamels ) {
		super( new BorderLayout() );		
		this.gameShelf = new GameShelf( gamels );
		this.gamels = gamels;
		
		field = new JTextField();
		field.addKeyListener( searchOnTyping );
		
		add( field , BorderLayout.NORTH );
		add( gameShelf, BorderLayout.CENTER );
		
	}
	
	KeyListener searchOnTyping = new KeyAdapter() {
		@Override
		public void keyReleased( KeyEvent ev ) {
			if ( field.getText().length() == 0 ) {
				System.out.println("Search bar is empty");
				gameShelf.setGameList( gamels );
			} else {
				 
				Set<Game> gameSearch = new HashSet<Game>();
				
				for ( Game game : gamels ) {
					if ( game.getName().contains( field.getText() ) ) {
						//System.out.println( game.getName() );
						gameSearch.add( game );
					}
				}
				
				gameShelf.setGameList( gameSearch );
			}
		}
	};
}
