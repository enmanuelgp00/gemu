package gemu.frame.main.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;        
import java.io.*;         
import java.util.*;
import java.awt.event.*;  
import gemu.game.*;
import gemu.shell.*;  
import gemu.util.*;

public class LibraryPanel extends GemuSplitPane {
	Banner banner;
	ActionBar actionBar;
	Shelf shelf;
	ArrayList<OnProcessListener> actionBarProcessListeners = new ArrayList<>();
	public LibraryPanel( Game[] games ) {                
		super( JSplitPane.VERTICAL_SPLIT );
		shelf = new Shelf( games );
		actionBar = new ActionBar();
		banner = new Banner() {
			@Override
			public void paintComponent( Graphics g ) {
				super.paintComponent(g);
				int bannerMaximumHeight = getMaximumSize().height;
				int shelfMinimumHeight = LibraryPanel.this.getHeight() - bannerMaximumHeight; 
				if ( shelfMinimumHeight < actionBar.getHeight() ) {
					shelfMinimumHeight = actionBar.getHeight();
				}
				shelf.setMinimumSize( new Dimension( 0, shelfMinimumHeight));
				if ( bannerMaximumHeight < LibraryPanel.this.getDividerLocation() ) {
					LibraryPanel.this.setDividerLocation( bannerMaximumHeight );
				}
			}
		};
		/*
		if( games.length > 0 ) {
			System.out.println( shelf.listBookCovers().length );
			setFocusedBookCover( shelf.listBookCovers()[0] );   
		}
		*/
		
		shelf.addOnBookCoverMouseAdapter( new Shelf.OnBookCoverMouseAdapter() {
			@Override
			public void mousePressed( MouseEvent event, BookCover cover ) {
				setFocusedBookCover( cover ); 
			}
		});
		
	
		
		Box box = new Box( BoxLayout.Y_AXIS );
		box.add( actionBar );
		box.add( shelf );
		
		setLeftComponent( banner );
		setRightComponent( box );
		
		setDividerSize(0);
	}
	public void addOnActionBarProcessListener( OnProcessListener listener ) {
		actionBarProcessListeners.add( listener );
	}
	public Shelf getShelf() {
		return shelf;
	}
	
	public void setFocusedBookCover( BookCover bookCover ) {
		actionBar.setBookCover( bookCover );	
		banner.setGame( bookCover.getGame() );	
	}
	
	private class ActionBar extends Box {
	
		BookCover bookCover;
		
		GemuButton buttonPlay = new GemuButton("Play", 5, 5 ) {
			{
				setEnabled( false );
				Insets insets = getInsets();
				setBorder( BorderFactory.createEmptyBorder( insets.top, 30, insets.bottom, 30 ));
			}
		};
		
		GemuButton buttonScreenshot = new GemuButton("Screenshot");    
		GemuButton buttonZip = new GemuButton("7zip", 5, 5 );
		GemuButton buttonFiles = new GemuButton("Files");
		GemuButton buttonDelete = new GemuButton("Delete") {
			{
				setBackgroundColor( Style.COLOR_BACKGROUND );
				setPressedBackground( new Color( 227, 2, 26 ) );
				setRolloverBackground( new Color( 237, 2, 36 ) );
			}
		};
		
		GemuButton[] buttons = new GemuButton[] {
			buttonScreenshot,
			buttonZip,
			buttonFiles, 
			buttonDelete
		};
		Label playingTimeLabel = new Label("");
		Label lastTimePlayedLabel = new Label("");
		
		
		protected ActionBar() {
			super(BoxLayout.X_AXIS );			
			 
			setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ));
			setDisabledStyle();
			buttonPlay.addActionListener( playAction );
			buttonZip.addActionListener( zipAction );
			buttonScreenshot.addActionListener( screenshotAction );
			buttonFiles.addActionListener( exploreFilesAction );
			
			add( buttonPlay );
			add( new JPanel( new GridLayout( 2, 2, 1, 1 ) ) {
				{                       
					setBackground( null );
					setOpaque( false );
					setBorder( BorderFactory.createEmptyBorder( 0, 7, 0, 0 ) );
					add( new Label("Playing Time") {						
						{
							setForeground( Color.GRAY );
						}
					});                                         
					add( new Label("Last Time Played"){ 
						{
							setForeground( Color.GRAY );
						}
					});
					add( playingTimeLabel );
					add( lastTimePlayedLabel );
				}
			});
			add( Box.createHorizontalGlue());
			
			for ( GemuButton button : buttons ) {                                                           
				add(button);
			}
			
