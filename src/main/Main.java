package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

/**
 * Database test.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class Main {

  /**
   * Starts the test application.
   * 
   * @param args No args.
   */
  public static void main(final String[] args) {
    Connection conn = null;
    try {
      // Class.forName("org.postgresql.Driver");
      final String url = "jdbc:postgresql://134.34.225.25:5432/GIS";
      conn = DriverManager.getConnection(url, "postgres", "admin");
      ((PGConnection) conn).addDataType("geometry",
          org.postgis.PGgeometry.class);
      final Statement s = conn.createStatement();
      final ResultSet r = s
          .executeQuery("select id, color, poly_geom from polytest");
      while(r.next()) {
        final PGgeometry geom = (PGgeometry) r.getObject(3);
        System.out.println(geom.toString());
      }
      s.close();
    } catch(final Exception e) {
      e.printStackTrace();
    } finally {
      if(conn != null) {
        try {
          conn.close();
        } catch(final Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

}
