package gemu.frame.main.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  
import gemu.game.*;
import gemu.shell.*;

public class LibraryPanel extends GemuSplitPane {
	Banner banner;
	ActionBar actionsBar;
	public LibraryPanel( Game[] games ) {                
		super( JSplitPane.VERTICAL_SPLIT );
		Shelf shelf = new Shelf( games );
		actionsBar = new ActionBar();
		banner = new Banner() {
			@Override
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				int bannerMaximumHeight = getMaximumSize().height;
				int shelfMinimumHeight = LibraryPanel.this.getHeight() - bannerMaximumHeight; 
				if ( shelfMinimumHeight < actionsBar.getHeight() ) {
					shelfMinimumHeight = actionsBar.getHeight();
				}
				shelf.setMinimumSize( new Dimension( 0, shelfMinimumHeight));
				if ( bannerMaximumHeight < LibraryPanel.this.getDividerLocation() ) {
					LibraryPanel.this.setDividerLocation( bannerMaximumHeight );
				}
			}
		};
		
		if( games.length > 0 ) {
			setFocusedGame( games[0]);   
		}
		
		shelf.addOnBookCoverMouseAdapter( new Shelf.OnBookCoverMouseAdapter() {
			@Override
			public void mousePressed( MouseEvent event, BookCover cover ) {
				setFocusedGame(cover.getGame()); 
			}
		});
		
	
		
		Box box = new Box( BoxLayout.Y_AXIS );
		box.add( actionsBar );
		box.add( shelf );
		
		setLeftComponent( banner );
		setRightComponent( box );
		
		setDividerSize(0);
	}
	
	public void setFocusedGame( Game game ) {
		banner.setGame(game);
		actionsBar.setGame(game);		
	}
	
	private class ActionBar extends Box {
	
		Game game;
		GemuButton buttonPlay = new GemuButton("Play", 5, 5 ) {
			{
				setVisible( false );
				Insets insets = getInsets();
				setBorder( BorderFactory.createEmptyBorder( insets.top, 30, insets.bottom, 30 ));
			}
		};
		
		
		GemuButton buttonScreenshot = new GemuButton("Screenshot");    
		GemuButton buttonZip = new GemuButton("7zip");
		GemuButton buttonFiles = new GemuButton("Files");
		GemuButton buttonDelete = new GemuButton("Delete");
		
		GemuButton[] buttons = new GemuButton[] {
			buttonScreenshot,
			buttonZip,
			buttonFiles, 
			buttonDelete
		};
		
		protected ActionBar() {
			super(BoxLayout.X_AXIS );			
			 
			setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ));
			setDeletedStyle();
			buttonPlay.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent event ) {
					if ( !getGame().isRunning() ) {				
						getGame().play( new OnProcessAdapter() {
							@Override
							public void processStarted( Process process ) {
								if ( process == getGame().getProcess() ) {
									setRunningStyle();
								}
							}
							@Override
							public void processFinished( Process process, int exitCode ) {
								if ( process == getGame().getProcess() ) {
									setStandbyStyle();
								}
							}
						});
					} else {
						getGame().stop();
					}	
				}
			} );
			
			buttonFiles.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent event )  {
					getGame().openDirectory();	
				}
			});
			
			add( buttonPlay );   
			add( Box.createHorizontalGlue());
			
			for ( GemuButton button : buttons ) {                                                           
				add(button);
			}
			
			addMouseListener( draggDivider );   
			addMouseMotionListener( draggDivider );
		}
		
		protected void setButtonZipUnzipStyle() {
			buttonZip.setText("Unzip");
		}
		
		protected void setButtonZipStandbyStyle() {
			buttonZip.setText("7zip");
		
		}
		
		protected void setButtonPlayReadyStyle() { 
			buttonPlay.setVisible( true );  
			buttonPlay.setText("Play");
			buttonPlay.setBackgroundColor( new Color( 0, 160, 0 ) );
			buttonPlay.setPressedBackground( new Color( 0, 150, 0 ) );
			buttonPlay.setRolloverBackground( new Color( 0, 180, 0 ) );
		}
		
		protected void setButtonPlayStopStyle() { 			
			buttonPlay.setVisible( true ); 
			buttonPlay.setText("Stop");
			buttonPlay.setBackgroundColor( Style.COLOR_BACKGROUND );
			buttonPlay.setPressedBackground( new Color( 227, 2, 26 ) );
			buttonPlay.setRolloverBackground( new Color( 237, 2, 36 ) );
		}
		
		protected void setGame( Game game ) {
			this.game = game;
			if ( game != null ) {
			
				buttonPlay.setVisible( true );
				for ( GemuButton button : buttons ) {
					button.setVisible( true );
				}
				
				if ( game.isStandby() ) { 
					setStandbyStyle();
				} else if( game.isRunning() ) { 
					setRunningStyle(); 
				} else if( game.isInZip() ) {					
					setInZipStyle();
				} else if ( game.isDeleted() ) {
					setDeletedStyle();
				}
				
			} else {
				setDeletedStyle();
			}
		}
		
		protected void setStandbyStyle() {
			buttonScreenshot.setVisible( false );
			setButtonPlayReadyStyle(); 
			setButtonZipStandbyStyle();
		}
		
		protected void setRunningStyle() {  
			buttonScreenshot.setVisible( true );
			setButtonPlayStopStyle();
			setButtonZipStandbyStyle();
		}
		
		protected void setInZipStyle() {
			buttonPlay.setVisible( false );
			setButtonZipUnzipStyle(); 
			buttonScreenshot.setVisible( false );
		}
		
		protected void setDeletedStyle() {
			buttonPlay.setVisible( false );
			for ( GemuButton button : buttons ) {
				button.setVisible( false );
			}
		};
		
		protected Game getGame() {
			return this.game;
		}
		
		MouseAdapter draggDivider = new MouseAdapter() {
																
			int initialMousePos;
			@Override
			public void mouseEntered( MouseEvent event ) {
				setCursor( Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			}   
			
			@Override
			public void mouseExited( MouseEvent event ) {
				setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
				
				
			@Override
			public void mousePressed( MouseEvent event ) {
				initialMousePos = event.getY() + getBounds().y;
			}
			
			@Override
			public void mouseDragged( MouseEvent event ) {
				
				int currentMousePos = event.getY() + getBounds().y;
				int movement = currentMousePos - initialMousePos;
				int  dividerLocation =  getDividerLocation();
				int location = dividerLocation + movement;
				
				if (  banner.getMaximumSize().height > location && location > banner.getMinimumSize().height && location < getParent().getParent().getHeight() - getHeight() ) {
					setDividerLocation( location );
				}  
				
			}
		};
		
	}
	
}