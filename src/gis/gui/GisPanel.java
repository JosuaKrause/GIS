package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.GeoMarkerMultiPolygon;
import gis.data.datatypes.GeoMarkerPoint;
import gis.data.datatypes.GeoMarkerPolygon;
import gis.data.datatypes.Table;
import gis.util.GeoMarkerList;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;

public class GisPanel extends JMapViewer {

  private final GeoMarkerList markers = new GeoMarkerList();

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
    this.markers.addAll(markers);
  }

  public void removeGeoMarkers(final Table table) {
    markers.removeAll(table);
  }

  @Override
  protected void paintComponent(final Graphics gfx) {
    super.paintComponent(gfx);
    final Graphics2D g = (Graphics2D) gfx.create();
    // paint markers
    for(final GeoMarker m : markers) {
      if(m instanceof GeoMarkerPoint) {
        final GeoMarkerPoint point = (GeoMarkerPoint) m;
        final Point2D p = getMapPosition(point.coordinate, true);
        if(p != null) {
          point.paint(g, p);
        }
      } else if(m instanceof GeoMarkerPolygon) {
        final GeoMarkerPolygon poly = (GeoMarkerPolygon) m;
        final Path2D path = new Path2D.Double();
        for(int i = 0; i < poly.polygon.length; ++i) {
          final Point2D p = getMapPosition(poly.polygon[i], false);
          if(i == 0) {
            path.moveTo(p.getX(), p.getY());
          } else {
            path.lineTo(p.getX(), p.getY());
          }
        }
        path.closePath();
        poly.paint(g, path);
      } else if(m instanceof GeoMarkerMultiPolygon) {
        final GeoMarkerMultiPolygon poly = (GeoMarkerMultiPolygon) m;
        for(final Coordinate[] polygon : poly.polygons) {
          final Path2D path = new Path2D.Double();
          for(int i = 0; i < polygon.length; ++i) {
            final Point2D p = getMapPosition(polygon[i], false);
            if(i == 0) {
              path.moveTo(p.getX(), p.getY());
            } else {
              path.lineTo(p.getX(), p.getY());
            }
          }
          path.closePath();
          poly.paint(g, path);
        }
      }
    }
    if(drawImage) {
      paintImage(g);
    }
    g.dispose();
  }

  private void paintImage(final Graphics2D gfx) {
    final Graphics2D g = (Graphics2D) gfx.create();
    final Insets insets = getInsets();
    g.translate((double) insets.left, (double) insets.top);
    g.drawImage(image, 0, 0, null);
    g.dispose();
  }

  private void updateImage() {
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
