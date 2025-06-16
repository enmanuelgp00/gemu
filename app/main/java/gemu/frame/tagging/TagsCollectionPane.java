package gemu.frame.tagging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gemu.game.Game;
import gemu.frame.main.gamepanel.GamePanel;

public class TagsCollectionPane extends JPanel {
	GamePanel gamePanel;
	
	TagsCollectionPane( GamePanel gamePanel ) {
		super();
		this.gamePanel = gamePanel;
		refresh();
		
	}
	public void refresh() {
		Game game = gamePanel.getGame();
		removeAll();
		for ( String tag : Game.tagsCollection ) {
			add( new TagCheckBox( gamePanel, tag ));
		}
		revalidate();
		repaint();
	}
	/*
	@Override
	public Dimension getPreferredSize() {
		LayoutManager layout = getLayout();
		layout.layoutContainer( this );

		int width = 0;
		int height = 0;
		Rectangle bounds = getComponents()[ getComponents().length - 1 ].getBounds();
		width = bounds.x + bounds.width;                                             
		height = bounds.y + bounds.height;
		
		return new Dimension( width, height );
		
	}*/
}