package gemu.frame.main.gamepanel.infolayer;

import gemu.game.Game;

import javax.swing.*;
import java.awt.*;
import gemu.frame.main.gamepanel.GamePanel;

public class InfoLayer extends JPanel {

	InfoTopBar infoTopBar;
	InfoBottomBar infoBottomBar;	
	GamePanel gamePanel;
	Game game;
	
	public InfoLayer( GamePanel gamePanel ) {	
		super( new BorderLayout() );
		
		this.gamePanel = gamePanel;
		this.game = gamePanel.getGame();
		
		infoTopBar = new InfoTopBar( game );
		infoBottomBar = new InfoBottomBar( gamePanel );
				
		setOpaque( false );		
		
		add( infoTopBar, BorderLayout.NORTH );
		add( infoBottomBar, BorderLayout.SOUTH );
		
	}	
	
	@Override
	public Dimension getPreferredSize() {
		LayoutManager layout = getLayout();
		
		for ( Component c : getComponents() ) {
			Rectangle bounds = c.getBounds();
		}
		return super.getPreferredSize();
	}
	
	public void refreshTags() {
		infoBottomBar.refreshTags();
	}

	public void refreshFileLength() {
		infoTopBar.refreshFileLength();
	}
	
}