package gis.data.datatypes;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

/**
 * A geo marker holds an element reference.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class GeoMarker {
  /** The reference. */
  private final ElementId id;

  private final String info;
  /** Whether the geo marker is currently selected. */
  public boolean selected;

  /**
   * Creates a geo marker.
   * 
   * @param id The reference id.
   */
  public GeoMarker(final String info, final ElementId id) {
    this.id = Objects.requireNonNull(id);
    selected = false;
    this.info = Objects.requireNonNull(info);
  }

  public String getInfo() {
    return info;
  }

  /**
   * Getter.
   * 
   * @return The reference id to reference the element in an SQL query.
   */
  public ElementId getId() {
    return id;
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
  public abstract boolean hasPolygon();

  /**
   * Getter.
   * 
   * @return All markers from this marker.
   */
  public abstract MapMarker[] getMarker();

  /**
   * Getter.
   * 
   * @return All polygons from this marker.
   */
  public abstract MapPolygon[] getPolygons();

  /**
   * Getter.
   * 
   * @return The world coordinate bounding box.
   */
  public abstract Rectangle2D getLatLonBBox();

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

  public abstract void setRadius(final double radius);

  public abstract void setColor(final Color color);

}
