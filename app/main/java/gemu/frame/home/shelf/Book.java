package gemu.frame.home.shelf;

import java.awt.*;
import javax.swing.*;
import gemu.common.*;

class Book extends JPanel {
	Book() {
		super();
		setPreferredSize( new Dimension( 110, 140 ));
		setBackground( Style.COLOR_SECONDARY.brighter() );
	}
}