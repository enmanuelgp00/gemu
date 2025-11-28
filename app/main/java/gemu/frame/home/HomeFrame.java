package gemu.frame.home;

import javax.swing.*;
import java.awt.*;                  
import java.awt.event.*;
import gemu.common.*;
import gemu.frame.home.search.*;  
import gemu.frame.home.shelf.*;  


public class HomeFrame extends JFrame {
	public HomeFrame() {
		super();
		setUndecorated( true );
		getContentPane().setBackground( Style.COLOR_BACKGROUND );
		((JPanel)getContentPane()).setBorder( BorderFactory.createEmptyBorder(2, 2, 2, 2));
		setSize( 800, 600 );      
		add( new TitleBar( this, "Gemu" ), BorderLayout.NORTH );
		add( new GemuSplitPane( JSplitPane.HORIZONTAL_SPLIT, new SearchPanel(), new LibraryPanel() ));
		addMouseListener( resizeAdapter );
		addMouseMotionListener( resizeAdapter );
		setVisible(true);
	}
	
	MouseAdapter resizeAdapter = new MouseAdapter() {
		Point initialLocation;
		
		boolean mousePressed = false;
		int factor = 10;
		int resizing = 0;
		
		final int top = 7;
		final int topLeft = 8;
		final int left = 1;
		final int buttonLeft = 2;
		final int button = 3;
		final int buttonRight = 4;
		final int right = 5;
		final int topRight = 6;
		
		@Override
		public void mouseExited( MouseEvent event ) { 
			if ( !mousePressed ) {		     
				setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ));	
			}
		}		
		@Override
		public void mousePressed( MouseEvent event ) {
			initialLocation = getLocation();
			mousePressed = true;
		} 
		@Override
		public void mouseReleased( MouseEvent event ) {
			setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR ));	
			mousePressed = false;
			resizing = 0;
		}
		@Override
		public void mouseMoved( MouseEvent event ) { 
			Point mousePos = event.getPoint();
			Dimension currentSize = getSize();
			if ( !mousePressed ) {
				if ( mousePos.x < factor && mousePos.y < currentSize.height - factor ) {
					setCursor( Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR ));
					resizing = left;
					
				} else if ( mousePos.x < factor && mousePos.y > currentSize.height - factor ) { 
					setCursor( Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR ));
					resizing = buttonLeft;
				
				} else if ( mousePos.x > factor && mousePos.x < currentSize.width - factor && mousePos.y > currentSize.height - factor ) { 
					setCursor( Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR ));
					resizing = button;
				
				} else if ( mousePos.x > currentSize.width - factor && mousePos.y > currentSize.height - factor ) {
					setCursor( Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR ));
					resizing = buttonRight;
				
				} else if ( mousePos.x > currentSize.width - factor && mousePos.y < currentSize.height - factor ) {
					setCursor( Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR ));
					resizing = right;
				
				}  else if ( mousePos.x > factor && mousePos.x < currentSize.width - factor && mousePos.y < factor ) {
					setCursor( Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR ));
					resizing = top;
				
				}
			}
		}
		@Override
		public void mouseDragged( MouseEvent event ) {
			Point mousePos = event.getPoint();
			Point currentLocation = getLocation();
			Dimension currentSize = getSize();
			
			Point newlocation = currentLocation;
			Dimension newsize = currentSize;
			
			switch (resizing) {
				case top:
					newlocation = new Point( currentLocation.x, currentLocation.y + mousePos.y );
					newsize = new Dimension( currentSize.width, currentSize.height - mousePos.y );
					
				break;             
				case topLeft:
				break;                
				case left:
				break;                    
				case buttonLeft:
				break;
				case button:
					newsize = new Dimension( currentSize.width, mousePos.y );
				break;
				case buttonRight:
					newsize = new Dimension( mousePos.x, mousePos.y );
				break;
				case right:
					newsize = new Dimension( mousePos.x, currentSize.height );
				break;
				case topRight:
				break;
			}
			
			
			setLocation( newlocation );
			setSize( newsize );
			
			//setLocation( locationX, locationY );
			
		}
	};
}