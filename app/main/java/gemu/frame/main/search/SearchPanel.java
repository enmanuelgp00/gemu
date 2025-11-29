package gemu.frame.main.search;

import javax.swing.*;
import java.awt.*;        
import gemu.common.*;

public class SearchPanel extends JPanel {
	public SearchPanel() {
		super();
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		setBorder( null );
		setBackground( Style.COLOR_SECONDARY );  
	
		add( new InputPanel() );   
		add( new ResultPanel() );
	}
}