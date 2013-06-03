package gis.data.datatypes;

import java.util.Objects;

/**
 * The element id of an geometry marker. This id can be used to reference the
 * element in a query.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class ElementId {
  /** The table. */
  private final Table table;
  /** The id in the table. */
  private final int id;

  /**
   * Creates an element id.
   * 
   * @param table The table.
   * @param id The id.
   */
  public ElementId(final Table table, final int id) {
    this.table = Objects.requireNonNull(table);
    this.id = id;
  }

  /**
   * Getter.
   * 
   * @return The table of the element.
   */
  public Table getTable() {
    return table;
  }

  /**
   * Getter.
   * 
   * @return The id.
   */
  public int getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + ((table == null) ? 0 : table.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if(this == obj) return true;
    if(!(obj instanceof ElementId)) return false;
    final ElementId other = (ElementId) obj;
    if(id != other.id) return false;
    if(table != other.table) return false;
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id: " + id + " table: " + table.name + "]";
  }

}
