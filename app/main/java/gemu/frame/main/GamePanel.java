package gemu.frame.main;

import gemu.util.Texts;
import gemu.file.File;
import java.io.IOException;
import gemu.system.*;
import gemu.game.Game;
import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.event.*;

public class GamePanel extends JPanel {
	TagsPanel tagsPanel;
	StatusPanel statusPanel;
	
	Game game;
	PopupMenu popupMenu;
	GamePanel( Game game ) {
		super();                    
		this.game = game; 
		setLayout( new OverlayLayout( this ) );
		BackgroundLayer backgroundLayer = new BackgroundLayer();
		
		popupMenu = new PopupMenu();
		tagsPanel = new TagsPanel();
		statusPanel = new StatusPanel();
		
		JPanel layer0 = new JPanel( new BorderLayout() );
		layer0.setOpaque( false );
		
		layer0.add( statusPanel, BorderLayout.NORTH );
		layer0.add( tagsPanel, BorderLayout.SOUTH );
		
		conditionalBackground();
		setBorder( BorderFactory.createEmptyBorder( 7, 7, 7, 7 ));
		
		updateTags();
		
		add( layer0 );
		add( backgroundLayer );
		
	}
	

	private void conditionalBackground() {
	   if ( game.isCompressed() ) {
			 setBackground( Color.GRAY);
		} else {                          
			 setBackground( Color.GREEN );		
		}
	}
	
	public void updateTags() {
		tagsPanel.removeAll();
		for ( String tag : game.getTags() ) {
			tagsPanel.add( new Tag( tag ) );
		}
	}
	
	class BackgroundLayer extends JPanel {
	
		int DEFAULT_WIDTH = 200;
		int DEFAULT_HEIGHT = 200;
		
		int imageWidth = 0;
		int imageHeight = 0;
		
		Image screenshot = null;
		
		BackgroundLayer() {
			super( new BorderLayout());
			
			setOpaque( false );
			add( new Title() );
			
			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked ( MouseEvent e ) {
					if ( SwingUtilities.isRightMouseButton(e)) {
						popupMenu.show( BackgroundLayer.this , e.getX(), e.getY());
					}
				}
			});
		}
		private class Title extends JLabel {
			Title() {
				super( "<html><p style=text-align:center;>" + game.getName() +"</p></html>" , SwingConstants.CENTER );
				setFont( new Font( "Courier New", Font.PLAIN, 24 ) );
				setForeground( Color.WHITE );
				
			}
		}
		
		@Override
		public Dimension getPreferredSize() {
			Dimension size = new Dimension( DEFAULT_WIDTH, DEFAULT_HEIGHT );
			File[] screenshots = game.getScreenshots();
			
			if ( screenshots.length > 0 ) {			
				setOpaque( true );
				
				try {
					screenshot = ImageIO.read( screenshots[0] );
					
					float widthSc = size.width / (float) screenshot.getWidth( null );
					float heightSc = size.height / (float) screenshot.getHeight( null );
					
					float scale = heightSc;
					
					imageWidth = (int)( screenshot.getWidth( null ) * scale );
					imageHeight = (int)( screenshot.getHeight( null ) * scale );
					
					size.width = imageWidth;
					size.height = imageHeight;
					
					
					
				} catch ( IOException ex ) {
				   System.out.println( ex.getMessage() );
				}
				
			} else {
				screenshot = null;
				setOpaque( false );
			}
			
			return size;
		}
		@Override
		public void paintComponent( Graphics g ) {
			super.paintComponent( g );
			
			if ( screenshot != null ) {
				g.drawImage( screenshot, 0, 0, imageWidth, imageHeight , this );			
			}
			
		}
		
	}
	
	class PopupMenu extends JPopupMenu { 
		JMenuItem play;
		JMenuItem compress;
		JMenuItem decompress;
		JMenuItem openFolder;
		
		PopupMenu() {
			super();			
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
					game.play();
				}
			});
			
			compress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					GamePanel.this.setBackground( Color.LIGHT_GRAY );
					game.compress( new OnSuccessListener() {
						@Override
						public void onSuccess() {
							GamePanel.this.setBackground( Color.GRAY );
						} 
						@Override
						public void onError() { 
							GamePanel.this.setBackground( Color.RED );							
						}
					} );
				}
			});
			
			decompress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					GamePanel.this.setBackground( Color.LIGHT_GRAY );
					game.decompress( new OnSuccessListener() {
						@Override
						public void onSuccess() {
							GamePanel.this.setBackground( Color.GREEN );
						}
						@Override
						public void onError() {
							GamePanel.this.setBackground( Color.RED );
						}
					} );
				}
			});
			
			openFolder.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.openFolder();
				}
			});
		}
		
		@Override
		public void show( Component c, int x, int y ) {
			super.show( c, x, y );
			if ( game.getCompressionState() == Game.COMPRESSION_STATE_FREE ) { 
			
				if ( game.isCompressed() ) {
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
	} // end of class { PopupMenu }
	
	class StatusPanel extends JPanel {
		Tag size;
		StatusPanel() {
			super( new FlowLayout( FlowLayout.RIGHT ) );
			setOpaque( false );
			size = new Tag( Texts.bytesToHumanVerbose( game.length() ) );
			size.setForeground( Color.WHITE );
			size.setBackground( Color.BLACK );
			add( size );
			
		}
		
	}
	
	class Tag extends JLabel {
		Tag( String name ) {
			super( name );
			setOpaque( true );
			setBackground( Color.PINK );
			setBorder( BorderFactory.createEmptyBorder( 2, 3, 0, 3 ));			
			setFont( new Font( "Consolas", Font.PLAIN, 13 ) );
		}
		
	}
	
	class TagsPanel extends JPanel {
		TagsPanel() {
			super( new FlowLayout( FlowLayout.LEFT ) );
			setOpaque( true );
		}
		
		@Override
		public Dimension getPreferredSize () {
			LayoutManager layout = getLayout();
			if ( layout != null ) {
				layout.layoutContainer( this );
			}
			
			int width = 0;
			int height = 0;
			
			Component[] components = getComponents();
			if ( components.length > 0 ) {
				Rectangle bounds = components[ components.length - 1 ].getBounds();
				width = bounds.width + bounds.x;
				height = bounds.height + bounds.y;
			}
												  
			Insets insets = getInsets();
			
			return new Dimension( width + insets.left + insets.right, height + insets.top + insets.bottom );
		}
	} // end of class { TagsLayer }
}