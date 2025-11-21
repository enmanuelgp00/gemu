package gemu.frame.main.gamepanel;

import gemu.frame.main.MessageBox;
import gemu.frame.main.SearchGamePanel;
import gemu.frame.tagging.TaggingFrame;
import gemu.game.Game;
import gemu.game.Games;
import gemu.system.*;
import gemu.system.event.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class PopupMenu extends JPopupMenu { 
		JMenuItem play;
		JMenuItem compress;
		JMenuItem decompress;
		JMenuItem openFolder;
		JMenuItem addTags;
		JMenuItem delete;
		
		GamePanel gamePanel;
		
		PopupMenu( GamePanel gamePanel, SearchGamePanel searchGamePanel ) {
			super();			
			
			this.gamePanel = gamePanel;
			
			JMenuItem[] items = new JMenuItem[] {				   
				play = new JMenuItem("Play Game"),
				compress = new JMenuItem("Compress"),
				decompress = new JMenuItem("Extract"),
				openFolder = new JMenuItem("Open Folder"),
				addTags = new JMenuItem("Tagging"),
				delete = new JMenuItem("Delete Game")
			};
			
			for ( JMenuItem item : items ) {
				add( item);
			}
			
			play.addActionListener( new ActionListener() {
				Game game = gamePanel.getGame();
				@Override
				public void actionPerformed( ActionEvent e ) {
					gamePanel.getGame().play( new OnProcessAdapter() {
						@Override
						public void onProcessStarted( Process p ) {
							Log.info( "Opening :" + game.getName() );
							game.setLastTimePlayed( System.currentTimeMillis() );
							searchGamePanel.setEnabledScreenshotButton( true );
						}
						@Override
						public void onProcessFinished( Process process, int exitCode ) {  
							if ( Games.runningGamesIds.keySet().size() > 0 ) {
								searchGamePanel.setEnabledScreenshotButton( false );							
							}
							if ( !game.isRunning() ) {
								if ( exitCode == 0 ) {   
									Log.info( "Closing :" + gamePanel.getGame().getName() );
									//gamePanel.getGame().findNewScreenshots();
									//gamePanel.refreshBackground();
								} else {                  
									Log.error( "Closing :" + gamePanel.getGame().getName() );
								}
							}
						}
					});
				}
			});
			
			compress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					new MessageBox( "Compress", "Are you sure you want to compress [" + gamePanel.getGame().getName() + "]" ) {
						@Override
						public void onAccept( MessageBox message ) {
							gamePanel.setBackground( Color.BLUE );
							gamePanel.getGame().compress( new OnProcessAdapter() {
								@Override
								public void onProcessFinished( Process process, int exitCode ) {
									if ( exitCode == 0 ) {  
										gamePanel.setBackground( GamePanel.COLOR_COMPRESSED );
										gamePanel.refreshFileLength();
									
									} else {  
										gamePanel.setBackground( GamePanel.COLOR_ERROR );	
									
									}
								}
							} );	
						}
					};
				}
			});
			
			decompress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					new MessageBox( "Extraction", "Are you sure you want to extract [" + gamePanel.getGame().getName() + "]") {
						@Override
						public void onAccept( MessageBox message ) { 
							   
							gamePanel.setBackground( Color.BLUE );  							
							gamePanel.getGame().extract( new OnProcessAdapter() {
								@Override
								public void onProcessFinished( Process process, int exitCode ) {
									if ( exitCode == 0 ) {  
										gamePanel.setBackground( GamePanel.COLOR_STANDBY );
										gamePanel.refreshFileLength();
									
									} else {
										gamePanel.setBackground( GamePanel.COLOR_ERROR );
									
									}
								}
							} );
						}
					};
				}
			});
			
			openFolder.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					gamePanel.getGame().openFolder();
				}
			});
			
			addTags.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					new TaggingFrame( gamePanel );
				}
			});
			
			delete.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					new MessageBox( "Delete", "Are you sure you want to delete [" + gamePanel.getGame().getName() + "]") {
						@Override
						public void onAccept( MessageBox message ) { 							
							gamePanel.getGame().delete(); 
							gamePanel.setBackground( GamePanel.COLOR_DELETED );
							gamePanel.refreshFileLength();
							
						}
					};
				}
			});
		}
		
		
		
		@Override
		public void show( Component c, int x, int y ) {
			super.show( c, x, y );
			if ( gamePanel.getGame().getState() == Games.STATE_STANDBY ) { 
			
				if ( gamePanel.getGame().isCompressed() ) {
					play.setEnabled( false );
					compress.setEnabled( false );
					decompress.setEnabled( true );
				} else {                        
					play.setEnabled( true );
					compress.setEnabled( true );
					decompress.setEnabled( false );
				} 
				delete.setEnabled( true );
			} else {
				play.setEnabled( false );
				compress.setEnabled( false );
				decompress.setEnabled( false );
				delete.setEnabled( false );
			}
		}
	}