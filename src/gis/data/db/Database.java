package gis.data.db;

import gis.data.NineCut;
import gis.data.datatypes.ElementId;
import gis.data.datatypes.Table;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.GISConfiguration;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

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
      final Coordinate c, final List<Query<?>> queries, final double maxDistMeters) {
    final List<ElementId> ids = new ArrayList<>();
    for(final Query<?> q : queries) {
      final Table t = q.getTable();
      switch(t.geometryType) {
        case POINT:
          getPointsByCoordinate(c, q, maxDistMeters, ids);
          break;
        case POLYGON:
          getPolygonsByCoordinate(c, q, ids);
          break;
        default:
          throw new IllegalStateException();
      }
    }
    return ids;
  }

  private void getPointsByCoordinate(final Coordinate c, final Query<?> q,
      final double maxDistMeters, final List<ElementId> ids) {
    final Table table = q.getTable();
    final String query = "SELECT " + table.idColumnName + " as gid FROM " + table.name +
        " WHERE ST_DWithin(" + table.geomColumnName + ", ST_SetSRID(ST_Point(" +
        c.getLon() + "," + c.getLat() + "), 4326), " + maxDistMeters + ", true)";
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while(rs.next()) {
        final String gid = rs.getString("gid");
        final ElementId id = new ElementId(q, gid);
        ids.add(id);
      }
    } catch(final SQLException e) {
      e.printStackTrace();
    }
  }

  private void getPolygonsByCoordinate(
      final Coordinate c, final Query<?> q, final List<ElementId> ids) {
    final Table table = q.getTable();
    final String query = "SELECT " + table.idColumnName + " as gid FROM " + table.name +
        " WHERE ST_Contains(" + table.geomColumnName + ", ST_SetSRID(ST_Point(" +
        c.getLon() + ", " + c.getLat() + "), 4326))";
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      while(rs.next()) {
        final String gid = rs.getString("gid");
        final ElementId id = new ElementId(q, gid);
        ids.add(id);
      }
    } catch(final SQLException e) {
      e.printStackTrace();
    }
  }

  public double getDistance(final ElementId from, final ElementId to) {
    final Table f = from.getQuery().getTable();
    final Table t = to.getQuery().getTable();
    double distance = Double.NaN;
    final String fromId = from.getId();
    final String fromIdCol = f.idColumnName;
    final String fromTable = f.name;
    final String fromGeom = f.geomColumnName;
    final String toId = to.getId();
    final String toIdCol = t.idColumnName;
    final String toTable = t.name;
    final String toGeom = t.geomColumnName;
    final String query = "SELECT ST_DISTANCE( " +
        "f." + fromGeom + ", t." + toGeom + " , true) as distance " +
        "FROM " + fromTable + " as f, " + toTable + " as t " +
        "WHERE f." + fromIdCol + " = " + fromId + " AND t." + toIdCol + " = " + toId;
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      if(!rs.next()) throw new IllegalStateException("expected more results");
      distance = rs.getDouble("distance");
      if(rs.next()) throw new IllegalStateException("too many result rows");
    } catch(final SQLException | IllegalStateException e) {
      e.printStackTrace();
    }
    return distance;
  }

  public Image getImage(final ElementId id) {
    final Table t = id.getQuery().getTable();
    final String query = "select photoUrl from " + t.name + " where " + t.idColumnName
        + " = '" + id.getId() + "'";
    String imgUrl;
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      if(!rs.next()) throw new IllegalStateException("expected more results");
      final String url = rs.getString("photoUrl");
      if(url == null || url.startsWith("img:")) return null;
      imgUrl = url.substring(4);
    } catch(final Exception e) {
      e.printStackTrace();
      return null;
    }
    if(imgUrl.endsWith("/")) {
      try (InputStreamReader in = new InputStreamReader(new URL(imgUrl).openStream());
          BufferedReader br = new BufferedReader(in)) {
        final Pattern p = Pattern
            .compile(".*<link rel=\"image_src\" href=\"(http:.*)\" id=\"image-src\">.*");
        String line;
        while((line = br.readLine()) != null) {
          final Matcher m = p.matcher(line);
          if(m.find()) {
            imgUrl = m.group(1);
            break;
          }
        }
      } catch(final Exception e) {
        e.printStackTrace();
        return null;
      }
    }
    System.out.print("trying to load: " + imgUrl);
    final BufferedImage img;
    try {
      img = ImageIO.read(new URL(imgUrl));
    } catch(final Exception e) {
      e.printStackTrace();
      return null;
    }
    System.out.println(" finished");
    return img.getScaledInstance(100, -1, Image.SCALE_SMOOTH);
  }

  public NineCut getNineCutDescription(final ElementId id1, final ElementId id2) {
    NineCut nc = null;
    final String query = "select " +
        "st_disjoint(a.geom, b.geom) as disjoint, " +
        "st_overlaps(a.geom, b.geom) as overlaps, " +
        "st_contains(a.geom, b.geom) as contains, " +
        "st_within(a.geom, b.geom) as inside, " +
        "st_touches(a.geom, b.geom) as meet, " +
        "st_covers(a.geom, b.geom) as covers, " +
        "st_covers(b.geom, a.geom) as covered_by, " +
        "st_equals(a.geom, b.geom) as equal " +
        "from " + id1.getQuery().getTable().name + " as a, " +
        id2.getQuery().getTable().name + " as b where a.gid = " +
        id1.getId() + " and b.gid = " + id2.getId();
    try (Connection connection = getConnection();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query)) {
      if(rs.next()) {
        nc = NineCut.get(rs.getBoolean("disjoint"), rs.getBoolean("meet"),
            rs.getBoolean("covers"), rs.getBoolean("overlaps"),
            rs.getBoolean("contains"),
            rs.getBoolean("equal"), rs.getBoolean("covered_by"), rs.getBoolean("inside"));
      }

    } catch(final SQLException e) {
      e.printStackTrace();
    }
    return nc;
  }
}
