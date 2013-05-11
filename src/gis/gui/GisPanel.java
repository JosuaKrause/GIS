package gis.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.datatypes.GeoMarkerPolygon;
import gis.data.datatypes.Table;
import gis.data.db.Database;
import gis.util.GeoMarkerList;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;


public class GisPanel extends JMapViewer {
	
	private Database db;
	private final GeoMarkerList markers = new GeoMarkerList();
	
	public GisPanel() {
		super();
		this.grabFocus();
		db = new Database();
		
		markers.addAll(db.getGeometry(Table.BERLIN_ADMINISTRATIVE));
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
				GeoMarkerPoint point = (GeoMarkerPoint)m;
				Point p = getMapPosition(point.coordinate, true);
				if (p != null) {
					point.paint(g, p);
				}
			} else if (m instanceof GeoMarkerPolygon) {
				GeoMarkerPolygon poly = (GeoMarkerPolygon)m;
				List<Polygon> screenPolygons = new ArrayList<Polygon>();
				for (Coordinate[] polygon : poly.polygons) {
					//System.out.println(java.util.Arrays.toString(polygon));
					
					int[] x = new int[polygon.length];
					int[] y = new int[polygon.length];
					
					for (int i = 0; i < polygon.length; ++i) {
						//Point p = getMapPosition(polygon[i].getLat() / 100000, polygon[i].getLon()/100000, false);
						Point p = getMapPosition(polygon[i], false);
						x[i] = p.x;
						y[i] = p.y;
					}
					
					//System.out.println(java.util.Arrays.toString(x));
					//System.out.println(java.util.Arrays.toString(y));
					
					Polygon screenPolygon = new Polygon(x, y, x.length);
					
					screenPolygons.add(screenPolygon);
				}
				poly.paint(g, screenPolygons);
			}
		}
	}
	
	
	
}
