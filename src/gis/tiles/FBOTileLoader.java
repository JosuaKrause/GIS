package gis.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

public abstract class FBOTileLoader extends ImageTileLoader {

  public FBOTileLoader(final TileLoaderListener listener, final TileLoader parent) {
    super(listener, parent);
    painter.setDaemon(true);
    painter.start();
  }

  Queue<TileInfo> infos = new LinkedList<>();

  Map<TileInfo, BufferedImage> imgs = new ConcurrentHashMap<>();

  private final Thread painter = new Thread() {

    @Override
    public void run() {
      try {
        Display.setVSyncEnabled(false);
        Display.setDisplayMode(new DisplayMode(1, 1));
        Display.create();
        init();
        out: while(!isInterrupted()) {
          TileInfo info;
          synchronized(this) {
            while((info = infos.poll()) == null) {
              try {
                wait();
              } catch(final InterruptedException e) {
                interrupt();
                break out;
              }
            }
          }
          final int width = info.getWidth();
          final int height = info.getHeight();
          final FBO fbo = new FBO(width, height);
          fbo.renderInit();
          render(info);
          fbo.beforeOut();
          final BufferedImage res = fbo.getFBOImage();
          imgs.put(info, res);
          synchronized(info) {
            info.notifyAll();
          }
          fbo.dispose();
        }
        Display.destroy();
      } catch(final LWJGLException e) {
        e.printStackTrace();
      }
    }

  };

  @Override
  protected BufferedImage createImageFor(final TileInfo info) throws IOException {
    synchronized(painter) {
      infos.add(info);
      painter.notifyAll();
    }
    synchronized(info) {
      for(;;) {
        final BufferedImage res = imgs.get(info);
        if(res != null) return res;
        try {
          info.wait();
        } catch(final InterruptedException e) {
          return null;
        }
      }
    }
  }

  protected abstract void init();

  protected abstract void render(TileInfo info);

}
