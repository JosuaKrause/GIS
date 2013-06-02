package gis.data.db;

import gis.data.db.config.FileConfiguration;
import gis.data.db.config.GISConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
    return DriverManager.getConnection(
        config.getUrl(), config.getUser(), config.getPassword());
  }

}
