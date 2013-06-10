package gis.data.datatypes;

import gis.gui.GisPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * A marker for multi polygons.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GeoMarkerPolygon extends GeoMarker {

  private static final long serialVersionUID = -6166480726226719333L;
  /** The polygon. */
  private final Coordinate[] polygon;
  /** The world coordinate bounding box. */
  private final Rectangle2D latLonBBox;

  /**
   * Creates a geo marker for the list of polygons.
   * 
   * @param info The element info.
   * @param id The reference id.
   * @param poly The polygon.
   */
  public GeoMarkerPolygon(final String info, final ElementId id, final Coordinate[] poly) {
    super(info, id);
    double minLat = Double.NaN;
    double maxLat = Double.NaN;
    double minLon = Double.NaN;
    double maxLon = Double.NaN;
    for(final Coordinate c : poly) {
      if(Double.isNaN(minLat) || minLat > c.getLat()) {
        minLat = c.getLat();
      }
      if(Double.isNaN(maxLat) || maxLat < c.getLat()) {
        maxLat = c.getLat();
      }
      if(Double.isNaN(minLon) || minLon > c.getLon()) {
        minLon = c.getLon();
      }
      if(Double.isNaN(maxLon) || maxLon < c.getLon()) {
        maxLon = c.getLon();
      }
    }
    polygon = poly;
    latLonBBox = new Rectangle2D.Double(
        minLon, minLat, maxLon - minLon, maxLat - minLat);
  }

  @Override
  public void paint(final Graphics2D g, final GisPanel panel, final boolean simple) {
    if(simple) {
      paintSimple(g, panel);
      return;
    }
    final Graphics2D g2 = (Graphics2D) g.create();
    g2.setComposite(getComposite());
    g2.setColor(getColor());
    final Path2D path = computeGeometry(panel);
    g2.fill(path);
    g2.dispose();
    g.setColor(outlineColor == null ? Color.BLACK : outlineColor);
    g.draw(path);
  }

  private Path2D computeGeometry(final GisPanel panel) {
    final Path2D path = new Path2D.Double();
    boolean first = true;
    for(final Coordinate coord : polygon) {
      final Point2D pos = panel.getMapPosition(coord, false);
      if(first) {
        path.moveTo(pos.getX(), pos.getY());
        first = false;
      } else {
        path.lineTo(pos.getX(), pos.getY());
      }
    }
    path.closePath();
    return path;
  }

  @Override
  public boolean pick(final Point2D pos, final GisPanel panel, final boolean simple) {
    if(simple) return pickSimple(pos, panel);
    return computeGeometry(panel).contains(pos);
  }

  @Override
  public Rectangle2D getLatLonBBox() {
    return latLonBBox;
  }

  @Override
  public void setRadius(final double radius) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isPoint() {
    return false;
  }

}
