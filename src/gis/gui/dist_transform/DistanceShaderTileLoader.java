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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class DistanceShaderTileLoader extends ShaderTileLoader {

  private final Query q;

  public DistanceShaderTileLoader(final ResetableTileListener listener, final Query q) {
    super(listener, new File("shaders/dist.vert"),
        new File("shaders/dist.frag"));
    this.q = q;
  }

  private static int createTexture(final ByteBuffer buff, final int size) {
    final int tex = GL11.glGenTextures();
    GL11.glBindTexture(GL11.GL_TEXTURE_1D, tex);
    GL11.glTexImage1D(GL11.GL_TEXTURE_1D, 0, GL30.GL_RGBA32F, size, 0,
        GL30.GL_RGBA32F, GL11.GL_FLOAT, buff);
    GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
    GL11.glTexParameterf(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
    GL11.glTexParameterf(GL11.GL_TEXTURE_1D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
    return tex;
  }

  private static final double EPS = 1e-3;

  @Override
  protected void settingVariables(final TileInfo<FBOTileLoader> info) {
    final List<GeoMarker> markers = q.getResult();
    final List<Float> lines = new ArrayList<>();
    lines.add(1f);
    lines.add(1f);
    lines.add(1f);
    lines.add(1f);
    for(final GeoMarker gm : markers) {
      addLines(lines, gm.convert(info), EPS);
    }
    final int linesTex = createTexture(wrap(lines), lines.size() / 4);
    GL11.glBindTexture(GL11.GL_TEXTURE_1D, linesTex);
    GL13.glActiveTexture(GL13.GL_TEXTURE0);

    ARBShaderObjects.glUniform1fARB(attr("lines_length"), lines.size() / 4);
    ARBShaderObjects.glUniform1iARB(attr("lines"), 0);
    ARBShaderObjects.glUniform2fARB(attr("size"), info.getWidth(), info.getHeight());
    ARBShaderObjects.glUniform2fARB(attr("tile"), info.tileX(), info.tileY());
    ARBShaderObjects.glUniform1fARB(attr("zoom"), info.zoom());
  }

  private static ByteBuffer wrap(final List<Float> floats) {
    final ByteBuffer buff = ByteBuffer.allocateDirect(floats.size() * 4);
    for(final Float f : floats) {
      buff.putFloat(f);
    }
    return buff;
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
