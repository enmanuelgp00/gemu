package gemu.common;

import javax.swing.*;
import javax.swing.plaf.basic.*;
import java.awt.*;


public class GemuSplitPane extends JSplitPane {
	public GemuSplitPane( int orientation, Component component0, Component component1 ) {
		super( orientation, component0, component1 );
		setUI( new BasicSplitPaneUI() {
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider( this ) {
					@Override
					public void paint( Graphics g ) {
						g.setColor( Style.COLOR_BACKGROUND );
						g.fillRect( 0,0,getWidth() , getHeight());
					}
					
				};
			}
		}); 
		setBackground( null );  
		setBorder( null );
		setOpaque( false );
		setContinuousLayout( true );
		setDividerSize(4);
	}
}
		/*

		new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, new SearchPanel(), new LibraryPanel()) {
			{
				setBackground( null );
				setOpaque( false );
				setBorder( null );
			}
		};		
		splitPane.setContinuousLayout( true );
		splitPane.setUI( new BasicSplitPaneUI() {
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				return new BasicSplitPaneDivider( this ) {
					@Override
					public void paint( Graphics g ) {
						g.setColor( Style.COLOR_BACKGROUND );
						g.fillRect( 0,0,getWidth() , getHeight());
					}
					
				};
			}
		});    
		splitPane.setBorder( null );
		splitPane.setDividerSize(4);
		*/