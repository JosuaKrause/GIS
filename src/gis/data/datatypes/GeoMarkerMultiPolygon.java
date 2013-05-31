package gis.data.datatypes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

/**
 * A marker for multi polygons.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GeoMarkerMultiPolygon extends GeoMarker {

  /** The style of the polygons. */
  private static final Style STYLE = new Style();

  static {
    STYLE.setColor(new Color(0, 0, 0, 255 * 2 / 5));
    STYLE.setBackColor(new Color(239, 138, 98, 255 / 3));
    STYLE.setStroke(new BasicStroke(3f));
  }

  /** The polygons. */
  private final MapPolygon[] polygons;

  /**
   * Creates a geo marker for the list of polygons.
   * 
   * @param id The id.
   * @param poly The list of polygons.
   */
  public GeoMarkerMultiPolygon(final ElementId id, final List<Coordinate[]> poly) {
    super(id);
    int pos = 0;
    polygons = new MapPolygon[poly.size()];
    for(final Coordinate[] coords : poly) {
      final MapPolygonImpl p = new MapPolygonImpl(coords);
      p.setStyle(STYLE);
      polygons[pos++] = p;
    }
  }

  @Override
  public boolean hasPolygon() {
    return true;
  }

  @Override
  public MapPolygon[] getPolygons() {
    return polygons;
  }

}
