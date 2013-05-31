package gis.gui;

import gis.data.datatypes.GeoMarker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
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

  public void addGeoMarkerList(final List<GeoMarker> markers) {
    for(final GeoMarker m : markers) {
      if(m.hasPolygon()) {
        for(final MapPolygon p : m.getPolygons()) {
          addMapPolygon(p);
        }
      } else {
        for(final MapMarker p : m.getMarker()) {
          addMapMarker(p);
        }
      }
    }
  }

  public void removeGeoMarkers(final List<GeoMarker> markers) {
    for(final GeoMarker m : markers) {
      if(m.hasPolygon()) {
        for(final MapPolygon p : m.getPolygons()) {
          removeMapPolygon(p);
        }
      } else {
        for(final MapMarker p : m.getMarker()) {
          removeMapMarker(p);
        }
      }
    }
  }

  @Override
  protected void paintComponent(final Graphics gfx) {
    final Graphics2D g2 = (Graphics2D) gfx;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    {
      final Graphics2D g = (Graphics2D) g2.create();
      super.paintComponent(g);
      g.dispose();
    }
    {
      final Graphics2D g = (Graphics2D) g2.create();
      if(drawImage) {
        paintImage(g);
      }
      g.dispose();
    }
    g2.dispose();
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

  // SELECT * FROM berlin_poi WHERE name LIKE 'Museum%';

  // TODO code reference
  // public Coordinate getPosition() {
  // double lon = OsmMercator.XToLon(center.x, zoom);
  // double lat = OsmMercator.YToLat(center.y, zoom);
  // return new Coordinate(lat, lon);
  // }

}
