package gemu.frame.home.search;


import gemu.common.*;
import java.awt.*;
import javax.swing.*;


public class ResultPanel extends JScrollPane {
	public ResultPanel() {
		super();
		JPanel panel = new JPanel();
		panel.setBackground( Style.COLOR_SECONDARY );
		setViewportView( panel );
		setBackground( null );
		setBorder( null );
	}
}