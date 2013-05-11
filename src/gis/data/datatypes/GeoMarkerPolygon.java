package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerPolygon extends GeoMarker {
	
	public List<Coordinate[]> polygons;
	public Color color = new Color(1, 0, 1, 0.5f);//rgba
	
	public GeoMarkerPolygon(ElementId id, List<Coordinate[]> polygons) {
		super(id);
		this.polygons = polygons;
	}

	
	public void paint(Graphics g, List<Polygon> polygons) {
		g.setColor(color);
		for (Polygon polygon : polygons) {
			g.fillPolygon(polygon);
		}
		g.setColor(Color.BLACK);
		for (Polygon polygon : polygons) {
			g.drawPolygon(polygon);
		}
	}
	
}
