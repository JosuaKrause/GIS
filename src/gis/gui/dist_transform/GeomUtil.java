package gis.gui.dist_transform;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public final class GeomUtil {

  private GeomUtil() {
    throw new AssertionError();
  }

  public static final double EPS = 1e-3;

  public static Point2D closestPoint(final Point2D p, final Line2D l) {
    final double lx = l.getX1();
    final double dx = l.getX2() - lx;
    final double ly = l.getY1();
    final double dy = l.getY2() - ly;
    final double projlenSq;
    double px = p.getX() - lx;
    double py = p.getY() - ly;
    double dotprod = px * dx + py * dy;
    if(dotprod <= 0.0) {
      projlenSq = 0.0;
    } else {
      px = dx - px;
      py = dy - py;
      dotprod = px * dx + py * dy;
      if(dotprod <= 0.0) {
        projlenSq = 0.0;
      } else {
        projlenSq = dotprod * dotprod / (dx * dx + dy * dy);
      }
    }
    final double onLineLen = Math.sqrt(projlenSq);
    return new Point2D.Double(lx + onLineLen * dx, ly + onLineLen * dy);
  }

  public static Point2D closestPoint(final Point2D p, final Shape s, final double eps) {
    final PathIterator pi = s.getPathIterator(null, eps);
    final Point2D best = new Point2D.Double(Double.NaN, Double.NaN);
    final Line2D line = new Line2D.Double(best, best);
    double bestDistSq = Double.POSITIVE_INFINITY;
    double firstX = Double.NaN;
    double firstY = Double.NaN;
    double lastX = Double.NaN;
    double lastY = Double.NaN;
    final double coords[] = new double[6];
    while(!pi.isDone()) {
      final boolean validLine;
      switch(pi.currentSegment(coords)) {
        case PathIterator.SEG_MOVETO:
          lastX = coords[0];
          lastY = coords[1];
          firstX = lastX;
          firstY = lastY;
          validLine = false;
          break;
        case PathIterator.SEG_LINETO: {
          final double x = coords[0];
          final double y = coords[1];
          line.setLine(lastX, lastY, x, y);
          lastX = x;
          lastY = y;
          validLine = true;
          break;
        }
        case PathIterator.SEG_CLOSE:
          line.setLine(lastX, lastY, firstX, firstY);
          validLine = true;
          break;
        default:
          throw new AssertionError();
      }
      if(validLine) {
        final Point2D candidate = closestPoint(p, line);
        final double distSq = candidate.distanceSq(p);
        if(distSq < bestDistSq) {
          bestDistSq = distSq;
          best.setLocation(candidate);
        }
      }
      pi.next();
    }
    return best;
  }

  public static Point2D closestPointWithin(
      final Point2D p, final Shape s, final double eps) {
    if(s.contains(p)) return p;
    return closestPoint(p, s, eps);
  }

}
