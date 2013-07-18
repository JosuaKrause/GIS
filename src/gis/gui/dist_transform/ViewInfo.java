package gis.gui.dist_transform;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public interface ViewInfo {

  Point2D convert(Coordinate coord);

  Rectangle2D getLatLonViewPort();

  int getWidth();

  int getHeight();

  double getMeterPerPixel();

  Point getCenter();

  int getZoom();

  Coordinate getPosition(int x, int y);

  void drawImage(Graphics2D g, BufferedImage img);

}
