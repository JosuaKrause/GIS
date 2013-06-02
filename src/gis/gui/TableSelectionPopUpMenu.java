package gis.gui;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class TableSelectionPopUpMenu extends JPopupMenu {
	
	@SuppressWarnings("unused")
	private final GisPanel gisPanel;
	
	public TableSelectionPopUpMenu(final GisPanel gisPanel) {
		this.gisPanel = gisPanel;
		
		this.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
				//do nothing
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
				gisPanel.repaint();
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent arg0) {
				//do nothing
			}
		});
	}
	
}
