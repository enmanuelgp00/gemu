package gemu.frame.pref;

import gemu.common.*;
import javax.swing.*;
import java.util.*;
import java.awt.*;      
import java.awt.geom.*;
import java.awt.event.*;
import gemu.frame.*;
import gemu.frame.main.shelf.*;
import gemu.game.*;

public class PreferencesFrame extends JFrame {
	GemuLabel title = new GemuLabel("") {
		{
			setFont( Style.FONT_LABEL );
			setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ));
		}
	};
	SimpleOption adminOption = new SimpleOption("Needs Admin", new GemuSwitch() {
			@Override
			public boolean isTurnOn() {
				return getBookCover().getGame().needsAdmin();
			}
		}, ( bookCover ) -> { 
			Game game = bookCover.getGame();
			game.setNeedsAdmin( !game.needsAdmin() );
		}
	);  
	SimpleOption lowClockOption = new SimpleOption("Low Clock CPU", new GemuSwitch() {
			@Override
			public boolean isTurnOn() {
				return getBookCover().getGame().usesLowClockCPU();
			}
		}, ( bookCover ) -> { 
			Game game = bookCover.getGame();
			game.setUsesLowClockCPU( !game.usesLowClockCPU() );
		}
	);  
	
	SimpleOption FavoriteOption = new SimpleOption("Favorite", new GemuSwitch() {
			@Override
			public boolean isTurnOn() {
				return getBookCover().getGame().isPinned();
			}
			
		}, ( bookCover ) -> { 
			Game game = bookCover.getGame();
			game.setPinned( !game.isPinned() );
			bookCover.repaint();
		});
		
	SimpleOption DeleteOption = new SimpleOption("", new GemuButton("Delete", 5, 5 ){
			{
				setBackgroundColor( Style.COLOR_BACKGROUND );
				setPressedBackground( new Color( 227, 2, 26 ) );
				setRolloverBackground( new Color( 237, 2, 36 ) );
			}
		}, ( bookCover ) -> {
			new ConfirmationFrame(( isAccepted )->{
				if ( isAccepted ) {
					bookCover.getGame().delete();
					bookCover.repaint();
				}
			});
		});  
	BookCover bookCover;
	
	ArrayList<Box> options = new ArrayList<>( Arrays.<Box>asList( 
		FavoriteOption,
		adminOption,
		lowClockOption,
		DeleteOption
	) );
	
	public PreferencesFrame() {
		super();
		setUndecorated( true );  
		setOpacity( 0.8f );			       
		getContentPane().setBackground( Style.COLOR_BACKGROUND );
		add( title, BorderLayout.NORTH );
		Box box = new Box( BoxLayout.Y_AXIS );
		box.add( Box.createRigidArea( new Dimension( 10, 10 )) );
		options.forEach( option -> box.add( option ));
		add( box );
		setSize( 290, 500 );
		setLocationRelativeTo( null );
		addWindowFocusListener( new WindowAdapter() {
			@Override
			public void windowLostFocus( WindowEvent event ) {
				setVisible( false );
				dispose();
			}
		});
		addMouseListener( transparentOnMouseExited );
		addMouseListener( moveOnDragging );
		addMouseMotionListener( moveOnDragging );
	}
	
	MouseListener transparentOnMouseExited = new MouseAdapter() {
		@Override
		public void mouseEntered( MouseEvent event ) {
			setOpacity( 1.0f );
		}
		@Override
		public void mouseExited( MouseEvent event ) { 
			if ( !contains( event.getPoint() ) ) {
				setOpacity( 0.8f );			
			}
		}
	};
	MouseAdapter moveOnDragging = new MouseAdapter() {
		Point mouseInitPos;
		@Override
		public void mousePressed( MouseEvent event ) {
			mouseInitPos = event.getPoint();
		}
		@Override
		public void mouseDragged( MouseEvent event ) {
			Point location = getLocation();
			Point pos = event.getPoint();
			setLocation( new Point( 
				location.x + pos.x - mouseInitPos.x,
				location.y + pos.y - mouseInitPos.y ));
			
		}
	};
	
	@FunctionalInterface
	private interface OptionAction {
		public void act( BookCover bookCover );
	}	
	
	public void setBookCover( BookCover bookCover ) {
		this.bookCover = bookCover;
		title.setText( bookCover.getGame().getTitle() );
		setSize( new Dimension( 290 , getPreferredSize().height ) );
		setShape( new RoundRectangle2D.Double( 0, 0, getWidth(), getHeight(), 5, 5 ));
	}
	
	public BookCover getBookCover() {
		return bookCover;
	}
	
	private class SimpleOption extends Box {
		JComponent button;
		SimpleOption( String title, JComponent button, OptionAction optionAction ) {
			super( BoxLayout.X_AXIS );
			setBorder( BorderFactory.createEmptyBorder( 7, 19, 7, 7 ) );
			GemuLabel label = new GemuLabel( title );
			button.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked( MouseEvent event ) {
					optionAction.act( getBookCover() );
				}
			});
			
			add( label );
			add( Box.createHorizontalGlue() );
			add( button );
		}
		
		JComponent getButton() {
			return button;
		}
	}
}