package gis.data.datatypes;

import java.awt.geom.Rectangle2D;
import java.util.Comparator;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

/**
 * A geo marker holds an element reference.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class GeoMarker {
  /** The element id. */
  public final ElementId id;
  /** Whether the geo marker is currently selected. */
  public boolean selected;

  /**
   * Creates a geo marker.
   * 
   * @param id The id.
   */
  public GeoMarker(final ElementId id) {
    this.id = id;
    selected = false;
  }

  /**
   * Getter.
   * 
   * @return The element id.
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

  @Override
  public boolean equals(final Object o) {
    if(o instanceof GeoMarker) {
      final GeoMarker m = (GeoMarker) o;
      return id.equals(m.id);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id.toString() + "]";
  }

  /** Allows for comparing geo markers. */
  private static final Comparator<GeoMarker> COMPARATOR = new Comparator<GeoMarker>() {

    private final Comparator<ElementId> comparator = ElementId.getComparator();

    @Override
    public int compare(final GeoMarker g1, final GeoMarker g2) {
      return comparator.compare(g1.id, g2.id);
    }

  };

  /**
   * Getter.
   * 
   * @return Compares geo markers.
   */
  public static final Comparator<GeoMarker> getComparator() {
    return COMPARATOR;
  }

}
