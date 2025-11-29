package gemu.common;

import java.awt.*;
import javax.swing.plaf.basic.*;
import javax.swing.*;

public class GemuScrollPane extends JScrollPane {
	public GemuScrollPane() {
		super();
		setBackground( Style.COLOR_SECONDARY );
		setBorder( BorderFactory.createEmptyBorder(5, 5, 5, 5) );
		final JScrollBar horizontalScroll = getHorizontalScrollBar();     
		final JScrollBar verticalScroll = getVerticalScrollBar();
		horizontalScroll.setUI( createBasicScrollBarUI() );  
		horizontalScroll.setUnitIncrement( 20 );
		verticalScroll.setUI( createBasicScrollBarUI() );     
		verticalScroll.setUnitIncrement( 20);
		
	}
	
	public BasicScrollBarUI createBasicScrollBarUI() {
		return new BasicScrollBarUI() {
			{
				setOpaque( false );
				setBackground( null );
				JScrollBar scrollbar;
				for ( Component c : getComponents() ) {
					if ( c instanceof JScrollBar ) {
						scrollbar = (JScrollBar)c; 
						if ( scrollbar.getOrientation() == JScrollBar.VERTICAL ) {
							scrollbar.setPreferredSize(new Dimension( 9, scrollbar.getPreferredSize().height ));						
						} else {
							scrollbar.setPreferredSize(new Dimension( scrollbar.getPreferredSize().width , 9 ));
						}
						scrollbar.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
						scrollbar.setBackground( null );
						scrollbar.setOpaque( false );
						
					}
				}
				
			}
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
				//c.setPreferredSize( );
				if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
					return ;
				}
				
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor( Style.COLOR_SECONDARY.brighter());
				g2d.fillRoundRect( thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 6, 6 );
				g2d.dispose();
			}
			@Override
			public void paintTrack( Graphics g, JComponent c, Rectangle trackBounds) {
				
			}
		};
	}
}



		