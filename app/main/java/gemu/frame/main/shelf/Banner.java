package gemu.frame.main.shelf;

import gemu.common.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import gemu.game.*;

public class Banner extends JPanel {
	BufferedImage bufferedImage = null;
	private double imageScaledHeight;
	Game game;
	JTextField titleLabel;
	Banner() {
		super();  
		setFocusable( true );
		setBackground( Style.COLOR_SECONDARY );
		setLayout( new OverlayLayout( this ));
		setMinimumSize( new Dimension( 0, 100 ));
		titleLabel = new JTextField() {
			{
				setBackground( null );
				setBorder( BorderFactory.createEmptyBorder( 1, 3, 0, 2 ) );
				setOpaque( false );
				setFont( Style.FONT_LABEL );// new Font( "MS Gothic", Font.PLAIN, 30 ) );
				setForeground( Style.COLOR_FOREGROUND );                      
				setCaretColor( Style.COLOR_FOREGROUND );
				setHorizontalAlignment( SwingConstants.RIGHT );
				setEditable( true );
				addKeyListener( new KeyAdapter() {
					@Override
					public void keyReleased( KeyEvent event ) {
						if ( event.getKeyCode() == (int)'\n' ) {
							getGame().setTitle( getText() );
							Banner.this.requestFocusInWindow();
						}
					}
					@Override
					public void keyTyped( KeyEvent event ) {
						getParent().revalidate();    
						getParent().repaint();
					}
				});
				addFocusListener( new FocusAdapter() {
					@Override
					public void focusLost( FocusEvent event ) {
						setText( game.getTitle() );
						getParent().revalidate();    
						getParent().repaint();
						
					}
				});
			}
			@Override
			public Dimension getMaximumSize() {			
				FontMetrics metrics = getFontMetrics( getFont() );
				int width = metrics.stringWidth( getText() );  
				int height = metrics.getHeight() + metrics.getAscent();
				return new Dimension( width, height );
			}
			@Override
			public void paintComponent( Graphics g ) {
				Graphics2D g2 = (Graphics2D)g.create();
				GradientPaint paint = new GradientPaint( -2, getHeight() / 3, new Color(0,0,0,0),  0 + 2, getHeight() + 15,  Style.COLOR_BACKGROUND );
				g2.setPaint(paint);
				g2.fillRect( 0, 0, getWidth(), getHeight() );  
				super.paintComponent(g);
				g2.dispose();
			}
		};
		
		titleLabel.setAlignmentY( Component.BOTTOM_ALIGNMENT );
		Box box = new Box( BoxLayout.X_AXIS );
		box.add( Box.createHorizontalGlue());
		box.add( titleLabel );
		add( box  );
		
		addMouseListener( new MouseAdapter(){
			@Override
			public void mousePressed( MouseEvent e ) {
				requestFocusInWindow();
			}
		});
	}
	
	public int getImageScaledHeight() {
		return (int)imageScaledHeight;
	}
	public Game getGame() {
		return game;
	}
	
	public void updateBufferedImage() {
		try {
			File cover = game.getCoverImage();
			if ( cover != null ) {
				bufferedImage = ImageIO.read( cover ); 			
			} else {
				bufferedImage = null;
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void setGame( Game game ) {
		this.game = game;
		titleLabel.setText(  game.getTitle() );
		requestFocusInWindow();                   
		updateBufferedImage();
		revalidate();
		repaint();
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		if ( bufferedImage != null ) {       
			Graphics2D g2d = ( Graphics2D)g.create();
			
			g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			
			double scale = (double)bufferedImage.getHeight( null ) / (double)bufferedImage.getWidth( null );
			double with = getWidth();
			imageScaledHeight = with * scale;
			
			setMaximumSize( new Dimension( 0, (int)imageScaledHeight ));
			g2d.drawImage( bufferedImage, 0, 0, (int)with, (int)imageScaledHeight, this );	
			g2d.dispose();
		
		} else {
			super.paintComponent(g);
			
		}
		
	}
	
}