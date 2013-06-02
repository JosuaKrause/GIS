package gis.gui;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Database;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GisControlPanel extends JPanel {
	
	private final Database db = new Database();
	
	private final List<TableSelectionCheckBox> tableSelectionCheckBoxes = new ArrayList<TableSelectionCheckBox>();
	private final GisPanel gisPanel;
	
	private SelectionManager selectionManager = new SelectionManager();
	
	public GisControlPanel(GisPanel gisPanel) {
		this.gisPanel = gisPanel;
		
		for (Table t : Table.values()) {
			addTableSelectionCheckBox(gisPanel, db, t);
		}
		
		setSize(getMinimumSize());
		
		addGisPanelListeners();
	}
	
	private void addTableSelectionCheckBox(final GisPanel gisPanel, final Database db, final Table table) {
		TableSelectionCheckBox box = new TableSelectionCheckBox(gisPanel, db, table);
		tableSelectionCheckBoxes.add(box);
		add(box);
	}
	
	private void addGisPanelListeners() {
		gisPanel.addMouseListener(new MouseSelectionListener(gisPanel, this));
	}
	
	private List<Table> getSelectedTables() {
		List<Table> tables = new ArrayList<Table>();
		for (TableSelectionCheckBox box : tableSelectionCheckBoxes) {
			if (box.isSelected()) {
				tables.add(box.getTable());
			}
		}
		return tables;
	}
	
	public boolean processSelectionClick(Point p, Coordinate c) {
		List<Table> tables = getSelectedTables();
		
		if (tables.size() > 0) {
			List<ElementId> ids = db.getByCoordinate(c, getSelectedTables(), gisPanel.getMeterPerPixel() * 5);
			List<GeoMarker> markers = new ArrayList<GeoMarker>();
			for (ElementId id : ids) {
				GeoMarker m = gisPanel.getGeoMarker(id);
				if (m != null) {
					markers.add(m);
				}
			}
			if (markers.size() == 1) {
				return selectionManager.clickedOn(markers.get(0));
			} else {
				JPopupMenu menu = new TableSelectionPopUpMenu(gisPanel);
				for (GeoMarker m : markers) {
					menu.add(new TableSelectionMenuItem(m, selectionManager));
				}
				menu.show(gisPanel, p.x, p.y);
			}
		}
		return false;
	}
	
}
