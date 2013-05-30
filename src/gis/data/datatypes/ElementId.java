package gis.data.datatypes;

import java.util.Comparator;

/**
 * The element id holds a reference of an element to its table and its id in the
 * table.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 */
public final class ElementId {
  /** The table of the element. */
  public final Table table;
  /** The id of the element in the table. */
  public final int gid;

  /**
   * Creates an element id for the given configuration.
   * 
   * @param table The table.
   * @param gid The id.
   */
  public ElementId(final Table table, final int gid) {
    this.table = table;
    this.gid = gid;
  }

  @Override
  public boolean equals(final Object o) {
    if(o instanceof ElementId) {
      final ElementId e = (ElementId) o;
      if(table == e.table && gid == e.gid) return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return table.hashCode() * 31 + gid;
  }

  /** Allows to compare two elements. */
  private static final Comparator<ElementId> COMPARATOR = new Comparator<ElementId>() {

    @Override
    public int compare(final ElementId e1, final ElementId e2) {
      final int comp = Table.getComparator().compare(e1.table, e2.table);
      if(comp == 0) {
        if(e1.gid < e2.gid) return -1;
        if(e1.gid > e2.gid) return 1;
        return 0;
      }
      return comp;
    }

  };

  /**
   * Getter.
   * 
   * @return Compares two element ids.
   */
  public static final Comparator<ElementId> getComparator() {
    return COMPARATOR;
  }

  @Override
  public String toString() {
    return "[" + table.toString() + " " + gid + "]";
  }

}
