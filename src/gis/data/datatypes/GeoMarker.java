package gis.data.datatypes;

import java.util.Comparator;

/**
 * A geo marker holds an element reference.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
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
