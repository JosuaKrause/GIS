package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerPolygon extends GeoMarker {
	public Coordinate[] polygon;
	public Color color = new Color(1, 0, 1, 0.5f);//rgba
	
	
	public GeoMarkerPolygon(ElementId id, Coordinate[] polygon) {
		super(id);
		this.polygon = polygon;
	}

	
	public void paint(Graphics g, Polygon polygon) {
		if (selected) {
			g.setColor(Color.WHITE);
		} else {
			g.setColor(color);
		}
		g.fillPolygon(polygon);
		g.setColor(Color.BLACK);
		g.drawPolygon(polygon);
	}
}
