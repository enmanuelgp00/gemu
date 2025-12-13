package gemu.frame;

import gemu.common.*;
import java.awt.*;   
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ConfirmationFrame extends JFrame {
	public ConfirmationFrame( OnAnswerListener listener ) {
		super();
		setUndecorated( true );
		getContentPane().setBackground( Style.COLOR_BACKGROUND );
		setSize( 200, 50 );
		Box layout = new Box( BoxLayout.X_AXIS );
		
		GemuButton acceptButton = new GemuButton("ACCEPT");                 
		acceptButton.addActionListener((ActionEvent)->{
			dispose();
			listener.answer( true );
		});
		
		GemuButton cancelButton = new GemuButton("CANCEL");                 
		cancelButton.addActionListener((ActionEvent)->{
			dispose();
			listener.answer( false );
		});
		
		layout.setBorder( BorderFactory.createEmptyBorder( 7, 7, 7, 7 ));
		layout.add( acceptButton );                              
		layout.add( Box.createHorizontalGlue() );              
		layout.add( cancelButton );
		add( layout );
		setLocationRelativeTo( null );
		setVisible( true );
	}
	
	@FunctionalInterface
	public interface OnAnswerListener {
		public void answer( boolean bool );
	}
}