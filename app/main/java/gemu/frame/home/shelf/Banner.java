package gemu.frame.home.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Banner extends JPanel {
	String[] buttonNameSet = new String[]{  "Open Folder", "Compress", "Delete"  };
	Banner() {
		super();
		//setLayout(new OverlayLayout( this ));
		setBackground( Color.PINK );
		
		//setMinimumSize( new Dimension( 300, 300 ));  
		//setMaximumSize( new Dimension( 9000, 300 ));
		//setPreferredSize( new Dimension( 300, 300 ));
		
		//add( new JButton() );
		//add( new TagsPanel() );
	}
	
}