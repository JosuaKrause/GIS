package gis.data.datatypes;

import gis.gui.GisPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
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
  private double radius = 0.0001;
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
    this.coord = coord;
    computeLatLonBBox();
  }

  /** Computes the bounding box if the radius has changed. */
  private void computeLatLonBBox() {
    final double a = radius;
    latLonBBox = new Rectangle2D.Double(
        coord.getLon() - a, coord.getLat() - a, a * 2, a * 2);
  }

  @Override
  public Rectangle2D getLatLonBBox() {
    return latLonBBox;
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
    final Point2D pos = panel.getMapPosition(coord, false);
    final Point2D other = panel.getMapPosition(
        new Coordinate(coord.getLat(), coord.getLon() + radius), false);
    final double r = other.getX() - pos.getX();
    final Shape e = new Ellipse2D.Double(pos.getX() - r, pos.getY() - r, 2 * r, 2 * r);
    g2.fill(e);
    g2.dispose();
    g.setColor(Color.BLACK);
    g.draw(e);
  }

  @Override
  public void setRadius(final double radius) {
    this.radius = radius;
    computeLatLonBBox();
  }

}
