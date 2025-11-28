package gemu.frame.home.shelf;

import gemu.common.*;
import java.awt.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

public class Shelf extends JScrollPane {
	Shelf() {
		super();
		JPanel content = new JPanel();
		content.setBackground( Style.COLOR_SECONDARY ); 
		setViewportView(content);
		setBackground( Style.COLOR_SECONDARY );
		setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) );
		getHorizontalScrollBar().setUI( createBasicScrollBarUI() );  
		getVerticalScrollBar().setUI( createBasicScrollBarUI() ); 
		
		for ( int i = 0; i < 10; i ++ ) {
			content.add( new Book());
		}
		
	}
	
	
		public BasicScrollBarUI createBasicScrollBarUI() {
			return new BasicScrollBarUI() {
				JButton zeroButton = new JButton() {
					{
						setPreferredSize( new Dimension( 0, 0 ));
					}
				};
				/*
				@Override
				protected Dimension getThumbSize() {
					if ( scrollbar.getOrientation() == JScrollBar.VERTICAL ) {
						return new Dimension( 10, 10 );
					}
					return new Dimension( 20, 4 );
				}
				*/
				@Override
				public JButton createDecreaseButton( int orientation) {
					return zeroButton;
				}   
				@Override
				public JButton createIncreaseButton( int orientation) {
					return zeroButton;
				}
				@Override
				public void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds ) {
					if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
						return ;
					}
					
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.setColor( Style.COLOR_SECONDARY.brighter());
					g2d.fillRoundRect( thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 20, 20 );
					g2d.dispose();
				}
				@Override
				public void paintTrack( Graphics g, JComponent c, Rectangle trackBounds) {
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setColor( Style.COLOR_SECONDARY);
					g2d.fillRect( trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height );
					g2d.dispose();
					
					
				}
			};
		}
}