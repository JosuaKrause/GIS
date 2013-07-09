package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;
import gis.gui.overlay.AbstractOverlayComponent;
import gis.gui.overlay.DistanceThresholdSelector;
import gis.gui.overlay.IOverlayComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

public class GisPanel extends JMapViewer {

  IImagePainter imagePainter;
  private BufferedImage image;

  private final List<IOverlayComponent> overlayComponents = new ArrayList<>();
  private DistanceThresholdSelector distanceThresholdSelector;

  private final BusyPainter busyPainter;

  public GisPanel() {
    setFocusable(true);
    // requestImageUpdate();
    addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(final ComponentEvent e) {
        alignOverlayComponents();
        // requestImageUpdate();
      }

    });

    // addMouseMotionListener(new MouseMotionAdapter() {
    //
    // @Override
    // public void mouseDragged(final MouseEvent arg0) {
    // if(imagePainter != null) {
    // updateImage();
    // }
    // }
    //
    // });

    addKeyListener(new KeyListener() {

      @Override
      public void keyTyped(final KeyEvent e) {
        System.out.println(e.getKeyChar());
        if(e.getKeyChar() == 'k') {
          imageUpdateRequested = true;
          repaint();
        } else if(e.getKeyChar() == 'l') {
          if(busyPainter.isRunning()) {
            busyPainter.stop();
          } else {
            busyPainter.start();
          }
          System.out.println(getMeterPerPixel());
          System.out.println("x" + 92.80207052971653 / 0.36375105061146223);
        }
      }

      @Override
      public void keyReleased(final KeyEvent e) {
        // TODO Auto-generated method stub

      }

      @Override
      public void keyPressed(final KeyEvent arg0) {
        // TODO Auto-generated method stub

      }
    });

    busyPainter = new BusyPainter(this);
  }

  private double imgPosX, imgPosY;

  private Image curHover;

  private final List<Query<?>> queries = new ArrayList<>();
  private Point centerPosition;
  private int zoomValue;

  public void addQuery(final Query<?> q) {
    queries.add(q);
    repaint();
  }

  public void removeQuery(final Query<?> q) {
    queries.remove(q);
    q.clearCache();
    repaint();
  }

  public void pick(final Point2D pos, final List<GeoMarker> picks) {
    final Coordinate tl = getPosition(0, 0);
    final Coordinate br = getPosition(getWidth(), getHeight());
    final Rectangle2D latLonVP;
    if(tl != null && br != null) {
      final double minLon = Math.min(tl.getLon(), br.getLon());
      final double maxLon = Math.max(tl.getLon(), br.getLon());
      final double minLat = Math.min(tl.getLat(), br.getLat());
      final double maxLat = Math.max(tl.getLat(), br.getLat());
      latLonVP = new Rectangle2D.Double(
          minLon, minLat, maxLon - minLon, maxLat - minLat);
    } else {
      latLonVP = null;
    }
    for(final Query<?> q : queries) {
      for(final GeoMarker m : q.getResult()) {
        if(latLonVP == null) {
          // we're too small anyway and nowhere near the border of the map
          continue;
        }
        // visibility check
        if(!m.inViewport(latLonVP)) {
          continue;
        }
        // smallness check
        final Rectangle2D box = m.getLatLonBBox();
        final double hor = box.getWidth() / latLonVP.getWidth() * getWidth();
        final double ver = box.getHeight() / latLonVP.getHeight() * getHeight();
        final boolean simple = (hor < 3 || ver < 3) || (hor < 6 && ver < 6);
        // painting the marker
        if(m.pick(pos, this, simple)) {
          picks.add(m);
        }
      }
    }
  }

  public Rectangle2D getLatLonViewPort() {
    final Coordinate tl = getPosition(0, 0);
    final Coordinate br = getPosition(getWidth(), getHeight());
    if(tl != null && br != null) {
      final double minLon = Math.min(tl.getLon(), br.getLon());
      final double maxLon = Math.max(tl.getLon(), br.getLon());
      final double minLat = Math.min(tl.getLat(), br.getLat());
      final double maxLat = Math.max(tl.getLat(), br.getLat());
      return new Rectangle2D.Double(
          minLon, minLat, maxLon - minLon, maxLat - minLat);
    }
    return null;
  }

  @Override
  protected void paintComponent(final Graphics gfx) {
    final Graphics2D g2 = (Graphics2D) gfx;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    final long start = System.nanoTime();
    { // draw map
      final Graphics2D g = (Graphics2D) g2.create();
      super.paintComponent(g);
      g.dispose();
    }
    { // draw elements
      final Rectangle2D latLonVP = getLatLonViewPort();
      for(final Query<?> q : queries) {
        if(!q.getPaintMarkers()) {
          // skip markers from this query
          continue;
        }
        for(final GeoMarker m : q.getResult()) {
          if(latLonVP == null) {
            // we're too small anyway and nowhere near the border of the map
            continue;
          }
          // visibility check
          if(!m.inViewport(latLonVP)) {
            continue;
          }
          // smallness check
          final Rectangle2D box = m.getLatLonBBox();
          final double hor = box.getWidth() / latLonVP.getWidth() * getWidth();
          final double ver = box.getHeight() / latLonVP.getHeight() * getHeight();
          final boolean simple = (hor < 3 || ver < 3) || (hor < 6 && ver < 6);
          // painting the marker
          final Graphics2D g = (Graphics2D) g2.create();
          m.paint(g, this, simple);
          g.dispose();
        }
      }
    }
    { // draw overlay image
      if(imagePainter != null && imageUpdateRequested) {
        imageUpdateRequested = false;
        updateImage();
        final Graphics2D g = (Graphics2D) g2.create();
        paintImage(g);
        imagePainter.paint(g);
        g.dispose();
      }
    }
    { // draw hover image
      if(curHover != null) {
        final Point p = new Point(getCenter());
        final int z = getZoom();
        if(centerPosition != null
            && (p.x != centerPosition.x || p.y != centerPosition.y || z != zoomValue)) {
          setHoverImage(null, p);
        } else {
          final Graphics2D g = (Graphics2D) g2.create();
          g.translate(imgPosX, imgPosY);
          g.drawImage(curHover, 0, 0, this);
          g.dispose();
        }
        centerPosition = p;
        zoomValue = z;
      }
    }
    { // draw HUD
      final double fps = 1.0 / (System.nanoTime() - start) * 1e9;
      final String hud = String.format("FPS: %.3f", fps);
      final float x = 5f;
      final float y = 16.5f;
      drawText(g2, hud, x, y);
    }
    // note -- after this method returns the zoomValue
    // slider is drawn with the same graphics object

    // draw overlayComponents
    for(final IOverlayComponent c : overlayComponents) {
      if(c.isVisible()) {
        c.paint(g2);
      }
    }
    // draw distance selector for "parks near water" task
    if(distanceThresholdSelector != null) {
      distanceThresholdSelector.paint(g2, getWidth() - 5, 5);
    }

  }

  // @Override
  // public void zoomIn(final Point p) {
  // super.zoomIn(p);
  // requestImageUpdate();
  // }
  //
  // @Override
  // public void zoomOut(final Point p) {
  // super.zoomOut(p);
  // requestImageUpdate();
  // }

  // @Override
  // public void setDisplayPosition(final Point mapPoint, final int x, final int
  // y,
  // final int zoom) {
  // super.setDisplayPosition(mapPoint, x, y, zoom);
  // requestImageUpdate();
  // }

  public static final void drawText(final Graphics2D g,
      final String text, final float x, final float y) {
    g.setColor(Color.WHITE);
    g.drawString(text, x - 1, y);
    g.drawString(text, x + 1, y);
    g.drawString(text, x, y - 1);
    g.drawString(text, x, y + 1);
    g.drawString(text, x - 1, y - 1);
    g.drawString(text, x + 1, y - 1);
    g.drawString(text, x - 1, y + 1);
    g.drawString(text, x + 1, y + 1);
    g.setColor(Color.BLACK);
    g.drawString(text, x, y);
  }

  public void setHoverImage(final Image img, final Point2D pos) {
    if(curHover != null) {
      curHover.flush();
    }
    curHover = img;
    imgPosX = pos.getX();
    imgPosY = pos.getY();
    repaint();
  }

  private void paintImage(final Graphics2D g) {
    final Insets insets = getInsets();
    g.translate((double) insets.left, (double) insets.top);
    g.drawImage(image, 0, 0, null);
  }

  private volatile boolean imageUpdateRequested = false;

  //
  // void requestImageUpdate() {
  // imageUpdateRequested = true;
  // }

  private void updateImage() {
    if(imagePainter != null) {
      System.out.println("*");// TODO
      final Insets insets = getInsets();
      final Dimension dim = getSize();
      final int width = Math.max(dim.width - insets.left - insets.right, 1);
      final int height = Math.max(dim.height - insets.top - insets.bottom, 1);
      image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
      imagePainter.paint(image);
    }
  }

  public void registerOverlayComponent(final IOverlayComponent overlayComponent) {
    overlayComponents.add(overlayComponent);
    if(overlayComponent.isVisible()) {
      alignOverlayComponents();
    }
  }

  /** Call when visibility of an overlay component has changed */
  public void alignOverlayComponents() {
    final int width = getWidth();
    final int height = getHeight();
    final Insets insets = getInsets();
    // TODO
    // supports only one left and one right component, for now
    for(final IOverlayComponent c : overlayComponents) {
      final Dimension dim = c.getDimension();
      int x;
      final int y = height - insets.bottom - dim.height
          - AbstractOverlayComponent.PADDING;
      if(c.getHorizontalAlignmentWeight() < 0) {
        x = insets.left + 2 * AbstractOverlayComponent.PADDING;
      } else {
        x = width - insets.right - dim.width - 1 - AbstractOverlayComponent.PADDING;
      }
      c.setPosition(new Point(x, y));
    }
  }

  public void openDistanceThresholdSelector(final Query<?> query,
      final double distanceInMeters) {
    if(distanceThresholdSelector == null) {
      distanceThresholdSelector = new DistanceThresholdSelector(query, distanceInMeters);
      add(distanceThresholdSelector);
    }
  }

  public void closeDistanceThresholdSelector() {
    if(distanceThresholdSelector != null) {
      remove(distanceThresholdSelector);
      distanceThresholdSelector = null;
    }
  }

  public double getThresholdDistanceInMeters() {
    if(distanceThresholdSelector != null) return distanceThresholdSelector.getDistanceInMeters();
    return -1;
  }

  public void setImagePainter(final IImagePainter imagePainter) {
    this.imagePainter = imagePainter;
    // requestImageUpdate();
    repaint();
  }
}
