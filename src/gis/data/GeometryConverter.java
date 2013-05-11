package gis.data;


import java.util.ArrayList;
import java.util.List;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.datatypes.GeoMarkerPolygon;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.postgis.Geometry;
import org.postgis.LinearRing;
import org.postgis.MultiPolygon;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;


public class GeometryConverter {
	
	public static GeoMarker convert(ElementId id, PGgeometry geom) {
		
		switch (geom.getGeoType()) {
			case Geometry.POINT:
				Point p = (Point)geom.getGeometry();
				return new GeoMarkerPoint(id, new Coordinate(p.y, p.x));
			case Geometry.MULTIPOLYGON:
				Polygon[] polys = ((MultiPolygon)geom.getGeometry()).getPolygons();
				List<Coordinate[]> polygons = new ArrayList<Coordinate[]>(polys.length);
				for (int i = 0; i < polys.length; ++i) {
					int numRings = polys[i].numRings();
					for (int j = 0; j < numRings; ++j) {
						LinearRing ring = polys[i].getRing(j);
						Point[] points = ring.getPoints();
						Coordinate[] coordinates = new Coordinate[points.length];
						for (int k = 0; k < points.length; ++k) {
							coordinates[k] = new Coordinate(points[k].y, points[k].x);
						}
						polygons.add(coordinates);
					}
				}
				return new GeoMarkerPolygon(id, polygons);
			default:
				System.err.println("unsupported geometry type " + geom.getGeoType()
						+ " " + geom.getType());
				return null;
		}
	}
	
}
