package gis.data;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.datatypes.GeoMarkerPolygon;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.postgis.Geometry;
import org.postgis.LinearRing;
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
      default:
        throw new UnsupportedOperationException(
            "unsupported geometry type " + geom.getGeoType() + " " + geom.getType());
    }
  }

  /** The mean radius of the earth in meters. */
  public static final double RADIUS_EARTH_MEAN = 6371009;

  /**
   * Computes an approximation of the distance of two points on the earth in
   * meters using the mean radius of the earth.
   * 
   * @param latA The latitude of point A in degrees.
   * @param lonA The longitude of point A in degrees.
   * @param latB The latitude of point B in degrees.
   * @param lonB The longitude of point B in degrees.
   * @return The distance of the two points in meters.
   */
  public static double earthDistance(final double latA, final double lonA,
      final double latB, final double lonB) {
    // mean radius of the earth
    return sphereDistance(latA, lonA, latB, lonB, RADIUS_EARTH_MEAN);
  }

  /**
   * Computes the distance of two points on a sphere.
   * 
   * @param latA The latitude of point A in degrees.
   * @param lonA The longitude of point A in degrees.
   * @param latB The latitude of point B in degrees.
   * @param lonB The longitude of point B in degrees.
   * @param radius The radius of the sphere.
   * @return The distance.
   */
  public static double sphereDistance(final double latA, final double lonA,
      final double latB, final double lonB, final double radius) {
    final double dLat = Math.toRadians(latB - latA) * .5;
    final double dLon = Math.toRadians(lonB - lonA) * .5;
    final double rLatA = Math.toRadians(latA);
    final double rLatB = Math.toRadians(latB);
    final double a = Math.sin(dLat) * Math.sin(dLat) +
        Math.sin(dLon) * Math.sin(dLon) * Math.cos(rLatA) * Math.cos(rLatB);
    final double c = Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return 2 * radius * c;
  }

  public static double meterToAngle(final double meters) {
    return meters / RADIUS_EARTH_MEAN;
  }

}
