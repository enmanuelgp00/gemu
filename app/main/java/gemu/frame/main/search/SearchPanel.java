package gemu.frame.main.search;

import javax.swing.*;
import java.awt.*;        
import gemu.common.*;
import gemu.game.*;

public class SearchPanel extends JPanel {
	
	InputPanel inputPanel;
	ResultPanel resultPanel;
	
	public SearchPanel( Game[] games ) {
		super();
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		setBorder( null );
		setBackground( Style.COLOR_SECONDARY );  
	
		resultPanel = new ResultPanel();         
		inputPanel = new InputPanel( games, resultPanel );
		
		add( inputPanel );   
		add( resultPanel );
		
	}
	
	public void addResultElementMouseListener( OnResultElementMouseAdapter listener ) {
		resultPanel.addResultElementMouseListener( listener );
	}
}