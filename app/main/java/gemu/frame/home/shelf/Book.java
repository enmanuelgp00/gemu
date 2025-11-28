package gemu.frame.home.shelf;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import gemu.common.*;

class Book extends JPanel {
	Box boxOnTop;
	boolean isMouseInside = false;
	Book() {
		super();
		setLayout( new OverlayLayout( this ) );
		setPreferredSize( new Dimension( 110, 140 ));
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
						
						g2d.setColor( Color.PINK );
						g2d.fillOval( 6, 10, 9, 9);
						g2d.fillOval( 14, 10, 9, 9);
						g2d.fillPolygon(new int[]{ 6, 15, 23 } , new int[]{ 16, 24, 16 } ,3);
					}
				});
			}
		});
		
		buttonsPane.add( Box.createVerticalGlue());
		buttonsPane.add( new Box( BoxLayout.X_AXIS ) {
			{                                    
				add( new GemuButton(">"));
				add( Box.createHorizontalGlue());
			}
		});
		add( buttonsPane );
		setBorder( BorderFactory.createLineBorder(Style.COLOR_SECONDARY ));
		addMouseMotionListener( expandOnRollover );  
		addMouseListener( expandOnRollover );
		
		
	}

	public void setIsMouseInside( Boolean beans ) {
		isMouseInside = beans;
	}
	public boolean isMouseInside() {
		return isMouseInside;
	}
	
	MouseAdapter expandOnRollover = new MouseAdapter() {
		@Override
		public void mousePressed( MouseEvent event) {
			System.out.println( event );
		}
		@Override
		public void mouseEntered( MouseEvent event ) {
			int width = 150;      
			//setComponentZOrder( component, 0 )
			if ( !isMouseInside() ) {
				setPreferredSize( new Dimension( width, 140 ));
				revalidate();
				repaint();
				setIsMouseInside( true );
			}
		}   
		
		@Override
		public void mouseExited( MouseEvent event ) {
			if ( !contains(event.getPoint())) {
				setPreferredSize( new Dimension( 110, 140 ));
				revalidate();
				repaint();
				setIsMouseInside( false );
			}
		}
	};
}