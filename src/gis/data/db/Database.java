package gis.data.db;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.Table;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.GISConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.postgresql.PGConnection;

/**
 * Handles database access.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class Database {

  static {
    // create postgresql driver
    try {
      Class.forName("org.postgresql.Driver");
    } catch(final ClassNotFoundException e) {
      throw new AssertionError("missing library", e);
    }
  }

  /** The database instance. */
  private static final Database INSTANCE = new Database();

  /**
   * Getter.
   * 
   * @return The database instance.
   */
  public static final Database getInstance() {
    return INSTANCE;
  }

  /** The configuration of the database. */
  private GISConfiguration config;

  /** Creates the database instance. */
  private Database() {
    try {
      config = new FileConfiguration(new FileInputStream(new File("config.txt")));
    } catch(final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Creates an SQL connection.
   * 
   * @return The connection.
   * @throws SQLException SQL Exception.
   */
  public Connection getConnection() throws SQLException {
    final Connection connection = DriverManager.getConnection(
        config.getUrl(), config.getUser(), config.getPassword());
    // add support for Geometry types
    ((PGConnection) connection).addDataType("geometry", org.postgis.PGgeometry.class);
    return connection;
  }

  public List<ElementId> getByCoordinate(
      final Coordinate c, final List<Table> tables, final double maxDistMeters) {
    final List<ElementId> ids = new ArrayList<>();
    for(final Table t : tables) {
      switch(t.geometryType) {
        case POINT:
          getPointsByCoordinate(c, t, maxDistMeters, ids);
          break;
        case POLYGON:
          getPolygonsByCoordinate(c, t, ids);
          break;
        default:
          throw new IllegalStateException();
      }
    }
    return ids;
  }

  private void getPointsByCoordinate(final Coordinate c, final Table table,
      final double maxDistMeters, final List<ElementId> ids) {
    final String query = "SELECT gid FROM " + table.name +
        " WHERE ST_DWithin(geom, ST_SetSRID(ST_Point(" +
        c.getLon() + "," + c.getLat() + "), 4326), " + maxDistMeters + ", true)";
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while(rs.next()) {
        final int gid = rs.getInt(1);
        final ElementId id = new ElementId(table, gid);
        ids.add(id);
      }
    } catch(final SQLException e) {
      e.printStackTrace();
    }
  }

  private void getPolygonsByCoordinate(
      final Coordinate c, final Table table, final List<ElementId> ids) {
    final String query = "SELECT gid FROM " + table.name +
        " WHERE ST_Contains(geom, ST_SetSRID(ST_Point(" +
        c.getLon() + ", " + c.getLat() + "), 4326))";
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while(rs.next()) {
        final int gid = rs.getInt(1);
        final ElementId id = new ElementId(table, gid);
        ids.add(id);
      }
    } catch(final SQLException e) {
      e.printStackTrace();
    }
  }
}
