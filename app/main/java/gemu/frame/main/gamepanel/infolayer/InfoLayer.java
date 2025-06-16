package gemu.frame.main.gamepanel.infolayer;

import gemu.game.Game;

import javax.swing.*;
import java.awt.*;

public class InfoLayer extends JPanel {

	InfoTopBar infoTopBar;
	InfoBottomBar infoBottomBar;	
	Game game;
	
	public InfoLayer( Game game ) {	
		super( new BorderLayout() );
		this.game = game;
		
		infoTopBar = new InfoTopBar( game );
		infoBottomBar = new InfoBottomBar( game );
				
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
	
	public void updateTags() {
		infoBottomBar.updateTags();
	}

	public void updateFileLength() {
		infoTopBar.updateFileLength();
	}
	
}