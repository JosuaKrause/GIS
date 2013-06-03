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
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

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

  /**
   * Stand-in rectangles get created when a polygon or marker is too small to
   * drawn regularly.
   * 
   * @author Joschi <josua.krause@googlemail.com>
   */
  private static class StandInRect extends Rectangle2D.Double {
    /** The color. */
    public final Color color;
    /** The background color. */
    public final Color backColor;
    /** The stroke. */
    public final Stroke stroke;

    /**
     * Returns a stand-in for the given map object with the corresponding
     * lat-lon-bounding box. (Note that the x coordinate corresponds to
     * longitude)
     * 
     * @param m The map object.
     * @param box The bounding box.
     * @param view The view.
     * @return The stand-in rectangle.
     */
    public static StandInRect getStandIn(
        final MapObject m, final Rectangle2D box, final JMapViewer view) {
      final Point2D tl = view.getMapPosition(box.getMinY(), box.getMinX(), false);
      final Point2D br = view.getMapPosition(box.getMinY(), box.getMinX(), false);
      final double minX = Math.min(tl.getX(), br.getX());
      final double maxX = Math.max(tl.getX(), br.getX());
      final double minY = Math.min(tl.getY(), br.getY());
      final double maxY = Math.max(tl.getY(), br.getY());
      return new StandInRect(m, minX, minY, maxX - minX, maxY - minY);
    }

    /**
     * Creates a stand-in rectangle.
     * 
     * @param m The map object.
     * @param x The left coordinate.
     * @param y The top coordinate.
     * @param w The width.
     * @param h The height.
     */
    private StandInRect(final MapObject m,
        final double x, final double y, final double w, final double h) {
      super(x, y, w, h);
      color = m.getColor();
      backColor = m.getBackColor();
      stroke = m.getStroke();
    }

  } // StandInRect

  /**
   * The list of stand-ins. Note that the list {@link #mapRectangleList} does
   * not work as expected. (ie. it's buggy)
   */
  private final List<StandInRect> standIns = new ArrayList<>();

  @Override
  protected void paintComponent(final Graphics gfx) {
    standIns.clear();
    mapMarkerList.clear();
    mapPolygonList.clear();
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
        final boolean addAsRect = (hor < 3 || ver < 3) || (hor < 6 && ver < 6);
        // adding the marker
        if(m.hasPolygon()) {
          if(addAsRect) {
            standIns.add(StandInRect.getStandIn(m.getPolygons()[0], box, this));
          } else {
            for(final MapPolygon p : m.getPolygons()) {
              mapPolygonList.add(p);
            }
          }
        } else {
          if(addAsRect) {
            standIns.add(StandInRect.getStandIn(m.getMarker()[0], box, this));
          } else {
            for(final MapMarker p : m.getMarker()) {
              mapMarkerList.add(p);
            }
          }
        }
      }
    }
    final Graphics2D g2 = (Graphics2D) gfx;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    final long start = System.nanoTime();
    { // draw map
      final Graphics2D g = (Graphics2D) g2.create();
      super.paintComponent(g);
      for(final StandInRect s : standIns) {
        final Graphics2D tmp = (Graphics2D) g.create();
        if(s.stroke != null) {
          tmp.setStroke(s.stroke);
        }
        tmp.setColor(s.backColor);
        tmp.fill(s);
        tmp.setColor(s.color);
        tmp.draw(s);
        tmp.dispose();
      }
      g.dispose();
    }

    /*
     * GeoMarkerLineString ls = (GeoMarkerLineString)m; //draw (partially)
     * visible linestrings, one at a time //has potential for errors if line,
     * but not its points should be visible for (int i = 0; i < ls.points.length
     * - 1; ++i) { Coordinate ca = ls.points[i]; Coordinate cb = ls.points[i +
     * 1]; Point pa = getMapPosition(ca, true); Point pb = getMapPosition(cb,
     * true); if (!(pa == null && pb == null)) { if (pa == null) { pa =
     * getMapPosition(ca, false); } else if (pb == null) { pb =
     * getMapPosition(cb, false); } ls.paintLineSegment(g, pb, pa); } }
     */

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
