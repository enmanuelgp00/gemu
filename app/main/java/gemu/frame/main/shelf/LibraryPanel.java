package gemu.frame.main.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;  
import gemu.game.*;
import gemu.shell.*;

public class LibraryPanel extends GemuSplitPane {
	Banner banner;
	public LibraryPanel( Game[] games ) {                
		super( JSplitPane.VERTICAL_SPLIT );
		Shelf shelf = new Shelf( games );
		ActionsBar actionsBar = new ActionsBar( null );
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
			banner.setGame( games[0] );
			actionsBar.setGame( games[0] );
		}
		
		shelf.addOnBookCoverMouseAdapter( new Shelf.OnBookCoverMouseAdapter() {
			@Override
			public void mousePressed( MouseEvent event, BookCover cover ) {
				banner.setGame( cover.getGame() );
				actionsBar.setGame( cover.getGame() );
			}
		});
		
	
		
		Box box = new Box( BoxLayout.Y_AXIS );
		box.add( actionsBar );
		box.add( shelf );
		
		setLeftComponent( banner );
		setRightComponent( box );
		
		setDividerSize(0);
	}
	
	private class ActionsBar extends Box {
	
		Game game;
		GemuButton buttonPlay = new GemuButton("Play", 5, 5 );
		
		
		GemuButton screenshot = new GemuButton("Screenshot");    
		GemuButton zipper = new GemuButton("7zip");
		GemuButton files = new GemuButton("Files");
		GemuButton delete = new GemuButton("Delete");
		
		GemuButton[] buttons = new GemuButton[] {
			screenshot,
			zipper,
			files, 
			delete
		};
		
		protected ActionsBar( Game game ) {
			super(BoxLayout.X_AXIS );			
			setGame( game );  
			setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ));
			setButtonPlayStopStyle();
			buttonPlay.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent event ) {
					if ( !getGame().isRunning() ) {				
						getGame().play( new OnProcessAdapter() {
							@Override
							public void processStarted( Process process ) {
								if ( process == getGame().getProcess() ) {
									setButtonPlayStopStyle();
								}
							}
							@Override
							public void processFinished( Process process, int exitCode ) {
								if ( process == getGame().getProcess() ) {
									setButtonPlayDefaultStyle();
								}
							}
						});
					} else {
						getGame().stop();
					}	
				}
			} );
			
			files.addActionListener( new ActionListener() {
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
		
		protected void setButtonPlayDefaultStyle() {  
			buttonPlay.setText("Play");
			buttonPlay.setBackgroundColor( new Color( 0, 160, 0 ) );
			buttonPlay.setPressedBackground( new Color( 0, 150, 0 ) );
			buttonPlay.setRolloverBackground( new Color( 0, 180, 0 ) );
		}
		
		protected void setButtonPlayStopStyle() {  
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
				
				if ( game.isRunning() ) {
					buttonPlay.setText("Stop");
					setButtonPlayStopStyle();
				} else {    
					buttonPlay.setText("Play");
					setButtonPlayDefaultStyle();
				}
				
			} else {
				buttonPlay.setVisible( false );
				for ( GemuButton button : buttons ) {
					button.setVisible( false );
				}
			}
		}
		
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
				
				if (  banner.getMaximumSize().height > location && location > banner.getMinimumSize().height ) {
					setDividerLocation( location );
				}  
				
			}
		};
		
	}
	
}