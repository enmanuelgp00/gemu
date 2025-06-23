package gemu.frame.main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class MessageBox extends JFrame {
	
	JLabel lblmessage;
	JPanel plbuttons;
	
	public MessageBox( String title, String message ) {
		super();
		lblmessage = new JLabel( "<html><p style=text-align:center; >" + message + "</p></html>" );
		lblmessage.setBackground( Color.PINK );
		lblmessage.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2) );
		plbuttons = new JPanel();
		plbuttons.add( new JButton("Accept") {
			{
				addActionListener( new ActionListener() {
				
					@Override
					public void actionPerformed( ActionEvent ev ) {
						onAccept( MessageBox.this );
						dispose();
					}
				});
			}
		});
		plbuttons.add( new JButton("Cancel") {
			{
				addActionListener( new ActionListener()  {
				
					@Override
					public void actionPerformed( ActionEvent ev ) {
						onCancel( MessageBox.this );
						dispose();
					}
				});
			}
		});
		add( lblmessage );
		add( plbuttons, BorderLayout.SOUTH );
		setTitle( title );
		setSize( 300, 150 );
		setResizable( false );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setLocationRelativeTo( null );
		setVisible( true );
	}
	
	
	public abstract void onAccept( MessageBox messagebox );	
	public void onCancel( MessageBox messagebox ) { };

}