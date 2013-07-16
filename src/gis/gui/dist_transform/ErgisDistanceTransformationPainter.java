package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;
import gis.tiles.ImageTileLoader.TileInfo;
import gis.tiles.TilePainter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.List;

public class ErgisDistanceTransformationPainter implements TilePainter {

  private final Query query;

  public ErgisDistanceTransformationPainter(final Query query) {
    this.query = query;
  }

  @Override
  public void paintTile(final BufferedImage img, final TileInfo<?> info) {
    final List<GeoMarker> markers = query.getResult();
    if(markers.size() == 0) return;
    final int w = img.getWidth();
    final int h = img.getHeight();
    final Graphics2D imgG = img.createGraphics();
    imgG.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_OFF);
    final double[] dist = new double[w * h];
    final Point[] targets = new Point[w * h];
    imgG.setColor(Color.WHITE);
    for(final GeoMarker marker : markers) {
      final Shape path = marker.convert(info);
      imgG.draw(path);
      imgG.fill(path);
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
          dist[index] = Float.MAX_VALUE;
          targets[index] = null;// TODO
        }
      }
    }// ///// up to this point code equals that of DistanceTransformationPainter

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
          dist[x] = dist[x - 1] + info.distance(x, 0, x - 1, 0);
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
          dist[y * w] = dist[(y - 1) * w] + info.distance(0, y, 0, y - 1);
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
          final double tltDist = distFromTarget(x, y, tlt, info);
          final double ltDist = distFromTarget(x, y, lt, info);
          final double tctDist = distFromTarget(x, y, tct, info);
          final double trtDist = distFromTarget(x, y, trt, info);
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
      final double tltDist = distFromTarget(x, y, tlt, info);
      final double ltDist = distFromTarget(x, y, lt, info);
      final double tctDist = distFromTarget(x, y, tct, info);
      Point target1;// will reference closest neighbouring target
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
    if(dist[(h - 1) * w - 1] != Float.MAX_VALUE) {
      // bottom row
      for(int x = w - 2; x >= 0; --x) {
        final int index = (h - 1) * w + x;
        final Point rnTarget = targets[index + 1];
        if(dist[index + 1] != Float.MAX_VALUE) {
          final double rnTargetDist = distFromTarget(x, h - 1, rnTarget, info);
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
        if(dist[index + w] != Float.MAX_VALUE) {
          final double bnTargetDist = distFromTarget(w - 1, y, bnTarget, info);
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
          final double bltDist = distFromTarget(x, y, blt, info);
          final double bctDist = distFromTarget(x, y, bct, info);
          final double brtDist = distFromTarget(x, y, brt, info);
          final double rtDist = distFromTarget(x, y, rt, info);
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
      // leftmost pixel (has just 3 neighbours)
      final int index = y * w;
      if(dist[index] != 0) {
        final Point bct = targets[index + w];
        final Point brt = targets[index + w + 1];
        final Point rt = targets[index + 1];
        final double bctDist = distFromTarget(0, y, bct, info);
        final double brtDist = distFromTarget(0, y, brt, info);
        final double rtDist = distFromTarget(0, y, rt, info);
        Point target1;// will reference closest neighbouring target
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
    // convert distances to pixel color in image
    for(int y = 0; y < h; ++y) {
      for(int x = 0; x < w; ++x) {
        img.setRGB(x, y, distanceToColor(dist[y * w + x]));
      }
    }
  }

  private static double distFromTarget(final int x, final int y,
      final Point target, final TileInfo<?> info) {
    if(target == null) return Float.MAX_VALUE;
    return info.distance(x, y, target.x, target.y);
  }

  public static final double MAX_DIST = 3000;

  private final static int distanceToColor(final double distance) {
    int i = (int) Math.round(255 * distance / MAX_DIST);
    i = Math.min(i, 255);
    // i = Math.max(i, 0);
    i = 255 - i;
    return ((255 - i) << 24) | (i << 16) | (i << 8) | i;// argb
  }

}
