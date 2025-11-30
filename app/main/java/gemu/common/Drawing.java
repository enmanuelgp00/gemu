package gemu.common;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public final class Drawing {
	public static BufferedImage drawHeart( Color color ) {
		return new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB) {
			{
				Graphics2D g2d = this.createGraphics();
				g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
				g2d.setColor( color );
				g2d.fillOval( 3, 5, 15, 15);
				g2d.fillOval( 15, 6, 14, 14);
				g2d.fillPolygon(new int[]{ 4, 15, 28 } , new int[]{ 16, 27, 17 } ,3);
				g2d.dispose();
			}
		};
	}
	
		
}