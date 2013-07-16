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

public abstract class FBOTileLoader extends ImageTileLoader<FBOTileLoader> {

  public FBOTileLoader(final ResetableTileListener listener) {
    super(listener);
  }

  static Queue<TileInfo<FBOTileLoader>> infos = new LinkedList<>();

  static Map<TileInfo<FBOTileLoader>, BufferedImage> imgs = new ConcurrentHashMap<>();

  static volatile boolean init = true;

  private static final Thread painter = new Thread() {

    @Override
    public void run() {
      Display.setVSyncEnabled(false);
      try {
        Display.setDisplayMode(new DisplayMode(1, 1));
        Display.create();
      } catch(final LWJGLException e) {
        e.printStackTrace();
        return;
      }
      while(true) {
        try {
          out: while(!isInterrupted()) {
            TileInfo<FBOTileLoader> info;
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
            final FBOTileLoader tl = info.getTileLoader();
            if(init) {
              tl.init();
              init = false;
            }
            final int width = info.getWidth();
            final int height = info.getHeight();
            final FBO fbo = new FBO(width, height);
            fbo.renderInit();
            tl.render(info);
            fbo.beforeOut();
            final BufferedImage res = fbo.getFBOImage();
            imgs.put(info, res);
            synchronized(info) {
              info.notifyAll();
            }
            fbo.dispose();
          }
          Display.destroy();
        } catch(final InterruptedException e) {
          interrupt();
          break;
        } catch(final Exception e) {
          // trying to recover after an exception
          e.printStackTrace();
          try {
            // dont spam the console
            Thread.sleep(5000);
          } catch(final InterruptedException i) {
            interrupt();
            break;
          }
          continue;
        }
        break;
      }
    }

  };

  static {
    painter.setDaemon(true);
    painter.start();
  }

  @Override
  protected BufferedImage createImageFor(final TileInfo<FBOTileLoader> info)
      throws IOException {
    enqueue(info);
    return getImage(info);
  }

  private static void enqueue(final TileInfo<FBOTileLoader> info) {
    synchronized(painter) {
      infos.add(info);
      painter.notifyAll();
    }
  }

  private static BufferedImage getImage(final TileInfo<FBOTileLoader> info) {
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

  protected abstract void render(TileInfo<FBOTileLoader> info);

  @Override
  public void reloadAll() {
    synchronized(painter) {
      init = true;
      imgs.clear();
      infos.clear();
      painter.notifyAll();
    }
    super.reloadAll();
  }

}
