package gemu.frame.main.gamepanel;

import gemu.io.File;
import gemu.game.Game;
import gemu.system.Log;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

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
			ArrayList<File> screenshots = new ArrayList<>( Arrays.<File>asList( game.getScreenshots() ) );  
			setOpaque( true );
			File source = null;
			File[] initScreenshots = game.getScreenshots();
			boolean mainScreenshotFound = false;
			
			for ( File s : initScreenshots ) {
					if (!s.exists()) {
						screenshots.remove(s);
						game.removeScreenshot(s);
					}
					
					if ( s.getName().equals("main_screenshot.png") ) {
						source = s;
						mainScreenshotFound = true;
						
					}
				}
				
			if ( screenshots.size() > 0 ) {
				
				if ( !mainScreenshotFound ) {
					source = screenshots.get(0);
				} else {
				
					for ( File f : screenshots ) {
						if (f.getName().equals("screenshot.jpg") || f.getName().equals("screenshot.png") || f.getName().equals("main_screenshot.png") ) {
							
							if ( game.removeScreenshot(f) ) {
								Log.info("Residual screeshot removed : " + f.getAbsolutePath() );
							} else {
								Log.error("Could not delete residual screenshot");
							}
						}
					}
				}
				
				
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