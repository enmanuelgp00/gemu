package gemu.frame.main.gamepanel.infolayer;

import javax.swing.*;
import java.awt.*;
import gemu.game.Game;
import gemu.frame.main.gamepanel.GamePanel;

public class InfoBottomBar extends Box {
	TagsPane tagsPane;
	InfoBottomBar( GamePanel gamePanel ) {
		super( BoxLayout.X_AXIS );		
		setBackground( Color.GREEN );
		setOpaque( false );
		
		VersionPane versionPane = new VersionPane( game );
		tagsPane = new TagsPane( game );
		
		add( tagsPane );
		add( Box.createHorizontalGlue() );
		add( versionPane );
							
		setMaximumSize( getPreferredSize() );		
	}
	
	public void updateTags() {
		tagsPane.updateTags();
	}
}