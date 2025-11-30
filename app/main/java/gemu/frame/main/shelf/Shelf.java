package gemu.frame.main.shelf;

import gemu.common.*;
import java.util.*;
import java.awt.*;           
import java.awt.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;    
import gemu.game.*;

public class Shelf extends GemuScrollPane {

	ArrayList<OnBookCoverMouseAdapter> onBookCoverMouseListeners = new ArrayList<>(); 
	
	Shelf( Game[] games ) {
		super();
		setFocusable( true );
		Content content = new Content();
		setBorder( BorderFactory.createEmptyBorder( 7, 0, 0, 0 ) ); 
		setViewportView( content );
		
		for ( Game game : games ) {
			BookCover bookCover = new BookCover( game );
			bookCover.addMouseListener( new MouseAdapter(){
				@Override
				public void mousePressed( MouseEvent event ) {
					for ( OnBookCoverMouseAdapter listener : onBookCoverMouseListeners ) {
						listener.mousePressed( event, bookCover );
					}
				}
			});
					
			content.add( bookCover);
		}
		 
		addMouseListener( new MouseAdapter(){
			@Override
			public void mousePressed( MouseEvent e ) {
				requestFocusInWindow();
			}
		});
	}
	
	public void addOnBookCoverMouseAdapter( OnBookCoverMouseAdapter listener ) {
		onBookCoverMouseListeners.add( listener );
	}
	
	static abstract class OnBookCoverMouseAdapter {
		void mousePressed( MouseEvent event, BookCover cover ) { }
	}
	
	
	class Content extends JPanel {
		Content() {
			super( new FlowLayout( FlowLayout.LEFT ));
			setBackground( Style.COLOR_SECONDARY );
		}
		@Override
		public void paintComponent( Graphics g ) {  
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			GradientPaint gradient = new GradientPaint( 0, 0, Style.COLOR_BACKGROUND, 0, 30, getBackground() );
			g2d.setPaint(gradient);
			g2d.fillRect( 0, 0, getWidth(), getHeight() );
			
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
			int gap = ((FlowLayout)getLayout()).getVgap();
			return new Dimension( width + insets.left + insets.right, height + insets.top + insets.bottom  + gap);  
		}
		
	}
	
	
}