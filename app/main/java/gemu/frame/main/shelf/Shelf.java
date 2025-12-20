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
	LinkedList<BookCover> bookCoversToUpdateBuffer = new LinkedList<>();
	boolean bufferUpdaterRunning = false;
	Shelf( Game[] games ) {
		super();
		setFocusable( true );
		Content content = new Content();
		setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) ); 
		setViewportView( content );
		addOnBookCoverMouseAdapter( draggingScroll );
		Thread th = new Thread(()->{
			bookCovers = new BookCover[ games.length ];
			for ( int i = 0; i < games.length; i++ ) {
				
				bookCovers[i] = new BookCover( games[i] );
				BookCover bookCover = bookCovers[i];
				bookCover.addMouseListener( createDedicatedMouseListener( bookCover ) );   
				bookCover.addMouseMotionListener( createDedicatedMouseListener( bookCover ) );
						
				content.add( bookCover );
				revalidate();
				repaint();
			}
		});
		th.start();
		getVerticalScrollBar().addAdjustmentListener( ( event ) -> {
			JViewport viewport = getViewport();
			int top = viewport.getViewPosition().y;
			int bottom = top + getHeight();
			Rectangle bounds;
			int bookTop;
			int bookBottom;
			
			for ( BookCover bookCover : bookCovers ) {
				if ( bookCover == null ) {
					continue;
				}
				bounds = bookCover.getBounds();
				bookTop = bounds.y;
				bookBottom = bookTop + bounds.height;
				int hgap = ((FlowLayout)content.getLayout()).getHgap();
				if ( top - ( bounds.height + hgap ) * 2 > bookBottom || bookTop > bottom + ( bounds.height + hgap ) * 2 ) {
					bookCoversToUpdateBuffer.remove(bookCover);
					bookCover.flushBufferedImage();
					
				} else if ( bookCover.getBufferedImage() == null && !bookCover.isLoadingBufferedImage() ) {
					
					if ( !bookCoversToUpdateBuffer.contains(bookCover) ) {
						bookCoversToUpdateBuffer.add( bookCover );					
					}
					Thread updateBufferedImageTh = new Thread(()->{
						BookCover bc;
						while( bookCoversToUpdateBuffer.size() > 0 ) {
								bc = bookCoversToUpdateBuffer.get(0);  
							if ( bc.getBufferedImage() == null ) {
								bc.updateBufferedImage();
								bc.repaint(); 
							}							
							bookCoversToUpdateBuffer.remove(bc);
							try {
								Thread.sleep( 100 );
							} catch( Exception e ) {}
							
						}
						
						setBufferUpdaterRunning( false );
					});
					if ( !isBufferUpdaterRunning() && bookCoversToUpdateBuffer.size() > 0 ) {
						updateBufferedImageTh.start(); 
						setBufferUpdaterRunning( true );
						
					} 
					
					
				}
				
			}
			System.gc();
		});
		addMouseListener( new MouseAdapter(){
			@Override
			public void mousePressed( MouseEvent e ) {
				requestFocusInWindow();
			}
		});
	}
	public boolean isBufferUpdaterRunning() {
		return bufferUpdaterRunning;
	}
	private void setBufferUpdaterRunning( boolean bool ) {
		bufferUpdaterRunning = bool;
	}
	public void scrollTo( BookCover bookCover ) {
		
		Rectangle  bookBounds = bookCover.getBounds();
		JViewport viewport = getViewport();
		
		if ( viewport.getViewPosition().y > bookBounds.y ) {
			viewport.setViewPosition( new Point( 0, bookBounds.y ));
		} else if ( viewport.getViewPosition().y + getHeight() < bookBounds.y + bookBounds.height ) {
			viewport.setViewPosition( new Point( 0, bookBounds.y - getHeight() + bookBounds.height ));
		}
	}
	
	public void addOnBookCoverMouseAdapter( OnBookCoverMouseAdapter listener ) {
		onBookCoverMouseListeners.add( listener );
	}
	
	static abstract class OnBookCoverMouseAdapter {                      
		void mouseClicked( MouseEvent event, BookCover cover ) { }    
		void mouseEntered( MouseEvent event, BookCover cover ) { } 
		void mouseExited( MouseEvent event, BookCover cover ) { } 
		void mousePressed( MouseEvent event, BookCover cover ) { } 
		void mouseReleased( MouseEvent event, BookCover cover ) { }
		void mouseMoved( MouseEvent event, BookCover cover ) { }
		void mouseDragged( MouseEvent event, BookCover cover ) { }
	}
	
	
	public BookCover[] listBookCovers() {
		return bookCovers;
	}
	
	OnBookCoverMouseAdapter draggingScroll = new OnBookCoverMouseAdapter() {
		Point initialMousePosition;    
		@Override
		public void mousePressed( MouseEvent event, BookCover bookCover ) {
			initialMousePosition = event.getPoint();
		} 
		@Override
		public void mouseDragged( MouseEvent event, BookCover bookCover ) {
			if ( (event.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) != 0 ) { 
				JViewport viewport = Shelf.this.getViewport();
				Point currentPoint = event.getPoint();
				Point mouseMovement = new Point(
					initialMousePosition.x - currentPoint.x,
					initialMousePosition.y - currentPoint.y	);
				Point movement =  new Point( 0,  viewport.getViewPosition().y + mouseMovement.y );
				if ( movement.y < 0 || movement.y > viewport.getView().getHeight() - getHeight() ) {
					initialMousePosition = currentPoint;
					return;
				}
				viewport.setViewPosition( movement );
			}
		}
	};
	
	MouseAdapter createDedicatedMouseListener( BookCover bookCover ) {
		return new MouseAdapter(){          
			@Override
			public void mouseClicked( MouseEvent event ) {
				onBookCoverMouseListeners.forEach( listener -> listener.mouseClicked( event, bookCover ) );
			}   
			@Override
			public void mouseEntered( MouseEvent event ) {
				onBookCoverMouseListeners.forEach( listener -> listener.mouseEntered( event, bookCover ) );
			}   
			@Override
			public void mouseExited( MouseEvent event ) {
				onBookCoverMouseListeners.forEach( listener -> listener.mouseExited( event, bookCover ) );
			}
			@Override
			public void mousePressed( MouseEvent event ) {
				for ( OnBookCoverMouseAdapter listener : onBookCoverMouseListeners ) {
					listener.mousePressed( event, bookCover );
				}
			}
			@Override
			public void mouseReleased( MouseEvent event ) {
				for ( OnBookCoverMouseAdapter listener : onBookCoverMouseListeners ) {
					listener.mouseReleased( event, bookCover );
				}
			}
			@Override
			public void mouseMoved( MouseEvent event ) {
				for ( OnBookCoverMouseAdapter listener : onBookCoverMouseListeners ) {
					listener.mouseMoved( event, bookCover );
				}
			}  
			@Override
			public void mouseDragged( MouseEvent event ) {
				for ( OnBookCoverMouseAdapter listener : onBookCoverMouseListeners ) {
					listener.mouseDragged( event, bookCover );
				}
			}
		};
	} 
	
	class Content extends JPanel {
		Content() {
			super( new FlowLayout( FlowLayout.LEFT, 5, 5 ));
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
			Component[] components = getComponents();
			Rectangle bounds = components[0].getBounds();
			int containerWidth = getWidth();
			int minimumGap = 5;
			int spaceOcupated = 0;
			int count = 1;
			while ( spaceOcupated + minimumGap + bounds.width < containerWidth ) {
				count++;
				spaceOcupated += minimumGap + bounds.width;
			}
			int hMargin = ( containerWidth - spaceOcupated) / count + minimumGap;
			FlowLayout layout = (FlowLayout)getLayout();
			layout.setHgap( hMargin );
			if ( layout != null ) {
				layout.layoutContainer( this );
			}
			int width = 0;
			int height = 0;
			
			if ( components.length > 0 ) {
				bounds = components[ components.length - 1 ].getBounds();
				width = bounds.width;
				height = bounds.y + bounds.height;
			}
			Insets insets = getInsets();
			int gap = ((FlowLayout)getLayout()).getVgap();
			return new Dimension( width + insets.left + insets.right, height + insets.top + insets.bottom  + gap );  
		}
		
	}
	
	
}