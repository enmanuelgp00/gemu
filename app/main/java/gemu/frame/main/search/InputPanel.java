package gemu.frame.main.search;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;      
import java.util.*;  
import gemu.common.*;
import gemu.game.*;

public class InputPanel extends Box {
	ResultPanel resultPanel;
	Game[] games;
	InputPanel( Game[] games, ResultPanel resultPanel ) {
		super( BoxLayout.X_AXIS );
		this.games = games;
		this.resultPanel = resultPanel;
		setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ));
		TextReference textReference = new TextReference();
		GemuButton tagsHint = new GemuButton("?");
		textReference.setMaximumSize( new Dimension( 1000, (int)tagsHint.getPreferredSize().getHeight() ) );
		textReference.setMinimumSize( new Dimension( 140, 1 ));
		add( textReference );
		add( new GemuButton("?")); 
		
	}
	
	class TextReference extends JTextField {
		boolean mouseInside = false;
		boolean runningTransition = false;
		Color placeholderColor;
		TextReference() {
			super();
			setFont( Style.FONT_LABEL );
			setForeground( Style.COLOR_FOREGROUND );
			setBackground( Style.COLOR_BACKGROUND );
			setCaretColor( Style.COLOR_FOREGROUND );
			setBorder( BorderFactory.createEmptyBorder( 0, 7, 0, 7 ));
			placeholderColor = getBackground();
			addKeyListener( new KeyAdapter() {
				@Override
				public void keyReleased( KeyEvent event ) {
					if ( getText().isEmpty() ) {
						resultPanel.clear();
						return;
					}
					ArrayList<Game> search = new ArrayList<>();
					
					for( Game g : games ) {
						if (g.getTitle().toLowerCase().contains(getText().toLowerCase())) {
							search.add(g);
						}
					}
					resultPanel.show(search.toArray( new Game[search.size()]));
				}
			} );
			addFocusListener( new FocusAdapter() {
				@Override
				public void focusGained( FocusEvent e ) {
					setPlaceholderColor( Color.GRAY );
					repaint();
				}
				@Override
				public void focusLost( FocusEvent e) {
					setPlaceholderColor( getBackground() );
					repaint();
				}
			});
			
			addMouseListener( new MouseAdapter(){
				@Override
				public void mouseEntered( MouseEvent event ) {
					setMouseInside( true );
					if ( !hasFocus() ) {
						placeholderTransition();
					}
					
				}
				@Override
				public void mouseExited( MouseEvent event ) {
					setMouseInside( false );
					repaint();
					
				}
			});
		}
		
		public void setMouseInside( boolean v ) {
			mouseInside = v;
		}
		
		public boolean isRunningTransition() {
			return runningTransition;
		}
		
		public void setRunningTransition( boolean boo) {
			runningTransition = boo;
		}
		
		public void placeholderTransition() {
			Thread th = new Thread(()->{
				
				try {
					
					int count = 0;
					while( count < 4 ) {
						setPlaceholderColor( getPlaceholderColor().brighter() ); 
						repaint();
						Thread.sleep(70);
						count++;
					}
				} catch ( Exception e) {
					e.printStackTrace();
				}
				if ( !hasFocus()) {
					setPlaceholderColor( getBackground() );				
				}
				setRunningTransition( false );
			});
			if ( !isRunningTransition() ) {
				setRunningTransition( true );
				th.start();
			}
		}
		public Color getPlaceholderColor() {
			return placeholderColor;
		}
		public void setPlaceholderColor( Color color ) {
			placeholderColor = color;
		}
		
		@Override
		public void paintComponent( Graphics g ) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;
			
			if ( getText().isEmpty() && ( mouseInside || hasFocus() ) ) {
				/*
				GradientPaint gradientPaint = new GradientPaint( 0, 0, Color.BLACK, getWidth(), getHeight(), getBackground() );
				g2d.setPaint( gradientPaint );
				g2d.fillRect(0, 0, getWidth(), getHeight() );
				*/
				g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2d.setColor( placeholderColor );
				g2d.setFont( getFont().deriveFont( Font.ITALIC ));
				FontMetrics fm = g2d.getFontMetrics();
				int y = ( getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2d.drawString( "Search by name", getInsets().left, y );
				
				
			}
			g2d.dispose();
		}
	}
}
