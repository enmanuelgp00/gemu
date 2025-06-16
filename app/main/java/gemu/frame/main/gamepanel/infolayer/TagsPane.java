package gemu.frame.main.gamepanel.infolayer;

import gemu.frame.tagging.TaggingFrame;
import gemu.game.Game;
import javax.swing.*;
import java.awt.*;    
import java.awt.event.*;   
import gemu.frame.main.gamepanel.GamePanel;

class TagsPane extends JPanel {

		GamePanel gamePanel;
		Game game;
		
		TagsPane( GamePanel gamePanel  ) {
			super( new FlowLayout( FlowLayout.LEFT ) );
			this.gamePanel = gamePanel;
			this.game = gamePanel.getGame();
			
			setOpaque( false );
			setBackground( Color.BLUE );
			refresh();
			setPreferredSize( new Dimension( 143, 73 ));
			setMaximumSize( getPreferredSize() );
		}
		
		public void refresh() {
			removeAll();
			for ( String tag : game.getTags() ) {
				add( new Tag( tag ) {
					{
						addMouseListener( new MouseAdapter() {
							@Override
							public void mouseEntered( MouseEvent e ) {
								setBackground( Color.WHITE );
							}                                  
							@Override
							public void mouseExited( MouseEvent e  ) {  
								setBackground( Color.PINK );
							}
							@Override
							public void mousePressed( MouseEvent e ) {
								if ( SwingUtilities.isLeftMouseButton(e) ) {
									new TaggingFrame( gamePanel );
								}
							}
						});
					}
				} );
			}
			revalidate();
			repaint();
		}
	}