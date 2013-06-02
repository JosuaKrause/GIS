package gis.data.datatypes;

import java.awt.geom.Rectangle2D;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

/**
 * A geo marker holds an element reference.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class GeoMarker {
  /** Whether the geo marker is currently selected. */
  public boolean selected;

  /**
   * Creates a geo marker.
   */
  public GeoMarker() {
    selected = false;
  }

  /**
   * Getter.
   * 
   * @return Whether this geo marker is selected.
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Getter.
   * 
   * @return Whether the marker consists of a polygon.
   */
  public boolean hasPolygon() {
    throw new UnsupportedOperationException();
  }

  /**
   * Getter.
   * 
   * @return All markers from this marker.
   */
  public MapMarker[] getMarker() {
    throw new UnsupportedOperationException();
  }

  /**
   * Getter.
   * 
   * @return All polygons from this marker.
   */
  public MapPolygon[] getPolygons() {
    throw new UnsupportedOperationException();
  }

  /**
   * Getter.
   * 
   * @return The world coordinate bounding box.
   */
  public Rectangle2D getLatLonBBox() {
    throw new UnsupportedOperationException();
  }

  /**
   * Checks whether the polygon is in the current viewport. Note that this
   * method may return weird results when applied near the International Date
   * Line. But since our data points lie nowhere near the International Date
   * Line this is no problem.
   * 
   * @param latLonViewport The viewport of the map viewer.
   * @return Whether the marker must be drawn.
   */
  public boolean inViewport(final Rectangle2D latLonViewport) {
    return latLonViewport.intersects(getLatLonBBox());
  }

}
