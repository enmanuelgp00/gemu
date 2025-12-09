package gemu.frame.main.shelf;

import gemu.common.*;
import java.util.*;
import java.awt.*;           
import java.awt.event.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;    
import gemu.game.*;

public class Shelf extends GemuScrollPane {
	BookCover[] bookCovers;
	ArrayList<OnBookCoverMouseAdapter> onBookCoverMouseListeners = new ArrayList<>(); 
	
	Shelf( Game[] games ) {
		super();
		setFocusable( true );
		Content content = new Content();
		setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) ); 
		setViewportView( content );
		
		Thread th = new Thread(()->{
			bookCovers = new BookCover[ games.length ];
			for ( int i = 0; i < games.length; i++ ) {
			
				bookCovers[i] = new BookCover( games[i] );
				BookCover bookCover = bookCovers[i];
				bookCover.addMouseListener( new MouseAdapter(){
					@Override
					public void mousePressed( MouseEvent event ) {
						for ( OnBookCoverMouseAdapter listener : onBookCoverMouseListeners ) {
							listener.mousePressed( event, bookCover );
						}
					}
				});
						
				content.add( bookCover );
				revalidate();
				repaint();
			}
		});
		th.start();
		
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
	
	
	public BookCover[] listBookCovers() {
		return bookCovers;
	}
	
	class Content extends JPanel {
		Content() {
			super( new FlowLayout( FlowLayout.CENTER ));
			setBackground( Style.COLOR_SECONDARY );
		}
		@Override
		public void paintComponent( Graphics g ) {  
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			//GradientPaint gradient = new GradientPaint( 0, 0, Style.COLOR_BACKGROUND, 0, getHeight(), getBackground() );
			GradientPaint gradient = new GradientPaint( -getWidth(), -getWidth(), Style.COLOR_BACKGROUND, getWidth(), getWidth(), getBackground() );
			g2d.setPaint(gradient);
			g2d.fillRect( -getWidth(), -getWidth(), getWidth(), getWidth() );
			
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