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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class ErgisDistanceTransformationPainter implements ImagePainter {

  private final Query query;

  private final Combiner combiner;

  public ErgisDistanceTransformationPainter(final Query query, final Combiner combiner) {
    this.query = Objects.requireNonNull(query);
    this.combiner = Objects.requireNonNull(combiner);
  }

  private static Point findNearest(final List<GeoMarker> outer,
      final Point pos, final ViewInfo info, final double mpp) {
    Point best = null;
    double bestDist = Double.POSITIVE_INFINITY;
    for(final GeoMarker gm : outer) {
      final Shape s = gm.convert(info);
      final Point2D bbx = GeomUtil.closestPointWithin(pos, s.getBounds2D(), GeomUtil.EPS);
      if(distFromTarget(pos.x, pos.y, bbx, mpp) > Combiner.MAX_DIST) {
        continue;
      }
      final Point2D p = GeomUtil.closestPointWithin(pos, s, GeomUtil.EPS);
      final double d = distFromTarget(pos.x, pos.y, p, mpp);
      if(d < bestDist) {
        best = new Point((int) p.getX(), (int) p.getY());
        bestDist = d;
      }
    }
    return best;
  }

  @Override
  public void paint(final Graphics2D g, final ViewInfo info) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.isEmpty()) return;
    final Rectangle2D vpLatLon = info.getLatLonViewPort();
    final int w = info.getWidth();
    final int h = info.getHeight();
    final double mpp = info.getMeterPerPixel();

    final BufferedImage img = new BufferedImage(
        w, h, BufferedImage.TYPE_INT_ARGB);
    final Graphics2D imgG = img.createGraphics();

    final double[] dist = new double[w * h];
    final Point[] targets = new Point[w * h];

    final List<GeoMarker> outer = new ArrayList<>();

    imgG.setColor(Color.WHITE);
    for(final GeoMarker m : markers) {
      final Rectangle2D mLatLonBBox = m.getLatLonBBox();
      if(!vpLatLon.intersects(mLatLonBBox)) {
        outer.add(m);
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

    // initialize border
    fillBorderY(info, w, h, mpp, dist, targets, outer, 0);
    fillBorderY(info, w, h, mpp, dist, targets, outer, w - 1);
    fillBorderX(info, w, mpp, dist, targets, outer, 0);
    fillBorderX(info, w, mpp, dist, targets, outer, h - 1);

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
          final double rnTargetDist = distFromTarget(x, h - 1, rnTarget, mpp);
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
          final double bnTargetDist = distFromTarget(w - 1, y, bnTarget, mpp);
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
        img.setRGB(x, y, combiner.distanceToColor(dist[y * w + x]));
      }
    }

    info.drawImage(g, img);
    img.flush();

    if(DEBUG) {
      g.setColor(Color.BLUE);
      for(final GeoMarker m : markers) {
        final Rectangle2D mLatLonBBox = m.getLatLonBBox();
        if(!vpLatLon.intersects(mLatLonBBox)) {
          continue;
        }
        // set polygon pixels as target pixels
        final Shape path = m.convert(info);
        g.draw(path);
        g.fill(path);
      }
    }
  }

  private static void fillBorderY(final ViewInfo info, final int w, final int h,
      final double mpp, final double[] dist, final Point[] targets,
      final List<GeoMarker> outer, final int x) {
    for(int y = 0; y < h; ++y) {
      final int index = y * w + x;
      if(targets[index] != null) {
        continue;
      }
      final Point near = findNearest(outer, new Point(x, y), info, mpp);
      dist[index] = distFromTarget(x, y, near, mpp);
      targets[index] = near;
    }
  }

  private static void fillBorderX(final ViewInfo info, final int w,
      final double mpp, final double[] dist, final Point[] targets,
      final List<GeoMarker> outer, final int y) {
    for(int x = 0; x < w; ++x) {
      final int index = y * w + x;
      if(targets[index] != null) {
        continue;
      }
      final Point near = findNearest(outer, new Point(x, y), info, mpp);
      dist[index] = distFromTarget(x, y, near, mpp);
      targets[index] = near;
    }
  }

  private static final boolean DEBUG = false;

  private static double distFromTarget(final int x, final int y,
      final Point2D target, final double metersPerPixel) {
    if(target == null) return Double.POSITIVE_INFINITY;
    return target.distance(x, y) * metersPerPixel;
  }

}
