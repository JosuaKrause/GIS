package gis.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

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

  volatile boolean init = true;

  private final Thread painter = new Thread() {

    @Override
    public void run() {
      try {
        Display.setVSyncEnabled(false);
        Display.setDisplayMode(new DisplayMode(1, 1));
        Display.create();
        out: while(!isInterrupted()) {
          TileInfo info;
          synchronized(this) {
            while((info = infos.poll()) == null) {
              if(init) {
                init();
                init = false;
              }
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
      } catch(final Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

  };

  @Override
  protected BufferedImage createImageFor(final TileInfo info) throws IOException {
    enqueue(info);
    return getImage(info);
  }

  private void enqueue(final TileInfo info) {
    synchronized(painter) {
      infos.add(info);
      painter.notifyAll();
    }
  }

  private BufferedImage getImage(final TileInfo info) {
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

  protected abstract void init() throws Exception;

  protected abstract void render(TileInfo info);

  public void reloadTiles() {
    synchronized(painter) {
      init = true;
      painter.notifyAll();
    }
    final TileLoader p = getParent();
    final List<TileInfo> keys = new ArrayList<>(imgs.keySet());
    for(final TileInfo info : keys) {
      imgs.remove(info);
      enqueue(info);
    }
    final TileLoaderListener listener = getListener();
    for(final TileInfo info : keys) {
      info.prepareTile(p);
      final BufferedImage img = getImage(info);
      info.setImage(img, listener, p);
    }
  }

}
