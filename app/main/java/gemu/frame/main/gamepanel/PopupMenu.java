package gemu.frame.main.gamepanel;

import gemu.game.Game;
import gemu.system.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PopupMenu extends JPopupMenu { 
		JMenuItem play;
		JMenuItem compress;
		JMenuItem decompress;
		JMenuItem openFolder;
		
		GamePanel gamePanel;
		
		PopupMenu( GamePanel gamePanel ) {
			super();			
			
			this.gamePanel = gamePanel;
			
			JMenuItem[] items = new JMenuItem[] {				   
				play = new JMenuItem("Play Game"),
				compress = new JMenuItem("Compress"),
				decompress = new JMenuItem("Extract"),
				openFolder = new JMenuItem("Open Folder")
			};
			
			for ( JMenuItem item : items ) {
				add( item);
			}
			
			play.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					gamePanel.getGame().play();
				}
			});
			
			compress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					gamePanel.setBackground( Color.BLUE );
					gamePanel.getGame().compress( new OnSuccessListener() {
						@Override
						public void onSuccess() {
							gamePanel.setBackground( Color.GRAY );
							gamePanel.updateFileLength();
						} 
						@Override
						public void onError() { 
							gamePanel.setBackground( Color.RED );							
						}
					} );
				}
			});
			
			decompress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					gamePanel.setBackground( Color.BLUE );
					gamePanel.getGame().decompress( new OnSuccessListener() {
						@Override
						public void onSuccess() {
							gamePanel.setBackground( Color.GREEN );
							gamePanel.updateFileLength();
						}
						@Override
						public void onError() {
							gamePanel.setBackground( Color.RED );
						}
					} );
				}
			});
			
			openFolder.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					gamePanel.getGame().openFolder();
				}
			});
		}
		
		@Override
		public void show( Component c, int x, int y ) {
			super.show( c, x, y );
			if ( gamePanel.getGame().getCompressionState() == Game.COMPRESSION_STATE_FREE ) { 
			
				if ( gamePanel.getGame().isCompressed() ) {
					play.setEnabled( false );
					compress.setEnabled( false );
					decompress.setEnabled( true );
				} else {                        
					play.setEnabled( true );
					compress.setEnabled( true );
					decompress.setEnabled( false );
				}
			} else {
				play.setEnabled( false );
				compress.setEnabled( false );
				decompress.setEnabled( false );
			}
		}
	}