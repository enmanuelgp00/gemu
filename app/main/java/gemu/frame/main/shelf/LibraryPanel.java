package gemu.frame.main.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LibraryPanel extends GemuSplitPane {
	public LibraryPanel() {                
		super( JSplitPane.VERTICAL_SPLIT );
		final Shelf shelf = new Shelf();
		final Banner banner = new Banner() {
			@Override
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				int bannerMaximumHeight = getMaximumSize().height;
				int shelfMinimumHeight = LibraryPanel.this.getHeight() - bannerMaximumHeight; 
				if ( shelfMinimumHeight < shelf.getToolsBar().getHeight() ) {
					shelfMinimumHeight = shelf.getToolsBar().getHeight();
				}
				shelf.setMinimumSize( new Dimension( 0, shelfMinimumHeight));
				if ( bannerMaximumHeight < LibraryPanel.this.getDividerLocation() ) {
					LibraryPanel.this.setDividerLocation( bannerMaximumHeight );
				}
			}
		};
		setLeftComponent( banner );
		setRightComponent( shelf );
		
		setDividerSize(5);
	}
	
}