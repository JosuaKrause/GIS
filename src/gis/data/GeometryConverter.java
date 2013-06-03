package gis.data;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerLineString;
import gis.data.datatypes.GeoMarkerMultiPolygon;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.datatypes.GeoMarkerPolygon;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.LinearRing;
import org.postgis.MultiPolygon;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;

/**
 * Converts geometries into geo markers.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public final class GeometryConverter {

  /** No constructor. */
  private GeometryConverter() {
    throw new AssertionError();
  }

  /**
   * Converts geometry into geo markers.
   * 
   * @param id The reference id.
   * @param geom The geometry of the element.
   * @param info The element information.
   * @return The converted geo marker.
   */
  public static GeoMarker convert(
      final ElementId id, final PGgeometry geom, final String info) {
    switch(geom.getGeoType()) {
      case Geometry.POINT:
        final Point p = (Point) geom.getGeometry();
        return new GeoMarkerPoint(info, id, new Coordinate(p.getY(), p.getX()));
      case Geometry.POLYGON:
        final Polygon poly = (Polygon) geom.getGeometry();
        if(poly.numRings() > 1) {
          System.err.println("numRings = " + poly.numRings());
        }
        final LinearRing polyRing = poly.getRing(0);
        final Point[] polyPoints = polyRing.getPoints();
        final Coordinate[] polyCoordinates = new Coordinate[polyPoints.length];
        for(int k = 0; k < polyPoints.length; ++k) {
          polyCoordinates[k] = new Coordinate(polyPoints[k].getY(), polyPoints[k].getX());
        }
        return new GeoMarkerPolygon(info, id, polyCoordinates);
      case Geometry.MULTIPOLYGON:
        final Polygon[] polys = ((MultiPolygon) geom.getGeometry()).getPolygons();
        final List<Coordinate[]> polygons = new ArrayList<>(polys.length);
        for(int i = 0; i < polys.length; ++i) {
          final int numRings = polys[i].numRings();
          for(int j = 0; j < numRings; ++j) {
            final LinearRing ring = polys[i].getRing(j);
            final Point[] points = ring.getPoints();
            final Coordinate[] coordinates = new Coordinate[points.length];
            for(int k = 0; k < points.length; ++k) {
              coordinates[k] = new Coordinate(points[k].getY(), points[k].getX());
            }
            polygons.add(coordinates);
          }
        }
        return new GeoMarkerMultiPolygon(info, id, polygons);
      case Geometry.LINESTRING:
        final LineString ls = (LineString) geom.getGeometry();
        final Point[] points = ls.getPoints();
        final Coordinate[] coordinates = new Coordinate[points.length];
        for(int i = 0; i < points.length; ++i) {
          coordinates[i] = new Coordinate(points[i].y, points[i].x);
        }
        return new GeoMarkerLineString(info, id, coordinates);
      default:
        throw new UnsupportedOperationException(
            "unsupported geometry type " + geom.getGeoType() + " " + geom.getType());
    }
  }

}
