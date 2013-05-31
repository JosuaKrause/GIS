package gis.data.datatypes;

import java.awt.BasicStroke;
import java.awt.Color;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * A point based geo marker.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GeoMarkerPoint extends GeoMarker {

  /** The stlye of point geo markers. */
  private static final Style STYLE = new Style();

  static {
    STYLE.setColor(new Color(0, 0, 0, 255 * 2 / 5));
    STYLE.setBackColor(new Color(5, 113, 176, 255 / 3));
    STYLE.setStroke(new BasicStroke(3f));
  }

  /** The point marker. */
  private final MapMarker[] marker;

  /**
   * Creates a point based geo marker.
   * 
   * @param id The id.
   * @param coordinate The position.
   */
  public GeoMarkerPoint(final ElementId id, final Coordinate coordinate) {
    super(id);
    final MapMarkerCircle m = new MapMarkerCircle(coordinate, 3);
    m.setStyle(STYLE);
    marker = new MapMarker[] { m};
  }

  @Override
  public boolean hasPolygon() {
    return false;
  }

  @Override
  public MapMarker[] getMarker() {
    return marker;
  }

}
