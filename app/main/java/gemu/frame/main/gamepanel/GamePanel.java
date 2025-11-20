package gemu.frame.main.gamepanel;

import gemu.frame.main.gamepanel.infolayer.InfoLayer;
import gemu.frame.main.SearchGamePanel;
import gemu.game.Game;
import gemu.game.Games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {

	public static Color COLOR_COMPRESSED = Color.LIGHT_GRAY;
	public static Color COLOR_STANDBY = Color.GREEN;
	public static Color COLOR_DELETED = Color.PINK;
	public static Color COLOR_ERROR = Color.RED;

	InfoLayer infoLayer;	
	Game game;
	PopupMenu popupMenu;
	MainLayer mainLayer;
	
	public GamePanel( Game game, SearchGamePanel searchGamePanel ) {
		super();                    
		this.game = game;
		game.setGamePanel( this );
		setLayout( new OverlayLayout( this ) );
		
		setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ));
			
		popupMenu = new PopupMenu( this, searchGamePanel );
		conditionalBackground();
		
		mainLayer = new MainLayer( game );
		infoLayer = new InfoLayer( this );
		
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
	
	public void refreshBackground() {
		mainLayer.revalidate();
		mainLayer.repaint();
		setPreferredSize( mainLayer.getPreferredSize() );
		revalidate();
		repaint();
	}
	
	public void refreshTags() {
		infoLayer.refreshTags();
	}
	
	public void refreshFileLength() {
		infoLayer.refreshFileLength();
	}
	
	public Game getGame() {
		return game;
	}

	private void conditionalBackground() {
		switch( game.getState() ) {
		
			case Games.STATE_DELETED :
				setBackground( COLOR_DELETED );
			break;                        
			case Games.STATE_STANDBY:
				if ( !game.isCompressed() ) {
					setBackground( COLOR_STANDBY );				
				} else {                 
					setBackground( COLOR_COMPRESSED );				
				}
			break;
		}
	}
	
}