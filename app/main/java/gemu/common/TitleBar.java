package gemu.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TitleBar extends JPanel {
	JLabel labelTitle;
	JFrame frame;
	final char minimize = '―';
	final char maximize = '+';
	final char close = 'x';
	
	char[] buttonTitles = new char[] {
		minimize,
		close 
	};
	
	public TitleBar( JFrame frame, String title ) {
		super();
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS ));
		this.frame = frame;
		labelTitle = new JLabel( title );
		
		labelTitle.setForeground( Style.COLOR_FOREGROUND );
		labelTitle.setFont( Style.FONT_TITLE_BAR );
		labelTitle.setBorder( BorderFactory.createEmptyBorder( 3, 7, 3, 3 ) );
		setBackground( Style.COLOR_BACKGROUND );
		
		add( labelTitle );
		add( Box.createHorizontalGlue() );
		
		addMouseMotionListener( moveWindow ); 
		addMouseListener( moveWindow );
		
		ActionListener actionListener = null;
		GemuButton button;
		
		for( char tl : buttonTitles ) {
			button = new GemuButton( String.valueOf(tl));
			switch(tl) {
				case minimize :
					actionListener = actionMinimize;
				break;  
				case maximize :                     
					actionListener = actionMaximize;
				break;
				case close :                        
					actionListener = actionCloseWindow; 
					button.setRolloverBackground( Color.RED );
					button.setPressedBackground( button.getRolloverBackground().darker() );
					button.setPressedForeground( Color.BLACK );
				break;
			}
			button.addActionListener( actionListener );
			add( button );
		}
		
	}
	
	
	ActionListener actionMinimize = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent event ) {
			frame.setState( JFrame.ICONIFIED );			
		}
	};
	
	ActionListener actionMaximize = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent event ) {   
			
		}
	};
	
	ActionListener actionCloseWindow = new ActionListener() {
		@Override
		public void actionPerformed( ActionEvent event ) {
			System.exit(0);
		}
	};
	
	MouseAdapter moveWindow = new MouseAdapter() {
		Point initialMousePos;
		@Override
		public void mousePressed( MouseEvent event ) {
			initialMousePos = event.getPoint();
		}
		
		@Override
		public void mouseDragged( MouseEvent event ) {
			Point newMousePos = event.getPoint();
			int x = (newMousePos.x - initialMousePos.x) + frame.getLocation().x;
			int y = (newMousePos.y - initialMousePos.y) + frame.getLocation().y;
			frame.setLocation(x , y );
		}
	};

}