package gemu.frame.main.shelf;

import gemu.common.*;
import java.awt.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

public class Shelf extends Box {
	Box toolsBar;
	GemuButton playButton = new GemuButton("PLAY");
	
	GemuButton[] buttons = new GemuButton[] {
		new GemuButton("MANAGE"),
		new GemuButton("HEART")
	};
	
	Shelf() {
		super( BoxLayout.Y_AXIS );
		
		toolsBar = new Box( BoxLayout.X_AXIS ) {
			{    
				add( playButton );   
				add( Box.createHorizontalGlue());
				for ( GemuButton button : buttons ) {
					add(button);
				}
			}
			
			
		};
		add( toolsBar );
		
		Content content = new Content();
		GemuScrollPane scroll = new GemuScrollPane();
		scroll.setBorder( BorderFactory.createEmptyBorder( 7, 0, 0, 0 ) ); 
		scroll.setViewportView( content );
		
		for ( int i = 0; i < 20; i ++ ) {
			content.add( new Book());
		}
		add( scroll );
		
	}
	
	public Box getToolsBar() {
		return toolsBar;
	}
	
	class Content extends JPanel {
		Content() {
			super( );
			setBackground( Style.COLOR_SECONDARY );
		}
		@Override
		public void paintComponent( Graphics g ) {  
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			GradientPaint gradient = new GradientPaint( 0, 0, Style.COLOR_BACKGROUND, 0, 50, getBackground() );
			g2d.setPaint(gradient);
			g2d.fillRect( 0, 0, getWidth(), 50 );
			
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