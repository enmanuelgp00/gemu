package gemu.frame.main.search;

import gemu.common.*;
import gemu.game.*;
import javax.swing.*;

public class ResultElement extends JLabel {
	Game game;
	protected ResultElement( Game game ) {
		super( game.getTitle() );
		this.game = game;
		setForeground( Style.COLOR_FOREGROUND );
	}
	
	public Game getGame() {
		return game;
	}
}