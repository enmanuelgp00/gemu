package gemu.frame.main;


import gemu.game.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
	Game game;
	PopupMenu popupMenu;
	GamePanel( Game game ) {
		super();
		popupMenu = new PopupMenu();
		this.game = game;
		setLayout( new OverlayLayout( this ) );
		setPreferredSize( new Dimension( 200, 200 ));
		setBackground( Color.GREEN );
		setBorder( BorderFactory.createEmptyBorder( 7, 7, 7, 7 ));
		add( new Body() );
		this.game = game;
	}
	
	class Body extends JPanel {
		Body() {
			super( new BorderLayout());
			setOpaque( false );
			String name = "<html><p style=text-align:center;>" + game.getName() +"</p></html>";
			
			JLabel label = new JLabel( name , SwingConstants.CENTER );
			add( label );
			
			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked ( MouseEvent e ) {
					if ( SwingUtilities.isRightMouseButton(e)) {
						popupMenu.show( Body.this , e.getX(), e.getY());
					}
				}
			});
		}
	}
	
	class PopupMenu extends JPopupMenu { 
		JMenuItem play;
		JMenuItem compress;
		JMenuItem decompress;
		JMenuItem openFolder;
		
		PopupMenu() {
			super();			
			JMenuItem[] items = new JMenuItem[] {				   
				play = new JMenuItem("Play Game"),
				compress = new JMenuItem("Compress"),
				decompress = new JMenuItem("Decompress"),
				openFolder = new JMenuItem("Open Folder")
			};
			
			for ( JMenuItem item : items ) {
				add( item);
			}
			
			play.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.play();
				}
			});
			
			compress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.compress();
				}
			});
			
			decompress.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.decompress();
				}
			});
			
			openFolder.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed( ActionEvent e ) {
					game.openFolder();
				}
			});
		}
		
		@Override
		public void show( Component c, int x, int y ) {
			super.show( c, x, y );
			if ( game.isCompressed() ) {
				play.setEnabled( false );
				compress.setEnabled( false );
				decompress.setEnabled( true );
			} else {                        
				play.setEnabled( true );
				compress.setEnabled( true );
				decompress.setEnabled( false );
			}
		}
	}
}