package gemu.frame.main.search;

import javax.swing.*;
import java.awt.*;        
import gemu.common.*;
import gemu.game.*;
import gemu.frame.main.shelf.*;

public class SearchPanel extends JPanel {
	
	InputPanel inputPanel;
	ResultPanel resultPanel;
	
	public SearchPanel( Shelf shelf ) {
		super();
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		setBorder( null );
		setBackground( Style.COLOR_SECONDARY );  
	
		resultPanel = new ResultPanel();         
		inputPanel = new InputPanel( shelf, resultPanel );
		
		add( inputPanel );   
		add( resultPanel );
		
	}
	
	public void addResultElementMouseListener( OnResultComponentMouseAdapter listener ) {
		resultPanel.addResultElementMouseListener( listener );
	}
}