package gemu.frame.home.shelf;

import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import gemu.common.*;

class Book extends JPanel {
	Box boxOnTop;
	final Dimension standardSize = new Dimension( 110, 140 );
	int detailWidth = 200;
	boolean isMouseInside = false;
	Image img = null;
	Book() {
		super();
		try {
			
			img = ImageIO.read( new File("build/main_screenshot.jpg"));
		} catch( Exception e ) {
			e.printStackTrace();
		}
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
					}
					@Override
					public void paintComponent( Graphics g ) {
						Graphics2D g2d = (Graphics2D)g;
						g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
						
						if ( getModel().isPressed() ) {
							g2d.setColor( Color.PINK );
						} else if ( getModel().isRollover() ) {
							g2d.setColor( Style.COLOR_SECONDARY.brighter());
						} else {
							g2d.setColor( Style.COLOR_SECONDARY );
						}
						g2d.fillOval( 6, 10, 9, 9);
						g2d.fillOval( 14, 10, 9, 9);
						g2d.fillPolygon(new int[]{ 6, 15, 23 } , new int[]{ 16, 24, 16 } ,3);
						
						//g2d.setColor( Style.COLOR_SECONDARY.brighter() );
						//g2d.fillOval( 7, 11, 8, 8);
						//g2d.fillOval( 14, 11, 8, 8);
						//g2d.fillPolygon(new int[]{ 7, 15, 22 } , new int[]{ 16, 23, 16 } ,3);
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
		Graphics2D g2 = (Graphics2D)g;
			double scale = (double)img.getWidth( null ) / (double)img.getHeight( null ); 
			double height = getHeight();
			double width = scale * height;
			detailWidth = (int)width;
			int x;
			if ( getWidth() == detailWidth ) {
				x = 0;			
			} else {
				x =  getWidth() / 2  - detailWidth / 2;
			}
			g2.drawImage( img, x, 0, (int)width, (int)height, this );
		
		
	}
}