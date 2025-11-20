package gemu.frame.main.gamepanel;

import gemu.io.File;
import gemu.game.Game;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

class MainLayer extends JPanel {
	
		public static int DEFAULT_WIDTH = 200;
		public static int DEFAULT_HEIGHT = 200;
		
		int imageWidth = 0;
		int imageHeight = 0;
		
		Image screenshot = null;
		
		Game game;
		
		MainLayer( Game game ) {
			super( new BorderLayout());
		
			this.game = game;
			setOpaque( false );
			add( new Title() );
			
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
				File source = screenshots[0];
				
				if ( !source.exists() ) {
					game.removeScreenshot( source );
					return size;
				}
				
				try {
					screenshot = ImageIO.read( source );
					
					float widthSc = size.width / (float) screenshot.getWidth( null );
					float heightSc = size.height / (float) screenshot.getHeight( null );
					
					float scale = heightSc;
					
					imageWidth = (int)( screenshot.getWidth( null ) * scale );
					imageHeight = (int)( screenshot.getHeight( null ) * scale );
					
					size.width = imageWidth;
					size.height = imageHeight;
					
					
					
				} catch ( IOException ex ) {
				   System.out.println( source.getAbsolutePath() + ex.getMessage() );
				   ex.printStackTrace();
				   
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