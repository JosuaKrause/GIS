package gis.data.datatypes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerMultiPolygon extends GeoMarker {

  public List<Coordinate[]> polygons;
  public Color color = new Color(1, 0, 1, 0.5f);// rgba

  public GeoMarkerMultiPolygon(final ElementId id, final List<Coordinate[]> polygons) {
    super(id);
    this.polygons = polygons;
  }

  private static final Stroke STROKE = new BasicStroke(25f);

  public void paint(final Graphics2D g, final Shape shape) {
    g.setColor(color);
    final Area a = new Area(shape);
    a.intersect(new Area(STROKE.createStrokedShape(shape)));
    g.fill(a);
    g.setColor(Color.BLACK);
    g.draw(shape);
  }

}
