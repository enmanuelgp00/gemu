package gemu.frame.main;

import gemu.game.Game;

import gemu.game.Game;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;

class GameDetailPane extends JPanel {
	final Font TAG_FONT = new Font("Courier", Font.PLAIN, 13 );
	Screenshot screenshot;
	Game game;
	JButton play;
	
	Tags tags;
	
	GameDetailPane( Game game ) {
		super( new BorderLayout() );// new FlowLayout( FlowLayout.LEFT ) );
		this.game = game;
		
		JPanel gamePane = new JPanel( new BorderLayout() );		
		
		screenshot = new Screenshot();
		tags = new Tags();
		
		play = new JButton("Play");
		
		gamePane.add( screenshot );
		gamePane.add( play , BorderLayout.SOUTH );
		
		add( gamePane );
		add( tags, BorderLayout.WEST );
				
	}
	
	void setGame( Game game ) {
		this.game = game;
		screenshot.repaint();
		tags.refresh();
		
	}
	
	private class Tags extends JPanel {
		Tags() {
			super( new FlowLayout( FlowLayout.LEFT ) );
			refresh();
		}
		
		public void refresh() {
			removeAll();
			for ( String s : game.getTags() ) {
				add( new Tag( s ));
			}
			revalidate();
			repaint();
		}
		
		class Tag extends JLabel {
			Tag( String string ) {
				super( string );
				setOpaque( true );
				setBackground( Color.PINK );
				setFont( TAG_FONT );
				setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3) );
			}
		}
	}
	
	private class Screenshot extends JPanel {
		Screenshot() {
			super();
			setPreferredSize( new Dimension( 300, 300 ) );
			setBackground( new Color( 0x00aa00 ) );
		}
		@Override
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			try {
				if ( game.getScreenshots() != null && game.getScreenshots().size() > 0 ) {
					Image image = ImageIO.read( game.getScreenshots().get(0) );
					g.drawImage( image, 0, 0, getWidth(), getHeight(), this );
				}
			} catch ( Exception e ) {
					e.printStackTrace();
			}
		}
	}
}