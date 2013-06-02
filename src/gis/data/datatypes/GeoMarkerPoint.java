package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerPoint extends GeoMarker {

	public Coordinate coordinate;
	public Color color;
	public int radius;
	
	public GeoMarkerPoint(ElementId id, Coordinate coordinate) {
		super(id);
		this.coordinate = coordinate;
		this.color = new Color(1, 0, 0, 0.5f);//rgba
		this.radius = 3;
	}
	
	public GeoMarkerPoint(ElementId id, Coordinate coordinate, Color color, int radius) {
		super(id);
		this.coordinate = coordinate;
		this.color = color;
		this.radius = radius;
	}
	
	public void paint(Graphics g, Point p) {
		if (selected) {
			g.setColor(Color.CYAN);
		} else {
			g.setColor(color);
		}
		int diameter = 2 * radius;
		g.fillOval(p.x - radius, p.y - radius, diameter, diameter);
		g.setColor(Color.BLACK);
		g.drawOval(p.x - radius, p.y - radius, diameter, diameter);
	}

}
