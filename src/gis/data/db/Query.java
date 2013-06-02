package gis.data.db;

import gis.data.GeometryConverter;
import gis.data.datatypes.GeoMarker;

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
 */
public class Query {

  /** The actual SQL query. */
  private final String query;

  /**
   * Creates a query.
   * 
   * @param query The SQL query.
   */
  public Query(final String query) {
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
      try (Statement s = conn.createStatement(); ResultSet r = s.executeQuery(query)) {
        while(r.next()) {
          geom.add((PGgeometry) r.getObject(2));
        }
      }
      for(int i = 0; i < geom.size(); ++i) {
        markers.add(GeometryConverter.convert(geom.get(i)));
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

  /** Clears the query cache. */
  public void clearCache() {
    hasContent = false;
    markers.clear();
  }

}
