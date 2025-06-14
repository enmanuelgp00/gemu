package gemu.frame.main.gamepanel.infolayer;

import gemu.game.Game;
import javax.swing.*;
import java.awt.*;

class TagsPane extends JPanel {
		Game game;
		TagsPane( Game game ) {
			super( new FlowLayout( FlowLayout.LEFT ) );
			
			setOpaque( false );
			setBackground( Color.BLUE );
			
			this.game = game;
			update();
			setPreferredSize( new Dimension( 143, 50 ));
			setMaximumSize( getPreferredSize() );
		}
		/*
		@Override
		public Dimension getPreferredSize () {
			LayoutManager layout = getLayout();
			if ( layout != null ) {
				layout.layoutContainer( this );
			}
			
			int width = 0;
			int height = 0;
			
			Component[] components = getComponents();
			
			if ( components.length > 0 ) {  
				Rectangle bounds = components[ components.length - 1 ].getBounds();
				width = bounds.width + bounds.x;
				height = bounds.height + bounds.y;
			}
			Insets insets = getInsets();
			
			return new Dimension( width + insets.left + insets.right , height + insets.top + insets.bottom + 5 );
		}
		*/
		public void update() {
			removeAll();
			for ( String tag : game.getTags() ) {
				add( new Tag( tag ) );
			}
		}
	}