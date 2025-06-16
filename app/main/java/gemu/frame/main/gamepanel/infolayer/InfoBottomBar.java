package gemu.frame.main.gamepanel.infolayer;

import javax.swing.*;
import java.awt.*;
import gemu.game.Game;

public class InfoBottomBar extends Box {
	InfoBottomBar( Game game ) {
		super( BoxLayout.X_AXIS );		
		setBackground( Color.GREEN );
		setOpaque( false );
		
		VersionPane versionPane = new VersionPane( game );
		TagsPane tagsPane = new TagsPane( game );
		
		add( tagsPane );
		add( Box.createHorizontalGlue() );
		add( versionPane );
							
		setMaximumSize( getPreferredSize() );		
	}
	
	public void updateTags() {
	
	}
}