			addMouseListener( draggDivider );   
			addMouseMotionListener( draggDivider );
		} 
		
		
		protected BookCover getBookCover() {
			return bookCover;
		}
		
		protected Game getGame() {
			return bookCover.getGame();
		}
		
		
		protected void setBookCover( BookCover bookCover ) {
			this.bookCover = bookCover;
			Game game = bookCover.getGame();
			updateGameInfoLabels();
			if ( game != null ) {
			
				buttonPlay.setEnabled( true );
				for ( GemuButton button : buttons ) {
					button.setEnabled( true );
				}
				if ( game.isInZippingProcess() ) {
					setInZipProcessStyle();
				} else if ( game.isStandby() ) { 
					setStandbyStyle();
				} else if( game.isRunning() ) { 
					setRunningStyle(); 
				} else if( game.isInZip() ) {					
					setInZipStyle();
				} else if ( game.isDeleted() ) {
					setDisabledStyle();
				}
				
			} else {
				setDisabledStyle();
			}
		}
		
		
		protected void setButtonZipUnzipStyle() { 
			buttonZip.setEnabled( true );  
			buttonZip.setText("Unzip");      
			buttonZip.setBackgroundColor( new Color( 0, 160, 0 ) );
			buttonZip.setPressedBackground( new Color( 0, 150, 0 ) );
			buttonZip.setRolloverBackground( new Color( 0, 180, 0 ) );
		}
		
		protected void setButtonZipStandbyStyle() {
			buttonZip.setEnabled( true );  
			buttonZip.setText("ZipUp");
   
			buttonZip.setBackgroundColor( Style.COLOR_BACKGROUND );
			buttonZip.setPressedBackground( new Color( 223, 187, 14 ) );
			buttonZip.setRolloverBackground( new Color(  211, 173, 10 ) );
		
		}
		protected void setbuttonPlayDisabledStyle() {
			buttonPlay.setBackgroundColor( Style.COLOR_BACKGROUND );
			buttonPlay.setEnabled( false ); 
		}
		protected void setButtonPlayReadyStyle() { 
			buttonPlay.setEnabled( true );  
			buttonPlay.setText("Play");
			buttonPlay.setBackgroundColor( new Color( 0, 160, 0 ) );
			buttonPlay.setPressedBackground( new Color( 0, 150, 0 ) );
			buttonPlay.setRolloverBackground( new Color( 0, 180, 0 ) );
		}
		
		protected void setButtonPlayStopStyle() { 			
			buttonPlay.setEnabled( true ); 
			buttonPlay.setText("Stop");
			buttonPlay.setBackgroundColor( Style.COLOR_BACKGROUND );
			buttonPlay.setPressedBackground( new Color( 227, 2, 26 ) );
			buttonPlay.setRolloverBackground( new Color( 237, 2, 36 ) );
		}
		
		protected void updateGameInfoLabels() {   
			playingTimeLabel.setText( HumanVerbose.hours( getGame().getPlayingTime() ) );
			lastTimePlayedLabel.setText( HumanVerbose.date( getGame().getLastTimePlayed() ) );
		}
		
		
		protected void setStandbyStyle() {
			buttonScreenshot.setEnabled( false );
			setButtonPlayReadyStyle(); 
			setButtonZipStandbyStyle();
			buttonFiles.setEnabled( true );
			buttonDelete.setEnabled( true );
		}
		
		protected void setRunningStyle() {  
			buttonScreenshot.setEnabled( true );
			setButtonPlayStopStyle();
			setButtonZipStandbyStyle();
			buttonZip.setEnabled( false );
			buttonFiles.setEnabled( true );
			buttonDelete.setEnabled( false );
		}
		
		protected void setInZipStyle() {
			setbuttonPlayDisabledStyle();
			setButtonZipUnzipStyle(); 
			buttonScreenshot.setEnabled( false );
			buttonFiles.setEnabled( true );
			buttonDelete.setEnabled( true );
		} 
		
		protected void setInZipProcessStyle() {
			setbuttonPlayDisabledStyle();
			for ( GemuButton button : buttons ) {
				button.setEnabled( false );
			}
		};
		
		protected void setDisabledStyle() {
			buttonPlay.setEnabled( false );
			for ( GemuButton button : buttons ) {
				button.setEnabled( false );
			}              
			buttonFiles.setEnabled( true );
		};
		
		
		
		//actionlisteners
		
		protected ActionListener playAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				if ( getGame().isStandby() ) {
					setDisabledStyle();
					getGame().play( new OnProcessListener() {
						@Override
						public void processStarted( long processId ) {
							if ( processId == getGame().getProcessId() ) { 
								setRunningStyle();				
							}
							
							for ( OnProcessListener listener : actionBarProcessListeners ) {
								listener.processStarted( processId );
							}
						}
						@Override
						public void streamLineRead( long processId, String line ) {
							for ( OnProcessListener listener : actionBarProcessListeners ) {
								listener.streamLineRead( processId, line );
							}
						}
						@Override
						public void processFinished( long processId, int exitCode ) {
							if ( processId == getGame().getProcessId() ) {
								updateGameInfoLabels();
								setStandbyStyle();
							}
							
							for ( OnProcessListener listener : actionBarProcessListeners ) {
								listener.processFinished( processId, exitCode );
							}
						}
					});
				} else {
					getGame().stop();
				}	
			}
		};
		
		protected ActionListener zipAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				Game game = getGame();                             
				BookCover bookCover = getBookCover();
				Thread th = new Thread(()->{
					if ( game.isInZip() ) {
						game.unzip( new OnProcessListener() {
							@Override
							public void processStarted( long processId ) {
								setInZipProcessStyle();
							
								for ( OnProcessListener listener : actionBarProcessListeners ) {
									listener.processStarted( processId );
								}
							}
							@Override
							public void streamLineRead( long processId, String line ) {
								for ( OnProcessListener listener : actionBarProcessListeners ) {
									listener.streamLineRead( processId, line );
								}
							}
							@Override
							public void processFinished( long processId, int exitCode ) {
								if ( exitCode == 0 ) {
									if ( processId == game.getProcessId() ) {
										setStandbyStyle();  
									}
									
									if ( !game.isInZip() ) {
										bookCover.updateLengthTags();
										bookCover.revalidate();
										bookCover.repaint();
									}
								}
							
								for ( OnProcessListener listener : actionBarProcessListeners ) {
									listener.processFinished( processId, exitCode );
								}
							}
						});
					} else if ( !game.isDeleted() ) {
						game.pack( new OnProcessListener() {
							@Override
							public void processStarted( long processId ) {
								setInZipProcessStyle();
								for ( OnProcessListener listener : actionBarProcessListeners ) {
									listener.processStarted( processId );
								}
							}
							
							@Override
							public void streamLineRead( long processId, String line ) {
								for ( OnProcessListener listener : actionBarProcessListeners ) {
									listener.streamLineRead( processId, line );
								}
							}
							
							@Override
							public void processFinished( long processId, int exitCode ) {
								if ( exitCode == 0 ) {
									if ( processId == game.getProcessId() ) {
										setInZipStyle();
									}
									
									if ( game.isInZip() ) {
										bookCover.updateLengthTags();
										bookCover.revalidate();
										bookCover.repaint();
									}
								}
								for ( OnProcessListener listener : actionBarProcessListeners ) {
									listener.processFinished( processId, exitCode );
								}
							}
						} );
					}
					
				});
				th.start();
				
			}
		};
			
		
		protected ActionListener screenshotAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				Game game = getGame();
				if ( !game.isRunning() ) {
					return;
				}                        
				try {
										 
					long id = game.getProcessId();
					File scriptFile = new File( new File( LibraryPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getParentFile() + "\\screenshot_script.txt" ); 
					BufferedReader reader = new BufferedReader( new InputStreamReader ( new FileInputStream( scriptFile ) ));
					char ch;
					int code;
					String screenshotFileName = Games.COVER_NAME;
					StringBuilder script = new StringBuilder();
					script.append("$id = " + id + "\n" );            
					script.append("$screenshotFileName = '" + screenshotFileName + "'\n" );
					while( ( code = reader.read() ) != -1 ) {
						ch = (char)code;
						if ( ch == '"' ) {  
							script.append( "\\" );							
						}
						script.append( ch );
					}
					reader.close();
					Shell.run( new OnProcessListener() {
						@Override
						public void processFinished( long processId, int exitCode ) {
							if ( exitCode == 0 ) {                             
								game.setCoverImage( new File( game.likeAbsolutePath( screenshotFileName ) ) );
								banner.updateBufferedImage();
								banner.revalidate();
								banner.repaint();
								
								bookCover.updateBufferedImage();
								bookCover.revalidate();
								bookCover.repaint();
							
							}
						}
					} , game.getDirectory(), "powershell", script.toString() );
				} catch ( Exception e ) {
					e.printStackTrace();
				}
			}
		};
		
		protected ActionListener exploreFilesAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event )  {
				getGame().openDirectory();	
			}
		}; 
		
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
		
		
		
		class Label extends JLabel {
			Label( String name ) {
				super( name );
				setFont( Style.FONT_MONO_SPACE );
				setForeground( Style.COLOR_FOREGROUND );
			}
		}
		
	}
	
}