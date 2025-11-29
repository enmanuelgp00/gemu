package gemu.frame.home.shelf;

import gemu.common.*;
import java.awt.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

public class Shelf extends Box {
	Shelf() {
		super( BoxLayout.Y_AXIS );
		
		Box about = new Box( BoxLayout.X_AXIS );
		about.add( new GemuButton("PLAY"));
		about.add( Box.createHorizontalGlue());
		about.add( new GemuButton("DELETE"));
		add( about );
		
		Content content = new Content();
		GemuScrollPane scroll = new GemuScrollPane();
		scroll.setViewportView( content );
		
		for ( int i = 0; i < 20; i ++ ) {
			content.add( new Book());
		}
		add( scroll );
		
	}
	
	class Content extends JPanel {
		Content() {
			super( );
			setBackground( Style.COLOR_SECONDARY );
		}
		@Override
		public Dimension getPreferredSize() {
			LayoutManager layout = getLayout();
			if ( layout != null ) {
				layout.layoutContainer( this );
			}
			int width = 0;
			int height = 0;
			Rectangle last;
			Component[] components = getComponents(); 
			if ( components.length > 0 ) {
				last = components[ components.length - 1 ].getBounds();
				width = last.x + last.width;
				height = last.y + last.height;
			}
			Insets insets = getInsets();
			return new Dimension( width + insets.left + insets.right, height + insets.top + insets.bottom );
		}
	}
	
	
}