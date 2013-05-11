package gis.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.db.Database;
import gis.util.GeoMarkerList;

import org.openstreetmap.gui.jmapviewer.JMapViewer;


public class GisPanel extends JMapViewer {
	
	private Database db;
	private final GeoMarkerList markers = new GeoMarkerList();
	
	public GisPanel() {
		super();
		this.grabFocus();
		db = new Database();
		
		markers.addAll(db.berlinLocation.getGeometry());
		System.out.println("markers.size() = " + markers.size());
	}
	
	public void addGeoMarkerList(List<GeoMarker> markers) {
		this.markers.addAll(markers);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		//paint markers
		for (GeoMarker m : markers) {
			if (m instanceof GeoMarkerPoint) {
				GeoMarkerPoint gmp = (GeoMarkerPoint)m;
				Point p = getMapPosition(gmp.getLat(), gmp.getLon(), true);
				if (p != null) {
					m.paint(g, p);
				}
			}
		}
	}
	
	
	
}
