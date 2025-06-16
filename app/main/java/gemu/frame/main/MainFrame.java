package gemu.frame.main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;  
import java.util.ArrayList;
import gemu.game.Game;                     
import gemu.frame.main.gameshelf.GameShelf;

public class MainFrame extends JFrame {
	public MainFrame( List<Game> gamels ) {
		add( new SearchGamePanel ( gamels ) );
		setTitle("[ " + gamels.size() + " ] Games ");
		setSize( 800, 600 );
		setVisible( true );
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
}