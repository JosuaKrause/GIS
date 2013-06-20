package gis.tiles;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.Reader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

public class ShaderTileLoader extends FBOTileLoader {

  private String vs;
  private String fs;
  private int program = 0;

  public ShaderTileLoader(final TileLoaderListener listener, final TileLoader parent,
      final Reader vertShader, final Reader fragShader) throws IOException {
    super(listener, parent);
    vs = drain(vertShader);
    fs = drain(fragShader);
  }

  private static String drain(final Reader r) throws IOException {
    final StringBuilder sb = new StringBuilder();
    final char[] buf = new char[1024];
    for(;;) {
      final int len = r.read(buf);
      if(len < 0) {
        break;
      }
      sb.append(buf, 0, len);
    }
    return sb.toString();
  }

  @Override
  protected void init() throws Exception {
    int vertShader = 0, fragShader = 0;
    try {
      vertShader = createShader(vs, ARBVertexShader.GL_VERTEX_SHADER_ARB);
      fragShader = createShader(fs, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
      vs = null;
      fs = null;
    } finally {
      if(vertShader == 0 || fragShader == 0) throw new Exception("error loading shader");
    }
    program = ARBShaderObjects.glCreateProgramObjectARB();
    if(program == 0) throw new Exception("cannot create program");
    ARBShaderObjects.glAttachObjectARB(program, vertShader);
    ARBShaderObjects.glAttachObjectARB(program, fragShader);
    ARBShaderObjects.glLinkProgramARB(program);
    if(ARBShaderObjects.glGetObjectParameteriARB(program,
        ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) throw new Exception(
        getLogInfo(program));

    ARBShaderObjects.glValidateProgramARB(program);
    if(ARBShaderObjects.glGetObjectParameteriARB(program,
        ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) throw new Exception(
        getLogInfo(program));
  }

  @Override
  protected void render(final TileInfo info) {
    final int w = info.getWidth();
    final int h = info.getHeight();
    ARBShaderObjects.glUseProgramObjectARB(program);
    glBegin(GL_QUADS);
    glVertex2d(0, 0);
    glVertex2d(w, 0);
    glVertex2d(w, h);
    glVertex2d(0, h);
    glEnd();
    ARBShaderObjects.glUseProgramObjectARB(0);
  }

  private static int createShader(final String src, final int type) throws Exception {
    int shader = 0;
    try {
      shader = ARBShaderObjects.glCreateShaderObjectARB(type);
      if(shader == 0) return 0;
      ARBShaderObjects.glShaderSourceARB(shader, src);
      ARBShaderObjects.glCompileShaderARB(shader);
      final int param = ARBShaderObjects.glGetObjectParameteriARB(shader,
          ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB);
      if(param == GL_FALSE) throw new Exception(
          "Error creating shader: " + getLogInfo(shader));
      return shader;
    } catch(final Exception e) {
      ARBShaderObjects.glDeleteObjectARB(shader);
      throw e;
    }
  }

  private static String getLogInfo(final int obj) {
    return ARBShaderObjects.glGetInfoLogARB(obj,
        ARBShaderObjects.glGetObjectParameteriARB(obj,
            ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
  }

}
