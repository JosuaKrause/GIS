package gis.data.db;

import gis.data.GeometryConverter;
import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.GISConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

/**
 * Handles database access.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class Database {

  // name, semi-major axis (equatorial radius), inverse flattening
  private static String WSG84 = "SPHEROID[\"WSG84\",6378137.0,298.257223563]";

  static {
    // create postgresql driver
    try {
      Class.forName("org.postgresql.Driver");
    } catch(final ClassNotFoundException e) {
      throw new AssertionError("missing library", e);
    }
  }

  private GISConfiguration config;

  public Database() {
    try {
      config = new FileConfiguration(new FileInputStream(new File("config.txt")));
    } catch(final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public List<GeoMarker> getGeometry(final Table table) {
    final List<GeoMarker> markers = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection(
        config.getUrl(), config.getUser(), config.getPassword())) {
      // add support for Geometry types
      ((PGConnection) conn).addDataType("geometry", org.postgis.PGgeometry.class);
      // create query statement
      final Statement s = conn.createStatement();
      // TODO
      final ResultSet r = s.executeQuery(
          "SELECT gid, geom FROM " + table.name + " LIMIT 10000");
      // iterate while there are polygons to retrieve
      final List<PGgeometry> geom = new ArrayList<>();
      final List<ElementId> ids = new ArrayList<>();
      while(r.next()) {
        ids.add(new ElementId(table, r.getInt(1)));
        geom.add((PGgeometry) r.getObject(2));
      }
      s.close();// close statement when finished
      for(int i = 0; i < geom.size(); ++i) {
        markers.add(GeometryConverter.convert(ids.get(i), geom.get(i)));
        System.out.println("loading: " + ((i + 1.0) / geom.size() * 100.0) + "%");
      }
    } catch(final Exception ex) {
      System.err.println(ex);
      ex.printStackTrace();
    }
    return markers;
  }

  // public double getDistanceToMuseums(Coordinate coordinate) {
  // Connection conn = null;
  // double distance = -1;
  // try{
  // Class.forName("org.postgresql.Driver");//create postgresql driver
  // conn = DriverManager.getConnection(config.getUrl(), config.getUser(),
  // config.getPassword());
  //
  // ((PGConnection)conn).addDataType("geometry",
  // org.postgis.PGgeometry.class);//add support for Geometry types
  // String geoText = "'POINT(" + coordinate.getLon() + " " +
  // coordinate.getLat() + ")'";
  // //System.out.println(geoText);
  // //String query =
  // "SELECT MIN(dist) FROM (SELECT ST_DISTANCE(ST_POINT(?, ?), geom) AS dist FROM berlin_poi WHERE name LIKE 'Museum%') as d;";
  // String query =
  // "SELECT MIN(dist) FROM (SELECT ST_DISTANCE_SPHEROID(ST_POINT(?, ?), geom, 'SPHEROID[\"WSG84\",6378137,298.257223563]') AS"
  // +
  // "dist FROM berlin_poi WHERE name LIKE 'Museum%') as d;";
  // PreparedStatement ps = conn.prepareStatement(query);
  // ps.setDouble(1, 50);
  // ps.setDouble(2, 5);
  //
  // ResultSet r = ps.executeQuery();
  //
  // while(r.next()) { //iterate while there are polygons to retrieve
  // distance = r.getDouble(1);
  // }
  // ps.close();//close statement when finished
  // }catch(Exception ex) {
  // System.err.println(ex);
  // ex.printStackTrace();
  // } finally {
  // if (conn != null)
  // try{
  // conn.close(); //close connection when finished
  // }catch(Exception ex) {}
  // }
  //
  // return distance;
  // }
}
