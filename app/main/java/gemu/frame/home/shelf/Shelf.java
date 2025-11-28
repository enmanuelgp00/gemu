package gemu.frame.home.shelf;

import gemu.common.*;
import java.awt.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

public class Shelf extends GemuScrollPane {
	Shelf() {
		super();
		Content content = new Content();
		setViewportView( content );
		
		for ( int i = 0; i < 20; i ++ ) {
			content.add( new Book());
		}
		
	}
	
	class Content extends JPanel {
		Content() {
			super();
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