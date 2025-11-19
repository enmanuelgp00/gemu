package gemu.frame.main.gamepanel.infolayer;

import gemu.game.Game;
import gemu.game.Games;

import gemu.util.Texts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class InfoTopBar extends JPanel {
	LengthPane lengthPane;
	Star star;
	
	Game game;
	
	InfoTopBar( Game game ) {
		super();		
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ) );
		setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
		setOpaque( false );
		
		this.game = game;
		
		
		lengthPane = new LengthPane();             
		refreshFileLength();
		
		add( lengthPane ); 
		add( Box.createHorizontalGlue() );
		add( new Star() );
		
	}

	public void refreshFileLength() {
		lengthPane.refresh();
	}
	
	class LengthPane extends JPanel {
		Tag current,
			length,
			compactlength;
			
		Color selectColor = Color.LIGHT_GRAY;
		Color selectForeground = Color.BLACK;
		Color defaultColor = Color.GRAY;
		Color defaultForeground = Color.LIGHT_GRAY;
		
		LengthPane () {
			super();
			setBackground( defaultColor );
			setLayout( new BoxLayout(this, BoxLayout.Y_AXIS ));
			
			Tag[] labels = new Tag[] { 
				length = new Tag(),  
				compactlength = new Tag()
			};
			
			for ( Tag l : labels ) {
				l.setAlignmentX( 1.0f);
				l.setBackground( defaultColor );
				add( l );
			}
		}
		
		public void refresh() {			
			
			if ( game.getState() != Games.STATE_DELETED ) {			
				if ( game.isCompressed() ) {
					selectStateColor( compactlength );
					defaultStateColor( length );
				} else {
					defaultStateColor( compactlength );   
					selectStateColor( length );
				}
			}
			
			length.setText( Texts.bytesToHumanVerbose( game.length() ) );
			compactlength.setText( Texts.bytesToHumanVerbose( game.compactLength() ) );
			
			revalidate();
			repaint();
		}
		
		public void selectStateColor( Tag tg ) {
			tg.setBackground( selectColor );
			tg.setForeground( selectForeground );
		}
		
		public void defaultStateColor( Tag tg ) {
			tg.setBackground( defaultColor );
			tg.setForeground( defaultForeground );
		}
	}
	
	class Star extends JPanel {
		Star() {
			super();
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