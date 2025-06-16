package gemu.frame.main.gamepanel.infolayer;

import gemu.game.Game;

import gemu.util.Texts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class InfoTopBar extends JPanel {
	Tag size;
	Star star;
	
	Game game;
	
	InfoTopBar( Game game ) {
		super();		
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		setOpaque( false );
		
		this.game = game;
		
		
		size = new Tag("");               		
		size.setForeground( Color.WHITE );
		size.setBackground( Color.BLACK );
		refreshFileLength();
		
		add( size ); 
		add( Box.createHorizontalGlue() );
		add( new Star() );
		
	}

	public void refreshFileLength() {
		size.setText( Texts.bytesToHumanVerbose( game.length() ));
	}
	
	class Star extends JPanel {
		Star() {
			super();
			setAlignmentY( Component.BOTTOM_ALIGNMENT );
			setPreferredSize( new Dimension( 10, 10 ) );
			setMaximumSize( getPreferredSize() );			
			setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
			
			if ( game.isFavorite() ) {   
				setBackground( Color.RED );
			}
			
			addMouseListener( new MouseAdapter() {
				@Override
				public void mousePressed( MouseEvent e ) {
					if ( SwingUtilities.isLeftMouseButton(e) ) {
						game.setFavorite( !game.isFavorite() );
						updateBackground();
					}
				}
			});
		}
		
		public void updateBackground() {
			Color background = null;
			if( game.isFavorite() ) {
				background = Color.RED;
			}
			setBackground( background );
		}
	}
}