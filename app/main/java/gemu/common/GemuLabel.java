package gemu.common;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GemuLabel extends JLabel {
	public GemuLabel( String name ) {
		super( name );
		setFont( Style.FONT_LABEL );
		setForeground( Style.COLOR_FOREGROUND );
	}
}