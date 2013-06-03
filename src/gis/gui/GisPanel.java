package gis.gui;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.db.Query;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

public class GisPanel extends JMapViewer {

  boolean drawImage = false;
  private BufferedImage image;

  public GisPanel() {
    grabFocus();
    updateImage();
    addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(final ComponentEvent e) {
        updateImage();
      }

    });
  }

  private final Set<Query<?>> queries = new HashSet<>();

  public void addQuery(final Query<?> q) {
    queries.add(q);
    repaint();
  }

  public void removeQuery(final Query<?> q) {
    queries.remove(q);
    q.clearCache();
    repaint();
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
          final Graphics2D g = (Graphics2D) g2.create();
          m.paint(g, this, simple);
          g.dispose();
        }
      }
    }
    { // draw overlay image
      final Graphics2D g = (Graphics2D) g2.create();
      if(drawImage) {
        paintImage(g);
      }
      g.dispose();
    }
    // draw HUD
    final double fps = 1.0 / (System.nanoTime() - start) * 1e9;
    final String hud = "FPS: " + fps;
    g2.setColor(Color.WHITE);
    final float x = 5f;
    final float y = 16.5f;
    g2.drawString(hud, x - 1, y);
    g2.drawString(hud, x + 1, y);
    g2.drawString(hud, x, y - 1);
    g2.drawString(hud, x, y + 1);
    g2.drawString(hud, x - 1, y - 1);
    g2.drawString(hud, x + 1, y - 1);
    g2.drawString(hud, x - 1, y + 1);
    g2.drawString(hud, x + 1, y + 1);
    g2.setColor(Color.BLACK);
    g2.drawString(hud, x, y);
    // note -- after this method returns the zoom
    // slider is drawn with the same graphics object
  }

  private void paintImage(final Graphics2D gfx) {
    final Graphics2D g = (Graphics2D) gfx.create();
    final Insets insets = getInsets();
    g.translate((double) insets.left, (double) insets.top);
    g.drawImage(image, 0, 0, null);
    g.dispose();
  }

  void updateImage() {
    final Insets insets = getInsets();
    final Dimension dim = getSize();
    final int width = Math.max(dim.width - insets.left - insets.right, 1);
    final int height = Math.max(dim.height - insets.top - insets.bottom, 1);
    image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

    // draw chess board pattern
    final int alpha = 32;
    final int tileSize = 8;
    final int whiteRgb = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
    final int blackRgb = (alpha << 24);
    for(int y = 0; y < image.getHeight(); ++y) {
      for(int x = 0; x < image.getWidth(); ++x) {
        if((x / tileSize + y / tileSize) % 2 == 0) {
          image.setRGB(x, y, whiteRgb);
        } else {
          image.setRGB(x, y, blackRgb);
        }
      }
    }

    // //draw distance transformation
    // for (int y = 0; y < image.getHeight(); ++y) {
    // for (int x = 0; x < image.getWidth(); ++x) {
    // double lon = OsmMercator.XToLon(x, zoom);
    // double lat = OsmMercator.YToLat(y, zoom);
    // Coordinate c = new Coordinate(lat, lon);
    //
    // }
    // }
  }

  private void addListeners() {
    addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(final ComponentEvent e) {
        updateImage();
      }

    });
  }

  public GeoMarker getGeoMarker(final ElementId id) {
    for(final Query<?> q : queries) {
      final GeoMarker m = q.get(id);
      if(m != null) return m;
    }
    throw new IllegalStateException();
  }

}
