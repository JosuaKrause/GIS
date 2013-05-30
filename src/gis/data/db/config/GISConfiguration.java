package gis.data.db.config;


/**
 * Provides the configuration for the GIS application.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 */
public interface GISConfiguration {

  /**
   * Getter.
   * 
   * @return The database URL.
   */
  String getUrl();

  /**
   * Getter.
   * 
   * @return The database user.
   */
  String getUser();

  /**
   * Getter.
   * 
   * @return The password.
   */
  String getPassword();

}
