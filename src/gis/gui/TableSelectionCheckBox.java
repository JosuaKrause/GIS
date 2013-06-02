package gis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import gis.data.datatypes.Table;
import gis.data.db.Database;

import javax.swing.JCheckBox;

@SuppressWarnings("unused")
public class TableSelectionCheckBox extends JCheckBox {
	
	private final GisPanel gisPanel;
	private final Database db;
	private final Table table;
	
	public TableSelectionCheckBox(final GisPanel gisPanel, final Database db, final Table table) {
		super(table.name);
		this.gisPanel = gisPanel;
		this.db = db;
		this.table = table;
		
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isSelected()) {
					gisPanel.addGeoMarkerList(db.getGeometry(table));
				} else {
					gisPanel.removeGeoMarkers(table);
				}
				gisPanel.repaint();
			}
		});
	}
	
	Table getTable() {
		return table;
	}
}
