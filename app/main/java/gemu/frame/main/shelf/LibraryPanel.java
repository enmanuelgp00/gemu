package gemu.frame.main.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  
import gemu.game.*;

public class LibraryPanel extends GemuSplitPane {
	Banner banner;
	public LibraryPanel( Game[] games ) {                
		super( JSplitPane.VERTICAL_SPLIT );
		Shelf shelf = new Shelf( games );
		ButtonsBar buttonsBar = new ButtonsBar();
		banner = new Banner() {
			@Override
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				int bannerMaximumHeight = getMaximumSize().height;
				int shelfMinimumHeight = LibraryPanel.this.getHeight() - bannerMaximumHeight; 
				if ( shelfMinimumHeight < buttonsBar.getHeight() ) {
					shelfMinimumHeight = buttonsBar.getHeight();
				}
				shelf.setMinimumSize( new Dimension( 0, shelfMinimumHeight));
				if ( bannerMaximumHeight < LibraryPanel.this.getDividerLocation() ) {
					LibraryPanel.this.setDividerLocation( bannerMaximumHeight );
				}
			}
		};
		
		if( games.length > 0 ) {
			banner.setGame( games[0] );
		}
		
		shelf.addOnBookCoverMouseAdapter( new Shelf.OnBookCoverMouseAdapter() {
			@Override
			public void mousePressed( MouseEvent event, BookCover cover ) {
				banner.setGame( cover.getGame() );
			}
		});
		
	
		
		Box box = new Box( BoxLayout.Y_AXIS );
		box.add( buttonsBar );
		box.add( shelf );
		
		setLeftComponent( banner );
		setRightComponent( box );
		
		setDividerSize(5);
	}
	
	private class ButtonsBar extends Box {
	
		
	
		ButtonsBar() {
			super(BoxLayout.X_AXIS );
			
			GemuButton playButton = new GemuButton("Play", 5, 5 );
			playButton.setBackgroundColor( new Color( 0, 160, 0 ) );
			playButton.setPressedBackground( new Color( 0, 150, 0 ) );
			playButton.setRolloverBackground( new Color( 0, 180, 0 ) );
			playButton.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent event ) {
					banner.getGame().play();
				}
			});
			GemuButton[] buttons = new GemuButton[] {
				new GemuButton("7zip"),                
				new GemuButton("Files"),
				new GemuButton("Trash")
			};
			
			
			setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 7 ));
			add( playButton );   
			add( Box.createHorizontalGlue());
			for ( GemuButton button : buttons ) {
				add(button);
			}
		}
	}
	
}