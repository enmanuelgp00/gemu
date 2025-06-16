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
		
		VersionPane versionPane = new VersionPane( gamePanel.getGame() );
		tagsPane = new TagsPane( gamePanel );
		
		add( tagsPane );
		add( Box.createHorizontalGlue() );
		add( versionPane );
							
		setMaximumSize( getPreferredSize() );		
	}
	
	public void refreshTags() {
		tagsPane.refresh();
	}
}