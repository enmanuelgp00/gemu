package gemu.frame.main.gamepanel.infolayer;

import gemu.game.Game;
import javax.swing.*;
import java.awt.*;

public class VersionPane extends Box {
	Tag version;
	public VersionPane( Game game ) {
		super( BoxLayout.Y_AXIS );
		String name = game.getVersion();
		
		if ( name != null ) {
			setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
			version = new Tag( name );
			version.setBackground( Color.WHITE );
			add( Box.createVerticalGlue() );
			add( version );
		}
	}
}