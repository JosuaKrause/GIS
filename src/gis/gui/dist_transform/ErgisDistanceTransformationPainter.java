package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;
import gis.gui.ImagePainter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class ErgisDistanceTransformationPainter implements ImagePainter {

  private final Query query;

  public ErgisDistanceTransformationPainter(final Query query) {
    this.query = query;
  }

  @Override
  public void paint(final Graphics2D g, final ViewInfo info) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.size() == 0) return;
    final Rectangle2D vpLatLon = info.getLatLonViewPort();
    final int w = info.getWidth();
    final int h = info.getHeight();
    final double mpp = info.getMeterPerPixel();

    final BufferedImage img = new BufferedImage(w, h,
        BufferedImage.TYPE_INT_ARGB);
    final Graphics2D imgG = img.createGraphics();

    final double[] dist = new double[w * h];
    final Point[] targets = new Point[w * h];

    imgG.setColor(Color.WHITE);
    for(final GeoMarker m : markers) {
      final Rectangle2D mLatLonBBox = m.getLatLonBBox();
      if(!vpLatLon.intersects(mLatLonBBox)) {
        continue;
      }
      // set polygon pixels as target pixels
      final Shape path = m.convert(info);
      imgG.draw(path);
      imgG.fill(path);
      // make sure that at least one pixel per marker is set

      final double coordX = mLatLonBBox.getCenterX();
      final double coordY = mLatLonBBox.getCenterY();
      final Point2D p = info.convert(new Coordinate(coordY, coordX));
      if(p != null) {
        imgG.fill(new Rectangle2D.Double(p.getX(), p.getY(), 1, 1));
      }
    }
    imgG.dispose();

    // initialize distances
    for(int y = 0; y < h; ++y) {
      for(int x = 0; x < w; ++x) {
        final int index = y * w + x;
        if((img.getRGB(x, y) & 255) == 255) {
          dist[index] = 0;
          targets[index] = new Point(x, y);
        } else {
          dist[index] = Double.POSITIVE_INFINITY;
          targets[index] = null;
        }
      }
    }

    // initialize top row
    int targetHalfCoordinate = -1;
    for(int x = 0; x < w; ++x) {
      if(dist[x] == 0) {
        targetHalfCoordinate = x;
        break;
      }
    }
    if(targetHalfCoordinate != -1) {
      for(int x = targetHalfCoordinate + 1; x < w; ++x) {
        if(dist[x] == 0) {
          targetHalfCoordinate = x;
        } else {
          dist[x] = dist[x - 1] + mpp;
          targets[x] = new Point(targetHalfCoordinate, 0);
        }
      }
    }
    // initialize left column
    targetHalfCoordinate = -1;
    for(int y = 0; y < h; ++y) {
      if(dist[w * y] == 0) {
        targetHalfCoordinate = y;
        break;
      }
    }
    if(targetHalfCoordinate != -1) {
      for(int y = targetHalfCoordinate + 1; y < h; ++y) {
        if(dist[w * y] == 0) {
          targetHalfCoordinate = y;
        } else {
          dist[y * w] = dist[(y - 1) * w] + mpp;
          targets[y * w] = new Point(0, targetHalfCoordinate);
        }
      }
    }
    // from top left to bottom right
    for(int y = 1; y < h; ++y) {
      for(int x = 1; x < w - 1; ++x) {// omit rightmost pixel
        final int index = y * w + x;
        if(dist[index] != 0) {
          final Point tlt = targets[index - w - 1];
          final Point tct = targets[index - w];
          final Point trt = targets[index - w + 1];
          final Point lt = targets[index - 1];
          final double tltDist = distFromTarget(x, y, tlt, mpp);
          final double ltDist = distFromTarget(x, y, lt, mpp);
          final double tctDist = distFromTarget(x, y, tct, mpp);
          final double trtDist = distFromTarget(x, y, trt, mpp);
          final Point target1;
          final double dist1;
          if(tltDist < ltDist) {
            target1 = tlt;
            dist1 = tltDist;
          } else {
            target1 = lt;
            dist1 = ltDist;
          }
          final Point target2;
          final double dist2;
          if(tctDist < trtDist) {
            target2 = tct;
            dist2 = tctDist;
          } else {
            target2 = trt;
            dist2 = trtDist;
          }
          if(dist1 < dist2) {
            targets[index] = target1;
            dist[index] = dist1;
          } else {
            targets[index] = target2;
            dist[index] = dist2;
          }
        }
      }
      // rightmost pixel (has just 3 neighbors)
      final int x = w - 1;
      final int index = (y + 1) * w - 1;
      final Point tlt = targets[index - w - 1];
      final Point tct = targets[index - w];
      final Point lt = targets[index - 1];
      final double tltDist = distFromTarget(x, y, tlt, mpp);
      final double ltDist = distFromTarget(x, y, lt, mpp);
      final double tctDist = distFromTarget(x, y, tct, mpp);
      Point target1;// will reference closest neighboring target
      double dist1;
      if(tltDist < ltDist) {
        target1 = tlt;
        dist1 = tltDist;
      } else {
        target1 = lt;
        dist1 = ltDist;
      }
      if(tctDist < dist1) {
        target1 = tct;
        dist1 = tctDist;
      }
      if(dist1 < dist[index]) {
        targets[index] = target1;
        dist[index] = dist1;
      }
    }
    // initialize bottom row and right column
    if(dist[(h - 1) * w - 1] < Double.POSITIVE_INFINITY) {
      // bottom row
      for(int x = w - 2; x >= 0; --x) {
        final int index = (h - 1) * w + x;
        final Point rnTarget = targets[index + 1];
        if(dist[index + 1] < Double.POSITIVE_INFINITY) {
          final double rnTargetDist = euclidian(x, h - 1, rnTarget.x, rnTarget.y, mpp);
          if(rnTargetDist < dist[index]) {
            dist[index] = rnTargetDist;
            targets[index] = rnTarget;
          }
        }
      }
      // right column
      for(int y = h - 2; y >= 0; --y) {
        final int index = (y + 1) * w - 1;
        final Point bnTarget = targets[index + w];
        if(dist[index + w] < Double.POSITIVE_INFINITY) {
          final double bnTargetDist = euclidian(w - 1, y, bnTarget.x, bnTarget.y, mpp);
          if(bnTargetDist < dist[index]) {
            dist[index] = bnTargetDist;
            targets[index] = bnTarget;
          }
        }
      }
    }
    // from bottom right to top left
    for(int y = h - 2; y >= 0; --y) {
      for(int x = w - 2; x >= 1; --x) {// omit leftmost pixel
        final int index = y * w + x;
        if(dist[index] != 0) {
          final Point blt = targets[index + w - 1];
          final Point bct = targets[index + w];
          final Point brt = targets[index + w + 1];
          final Point rt = targets[index + 1];
          final double bltDist = distFromTarget(x, y, blt, mpp);
          final double bctDist = distFromTarget(x, y, bct, mpp);
          final double brtDist = distFromTarget(x, y, brt, mpp);
          final double rtDist = distFromTarget(x, y, rt, mpp);
          Point target1;
          double dist1;
          if(bltDist < bctDist) {
            target1 = blt;
            dist1 = bltDist;
          } else {
            target1 = bct;
            dist1 = bctDist;
          }
          final Point target2;
          final double dist2;
          if(brtDist < rtDist) {
            target2 = brt;
            dist2 = brtDist;
          } else {
            target2 = rt;
            dist2 = rtDist;
          }
          // make sure that target represents closest target
          if(dist2 < dist1) {
            target1 = target2;
            dist1 = dist2;
          }
          if(dist1 < dist[index]) {
            targets[index] = target1;
            dist[index] = dist1;
          }
        }
      }
      // leftmost pixel (has just 3 neighbors)
      final int index = y * w;
      if(dist[index] != 0) {
        final Point bct = targets[index + w];
        final Point brt = targets[index + w + 1];
        final Point rt = targets[index + 1];
        final double bctDist = distFromTarget(0, y, bct, mpp);
        final double brtDist = distFromTarget(0, y, brt, mpp);
        final double rtDist = distFromTarget(0, y, rt, mpp);
        Point target1;// will reference closest neighboring target
        double dist1;
        if(bctDist < brtDist) {
          target1 = bct;
          dist1 = bctDist;
        } else {
          target1 = brt;
          dist1 = brtDist;
        }
        if(rtDist < dist1) {
          target1 = rt;
          dist1 = rtDist;
        }
        if(dist1 < dist[index]) {
          targets[index] = target1;
          dist[index] = dist1;
        }
      }
    }

    // ////// again old code
    // convert distances to pixel color in image
    for(int y = 0; y < h; ++y) {
      for(int x = 0; x < w; ++x) {
        img.setRGB(x, y, distanceToColor(dist[y * w + x]));
      }
    }

    g.drawImage(img, 0, 0, null);
  }

  private static double distFromTarget(final int x, final int y,
      final Point target, final double metersPerPixel) {
    if(target == null) return Double.POSITIVE_INFINITY;
    return euclidian(target.x, target.y, x, y, metersPerPixel);
  }

  private static final double euclidian(final int x1, final int y1, final int x2,
      final int y2, final double metersPerPixel) {
    final double dx = x1 - x2;
    final double xx = dx * dx;
    final double dy = y1 - y2;
    final double yy = dy * dy;
    return Math.sqrt(xx + yy) * metersPerPixel;
  }

  private static final double MAX_DIST = 100;

  private static final int distanceToColor(final double distance) {
    int i = (int) Math.round(255 * distance / MAX_DIST);
    i = Math.min(i, 255);
    // i = Math.max(i, 0);
    i = 255 - i;
    // ARGB
    return ((255 - i) << 24) | (i << 16) | (i << 8) | i;
  }

}
