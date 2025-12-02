package gemu.frame.main.search;


import gemu.common.*;
import java.awt.*;          
import java.awt.event.*;
import javax.swing.*; 
import java.util.*;
import gemu.game.*;


public class ResultPanel extends GemuScrollPane {
	ArrayList<OnResultElementMouseAdapter> listeners = new ArrayList<>();
	JPanel panel;
	public ResultPanel() {
		super();
		panel = new JPanel();  
		panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ));
		panel.setBackground( Style.COLOR_SECONDARY );
		setViewportView( panel );
		
	}
	
	public void addResultElementMouseListener( OnResultElementMouseAdapter listener ) {
		listeners.add( listener );
	}
	
	public void show( Game[] games ) {
		panel.removeAll();
		for ( Game g : games) {
			ResultElement element = new ResultElement(g);
			element.addMouseListener( new MouseAdapter() {
				@Override
				public void mousePressed( MouseEvent event ) {
					for ( OnResultElementMouseAdapter listener : listeners ) {
						listener.mousePressed( element, event );
					}
				}
			});
			panel.add( element );
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