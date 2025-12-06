package gemu.frame.main.search;

import gemu.common.*;
import gemu.game.*;
import gemu.frame.main.shelf.*;
import javax.swing.*;
import java.awt.*;

public class ResultComponent extends JLabel {
	BookCover bookCover;
	protected ResultComponent( BookCover bookCover ) {
		super( bookCover.getGame().getTitle() );
		this.bookCover = bookCover;
		setBorder( BorderFactory.createEmptyBorder( 4, 0, 4, 0 ) );
		setForeground( Style.COLOR_FOREGROUND );
		setFont( new Font( "MS Gothic", Font.PLAIN, 14 ));
	}
	
	public BookCover getBookCover() {
		return bookCover;
	}
}