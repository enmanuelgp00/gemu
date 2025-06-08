package gemu.frame.main;

import javax.swing.*;
import java.awt.*;        

class PreloadPanel extends JPanel {
		PreloadPanel( LayoutManager layout ) {
			super( layout );
		}
		// Override to calculate proper preferred size
		@Override
		public Dimension getPreferredSize() {
			// Get the layout manager
			LayoutManager layout = getLayout();
			if (layout != null) {
				// Let FlowLayout arrange components
				layout.layoutContainer(this);
			}
			// Calculate needed dimensions
			int width = getParent() != null ? getParent().getWidth() : 0;
			int height = 0;
			if (getComponentCount() > 0) {
				Rectangle bounds = getComponent(getComponentCount() - 1).getBounds();
				height = bounds.y + bounds.height;
			}
			// Add insets, paddings of the container
			Insets insets = getInsets();
			return new Dimension(
				width + insets.left + insets.right,
				height + insets.top + insets.bottom + 5 // + 5 extra padding 
			);
		}
	}