package gis.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public abstract class FBOTileLoader extends ImageTileLoader {

  public FBOTileLoader(final ResetableTileListener listener) {
    super(listener);
    painter.setDaemon(true);
    painter.start();
  }

  Queue<TileInfo> infos = new LinkedList<>();

  Map<TileInfo, BufferedImage> imgs = new ConcurrentHashMap<>();

  volatile boolean init = true;

  private final Thread painter = new Thread() {

    @Override
    public void run() {
      while(true) {
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
