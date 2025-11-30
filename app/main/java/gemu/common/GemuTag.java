package gemu.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GemuTag extends JPanel {
	JLabel title;
	JButton button;
	public GemuTag( String name ) {
		setBackground( null );
		setOpaque( false );
		setBorder(null);
		title = new JLabel( name ) {
			{
				setFont( Style.FONT_TAGS ); 
				
			}
		};
		add( title );
		button = new JButton("-") {
			{
				setBorder( BorderFactory.createEmptyBorder( 2, 8, 2, 8 ) );
				setBorderPainted( false );
				setFocusPainted( false );
				setContentAreaFilled( false );
			}
			@Override
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setColor( Color. );              
				g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2.fillRoundRect( 0, 0, getWidth(), getHeight(), 19, 19 );
				super.paintComponent(g);
				g2.dispose();
			}
		};
		add( button );
		
	}
	@Override
	public void paintComponent( Graphics g) {    
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g.create(); 
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2.setColor( Color.PINK );
		g2.fillRoundRect( 0, 0, getWidth(), getHeight(), 19, 19 );
		g2.dispose();
	}

}