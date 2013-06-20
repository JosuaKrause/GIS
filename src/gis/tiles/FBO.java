package gis.tiles;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

public final class FBO {

  private final int width;
  private final int height;

  private final int fboID;
  private final int colID;

  public FBO(final int width, final int height) {
    this.width = width;
    this.height = height;
    glViewport(0, 0, width, height);
    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    GLU.gluOrtho2D(0, width, height, 0);
    glMatrixMode(GL_MODELVIEW);
    glLoadIdentity();
    glClearColor(0f, 0f, 0f, 0f);
    glShadeModel(GL_SMOOTH);
    if(!canFBO()) throw new AssertionError("no FBO support!");
    fboID = glGenFramebuffersEXT();
    colID = glGenTextures();
    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboID);
    glBindTexture(GL_TEXTURE_2D, colID);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT,
        (ByteBuffer) null);
    glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,
        GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, colID, 0);
    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
  }

  public void renderInit() {
    glViewport(0, 0, width, height);
    glBindTexture(GL_TEXTURE_2D, 0);
    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, fboID);
    glClearColor(0, 0, 0, 0);
    glClear(GL_COLOR_BUFFER_BIT);
    glLoadIdentity();
  }

  public void beforeOut() {
    glEnable(GL_TEXTURE_2D);
    glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
    glBindTexture(GL_TEXTURE_2D, colID);
  }

  /**
   * Creates a buffered image from the current FBO.
   * 
   * @return The image.
   */
  public BufferedImage getFBOImage() {
    final ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
    glGetTexImage(GL_TEXTURE_2D, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    final BufferedImage image = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
    final int[] px = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    for(int x = 0; x < width; x++) {
      for(int y = 0; y < height; y++) {
        final int i = (x + (width * y)) * 4;
        final int r = buffer.get(i) & 0xff;
        final int g = buffer.get(i + 1) & 0xff;
        final int b = buffer.get(i + 2) & 0xff;
        final int a = buffer.get(i + 3) & 0xff;
        final int argb = (a << 24) | (r << 16) | (g << 8) | b;
        final int off = (x + width * (height - y - 1));
        px[off] = argb;
      }
    }
    return image;
  }

  public static boolean canFBO() {
    return GLContext.getCapabilities().GL_EXT_framebuffer_object;
  }

}
