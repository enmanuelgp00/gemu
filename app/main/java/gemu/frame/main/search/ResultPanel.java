package gemu.frame.main.search;


import gemu.common.*;
import java.awt.*;
import javax.swing.*;


public class ResultPanel extends GemuScrollPane {
	public ResultPanel() {
		super();
		JPanel panel = new JPanel();  
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ));
		panel.setBackground( Style.COLOR_SECONDARY );
		setViewportView( panel );
		
	}
}