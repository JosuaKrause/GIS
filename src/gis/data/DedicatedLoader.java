package gis.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DedicatedLoader {

  private static final ExecutorService LOADER = Executors.newCachedThreadPool();

  protected volatile Loader cur;

  public static abstract class Loader implements Runnable {

    private DedicatedLoader loader;

    void setLoader(final DedicatedLoader loader) {
      this.loader = loader;
    }

    protected boolean stillAlive() {
      if(Thread.currentThread().isInterrupted()) return false;
      return loader.cur == this;
    }

  } // Loader

  public void load(final Loader l) {
    l.setLoader(this);
    if(cur != null) {
      synchronized(cur) {
        cur.notifyAll();
      }
    }
    cur = l;
    LOADER.execute(l);
  }

}
