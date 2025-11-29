package gemu.frame.home.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LibraryPanel extends GemuSplitPane {
	public LibraryPanel() {                
		super( JSplitPane.VERTICAL_SPLIT );
		final Shelf shelf = new Shelf();
		shelf.setMinimumSize( new Dimension(0, 200) );
		final Banner banner = new Banner() {
			@Override
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				int maximumHeight = getMaximumSize().height;
				shelf.setMinimumSize( new Dimension( 0, LibraryPanel.this.getHeight() - maximumHeight));
				if ( maximumHeight < LibraryPanel.this.getDividerLocation() ) {
					LibraryPanel.this.setDividerLocation( maximumHeight );
				}
			}
		};
		setLeftComponent( banner );
		setRightComponent( shelf );
		
		setDividerSize(5);
	}
	
}