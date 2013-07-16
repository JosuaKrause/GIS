package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;
import gis.tiles.FBOTileLoader;
import gis.tiles.ResetableTileListener;
import gis.tiles.ShaderTileLoader;

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.ARBShaderObjects;

public class DistanceShaderTileLoader extends ShaderTileLoader {

  private final Query q;

  public DistanceShaderTileLoader(final ResetableTileListener listener, final Query q) {
    super(listener, new File("shaders/dist.vert"),
        new File("shaders/dist.frag"));
    this.q = q;
  }

  private static final double EPS = 1e-3;

  @Override
  protected void settingVariables(final TileInfo<FBOTileLoader> info) {
    final List<GeoMarker> markers = q.getResult();
    final IntBuffer sizes = IntBuffer.allocate(markers.size());
    final List<Float> lines = new ArrayList<>();
    for(final GeoMarker gm : markers) {
      sizes.put(addLines(lines, gm.convert(info), EPS));
    }
    ARBShaderObjects.glUniform1ARB(attr("sizes"), sizes);
    ARBShaderObjects.glUniform1ARB(attr("lines"), wrap(lines));
    ARBShaderObjects.glUniform2fARB(attr("size"), info.getWidth(), info.getHeight());
    ARBShaderObjects.glUniform2fARB(attr("tile"), info.tileX(), info.tileY());
    ARBShaderObjects.glUniform1fARB(attr("zoom"), info.zoom());
  }

  private static FloatBuffer wrap(final List<Float> floats) {
    final float[] f = new float[floats.size()];
    for(int i = 0; i < f.length; ++i) {
      f[i] = floats.get(i);
    }
    return FloatBuffer.wrap(f);
  }

  private static int addLines(final List<Float> lines, final Shape s, final double eps) {
    int size = 0;
    final PathIterator pi = s.getPathIterator(null, eps);
    final Line2D line = new Line2D.Double();
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
        size += 4;
        lines.add((float) line.getX1());
        lines.add((float) line.getY1());
        lines.add((float) line.getX2());
        lines.add((float) line.getY2());
      }
      pi.next();
    }
    return size;
  }

}
