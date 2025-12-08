package gemu.frame.main.shelf;

import java.awt.*;   
import java.awt.image.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import gemu.common.*;
import gemu.game.*;     
import gemu.util.*;

public class BookCover extends JPanel {
	Box boxOnTop;
	final Dimension standardSize = new Dimension( 110, 140 );
	int detailWidth = 0;
	int coverXViewport = 0;
	boolean isMouseInside = false;
	BufferedImage bufferedImage = null;
	Game game;               
	Tag lengthTag;
	Tag zipLengthTag;
	
	public BookCover( Game game ) {
		super();
		this.game = game;
		updateBufferedImage();
		coverXViewport = game.getCoverXViewport();
		setLayout( new OverlayLayout( this ) );
		setPreferredSize( standardSize );
		setBackground( Style.COLOR_SECONDARY.brighter() );
		Box layer0 = new Box( BoxLayout.Y_AXIS );
		
		
		layer0.add( new Box( BoxLayout.X_AXIS ) {
			{
				add( Box.createHorizontalGlue());
				add( new GemuButton("") {
					{
						setPreferredSize( new Dimension( 30, 30));
						setMaximumSize( new Dimension( 30, 30 ));
						setBackground( Style.COLOR_BACKGROUND );
					}
					@Override
					public void paintComponent( Graphics g ) {
						Graphics2D g2d = (Graphics2D)g.create();
						g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
						Color alphacolor = new Color( 100, 100, 100, 100 );
						g2d.drawImage( Drawing.drawHeart( alphacolor ), 0, 0, getWidth(), getHeight(), this );
						g2d.dispose();
					}
				});
			}
		});
		
		layer0.add( Box.createVerticalGlue());
		layer0.add( new Box( BoxLayout.X_AXIS ) {
			{
				add( new Box( BoxLayout.Y_AXIS ) {
					{
						int alpha = 200;
						lengthTag =  new Tag(
							HumanVerbose.bytes( game.getLength(), HumanVerbose.DECIMAL_BYTES ),
							new Color( 200, 200, 200, alpha ),
							Color.BLACK );
							
						Color dftBackground = Style.COLOR_BACKGROUND;	
						zipLengthTag = new Tag(
							HumanVerbose.bytes( game.getZipLength(), HumanVerbose.DECIMAL_BYTES ),
							new Color( dftBackground.getRed(), dftBackground.getGreen(), dftBackground.getBlue(), alpha ),
							Color.WHITE );
							
						setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
						add( lengthTag );
						add( Box.createRigidArea( new Dimension( 2, 2 )));
						add( zipLengthTag );
						lengthTag.setMaximumSize( new Dimension( 100, getMaximumSize().height ) );
						zipLengthTag.setMaximumSize( new Dimension( 100, getMaximumSize().height ) );
					}
				} );
				add( Box.createHorizontalGlue() );
			}
		} );
		
		add( layer0 );
		setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1));
		addMouseMotionListener( expandOnRollover );  
		addMouseListener( expandOnRollover );
		
	}
	
	public void updateLengthTags() {
		lengthTag.setText( HumanVerbose.bytes( game.getLength(), HumanVerbose.DECIMAL_BYTES ));
		zipLengthTag.setText( HumanVerbose.bytes( game.getZipLength(), HumanVerbose.DECIMAL_BYTES ) );
	}
	
	private class Tag extends Box {
		Color background;
		JLabel label;
		Tag( String name, Color background, Color foreground ) {
			super( BoxLayout.X_AXIS );       
			this.background = background;
			setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ));
			add( Box.createHorizontalGlue());
			label = new JLabel( name ) { 
				{
					setFont( Style.FONT_MONO_SPACE );
					setForeground( foreground );
				}
			};
			add( label );
		}
		public void setText( String name ) {
			label.setText( name );
		}
		@Override
		public void paintComponent( Graphics g ) { 
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g2.setColor( background );
			g2.fillRoundRect( 0, 0, getWidth(), getHeight(), 10, 10 );
			g2.dispose();
		}
	}
	
	public void updateBufferedImage() { 
		try {
			File f = game.getCoverImage();
			if ( f != null ) {
				bufferedImage = ImageIO.read( f );
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setIsMouseInside( Boolean beans ) {
		isMouseInside = beans;
	}
	public boolean isMouseInside() {
		return isMouseInside;
	}
	int initialX = -1;
	int initialY = -1;
	MouseAdapter expandOnRollover = new MouseAdapter() {
		int initialMouseX = 0;
		@Override
		public void mousePressed( MouseEvent event ) {
		
			if ( event.getButton() == MouseEvent.BUTTON2 ) {                                           
				initialMouseX = event.getX() + coverXViewport;	
			}
			//setBounds( initialX + 1, initialY + 1, standardSize.width -2, standardSize.height - 2  );
			
		}
		@Override
		public void mouseReleased( MouseEvent event ) {
			if ( event.getButton() == MouseEvent.BUTTON2){ 
				getGame().setCoverXViewport(coverXViewport);
			}
			/*
			if ( isMouseInside ) {
				setBounds( initialX - 2, initialY - 2, standardSize.width + 4, standardSize.height + 4 );			
			}
			*/
		}
		@Override
		public void mouseMoved( MouseEvent event ) {
			/*
			if ( initialX == -1 ) {
				initialX = getX();
			}                       
			if ( initialY == -1 ) {
				initialY = getY();
			}
			if ( contains(event.getPoint())) {
				setBounds( initialX - 2, initialY - 2, standardSize.width + 4, standardSize.height + 4 );
			}
			*/
			//setIsMouseInside( true );
		}
		@Override
		public void mouseEntered( MouseEvent event ) {
			//setComponentZOrder( component, 0 )
			setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
		}   
		
		@Override
		public void mouseExited( MouseEvent event ) {
			setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ));
			/*
			if ( !contains(event.getPoint())) {
				setPreferredSize( standardSize );
				revalidate();
				repaint();   
				//setIsMouseInside( false );
				initialX = -1;
				initialY = -1;
				
			}
			*/
			
		}
		@Override
		public void mouseDragged( MouseEvent event ) {     
			
			if ( (event.getModifiersEx() & InputEvent.BUTTON2_DOWN_MASK) != 0 ) {
				coverXViewport = initialMouseX - event.getX();
				revalidate();
				repaint();			
			}
		}
	};
	
	@Override
	public void paintComponent( Graphics g ) {        
		Graphics2D g2 = (Graphics2D)g.create();   
		g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		if ( bufferedImage != null ) {  
			double scale = (double)bufferedImage.getWidth( null ) / (double)bufferedImage.getHeight( null ); 
			double height = getHeight();
			double width = scale * height;
			detailWidth = (int)width;
			int x;
			if ( getWidth() == detailWidth ) {
				x = 0;			
			} else {
				x = (getWidth() - detailWidth) / 2;
			}
			int pos = x - coverXViewport;
			if ( pos > - (detailWidth - getWidth()) && pos < 0 ) {
				
					/*
					BufferedImage intGray = new BufferedImage( bufferedImage.getWidth(),bufferedImage.getHeight() , BufferedImage.TYPE_BYTE_GRAY );
					Graphics2D g2dGray = intGray.createGraphics();
					g2dGray.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
					g2dGray.drawImage( bufferedImage, 0, 0, null );
					g2dGray.dispose();                                                            
					g2.drawImage( intGray, pos, 0, (int)width, (int)height, this );
					*/
														  
				g2.drawImage( bufferedImage, pos, 0, (int)width, (int)height, this );
				
			}
		} else {
			super.paintComponent(g2);			
		} 	
		
		if ( game.isInZip() ) {     
			Color background = Style.COLOR_BACKGROUND;
			g2.setColor( new Color( background.getRed(), background.getGreen(), background.getBlue(), 150 ));
			g2.fillPolygon( new int[]{0, 0, getWidth() }, new int[]{0, getHeight(), getHeight() }, 3 ); 
		}
		g2.dispose();	
	}
}