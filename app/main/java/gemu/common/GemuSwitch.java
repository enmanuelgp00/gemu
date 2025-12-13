package gemu.common;

import javax.swing.*;
import java.awt.*;           
import java.awt.event.*;

public class GemuSwitch extends JPanel {
	boolean transitionRunning = false;
	boolean turnOn = false;
	Color levelColor = Color.WHITE;
	int padding = 3;                 
	Point levelPosition = new Point( padding, padding );
	Dimension levelSize;
	public GemuSwitch() {
		super();
		setBorder( null );
		setOpaque( false );
		setBackground( null );
		setMaximumSize( getPreferredSize() );                                              
		addMouseListener( changeStateOnClick );
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension( 50, 30 );
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;           
		levelSize = new Dimension( getHeight() - padding * 2, getHeight() - padding * 2 );
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		Color lcolor = new Color( levelColor.getRed(), levelColor.getGreen(), levelColor.getBlue(), 100 );
		if ( !isTransitionRunning() ) { 
			if ( isTurnOn() ) {
				levelPosition.x = getWidth() - levelSize.width - padding;
				levelPosition.y = getHeight() - levelSize.height - padding; 
			
			} else {
				levelPosition.y = padding; 
				levelPosition.x = padding;
			}
		}
			
		if ( isTurnOn() ) {
			lcolor = levelColor;
		}                      
		g2.fillRoundRect( 0, 0, getWidth(), getHeight(), getHeight(), getHeight() );                         
		g2.setColor( lcolor );
		g2.fillRoundRect( levelPosition.x, levelPosition.y, levelSize.width, levelSize.height, getWidth(), getHeight() );
		g2.dispose();
	}
	
	public boolean isTurnOn() {
		return turnOn;
	}
	
	public void setTurnOn( boolean bool ) {
		turnOn = bool;
	}
	private void setTransitionRunning( boolean bool ) {
		transitionRunning = bool;
	}
	public boolean isTransitionRunning() {
		return transitionRunning;
	}
	public void displayTransition() {
		if( !isTransitionRunning() ) {
			Thread transition = new Thread(()->{
				boolean initState = isTurnOn();
				setTransitionRunning( true );
				
				int max = getWidth() - levelSize.width - padding;
				if ( isTurnOn() ) {
					for ( int x = padding; x < max; x++ ) {
						levelPosition.x = x;
						try {						
							Thread.sleep( 10 );
						} catch( Exception e ) {}
						repaint();
					}
				} else {
					for ( int x = max; x > padding; x-- ) {
						levelPosition.x = x;
						try {						
							Thread.sleep( 10 );
						} catch( Exception e ) {}
						repaint();
					} 
				}
				
				
				setTransitionRunning( false );
				
				if ( initState != isTurnOn() ) {
					displayTransition();
				}
			});
			transition.start();
		}
	}
	
	MouseListener changeStateOnClick = new MouseAdapter() {
		@Override
		public void mouseClicked( MouseEvent event ) {
			setTurnOn( !isTurnOn() );
			displayTransition();
		}
	};
} 