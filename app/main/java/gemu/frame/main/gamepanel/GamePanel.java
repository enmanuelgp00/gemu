package gemu.frame.main.gamepanel;

import gemu.frame.main.gamepanel.infolayer.InfoLayer;
import gemu.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {

	InfoLayer infoLayer;	
	Game game;
	PopupMenu popupMenu;
	
	public GamePanel( Game game ) {
		super();                    
		this.game = game; 
		setLayout( new OverlayLayout( this ) );
		
		setBorder( BorderFactory.createEmptyBorder( 7, 7, 7, 7 ));
			
		popupMenu = new PopupMenu( this );
		conditionalBackground();
		
		MainLayer mainLayer = new MainLayer( game );
		infoLayer = new InfoLayer( game );
		
		add( infoLayer );
		add( mainLayer );               
		
		setPreferredSize( mainLayer.getPreferredSize() );		
		
		addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked ( MouseEvent e ) {
				if ( SwingUtilities.isRightMouseButton(e)) {
					popupMenu.show( GamePanel.this , e.getX(), e.getY());
				}
			}
		});
	}
	
		
	public void updateFileLength() {
		infoLayer.updateFileLength();
	}
	public Game getGame() {
		return game;
	}

	private void conditionalBackground() {
	   if ( game.isCompressed() ) {
			 setBackground( Color.GRAY);
		} else {                          
			 setBackground( Color.GREEN );		
		}
	}
	
}