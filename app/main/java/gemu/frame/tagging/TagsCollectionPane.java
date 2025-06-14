package gemu.frame.tagging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gemu.game.Game;

public class TagsCollectionPane extends JPanel {
	TagsCollectionPane( Game game ) {
		super();  
		for ( String tag : Game.tagsCollection ) {
			add( new TagCheckBox( game, tag ));
		}
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