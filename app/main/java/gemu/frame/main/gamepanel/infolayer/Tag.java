package gemu.frame.main.gamepanel.infolayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Tag extends JLabel {
	Tag( String name ) {
		super( name );
		setOpaque( true );
		setBackground( Color.PINK );
		setBorder( BorderFactory.createEmptyBorder( 2, 3, 0, 3 ));			
		setFont( new Font( "Consolas", Font.PLAIN, 13 ) );
		addMouseEvent( new MouseAdapter() ) {
		
		}
	}
}