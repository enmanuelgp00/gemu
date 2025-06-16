package gemu.frame.tagging;

import gemu.game.Game; 
import gemu.frame.main.gamepanel.GamePanel;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;   
import java.awt.event.*; 
import java.util.Set; 
import java.util.HashSet; 
import java.util.Arrays;

public class TagCheckBox extends JCheckBox {
	GamePanel gamePanel;
	Game game;
	
	TagCheckBox( GamePanel gamePanel, String tag ) {
		super( tag );
		this.gamePanel = gamePanel;
		this.game = gamePanel.getGame();
		
		Set<String> tagSet = new HashSet<String>( Arrays.<String>asList( game.getTags() ) );
		if ( tagSet.contains( tag ) ) {
			setSelected( true );
		}
		
		addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged( ItemEvent e ) {
				if ( isSelected() ) {
					game.addTag( tag );
				} else {               
					game.removeTag( tag );
				}
				gamePanel.refreshTags();
			}
		});
	}
}