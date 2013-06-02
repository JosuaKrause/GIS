package gis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gis.data.datatypes.GeoMarker;

import javax.swing.JMenuItem;

@SuppressWarnings("unused")
public class TableSelectionMenuItem extends JMenuItem {
	
	private final GeoMarker marker;
	private final SelectionManager selectionManager;
	
	public TableSelectionMenuItem(final GeoMarker marker, final SelectionManager selectionManager) {
		
		this.marker = marker;
		this.selectionManager = selectionManager;
		
		if (marker.info == null) {
			setText(marker.id.toString());
		} else {
			setText(marker.info);
		}
		
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectionManager.clickedOn(marker);
			}
		});
	}
	
}
