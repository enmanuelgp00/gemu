package gemu.common;

import javax.swing.JLabel;

public class GemuLabel extends JLabel {
	public GemuLabel( String title )  {
		super( title );
		setFont( Style.FONT_MONO_SPACE );
		setForeground( Style.COLOR_FOREGROUND );
	}
	
	public GemuLabel( String title, float size )  {
		super( title );
		setFont( Style.FONT_MONO_SPACE.deriveFont( size ) );
		setForeground( Style.COLOR_FOREGROUND );
	}
}