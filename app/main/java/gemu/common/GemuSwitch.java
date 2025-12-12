package gemu.common;

import javax.swing.*;
import java.awt.*;           
import java.awt.event.*;

public class GemuSwitch extends JPanel {
	boolean turnOn = false;
	Color levelColor = Color.WHITE;
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
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		Point levelPosition = new Point( 3, 3 );
		Dimension levelSize = new Dimension( getHeight() - levelPosition.x * 2, getHeight() - levelPosition.y * 2 );
		Color lcolor = new Color( levelColor.getRed(), levelColor.getGreen(), levelColor.getBlue(), 100 );
		if ( isTurnOn() ) {
			levelPosition = new Point( 
				getWidth() - levelSize.width - levelPosition.x,
				getHeight() - levelSize.height - levelPosition.y
				); 
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
	
	MouseListener changeStateOnClick = new MouseAdapter() {
		@Override
		public void mouseClicked( MouseEvent event ) {
			setTurnOn( !isTurnOn() );
			repaint();
		}
	};
}