package gemu.frame.home.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LibraryPanel extends GemuSplitPane {
	public LibraryPanel() {                  
		super( JSplitPane.VERTICAL_SPLIT, new Banner(), new Shelf() );
		
		
		
		setDividerSize(5);
	}
}