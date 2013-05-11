package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class GeoMarkerPoint extends GeoMarker {

	protected double lat, lon;
	protected Color color;
	protected int radius;
	
	public GeoMarkerPoint(ElementId id, double lat, double lon) {
		super(id);
		this.lat = lat;
		this.lon = lon;
		this.color = new Color(1, 0, 0, 0.5f);//rgba
		this.radius = 3;
	}
	
	public GeoMarkerPoint(ElementId id, double lat, double lon, Color color, int radius) {
		super(id);
		this.lat = lat;
		this.lon = lon;
		this.color = color;
		this.radius = radius;
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	@Override
	public void paint(Graphics g, Point p) {
		g.setColor(color);
		int diameter = 2 * radius;
		g.fillOval(p.x - radius, p.y - radius, diameter, diameter);
		
		g.setColor(Color.BLACK);
		g.drawOval(p.x - radius, p.y - radius, diameter, diameter);
	}

}
