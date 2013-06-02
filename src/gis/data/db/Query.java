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
import java.util.List;
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

  /**
   * Creates a query.
   * 
   * @param query The SQL query. The column named 'gid' of the result must be
   *          the id of the element in the table given here. The column 'geom'
   *          must be the geometric reference.
   * @param table The table whose elements are returned. (At least for which the
   *          ids count)
   */
  public Query(final String query, final Table table) {
    this.table = Objects.requireNonNull(table);
    this.query = Objects.requireNonNull(query);
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
      final List<Integer> ids = new ArrayList<>();
      final List<T> flavour = new ArrayList<>();
      try (Statement s = conn.createStatement(); ResultSet r = s.executeQuery(query)) {
        while(r.next()) {
          ids.add(r.getInt("gid"));
          geom.add((PGgeometry) r.getObject("geom"));
          flavour.add(getFlavour(r));
        }
      }
      for(int i = 0; i < geom.size(); ++i) {
        final GeoMarker m = GeometryConverter.convert(
            new ElementId(table, ids.get(i)), geom.get(i));
        addFlavour(m, flavour.get(i));
        markers.add(m);
        if(i % 50 == 0) {
          System.out.println("fetching: " + ((i + 1.0) / geom.size() * 100.0) + "%");
        }
      }
      hasContent = true;
    } catch(final SQLException e) {
      throw new IllegalStateException(e);
    }
    return markers;
  }

  protected T getFlavour(final ResultSet r) throws SQLException {
    return null;
  }

  protected void addFlavour(final GeoMarker m, final T f) {
    // nothing here
  }

  /** Clears the query cache. */
  public void clearCache() {
    hasContent = false;
    markers.clear();
  }

}
