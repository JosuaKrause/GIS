package gis.data;


import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPoint;

import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;


public class GeometryConverter {
	
	public static GeoMarker convert(ElementId id, PGgeometry geom) {
		
		switch (geom.getGeoType()) {
			case Geometry.POINT:
				Point p = (Point)geom.getGeometry();
				return new GeoMarkerPoint(id, p.y, p.x);
			default:
				System.err.println("unsupported geometry type " + geom.getGeoType()
						+ " " + geom.getType());
				return null;
		}
	}
	
}
