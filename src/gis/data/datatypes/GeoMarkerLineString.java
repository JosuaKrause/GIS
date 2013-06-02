package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerLineString extends GeoMarker {

	public Coordinate[] points;
	public Color color = Color.BLUE;
	
	public GeoMarkerLineString(ElementId id, Coordinate[] points) {
		super(id);
		this.points = points;
	}
	
	public void paintLineSegment(Graphics g, Point head, Point tail) {
		if (selected) {
			g.setColor(Color.YELLOW);
		} else {
			g.setColor(color);
		}
		g.drawLine(tail.x, tail.y, head.x, head.y);
	}
}
