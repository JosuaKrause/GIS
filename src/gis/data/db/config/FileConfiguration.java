package gis.data.db.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Loads the configuration located in an input stream.
 * 
 * @author Joschi <josua.krause@gmail.com>
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 */
public class FileConfiguration implements GISConfiguration {
  /** The internal properties. */
  private final Properties props;

  /**
   * Loads the configuration from the given input stream.
   * 
   * @param in The input stream.
   * @throws IOException I/O Exception.
   */
  public FileConfiguration(final InputStream in) throws IOException {
    props = new Properties();
    props.load(in);
    // make sure that all required keys are present
    final ConfigKey[] keys = ConfigKey.values();
    for(final ConfigKey key : keys) {
      Objects.requireNonNull(props.get(key.getKey()),
          "key " + key.getKey() + " missing in configuration file");
    }
  }

  @Override
  public String getUrl() {
    return "jdbc:postgresql://" + props.get(ConfigKey.HOSTNAME.getKey()) + ":" +
        props.get(ConfigKey.PORT.getKey()) + "/" + props.get(ConfigKey.DBNAME.getKey());
  }

  @Override
  public String getUser() {
    return "" + props.get(ConfigKey.USER.getKey());
  }

  @Override
  public String getPassword() {
    return "" + props.get(ConfigKey.PASSWORD.getKey());
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "[url=" + getUrl() + "; user=" + getUser() + "; password=" + getPassword()
        + "]";
  }

  /**
   * All valid config fields.
   * 
   * @author Joschi <josua.krause@gmail.com>
   * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
   */
  private static enum ConfigKey {
    /** The hostname field. */
    HOSTNAME("hostname"),
    /** The port field. */
    PORT("port"),
    /** The database name field. */
    DBNAME("dbname"),
    /** The user field. */
    USER("user"),
    /** The password field. */
    PASSWORD("password"), ; // EOD

    /** The key for the field. */
    private final String key;

    /**
     * Creates a key for a field.
     * 
     * @param key The key.
     */
    private ConfigKey(final String key) {
      this.key = key;
    }

    /**
     * Getter.
     * 
     * @return The key for this field.
     */
    String getKey() {
      return key;
    }

  } // ConfigKey

}
