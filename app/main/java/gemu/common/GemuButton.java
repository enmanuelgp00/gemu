package gemu.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GemuButton extends JButton {
	private Color background = Style.COLOR_BACKGROUND;
	private Color foreground = Style.COLOR_FOREGROUND;
	
	private Color pressedBackground = background.darker();
	private Color pressedForeground = Style.COLOR_FOREGROUND;
	
	private Color rolloverBackground = background.brighter();
	private Color rolloverForeground = Style.COLOR_FOREGROUND;
	
	int v = 0;
	int p = 0;
	
	public GemuButton( String title ) {
		super( title );
		a();
	}       
	
	public GemuButton( String title, int v, int p ) {
		super( title );
		a();
		this.v = v;
		this.p = p;
	}
	
	
	private void a() {
		setForeground( foreground );
		setFont( Style.FONT_MONO_SPACE );
		setContentAreaFilled( false );
		setBorderPainted( false );
		setFocusPainted( false );
		setBackground(null);
		setBorder( BorderFactory.createEmptyBorder( 8, 10, 5, 10 ));
		addMouseListener( new MouseAdapter() {
			@Override
			public void mouseReleased( MouseEvent event ) {
				setForeground( foreground );
			}
		});
	
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		Graphics2D g2d = ( Graphics2D ) g.create();
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING , RenderingHints.VALUE_ANTIALIAS_ON );
		if ( getModel().isPressed() ) {
			g2d.setColor( pressedBackground );
			setForeground( pressedForeground );
		} else if ( getModel().isRollover() ) {
			g2d.setColor( rolloverBackground );
			setForeground( rolloverForeground );
		} else {
			g2d.setColor( background );
			setForeground( getForeground() );
		}
		g2d.fillRoundRect( 0, 0, getWidth(), getHeight(), v, p );
		super.paintComponent( g2d );
	}
	
	public void setBackgroundColor( Color color ) {
		background = color;
	}
	
	public void setForegroundColor( Color color ) {
		foreground = color;
	} 
	
	public Color getBackgroundColor() {
		return background;
	}
	
	public Color getForegroundColor() {
		return getForeground();
	}
	
	
	public void setPressedBackground( Color color ) {
		pressedBackground = color;
	} 
	
	public void setPressedForeground( Color color ) {
		pressedForeground = color;
	}  
	
	public Color getPressedBackground() {
		return pressedBackground;
	} 
	
	public Color getPressedForeground() {
		return pressedForeground;
	}
	
	
	public void setRolloverBackground( Color color ) {
		rolloverBackground = color;
	}
	
	public void setRolloverForeground( Color color ) {
		rolloverForeground = color;
	} 
	
	public Color getRolloverBackground() {
		return rolloverBackground;
	}
	
	public Color getRolloverForeground() {
		return rolloverForeground;
	}
}