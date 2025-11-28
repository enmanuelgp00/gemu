package gemu.frame.home.search;


import gemu.common.*;
import java.awt.*;
import javax.swing.*;


public class ResultPanel extends GemuScrollPane {
	public ResultPanel() {
		super();
		JPanel panel = new JPanel();  
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ));
		for ( int i = 0; i < 100; i++ ) {
			panel.add( new GemuButton( "title " + String.valueOf(i)));
		}
		panel.setBackground( Style.COLOR_SECONDARY );
		setViewportView( panel );
		
	}
}