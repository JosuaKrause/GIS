package gis.data.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * A polygon marker.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GeoMarkerPolygon extends GeoMarkerMultiPolygon {

  /**
   * Converts the polygon into a list of polygons.
   * 
   * @param p The polygon.
   * @return A list of one polygon.
   */
  private static List<Coordinate[]> toList(final Coordinate[] p) {
    // Arrays.asList(...) does not work :(
    final List<Coordinate[]> list = new ArrayList<>(1);
    list.add(p);
    return list;
  }

  /**
   * Creates a polygon geo marker.
   * 
   * @param id The reference id.
   * @param polygon The polygon.
   */
  public GeoMarkerPolygon(final String info, final ElementId id,
      final Coordinate[] polygon) {
    super(info, id, toList(polygon));
  }

}
