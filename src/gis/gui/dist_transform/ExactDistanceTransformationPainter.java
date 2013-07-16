package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;
import gis.tiles.ImageTileLoader.TileInfo;
import gis.tiles.TilePainter;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ExactDistanceTransformationPainter implements TilePainter {

  private final Query query;

  public ExactDistanceTransformationPainter(final Query query) {
    this.query = query;
  }

  @Override
  public void paintTile(final BufferedImage img, final TileInfo info) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.isEmpty()) return;
    // final Graphics2D imgG = img.createGraphics();
    // imgG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    // RenderingHints.VALUE_ANTIALIAS_OFF);
    // imgG.setColor(Color.BLACK);
    // imgG.fillRect(0, 0, img.getWidth() + 1, img.getHeight() + 1);
    // imgG.setColor(Color.WHITE);
    // for(final GeoMarker marker : markers) {
    // final Shape path = marker.convert(info);
    // imgG.draw(path);
    // imgG.fill(path);
    // }
    // imgG.dispose();

    final List<Shape> shapes = new ArrayList<>(markers.size());
    for(final GeoMarker marker : markers) {
      shapes.add(marker.convert(info));
    }
    final int raster = 10;
    final Graphics g = img.getGraphics();
    final Point2D pos = new Point2D.Double();
    for(int y = 0; y < info.getHeight(); y += raster) {
      for(int x = 0; x < info.getWidth(); x += raster) {
        pos.setLocation(x, y);
        double m = Double.POSITIVE_INFINITY;
        for(final Shape s : shapes) {
          final double tm = GeomUtil.distance(pos, s.getBounds2D(), EPS);
          if(tm >= MAX_DIST) {
            continue;
          }
          final double pxls = GeomUtil.distance(pos, s, EPS);
          if(pxls < m) {
            m = pxls;
          }
        }
        g.setColor(new Color(distanceToColor(m), true));
        g.fillRect(x, y, raster, raster);
      }
    }
    g.dispose();
  }

  public static final double EPS = 1e-3;

  public static final double MAX_DIST = 50;

  private final static int distanceToColor(final double distance) {
    int i = (int) Math.round(255 * distance / MAX_DIST);
    i = Math.min(i, 255);
    // i = Math.max(i, 0);
    i = 255 - i;
    return ((255 - i) << 24) | (i << 16) | (i << 8) | i;// argb
  }

}
