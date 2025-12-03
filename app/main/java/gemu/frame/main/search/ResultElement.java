package gemu.frame.main.search;

import gemu.common.*;
import gemu.game.*;
import javax.swing.*;
import java.awt.*;

public class ResultElement extends JLabel {
	Game game;
	protected ResultElement( Game game ) {
		super( game.getTitle() );
		this.game = game;
		setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 0 ) );
		setForeground( Style.COLOR_FOREGROUND );
		setFont( new Font( "MS Gothic", Font.PLAIN, 14 ));
	}
	
	public Game getGame() {
		return game;
	}
}