package gemu.frame.main.gameshelf;

import gemu.frame.main.SearchGamePanel;
import gemu.game.Game;
import java.util.List;
import java.util.Set;
import javax.swing.JScrollPane;

public class GameShelf extends JScrollPane {
	GameListPane gameListPane;
	List<Game> gamels;
	public GameShelf( List<Game> gamels, SearchGamePanel searchGamePanel ) {
		super();
		gameListPane = new GameListPane( searchGamePanel );
		this.gamels = gamels;
		setGameList( gamels );
		setViewportView( gameListPane );
		setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		getVerticalScrollBar().setUnitIncrement(20);
		
	}
	
	public void setGameList( List<Game> list ) {
		gameListPane.setList( list );
	}
	
	public void setGameList( Set<Game> list ) {
		gameListPane.setList( list );
	}
	
	
}