package gemu.frame.main.shelf;

import java.awt.*;   
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import gemu.common.*;
import gemu.game.*;

class BookCover extends JPanel {
	Box boxOnTop;
	final Dimension standardSize = new Dimension( 110, 140 );
	int detailWidth = 0;
	boolean isMouseInside = false;
	BufferedImage bufferedImage = null;
	Game game;
	BookCover( Game game ) {
		super();
		this.game = game; 
		try {
			
			bufferedImage = ImageIO.read( game.getCover());
		} catch( Exception e ) { }
		setLayout( new OverlayLayout( this ) );
		setPreferredSize( standardSize );
		setBackground( Style.COLOR_SECONDARY.brighter() );
		Box buttonsPane = new Box( BoxLayout.Y_AXIS );
		
		buttonsPane.add( new Box( BoxLayout.X_AXIS ) {
			{
				add( Box.createHorizontalGlue());
				add( new GemuButton("") {
					{
						setPreferredSize( new Dimension( 30, 30));
						setMaximumSize( new Dimension( 30, 30 ));
						setBackground( Style.COLOR_BACKGROUND );
					}
					@Override
					public void paintComponent( Graphics g ) {
						Graphics2D g2d = (Graphics2D)g.create();
						g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
						g2d.drawImage( Drawing.drawHeart( getBackground() ), 0, 0, getWidth(), getHeight(), this );
						g2d.dispose();
					}
				});
			}
		});
		
		buttonsPane.add( Box.createVerticalGlue());
		add( buttonsPane );
		setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1));
		addMouseMotionListener( expandOnRollover );  
		addMouseListener( expandOnRollover );
		
		
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setIsMouseInside( Boolean beans ) {
		isMouseInside = beans;
	}
	public boolean isMouseInside() {
		return isMouseInside;
	}
	int initialX = -1;
	int initialY = -1;
	MouseAdapter expandOnRollover = new MouseAdapter() {
		@Override
		public void mousePressed( MouseEvent event ) {
			setBounds( initialX + 1, initialY + 1, standardSize.width -2, standardSize.height - 2  );
			
		}
		@Override
		public void mouseReleased( MouseEvent event ) {
			if ( isMouseInside ) {
				setBounds( initialX - 2, initialY - 2, standardSize.width + 4, standardSize.height + 4 );			
			}
		}
		@Override
		public void mouseMoved( MouseEvent event ) {
			if ( initialX == -1 ) {
				initialX = getX();
			}                       
			if ( initialY == -1 ) {
				initialY = getY();
			}
			if ( contains(event.getPoint())) {
				setBounds( initialX - 2, initialY - 2, standardSize.width + 4, standardSize.height + 4 );
			}
			setIsMouseInside( true );
		}
		@Override
		public void mouseEntered( MouseEvent event ) {
			//setComponentZOrder( component, 0 )
		}   
		
		@Override
		public void mouseExited( MouseEvent event ) {
			if ( !contains(event.getPoint())) {
				setPreferredSize( standardSize );
				revalidate();
				repaint();   
				setIsMouseInside( false );
				initialX = -1;
				initialY = -1;
				
			}
			
		}
	};
	
	@Override
	public void paintComponent( Graphics g ) {
		if ( bufferedImage != null ) {     
			Graphics2D g2 = (Graphics2D)g.create();
			double scale = (double)bufferedImage.getWidth( null ) / (double)bufferedImage.getHeight( null ); 
			double height = getHeight();
			double width = scale * height;
			detailWidth = (int)width;
			int x;
			if ( getWidth() == detailWidth ) {
				x = 0;			
			} else {
				x =  getWidth() / 2  - detailWidth / 2;
			}
			g2.drawImage( bufferedImage, x, 0, (int)width, (int)height, this ); 
			g2.dispose();	
		} else {
			super.paintComponent(g);			
		}
	}
}