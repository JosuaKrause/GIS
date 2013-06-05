package gis.data.db;

import gis.data.GeometryConverter;
import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.postgis.PGgeometry;

/**
 * A query holds the results of an SQL query.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * @param <T> The type of flavour.
 */
public class Query<T> {
  /** The actual SQL query. */
  private final String query;
  /** The table. */
  private final Table table;
  /** The name of the query. */
  private final String name;

  /**
   * Creates a query.
   * 
   * @param query The SQL query. The column names used are taken from the
   *          {@link Table} definition.
   * @param table The table whose elements are returned. (At least for which the
   *          ids count)
   * @param name The name of the query.
   */
  public Query(final String query, final Table table, final String name) {
    this.name = Objects.requireNonNull(name);
    this.table = Objects.requireNonNull(table);
    this.query = Objects.requireNonNull(query);
  }

  /** The lookup for ids. */
  private final Map<ElementId, GeoMarker> map = new HashMap<>();

  /**
   * Getter.
   * 
   * @return The table.
   */
  public Table getTable() {
    return table;
  }

  /**
   * Getter.
   * 
   * @return The name.
   */
  public String getName() {
    return name;
  }

  /** Whether the content has been already fetched. */
  private boolean hasContent;
  /** The list of markers. */
  private final List<GeoMarker> markers = new ArrayList<>();

  /**
   * Getter.
   * 
   * @return Fetches the result of the query.
   */
  public List<GeoMarker> getResult() {
    if(hasContent) return markers;
    System.out.println("executing: " + query);
    try (Connection conn = Database.getInstance().getConnection()) {
      final List<PGgeometry> geom = new ArrayList<>();
      final List<String> ids = new ArrayList<>();
      final List<String> infos = new ArrayList<>();
      final List<T> flavour = new ArrayList<>();
      final long start = System.currentTimeMillis();
      try (Statement s = conn.createStatement(); ResultSet r = s.executeQuery(query)) {
        while(r.next()) {
          ids.add(r.getString(table.idColumnName));
          geom.add((PGgeometry) r.getObject(table.geomColumnName));
          infos.add(r.getString(table.infoColumnName));
          flavour.add(getFlavour(r));
        }
      }
      System.out.println("query took: " + (System.currentTimeMillis() - start) + "ms");
      for(int i = 0; i < geom.size(); ++i) {
        final String info = infos.get(i);
        final GeoMarker m = GeometryConverter.convert(
            new ElementId(this, ids.get(i)), geom.get(i),
            info == null ? "" + ids.get(i) : info);
        addFlavour(m, flavour.get(i));
        markers.add(m);
        map.put(m.getId(), m);
      }
      hasContent = true;
    } catch(final SQLException e) {
      throw new IllegalStateException(e);
    }
    return markers;
  }

  /**
   * Retrieves the flavour of the query.
   * 
   * @param r The current row.
   * @return The flavour for the row.
   * @throws SQLException If an exception occurs.
   */
  @SuppressWarnings("unused")
  protected T getFlavour(final ResultSet r) throws SQLException {
    return null;
  }

  /**
   * Adds flavour to the geo marker.
   * 
   * @param m The marker.
   * @param f The flavour.
   */
  protected void addFlavour(@SuppressWarnings("unused") final GeoMarker m,
      @SuppressWarnings("unused") final T f) {
    // nothing here
  }

  /** Clears the query cache. */
  public void clearCache() {
    hasContent = false;
    markers.clear();
    map.clear();
  }

  /**
   * Getter.
   * 
   * @param id The id.
   * @return The marker for the given id or <code>null</code> if the id does not
   *         occur in the result of the query.
   */
  public GeoMarker get(final ElementId id) {
    return map.get(id);
  }

}
