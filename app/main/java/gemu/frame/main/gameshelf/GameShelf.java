package gemu.frame.main.gameshelf;

import gemu.game.Game;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class GameShelf extends JScrollPane {
	GameListPane gameListPane;
	
	public GameShelf( List<Game> gamels ) {
		super();
		gameListPane = new GameListPane();
		setGameList( gamels );
		setViewportView( gameListPane );
		setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		
		
	}
	
	public void setGameList( List<Game> list ) {
		gameListPane.setList( list );
	}
	
	public void setGameList( Set<Game> list ) {
		gameListPane.setList( list );
	}
	
	
}