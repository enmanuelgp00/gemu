package gemu.frame.home.shelf;

import gemu.common.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;

public class Banner extends JPanel {
	Image img = null;
	Banner() {
		super();
		setLayout( new BorderLayout());
		setMinimumSize( new Dimension( 0, 200 ));
		try {
			img = ImageIO.read( new File("build/main_screenshot.jpg") );			
		} catch ( Exception e ) {}
		
		
		
	}
	@Override
	public void paintComponent( Graphics g ) {
		Graphics2D g2 = (Graphics2D) g;
		double scale = (double)img.getHeight( null ) / (double)img.getWidth( null );
		double with = getWidth();
		double height = with * scale;
		g2.drawImage( img, 0, 0, (int)with, (int)height, this );
	}
	
}