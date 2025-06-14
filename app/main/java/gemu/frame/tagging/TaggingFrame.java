package gemu.frame.tagging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import gemu.game.Game;

public class TaggingFrame extends JFrame {
	
	Game game;
	TagsCollectionPane tagsCollectionPane;
	public TaggingFrame( Game game ) {
		super();
		setTitle("Tagging: " + game.getName() );
		this.game = game;
		
		add( new Body() );		
		
		setResizable( false );
		setSize( 500, 500 );
		setLocationRelativeTo( null );
		setVisible( true );
	}
	
	class Body extends JPanel {
		Body() {
			super();
			setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
			add( new TextField());
			tagsCollectionPane = new TagsCollectionPane( game );
			add( tagsCollectionPane );
		}
		
		private class TextField extends JTextField {
			TextField() {
				super();
				setPreferredSize( new Dimension( 300 , getPreferredSize().height ));
				setMaximumSize( getPreferredSize());
				addKeyListener( new KeyAdapter() {
					@Override
					public void keyPressed( KeyEvent e ) { 
						char ch = (char) e.getKeyCode();   
						if ( ch == '\n' ) {
							String tag = getText();
							if ( tag != "" ) {
								game.addTag( tag );
								setText("");
								if ( !Game.tagsCollection.contains( tag ) ) {
									System.out.println("[" + tag + "] is a new tag ");
									Game.tagsCollection.add( tag );
									tagsCollectionPane.add( new TagCheckBox( game, tag ));
								}
							}
						}
					}        
				});
			}
		}
	}	
}
		