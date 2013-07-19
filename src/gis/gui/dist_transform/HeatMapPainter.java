package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;
import gis.gui.ImagePainter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeatMapPainter implements ImagePainter {

  private final Query query;

  private final Combiner combiner;

  public HeatMapPainter(final Query query, final Combiner combiner) {
    this.query = Objects.requireNonNull(query);
    this.combiner = Objects.requireNonNull(combiner);
  }

  @Override
  public void paint(final Graphics2D g, final ViewInfo info) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.isEmpty()) return;

    final List<Shape> shapes = new ArrayList<>(markers.size());
    for(final GeoMarker marker : markers) {
      shapes.add(marker.convert(info));
    }
    final int raster = 5;
    final Point2D pos = new Point2D.Double();
    for(int y = 0; y < info.getHeight(); y += raster) {
      for(int x = 0; x < info.getWidth(); x += raster) {
        pos.setLocation(x, y);
        double m = Double.POSITIVE_INFINITY;
        for(final Shape s : shapes) {
          final double tm = GeomUtil.distance(pos, s.getBounds2D(), GeomUtil.EPS);
          if(tm >= Combiner.MAX_DIST) {
            continue;
          }
          final double pxls = GeomUtil.distance(pos, s, GeomUtil.EPS);
          if(pxls < m) {
            m = pxls;
          }
        }
        g.setColor(new Color(combiner.distanceToColor(m), true));
        g.fillRect(x, y, raster, raster);
      }
    }
    g.dispose();
  }

}