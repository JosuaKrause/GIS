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
  public void paint(final Graphics2D gfx, final ViewInfo info, final ProgressListener prog) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.isEmpty()) return;

    final List<Shape> shapes = new ArrayList<>(markers.size());
    for(final GeoMarker marker : markers) {
      shapes.add(marker.convert(info));
    }
    Graphics2D g = gfx;
    for(int raster = 100; raster > 0; raster /= 2) {
      final Point2D pos = new Point2D.Double();
      for(int y = 0; y < info.getHeight(); y += raster) {
        if(!prog.stillAlive()) return;
        for(int x = 0; x < info.getWidth(); x += raster) {
          pos.setLocation(x + raster / 2, y + raster / 2);
          double m = 0;
          for(final Shape s : shapes) {
            final double tm = GeomUtil.distance(pos, s.getBounds2D(), GeomUtil.EPS);
            if(tm >= combiner.maxDistance()) {
              continue;
            }
            final double pxls = GeomUtil.distance(pos, s, GeomUtil.EPS);
            m += Math.max(combiner.maxDistance() - pxls, 0);
          }
          g.setColor(new Color(combiner.distanceToColor(m), true));
          g.fillRect(x, y, raster, raster);
        }
      }
      g = prog.commitProgress(g);
    }
    g.dispose();
  }

}
