package gemu.frame.main.search;


import gemu.common.*;
import java.awt.*;          
import java.awt.event.*;
import javax.swing.*; 
import java.util.*;
import gemu.game.*; 
import gemu.frame.main.shelf.*;


public class ResultPanel extends GemuScrollPane {
	ArrayList<OnResultComponentMouseAdapter> listeners = new ArrayList<>();
	JPanel panel;
	public ResultPanel() {
		super();
		panel = new JPanel();  
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ));
		panel.setBackground( Style.COLOR_SECONDARY );
		setViewportView( panel );
		
	}
	
	public void addResultComponentMouseListener( OnResultComponentMouseAdapter listener ) {
		listeners.add( listener );
	}
	
	public void show( BookCover[] bookCovers ) {
		panel.removeAll();
		for ( BookCover bookCover : bookCovers ) {
			ResultComponent rcomponent = new ResultComponent( bookCover );
			rcomponent.addMouseListener( new MouseAdapter() {
				@Override
				public void mousePressed( MouseEvent event ) {
					for ( OnResultComponentMouseAdapter listener : listeners ) {
						listener.mousePressed( rcomponent, event );
					}
				}
			});
			panel.add( rcomponent );
		}
		panel.revalidate();
		panel.repaint();
	}
	
	public void clear() {
		panel.removeAll();  
		panel.revalidate();
		panel.repaint();
		
	}
}