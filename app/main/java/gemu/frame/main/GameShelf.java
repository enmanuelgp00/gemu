package gemu.frame.main;

import gemu.game.Game;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.util.List;

public class GameShelf extends JScrollPane {
	List<Game> gamels;
	GameShelf( List<Game> gamels ) {
		super();
		this.gamels = gamels;
		setViewportView( new Body() );
		setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
	}
	
	class Body extends JPanel {
		Body() {
			super( new FlowLayout( FlowLayout.CENTER ) );
			for ( Game g : gamels ) {
				setBackground( Color.PINK );
				add( new GamePanel(g) );
			}
		}
		
		@Override
		public Dimension getPreferredSize() {
			LayoutManager layout = getLayout();
			if ( layout != null ) {
				layout.layoutContainer( this );
			}
			
			int width = 0;
			int height = 0;
			
			Component[] components = getComponents();
			Component lastComponent = components[ components.length - 1 ];
			
			Rectangle bounds = lastComponent.getBounds();
			height = bounds.y + bounds.height;
			width = bounds.x + bounds.width;
			
			Insets insets = getInsets();
			
			return new Dimension( width + insets.left + insets.right, 
			height + insets.top + insets.bottom + 5 );
		}
	}
}