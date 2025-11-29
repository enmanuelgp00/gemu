package gemu.frame.home.shelf;

import gemu.common.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

public class Banner extends JPanel {
	BufferedImage bufferedImage = null;
	private double imageScaledHeight;
	Banner() {
		super();
		setLayout( new BorderLayout());
		setMinimumSize( new Dimension( 0, 100 ));
		try {
			bufferedImage = ImageIO.read( new File("build/main_screenshot.jpg") );
		} catch ( Exception e ) {}
		
		
		
	}
	
	public int getImageScaledHeight() {
		return (int)imageScaledHeight;
	}
	
	@Override
	public void paintComponent( Graphics g ) {
		//Graphics2D g2 = bufferedImage.createGraphics();		
		Graphics2D g2d = ( Graphics2D)g;		
		g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		
		double scale = (double)bufferedImage.getHeight( null ) / (double)bufferedImage.getWidth( null );
		double with = getWidth();
		imageScaledHeight = with * scale;
		
		setMaximumSize( new Dimension( 0, (int)imageScaledHeight ));
		BufferedImage intGray = new BufferedImage( bufferedImage.getWidth(),bufferedImage.getHeight() , BufferedImage.TYPE_BYTE_GRAY );
		Graphics2D g2dGray = intGray.createGraphics();
		g2dGray.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g2dGray.drawImage( bufferedImage, 0, 0, null );
		g2dGray.dispose();
		
		g2d.drawImage( intGray, 0, 0, (int)with, (int)imageScaledHeight, this );
		g2d.dispose();
		
	}
	
}