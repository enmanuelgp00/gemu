package gemu.frame.main.gameshelf;

import gemu.frame.main.gamepanel.GamePanel;
import gemu.game.Game;
import javax.swing.JPanel;
import java.awt.*;
import java.util.List;
import java.util.Set;  
import gemu.frame.main.gamepanel.GamePanel;

class GameListPane extends JPanel {
		GameListPane() {
			super( new FlowLayout( FlowLayout.CENTER ) );
		}
		
		void setList( List<Game> list ) {
			if ( getComponents().length > 0 ) {
				removeAll();
			}
			
			for ( Game g : list ) {
				add( new GamePanel( g ));
			}
			
			revalidate();
			repaint();
		}
		
		void setList( Set<Game> list ) {
			if ( getComponents().length > 0 ) {
				removeAll();
			}
			
			for ( Game g : list ) {
				add( new GamePanel( g ));
			}
			
			revalidate();
			repaint();
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
			/*
			for ( Component c : components ) {
				Rectangle bounds = c.getBounds();
				System.out.println( bounds.x + " " + bounds.y );
			}
			*/
			if ( components.length > 0 ) {
				Component lastComponent = components[ components.length - 1 ];
				
				Rectangle bounds = lastComponent.getBounds();
				height = bounds.y + bounds.height;
				width = bounds.x + bounds.width;
			
			}
			
			Insets insets = getInsets();
			
			return new Dimension( width + insets.left + insets.right, 
			height + insets.top + insets.bottom + 5 );
		}
	}