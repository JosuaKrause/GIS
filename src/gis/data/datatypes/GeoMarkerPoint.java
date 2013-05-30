package gis.data.datatypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerPoint extends GeoMarker {

  public Coordinate coordinate;
  public Color color;
  public double radius;

  public GeoMarkerPoint(final ElementId id, final Coordinate coordinate) {
    super(id);
    this.coordinate = coordinate;
    color = new Color(1, 0, 0, 0.5f);// rgba
    radius = 3;
  }

  public GeoMarkerPoint(final ElementId id, final Coordinate coordinate,
      final Color color, final int radius) {
    super(id);
    this.coordinate = coordinate;
    this.color = color;
    this.radius = radius;
  }

  private Shape toShape(final Point2D p) {
    final double dia = 2 * radius;
    return new Ellipse2D.Double(p.getX() - radius, p.getY() - radius, dia, dia);
  }

  public void paint(final Graphics2D g, final Point2D p) {
    g.setColor(color);
    final Shape s = toShape(p);
    g.fill(s);
    g.setColor(Color.BLACK);
    g.draw(s);
  }

}
