package gis.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;

public class BusyPainter {

  final GisPanel gisPanel;
  private final BusyPainterRunnable runnable;
  final Image[] images;

  public BusyPainter(final GisPanel gisPanel) {
    this.gisPanel = gisPanel;
    runnable = new BusyPainterRunnable();
    images = new Image[8];
    for(int i = 0; i < images.length; ++i) {
      final URL url = getClass().getResource("/busy_" + i + ".png");
      images[i] = new ImageIcon(url).getImage();
    }
  }

  public synchronized void start() {
    if(!runnable.running) {// not safe, but better than nothing
      new Thread(runnable).start();
    }
  }

  public synchronized void stop() {
    runnable.running = false;
  }

  public boolean isRunning() {
    return runnable.running;
  }

  class BusyPainterRunnable implements Runnable {

    private static final long SLEEP_INTERVAL_MS = 100;

    volatile boolean running = false;
    private int counter = 0;

    @Override
    public void run() {
      running = true;
      while(running) {
        try {
          Thread.sleep(SLEEP_INTERVAL_MS);
        } catch(final InterruptedException e) {
          running = false;
        }
        final Graphics g = gisPanel.getGraphics();
        if(g == null) {
          running = false;
          return;
        }
        final Graphics2D g2 = (Graphics2D) g.create();
        final int x = (gisPanel.getWidth() - images[counter].getWidth(gisPanel)) / 2;
        final int y = (gisPanel.getHeight() - images[counter].getHeight(gisPanel)) / 2;
        g2.drawImage(images[counter], x, y, null);
        ++counter;
        if(counter >= images.length) {
          counter = 0;
        }
        g2.dispose();
      }
    }

  }

}
