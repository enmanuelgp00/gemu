package gemu.frame.main;

import java.io.File;
import javax.swing.*;
import java.awt.*;  
import java.awt.event.*;
import gemu.game.Game;  
import java.util.List;


public class MainFrame extends JFrame {

	static final Font FONT_ITEM = new Font("Consolas", Font.PLAIN, 13 ); 
	static final Font FONT_TITLE = new Font("Aerial", Font.BOLD, 21 );
	
	StatusBar statusBar;
	GameDetailPane gameDetailPane;
	
	public MainFrame( List<Game> gameList ) {
		
		super();
		statusBar = new StatusBar();		
		JPanel aside = new JPanel();
		aside.add( new SearchBar() {
			@Override
			public void onSubmit( String search ) {
				System.out.println( search );
			}
		});
		
		JPanel shelf = new PreloadPanel( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
		shelf.setBorder( BorderFactory.createEmptyBorder( 11, 11, 11, 11 ) );
		for ( Game g : gameList ) {
			shelf.add( new GamePanel( g ));			
		}
		JScrollPane shelfScroll = new JScrollPane( shelf );
		shelfScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel main = new JPanel();
		main.setLayout( new BoxLayout( main, BoxLayout.Y_AXIS ) );		
		gameDetailPane = new GameDetailPane( gameList.get(0));
		
		//main.add( gameDetailPane );
		main.add( shelfScroll );
		
		JSplitPane page = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT , aside, main );
		add( page );
		add( statusBar, BorderLayout.SOUTH );
		
		setSize( 800, 600 );
		setVisible( true );
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	abstract class SearchBar extends JPanel {
		JTextField field;
		
		SearchBar() {
			super( );
			setOpaque( false );
			field = new JTextField();
			field.setPreferredSize( new Dimension( 200, field.getPreferredSize().height ) );
						
			field.addKeyListener( new KeyAdapter() {
				@Override
				public void keyPressed( KeyEvent e ) {
					if ( (char) e.getKeyCode() == '\n') {
						onSubmit( field.getText() );
					}
				}
			});
			add( field );
		}
		
		public abstract void onSubmit( String search );
		
	}
	
	class StatusBar extends JPanel {
		JLabel message;
		StatusBar() {
			super( new FlowLayout( FlowLayout.LEFT ) );
			message = new JLabel( "...", SwingConstants.CENTER );
			add( message );
		}
		
		void setMessage( String msg ) {
			message.setText( msg );
		}
	}
	
}