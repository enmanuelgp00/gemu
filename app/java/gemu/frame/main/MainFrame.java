package gemu.frame.main;

import gemu.frame.*;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import gemu.game.Game;


public class MainFrame extends JFrame {
	final Font itemFont = new Font("Consolas", Font.PLAIN, 13 ); 
	final Font titleFont = new Font("Aerial", Font.BOLD, 21 );
	public MainFrame( List<Game> gameList ) {
		
		super();		
		JPanel panel = new PreloadPanel( new FlowLayout( FlowLayout.CENTER, 5, 5 ) );
		panel.setBackground( Color.BLACK );
		for ( Game g : gameList ) {
			//if ( !g.isCompressed() ) {
				panel.add( new GamePanel( g ));
			//}
		}
		JScrollPane scroll = new JScrollPane( panel );
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add( scroll );
		setSize( 1280, 720 );
		setVisible( true );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	class GamePanel extends JPanel {
		Image imageBackground = null;
		GamePanel( Game game ) {
			super();
			final int WIDTH = 200;
			final int HEIGHT = 200;
			setLayout( new OverlayLayout( this ));
			setBackground( new Color(0x00aa00));
			setPreferredSize( new Dimension( WIDTH, HEIGHT ));
			setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );						
			
			JPanel tags = new PreloadPanel( new FlowLayout( FlowLayout.LEFT )); 
			tags.setBackground( new Color(0x00dddd));
			for ( String tag : game.getTags() ) {
				tags.add(  new TagLabel( tag ) );
			}
			
			tags.setOpaque( false );
			JPanel tagWrapper = new JPanel( new BorderLayout());
			tagWrapper.setOpaque( false );
			tagWrapper.add( tags, BorderLayout.SOUTH );
			add( tagWrapper ); 
			
			List<File> screenshots;
			if ( ( screenshots = game.getScreenshots()) != null ) {
				try {
					imageBackground = ImageIO.read( screenshots.get(0));
				} catch ( IOException e ){
					e.printStackTrace();
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
							game.openFolder();
						break;               
						case MouseEvent.BUTTON2:
						break;
						case MouseEvent.BUTTON3:
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
				setFont( titleFont );
				setForeground( Color.WHITE );
				setBackground( new Color(0x004444));
				setOpaque( false );
			}
		}
		
		class TagLabel extends JLabel {
			TagLabel( String title ) {
				super( title ); 
				setOpaque( true );
				setFont( itemFont );
				setBackground( Color.PINK );
				int bottom = 0;
				int top = 3;
				int left = 3;
				int right = 3;
				setBorder( BorderFactory.createEmptyBorder( top, right, bottom, left ));
				
			}
		}
	}
	
	class PreloadPanel extends JPanel {
		PreloadPanel( LayoutManager layout ) {
			super( layout );
		}
		// Override to calculate proper preferred size
		@Override
		public Dimension getPreferredSize() {
			// Get the layout manager
			LayoutManager layout = getLayout();
			if (layout != null) {
				// Let FlowLayout arrange components
				layout.layoutContainer(this);
			}
			// Calculate needed dimensions
			int width = getParent() != null ? getParent().getWidth() : 0;
			int height = 0;
			if (getComponentCount() > 0) {
				Rectangle bounds = getComponent(getComponentCount() - 1).getBounds();
				height = bounds.y + bounds.height;
			}
			// Add insets, paddings of the container
			Insets insets = getInsets();
			return new Dimension(
				width + insets.left + insets.right,
				height + insets.top + insets.bottom + 5 // + 5 extra padding 
			);
		}
	}
	
}