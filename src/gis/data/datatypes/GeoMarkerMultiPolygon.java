package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerMultiPolygon extends GeoMarker {
	
	public List<Coordinate[]> polygons;
	public Color color = new Color(1, 0, 1, 0.5f);//rgba
	
	
	public GeoMarkerMultiPolygon(ElementId id, List<Coordinate[]> polygons) {
		super(id);
		this.polygons = polygons;
	}

	
	public void paint(Graphics g, Polygon polygon) {
		g.setColor(color);
		g.fillPolygon(polygon);
		g.setColor(Color.BLACK);
		g.drawPolygon(polygon);
	}
	
}
