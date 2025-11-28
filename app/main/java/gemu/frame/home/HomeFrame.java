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
		((JPanel)getContentPane()).setBorder( BorderFactory.createEmptyBorder(0, 3, 3, 3));
		setSize( 800, 600 );      
		add( new TitleBar( this, "Gemu" ), BorderLayout.NORTH );
		add( new GemuSplitPane( JSplitPane.HORIZONTAL_SPLIT, new SearchPanel(), new LibraryPanel() ));
		addMouseListener( resizeAdapter );
		addMouseMotionListener( resizeAdapter );
		setVisible(true);
	}
	
	MouseAdapter resizeAdapter = new MouseAdapter() {
		Point initialLocation;
		int factor = 10;
		int resizing = 0;
		boolean mousePressed = false;
		final int left = 1;
		final int buttonLeft = 2;
		final int button = 3;
		final int buttonRight = 4;
		final int right = 5;
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
				
				}
			}
		}
		@Override
		public void mouseDragged( MouseEvent event ) {
			Point mousePos = event.getPoint();
			Dimension currentSize = getSize();
			
			switch (resizing) {
				case buttonRight:  
					setSize(event.getX(), event.getY());
				break;
			}
			
			//setLocation( locationX, locationY );
			
		}
	};
}