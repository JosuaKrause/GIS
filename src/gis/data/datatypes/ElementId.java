package gis.data.datatypes;

import gis.data.db.Query;

import java.io.Serializable;
import java.util.Objects;

/**
 * The element id of an geometry marker. This id can be used to reference the
 * element in a query.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class ElementId implements Serializable {
  private static final long serialVersionUID = 4781869027332841588L;
  /** The id in the table. */
  private final String id;
  /** The query. */
  private transient Query query;

  /**
   * Creates an element id.
   * 
   * @param query The query.
   * @param id The id.
   */
  public ElementId(final Query query, final String id) {
    this.query = Objects.requireNonNull(query);
    this.id = Objects.requireNonNull(id);
  }

  /**
   * Getter.
   * 
   * @return The query of the element.
   */
  public Query getQuery() {
    return query;
  }

  /**
   * Getter.
   * 
   * @return The id.
   */
  public String getId() {
    return id;
  }

  public void setQuery(final Query query) {
    this.query = Objects.requireNonNull(query);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id.hashCode();
    result = prime * result + query.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if(this == obj) return true;
    if(!(obj instanceof ElementId)) return false;
    final ElementId other = (ElementId) obj;
    if(!id.equals(other.id)) return false;
    if(!query.equals(other.query)) return false;
    return true;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id: " + id + " query: " + query.getName() + "]";
  }

}
