package gemu.frame.main.shelf;

import gemu.common.*;
import javax.swing.*;
import java.awt.*;        
import java.io.*;
import java.awt.event.*;  
import gemu.game.*;
import gemu.shell.*;  
import gemu.util.*;

public class LibraryPanel extends GemuSplitPane {
	Banner banner;
	ActionBar actionBar;
	Shelf shelf;
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
			setDeletedStyle();
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
					add( new Label("Playing Time"));                                         
					add( new Label("Last Time Played"));
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
		
		class Label extends JLabel {
			Label( String name ) {
				super( name );
				setFont( Style.FONT_MONO_SPACE );
				setForeground( Style.COLOR_FOREGROUND );
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
			buttonPlay.setEnabled( false ); 
			buttonPlay.setText("Play");
			buttonPlay.setBackgroundColor( Style.COLOR_BACKGROUND );
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
		
		protected void setBookCover( BookCover bookCover ) {
			this.bookCover = bookCover;
			Game game = bookCover.getGame();
			updateGameInfoLabels();
			if ( game != null ) {
			
				buttonPlay.setEnabled( true );
				for ( GemuButton button : buttons ) {
					button.setEnabled( true );
				}
				if ( game.isInZipProcess() ) {
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
				setDeletedStyle();
			}
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
			buttonPlay.setEnabled( false );
			for ( GemuButton button : buttons ) {
				button.setEnabled( false );
			}
		};
		
		protected void setDeletedStyle() {
			buttonPlay.setEnabled( false );
			for ( GemuButton button : buttons ) {
				button.setEnabled( false );
			}
		};
		
		
		
		
		protected Game getGame() {
			return bookCover.getGame();
		}
		//actionlisteners
		
		protected ActionListener playAction = new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent event ) {
				if ( !getGame().isRunning() ) {				
					getGame().play( new OnProcessListener() {
						@Override
						public void processStarted( Process process ) {
							if ( process == getGame().getProcess() ) {
								setRunningStyle();
							}
						}
						@Override
						public void processFinished( Process process, int exitCode ) {
							if ( process == getGame().getProcess() ) {
								updateGameInfoLabels();
								setStandbyStyle();
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
				Thread th = new Thread(()->{
					if ( game.isInZip() ) {
						game.unzip( new OnProcessListener() { @Override
							public void processStarted( Process p ) {
								setInZipProcessStyle();
							}
							@Override
							public void processFinished( Process p, int exitCode ) {
								if ( exitCode == 0 ) {
									if ( !game.isInZip() ) {
										setStandbyStyle();
									}
								}
							}
						});
					} else if ( !game.isDeleted() ) {
						System.out.println("Tring to pack");
						game.pack( new OnProcessListener() {
							@Override
							public void processStarted( Process p ) {
								setInZipProcessStyle();
							}
							@Override
							public void processFinished( Process p, int exitCode ) {
								if ( exitCode == 0 ) {
									if ( game.isInZip() ) {
										setInZipStyle();
									}
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
										 
					long id = game.getProcess().pid();
					File scriptFile = new File( new File( LibraryPanel.class.getProtectionDomain().getCodeSource().getLocation().getPath() ).getParentFile() + "\\screenshot_script.txt" ); 
					BufferedReader reader = new BufferedReader( new InputStreamReader ( new FileInputStream( scriptFile ) ));
					char ch;
					int code;
					String screenshotFileName = "main_screeshot.jpg";
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
						public void processFinished( Process p, int exitCode ) {
							if ( exitCode == 0 ) {                             
								game.setCoverImage( new File( game.likeAbsolutePath( screenshotFileName ) ) );
								System.out.println("A");
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
		
	}
	
}