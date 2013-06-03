package gis.data.datatypes;

import gis.gui.GisPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * A point based geo marker.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GeoMarkerPoint extends GeoMarker {

  /** The radius. */
  private double radius = 0.05;
  /** The point marker. */
  private final Coordinate coord;
  /** The world coordinate bounding box. */
  private Rectangle2D latLonBBox;

  /**
   * Creates a point based geo marker.
   * 
   * @param info The element info.
   * @param id The reference id.
   * @param coord The position.
   */
  public GeoMarkerPoint(final String info, final ElementId id, final Coordinate coord) {
    super(info, id);
    setColor(new Color(5, 113, 176, 255 / 3));
    this.coord = coord;
    computeLatLonBBox();
  }

  /** Computes the bounding box if the radius has changed. */
  private void computeLatLonBBox() {
    latLonBBox = new Rectangle2D.Double(
        coord.getLon() - radius, coord.getLat() - radius,
        radius * 2, radius * 2);
  }

  @Override
  public Rectangle2D getLatLonBBox() {
    return latLonBBox;
  }

  @Override
  public void paint(final Graphics2D g, final GisPanel panel, final boolean simple) {
    g.setColor(getColor());
    if(simple) {
      paintSimple(g, panel);
      return;
    }
    final Rectangle2D r = transformRect(panel, getLatLonBBox());
    final Shape e = new Ellipse2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    g.fill(e);
  }

  @Override
  public void setRadius(final double radius) {
    this.radius = radius;
    computeLatLonBBox();
  }

}
