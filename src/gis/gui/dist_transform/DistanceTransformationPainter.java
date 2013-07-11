package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerPolygon;
import gis.data.db.Query;
import gis.gui.GisPanel;
import gis.gui.IImagePainter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class DistanceTransformationPainter implements IImagePainter {

  private final GisPanel gisPanel;
  private final Query query;

  private float[] dist = new float[0];

  public DistanceTransformationPainter(final GisPanel gisPanel, final Query query) {
    this.gisPanel = gisPanel;
    this.query = query;
  }

  @Override
  public void paint(final BufferedImage image) {
    System.out.println("painting");// TODO
    final List<GeoMarker> markers = query.getResult();
    if(markers.size() == 0) return;
    final Rectangle2D vpLatLon = gisPanel.getLatLonViewPort();
    final int w = image.getWidth();
    final int h = image.getHeight();
    for(final GeoMarker marker : markers) {
      final GeoMarkerPolygon m = (GeoMarkerPolygon) marker;
      final Rectangle2D mLatLonBBox = m.getLatLonBBox();
      if(!vpLatLon.intersects(mLatLonBBox)) {
        continue;
      }
      final Path2D path = m.computeGeometry(gisPanel);
      for(int y = 0; y < h; ++y) {
        for(int x = 0; x < w; ++x) {
          if(path.contains(x, y)) {
            final int rgb = (255 << 24) | (255 << 16);
            image.setRGB(x, y, rgb);
          }
        }
      }

      // final double hor = box.getWidth() / vpLatLon.getWidth() * w;
      // final double ver = box.getHeight() / vpLatLon.getHeight() * h;

      // TODO
    }
  }

  @Override
  public void paint(final Graphics2D g) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.size() == 0) return;
    final Rectangle2D vpLatLon = gisPanel.getLatLonViewPort();
    final int w = gisPanel.getWidth();
    final int h = gisPanel.getHeight();
    final double metersPerPixel = gisPanel.getMeterPerPixel();
    final float mpp = (float) metersPerPixel;
    final float mpdp = (float) (Math.sqrt(2) * metersPerPixel);

    final BufferedImage img = new BufferedImage(w, h,
        BufferedImage.TYPE_INT_ARGB);
    final Graphics2D imgG = img.createGraphics();

    if(w * h != dist.length) {
      dist = new float[w * h];
    }

    for(final GeoMarker marker : markers) {
      final GeoMarkerPolygon m = (GeoMarkerPolygon) marker;
      final Rectangle2D mLatLonBBox = m.getLatLonBBox();
      if(!vpLatLon.intersects(mLatLonBBox)) {
        continue;
      }
      final Path2D path = m.computeGeometry(gisPanel);
      g.setColor(Color.WHITE);
      imgG.setColor(Color.WHITE);
      imgG.draw(path);
    }
    imgG.dispose();

    // initialize distances
    for(int y = 0; y < h; ++y) {
      for(int x = 0; x < w; ++x) {
        if((img.getRGB(x, y) & 255) == 255) {
          dist[y * w + x] = 0;
        } else {
          dist[y * w + x] = Float.MAX_VALUE;
        }
      }
    }

    // TODO this is actually slighly flawed, last calculation in each line takes
    // accesses pixel from wrong line

    // apply 8-neighbourhood distance calculations top-bottom, left-right
    float d;// for distances
    for(int x = 1; x < w; ++x) {
      d = dist[x - 1] + mpp;
      dist[x] = Math.min(dist[x], d);
    }
    for(int y = 1; y < h; ++y) {
      d = dist[y - 1] + mpp;
      dist[y] = Math.min(dist[y], d);
    }
    for(int y = 1; y < h - 1; ++y) {
      for(int x = 1; x < w - 1; ++x) {
        dist[w * y + x] = getTopLeftNeighbourhoodDistance(x, y, w, h, mpp, mpdp);
      }
    }
    // apply 8-neighbourhood distance calculations bottom-top, right-left
    for(int x = w - 2; x >= 0; --x) {
      d = dist[x + 1] + mpp;
      dist[x] = Math.min(dist[x], d);
    }
    for(int y = h - 2; y >= 0; --y) {
      d = dist[y + 1] + mpp;
      dist[y] = Math.min(dist[y], d);
    }
    for(int y = h - 2; y > 0; --y) {
      for(int x = w - 2; x > 0; --x) {
        dist[w * y + x] = getBottomRightNeighbourhoodDistance(x, y, w, h, mpp, mpdp);
      }
    }

    // for(int y = 1; y < h - 1; ++y) {
    // for(int x = 1; x < w - 1; ++x) {
    // final float t = dist[w * (y - 1) + x - 1] + dist[w * (y - 1) + x]
    // + dist[w * (y - 1) + x + 1] +
    // dist[w * y + x - 1] + dist[w * y + x] + dist[w * y + x + 1] +
    // dist[w * (y + 1) + x - 1] + dist[w * (y + 1) + x] + dist[w * (y + 1) + x
    // + 1];
    // dist[w * y + x] = t / 9;
    // }
    // }

    // convert distances to pixel color in image
    for(int y = 0; y < h; ++y) {
      for(int x = 0; x < w; ++x) {
        img.setRGB(x, y, distanceToColor(dist[y * w + x]));
      }
    }

    g.drawImage(img, 0, 0, null);

  }

  private final float getTopLeftNeighbourhoodDistance(final int x, final int y,
      final int w, @SuppressWarnings("unused") final int h, final float mpp,
      final float mpdp) {
    // ###
    // #XO
    // OOO
    final float d = Math.min(dist[w * (y - 1) + x - 1], dist[w * (y - 1) + x + 1]) + mpdp;
    final float o = Math.min(dist[w * y + x - 1], dist[w * (y - 1) + x]) + mpp;
    final float n = Math.min(d, o);
    return Math.min(dist[w * y + x], n);
  }

  private final float getBottomRightNeighbourhoodDistance(final int x, final int y,
      final int w, @SuppressWarnings("unused") final int h, final float mpp,
      final float mpdp) {
    // OOO
    // OX#
    // ###
    final float d = Math.min(dist[w * (y + 1) + x - 1], dist[w * (y + 1) + x + 1]) + mpdp;
    final float o = Math.min(dist[w * y + x + 1], dist[w * (y + 1) + x]) + mpp;
    final float n = Math.min(d, o);
    return Math.min(dist[w * y + x], n);
  }

  @SuppressWarnings("unused")
  @Deprecated
  private final float getEightNeighbourhoodDistance(final int x, final int y,
      final int w, final int h, final float mpp, final float mpdp) {
    // diagonal distances
    final float t = Math.min(dist[w * (y - 1) + x - 1], dist[w * (y - 1) + x + 1]);
    final float b = Math.min(dist[w * (y + 1) + x - 1], dist[w * (y + 1) + x + 1]);
    final float diagonal = Math.min(t, b) + mpdp;
    // vertical/horizontal distances
    final float hor = Math.min(dist[w * y + x - 1], dist[w * y + x + 1]);
    final float ver = Math.min(dist[w * (y - 1) + x], dist[w * (y + 1) + x]);
    final float orthogonal = Math.min(hor, ver) + mpp;
    // compare distances of neighbours with current pixel
    final float nMinDist = Math.min(diagonal, orthogonal);
    return Math.min(dist[y * w + x], nMinDist);
  }

  private static int distanceToColor(final float distance) {
    int i = Math.round(255 * distance / 3000);
    i = Math.min(i, 255);
    // i = Math.max(i, 0);
    i = 255 - i;
    return (i << 24) | (i << 16) | (i << 8) | i;
  }

}
