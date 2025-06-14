package gemu.frame.tagging;

import gemu.game.Game; 
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;   
import java.awt.event.*; 
import java.util.Set; 
import java.util.HashSet; 
import java.util.Arrays;

public class TagCheckBox extends JCheckBox {
	TagCheckBox( Game game, String tag ) {
		super( tag );
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
				
			}
		});
	}
}