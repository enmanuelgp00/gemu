package gemu.frame.main.gameshelf;

import gemu.game.Game;
import java.util.List;
import java.util.Set;
import javax.swing.JScrollPane;

public class GameShelf extends JScrollPane {
	GameListPane gameListPane;
	List<Game> gamels;
	public GameShelf( List<Game> gamels ) {
		super();
		gameListPane = new GameListPane();
		this.gamels = gamels;
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