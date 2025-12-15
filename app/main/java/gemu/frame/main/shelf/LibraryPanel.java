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
import gemu.frame.main.search.*;
import gemu.frame.pref.*;

public class LibraryPanel extends GemuSplitPane {
	Banner banner;
	ActionBar actionBar;
	Shelf shelf;
	RunningProcessPanel runningProcessPanel;
	PreferencesFrame preferencesFrame;
	ArrayList<OnProcessListener> actionBarProcessListeners = new ArrayList<>();
	ArrayList<BookCover> zippingList = new ArrayList<>();
	public LibraryPanel( Game[] games ) {                
		super( JSplitPane.VERTICAL_SPLIT );
		shelf = new Shelf( games );
		actionBar = new ActionBar();
		preferencesFrame = new PreferencesFrame();
		runningProcessPanel = new RunningProcessPanel();
		
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
		
		shelf.addOnBookCoverMouseAdapter( new Shelf.OnBookCoverMouseAdapter() {
			@Override
			public void mouseClicked( MouseEvent event, BookCover cover ) {
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
	
	public BookCover[] getZippingList() {
		return zippingList.toArray( new BookCover[zippingList.size()] );
	}
	
	public RunningProcessPanel getRunningProcessPanel() {
		return runningProcessPanel;
	}
	
	public void addOnActionBarProcessListener( OnProcessListener listener ) {
		actionBarProcessListeners.add( listener );
	}
	public Shelf getShelf() {
		return shelf;
	}
	
	public void setFocusedBookCover( BookCover bookCover ) {
		BookCover focusedBook = getFocusedBookCover();
		if ( focusedBook != null ) {
			focusedBook.setHighlight( false );
		}
		actionBar.setBookCover( bookCover );                
		preferencesFrame.setBookCover( bookCover );
		banner.setGame( bookCover.getGame() );	
		shelf.scrollTo( bookCover );
	}
	
	public BookCover getFocusedBookCover() {
		return actionBar.getBookCover();
	}
	
	public class RunningProcessPanel extends ResultPanel {
			RunningProcessPanel() {
			super();
		}
	
		public void refresh() {
			show( getZippingList() );
		}
	}
	
	private class ActionBar extends Box {
		
		boolean zippingThreadRunning = false;
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
		GemuButton buttonPreferences = new GemuButton("Settings") {
			{
				addActionListener( (ActionEvent ) -> preferencesFrame.setVisible( true ) );
			}
		}; 
		
		ArrayList<GemuButton> buttons = new ArrayList<GemuButton>( Arrays.<GemuButton>asList(
			buttonPlay,
			buttonScreenshot,
			buttonZip,
			buttonFiles, 
			buttonPreferences
		) );
		GemuLabel playingTimeLabel = new GemuLabel("?");
		GemuLabel lastTimePlayedLabel = new GemuLabel("?");
		
		
		protected ActionBar() {
			super(BoxLayout.X_AXIS );			
			 
			setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ));
			setDeletedStyle();
			buttonPlay.addActionListener( playAction );
			buttonZip.addActionListener( zipAction );
			buttonPreferences.addActionListener( openPreferencesWindow );
			buttonScreenshot.addActionListener( screenshotAction );
			buttonFiles.addActionListener( exploreFilesAction );
			
			add( buttonPlay );
			add( new JPanel( new GridBagLayout() ) {
				{                       
					setBackground( null );
					GridBagConstraints gbc = new GridBagConstraints();
					
					setOpaque( false );
					setBorder( BorderFactory.createEmptyBorder( 0, 7, 0, 0 ) );
					gbc.insets = new Insets( 0, 7, 0, 7 );
					gbc.anchor = GridBagConstraints.WEST;
					gbc.fill = GridBagConstraints.VERTICAL;
					
					gbc.gridx = 0;
					gbc.gridy = 0;
					add( new GemuLabel("Time Playing") {						
						{
							setForeground( Color.GRAY );
						}
					}, gbc );
					
					gbc.gridx = 1;
					gbc.gridy = 0;
					add( new GemuLabel("Last Time Played"){ 
						{
							setForeground( Color.GRAY );
							setHorizontalAlignment( SwingConstants.LEFT );
						}
					}, gbc );
					
					gbc.gridx = 0;
					gbc.gridy = 1;
					add( playingTimeLabel, gbc );
					
					gbc.gridx = 1;
					gbc.gridy = 1;
					add( lastTimePlayedLabel, gbc );
					
				}
				@Override
				public Dimension getMaximumSize() {
					return new Dimension( getPreferredSize() );
				}
			});
			add( Box.createHorizontalGlue());
			buttons.forEach( (button) -> {
				if ( button != buttonPlay ) {
					add( button );
				}
			});
			
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
			bookCover.setHighlight( true );
			Game game = bookCover.getGame();
			updateGameInfoLabels();
			if ( game != null ) {
			
				buttons.forEach((b)-> b.setEnabled(true ) );
				if ( game.isInZippingProcess() || zippingList.contains( bookCover) ) {
					setInZipProcessStyle();
				} else if ( game.isStandby() ) { 
					setStandbyStyle();
				} else if( game.isRunning() ) { 
					setRunningStyle(); 
				} else if( game.isInZip() ) {					
					setInZipStyle();
				} else if ( game.isDeleted() ) {
					setDeletedStyle();
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
			buttonPreferences.setEnabled( true ); 
		}
		
		protected void setRunningStyle() {  
			buttonScreenshot.setEnabled( true );
			setButtonPlayStopStyle();
			setButtonZipStandbyStyle();
			buttonZip.setEnabled( false );
			buttonFiles.setEnabled( true );
			buttonPreferences.setEnabled( false );
		}
		
		protected void setInZipStyle() {
			setbuttonPlayDisabledStyle();
			setButtonZipUnzipStyle(); 
			buttonScreenshot.setEnabled( false );
			buttonFiles.setEnabled( true );
			buttonPreferences.setEnabled( true );
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
		
		protected void setDeletedStyle() {
			buttons.forEach(( button ) -> {
				if (!( button == buttonPlay || button == buttonFiles )) {
					button.setEnabled( false );
				}
			});           
		};
		
		
		//actionlisteners
		ActionListener openPreferencesWindow = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				preferencesFrame.setBookCover( getBookCover() );
				preferencesFrame.setVisible( true );
			}
		};
		protected ActionListener playAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				if ( getGame().isStandby() ) {
					setDisabledStyle(); 
					BookCover bookCover = getBookCover();
					Game game = getGame();
					/*
					Thread playingTimeChecker = new Thread(()->{
						while( game.isRunning() ) {
							try {               
								Thread.sleep( 1000 );
							} catch( Exception e ) {}
						}
					});
					*/
					game.play( new OnProcessListener() {
						@Override
						public void processStarted( long processId ) {
							if ( processId == getGame().getProcessId() ) { 
								setRunningStyle();				
							}
							
							//playingTimeChecker.start();
							for ( OnProcessListener listener : actionBarProcessListeners ) {
								listener.processStarted( processId );
							}
						}
						@Override
						public void streamLineRead( long processId, String line ) {
							updateGameInfoLabels();
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
							
							bookCover.updateLengthTags();
							bookCover.revalidate();
							bookCover.repaint();
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
		private void setZippingThreadRunning( boolean b ) {
			zippingThreadRunning = b;
		}
		protected boolean isZippingThreadRunning() {
			return zippingThreadRunning;
		}
		protected ActionListener zipAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				zippingList.add( getBookCover() ); 
				setInZipProcessStyle();
				runningProcessPanel.refresh();
				if ( isZippingThreadRunning() ) {
					return;
				}
				
				Thread th = new Thread(()->{
					setZippingThreadRunning( true );
					while ( zippingList.size() > 0 ) {
						BookCover bookCover = zippingList.get(0);
						Game game = bookCover.getGame();
						if ( game.isInZip() ) {
							game.unzip( new OnProcessListener() {
								@Override
								public void processStarted( long processId ) {
								
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
										if ( processId == getGame().getZippingProcessId() ) {
											setStandbyStyle();  
										}
										bookCover.updateLengthTags();
										bookCover.revalidate();
										bookCover.repaint();
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
										if ( processId == getGame().getZippingProcessId() ) {
											setInZipStyle();
										}
										bookCover.updateLengthTags();
										bookCover.revalidate();
										bookCover.repaint();
									}
									
									for ( OnProcessListener listener : actionBarProcessListeners ) {
										listener.processFinished( processId, exitCode );
									}
								}
							} );
						}
						zippingList.remove( bookCover );
						runningProcessPanel.refresh();
						
					} // end while
					
					setZippingThreadRunning( false );	
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
					script.append("$screenshotFileName = '" + screenshotFileName + ".jpg'\n" );
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
								banner.repaint();
								
								bookCover.updateBufferedImage();
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
		
	}
	
}