package gemu.frame.main;


import gemu.game.Game;
import gemu.Shell;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.List;       
import java.util.Set;       
import java.util.HashSet;

class GamePanel extends JPanel {
		PopupMenu popupMenu;
		Game game;
		Image imageBackground = null;
		GamePanel( Game game ) {
			super();
			this.game = game;
			final int WIDTH = 200;
			final int HEIGHT = 200;
			popupMenu = new PopupMenu(); 			
			setLayout( new OverlayLayout( this ));
			setBackground( new Color(0x00aa00));
			setPreferredSize( new Dimension( WIDTH, HEIGHT ));
			setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );						
			
			JPanel tags = new PreloadPanel( new FlowLayout( FlowLayout.LEFT )); 
			tags.setBackground( new Color(0x00dddd));			
			tags.setOpaque( false );
			for ( String tag : game.getTags() ) {
				tags.add(  new TagLabel( tag ) );
			}
			
			JPanel layer = new JPanel( new BorderLayout());
			layer.setOpaque( false );
			
			layer.add( tags, BorderLayout.SOUTH );
			add( layer ); 
			
			List<File> screenshots;
			if ( ( screenshots = game.getScreenshots()) != null ) {
				File screenshot = screenshots.get(0);
				if ( screenshot.exists() ) {
					try {
						imageBackground = ImageIO.read( screenshots.get(0));
					} catch ( IOException e ){
						e.printStackTrace();
					}					
				} else {
					game.removeScreenshot( screenshot ).commit();
				}
			}           
			
			JPanel wrapper = new JPanel( new BorderLayout());
			wrapper.add(new TitleLabel( game.getName() )); 
			wrapper.setOpaque( false );
			add( wrapper ); 
			
			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked( MouseEvent e ) {
					switch( e.getButton() ) {
						case MouseEvent.BUTTON1:
						break;               
						case MouseEvent.BUTTON2:
						break;
						case MouseEvent.BUTTON3:
							popupMenu.show( GamePanel.this , e.getX(), e.getY() );
						break;
					}
				}
			} );
		}
		
		@Override
		public void paintComponent( Graphics g) {
			super.paintComponent(g);
			if ( imageBackground != null ) {
				g.drawImage( imageBackground, 0, 0, getWidth(), getHeight(), this );
			}
		}
		
		class TitleLabel extends JLabel {
			TitleLabel( String name ) {
				super( "<html><p style=text-align:center;>" + name + "</p></html>", SwingConstants.CENTER );
				setFont( MainFrame.FONT_TITLE );
				setForeground( Color.WHITE );
				setBackground( new Color(0x004444));
				setOpaque( false );
			}
		}
		
		class TagLabel extends JLabel {
			TagLabel( String title ) {
				super( title ); 
				setOpaque( true );
				setFont( MainFrame.FONT_ITEM );
				setBackground( Color.PINK );
				int bottom = 0;
				int top = 3;
				int left = 3;
				int right = 3;
				setBorder( BorderFactory.createEmptyBorder( top, right, bottom, left ));
				
			}
		}
		
		class PopupMenu extends JPopupMenu {
			JMenuItem play;
			JMenuItem compress;
			JMenuItem folder;
			PopupMenu() {
				super();
				JMenuItem[] items = new JMenuItem[] {
					play = new JMenuItem("Play Game"), 
					compress = new JMenuItem(""),
					folder = new JMenuItem("Open Folder")
				};
				play.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						game.play();
					}
				} );
				folder.addActionListener( new ActionListener() {
					@Override
					public void actionPerformed( ActionEvent e ) {
						game.openFolder();
					}
				} );
				
				for ( JMenuItem item : items ) {
					add( item );
				}
			} 
			@Override
			public void show( Component component , int x, int y ) {
				super.show( component, x, y); 
				String title = "Compress";
				
				if ( game.isCompressed() ) {
					title = "Decompress";
					this.remove( play ); 
					compress.removeActionListener( compressAction );
					compress.addActionListener( decompressAction ); 
					revalidate();
					repaint();
				} else {
					this.add( play );
					compress.removeActionListener( decompressAction );
					compress.addActionListener( compressAction ); 
					revalidate();
					repaint();
				}
				compress.setText( title );
			}
			
			ActionListener compressAction = new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.compress( new Shell.OnProcessListener() {
						@Override
						public void onProcessStarted( Process process ) {								
							System.out.println( "Compression started ");
						
						}
						@Override
						public void onStreamLineRead( String line ) {}
						@Override
						public void onProcessFinished( Process process, int exitCode ) { 									
							System.out.println( "Compression finished ");
						}
					} );
				}
			}; 
			ActionListener decompressAction = new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.decompress( new Shell.OnProcessListener() {
						@Override
						public void onProcessStarted( Process process ) {
							System.out.println( "Decompression started ");
						} 
						@Override
						public void onStreamLineRead( String line ) {}
						
						@Override
						public void onProcessFinished( Process process, int exitCode ) {									
							System.out.println( "Decompression finished ");								
						}
					} );
				}
			};
		}
	}