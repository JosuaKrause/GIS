package gis.data.datatypes;

import gis.gui.GisPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GeoMarkerLineString extends GeoMarker {

  private final Coordinate[] points;
  private Color color = Color.BLUE;

  public GeoMarkerLineString(final String info, final ElementId id,
      final Coordinate[] points) {
    super(info, id);
    this.points = points;
  }

  @Override
  public void paint(final Graphics2D g, final GisPanel panel, final boolean simple) {
    // TODO
    /*
     * GeoMarkerLineString ls = (GeoMarkerLineString)m; //draw (partially)
     * visible linestrings, one at a time //has potential for errors if line,
     * but not its points should be visible for (int i = 0; i < ls.points.length
     * - 1; ++i) { Coordinate ca = ls.points[i]; Coordinate cb = ls.points[i +
     * 1]; Point pa = getMapPosition(ca, true); Point pb = getMapPosition(cb,
     * true); if (!(pa == null && pb == null)) { if (pa == null) { pa =
     * getMapPosition(ca, false); } else if (pb == null) { pb =
     * getMapPosition(cb, false); } ls.paintLineSegment(g, pb, pa); } }
     */
  }

  @Override
  public boolean pick(final Point2D pos, final GisPanel panel, final boolean simple) {
    // TODO
    return false;
  }

  @Override
  public Rectangle2D getLatLonBBox() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setColor(final Color color) {
    this.color = color;
  }

  @Override
  public void setRadius(final double radius) {
    throw new UnsupportedOperationException();
  }

}
