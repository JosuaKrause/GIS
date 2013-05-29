package gis.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.datatypes.GeoMarkerMultiPolygon;
import gis.data.datatypes.GeoMarkerPolygon;
import gis.data.datatypes.Table;
import gis.data.db.Database;
import gis.util.GeoMarkerList;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmMercator;


public class GisPanel extends JMapViewer {
	
	private final GeoMarkerList markers = new GeoMarkerList();
	
	boolean drawImage = false;
	private BufferedImage image;
	
	public GisPanel() {
		super();
		this.grabFocus();
		updateImage();
		
		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				updateImage();
			}
			
		});
	}
	
	public void addGeoMarkerList(List<GeoMarker> markers) {
		this.markers.addAll(markers);
	}
	
	public void removeGeoMarkers(Table table) {
		markers.removeAll(table);
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
					
				int[] x = new int[poly.polygon.length];
				int[] y = new int[poly.polygon.length];
					
				for (int i = 0; i < poly.polygon.length; ++i) {
					Point p = getMapPosition(poly.polygon[i], false);
					x[i] = p.x;
					y[i] = p.y;
				}
					
				poly.paint(g, new Polygon(x, y, x.length));
				
			} else if (m instanceof GeoMarkerMultiPolygon) {
				GeoMarkerMultiPolygon poly = (GeoMarkerMultiPolygon)m;
				for (Coordinate[] polygon : poly.polygons) {
					
					int[] x = new int[polygon.length];
					int[] y = new int[polygon.length];
					
					for (int i = 0; i < polygon.length; ++i) {
						Point p = getMapPosition(polygon[i], false);
						x[i] = p.x;
						y[i] = p.y;
					}
					
					poly.paint(g, new Polygon(x, y, x.length));
				}
				
			}
			
		}
		if (drawImage) {
			paintImage(g);
		}
	}
	
	private void paintImage(Graphics g) {
		Insets insets = getInsets();
		g.drawImage(image,
				insets.left,//x
				insets.top,//y
				null);//ImageObserver
	}
	
	private void updateImage() {
		Insets insets = getInsets();
		Dimension dim = getSize();
		int width = Math.max(dim.width - insets.left - insets.right, 1);
		int height = Math.max(dim.height - insets.top - insets.bottom, 1);
		image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		
		//draw chess board pattern
		int alpha = 32;
		int tileSize = 8;
		final int whiteRgb = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
		final int blackRgb = (alpha << 24);
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				if ((x / tileSize + y / tileSize) % 2 == 0) {
					image.setRGB(x, y, whiteRgb);
				} else {
					image.setRGB(x, y, blackRgb);
				}
			}
		}
		
//		//draw distance transformation
//		for (int y = 0; y < image.getHeight(); ++y) {
//			for (int x = 0; x < image.getWidth(); ++x) {
//				double lon = OsmMercator.XToLon(x, zoom);
//				double lat = OsmMercator.YToLat(y, zoom);
//		        Coordinate c = new Coordinate(lat, lon);
//		       
//			}
//		}
	}
	
	//SELECT * FROM berlin_poi WHERE name LIKE 'Museum%';
	
	//TODO code reference
//	public Coordinate getPosition() {
//        double lon = OsmMercator.XToLon(center.x, zoom);
//        double lat = OsmMercator.YToLat(center.y, zoom);
//        return new Coordinate(lat, lon);
//    }
	
}
