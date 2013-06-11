package gis.data.db;

import gis.data.NineCut;
import gis.data.datatypes.ElementId;
import gis.data.datatypes.Table;
import gis.data.db.config.FileConfiguration;
import gis.data.db.config.GISConfiguration;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.postgresql.PGConnection;

/**
 * Handles database access.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public final class Database {

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

  public static void ensureFile(final File file) {
    if(file.exists()) return;
    if(file.mkdirs()) return;
    ensureFile(file.getParentFile());
    if(file.isDirectory()) {
      file.mkdir();
    }
  }

  public static File getImageFileFor(final ElementId id) {
    final File file = new File(
        "cache/img/" + id.getQuery().getTable().name + "/" + id.getId() + ".png");
    ensureFile(file.getParentFile());
    return file;
  }

  public static File getQueryCacheFileFor(final Query query) {
    final File file = new File(
        "cache/query/" + query.uniqueHash() + ".res");
    ensureFile(file.getParentFile());
    return file;
  }

  private final ConcurrentHashMap<ElementId, Object> loadBlock = new ConcurrentHashMap<>();

  public Image getImage(final ElementId id) {
    final File cache = getImageFileFor(id);
    final Object own = new Object();
    Object lock = loadBlock.putIfAbsent(id, own);
    if(lock == null) {
      lock = own;
    }
    synchronized(lock) {
      if(!cache.exists()) {
        final RenderedImage img = createImage(id);
        try {
          if(img == null) {
            cache.createNewFile();
          } else {
            ImageIO.write(img, "PNG", cache);
          }
        } catch(final IOException e) {
          e.printStackTrace();
          loadBlock.remove(id, lock);
          return null;
        }
      }
    }
    loadBlock.remove(id, lock);
    if(cache.length() == 0) return null;
    try {
      return ImageIO.read(cache);
    } catch(final IOException e) {
      return null;
    }
  }

  private RenderedImage createImage(final ElementId id) {
    final Table t = id.getQuery().getTable();
    final String query = "select photoUrl from " + t.name + " where "
        + t.idColumnName + " = '" + id.getId() + "'";
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
    final Image image = img.getScaledInstance(100, -1, Image.SCALE_SMOOTH);
    final BufferedImage buff = new BufferedImage(image.getWidth(null),
        image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    final Graphics2D gfx = buff.createGraphics();
    gfx.drawImage(image, 0, 0, null);
    gfx.dispose();
    image.flush();
    img.flush();
    return buff;
  }

  public NineCut getNineCutDescription(final ElementId id1, final ElementId id2) {
    final Table a = id1.getQuery().getTable();
    final String ag = a.geomColumnName;
    final String ai = a.idColumnName;
    final Table b = id2.getQuery().getTable();
    final String bg = b.geomColumnName;
    final String bi = b.idColumnName;
    NineCut nc = null;
    final String query = "select " +
        "st_disjoint(a." + ag + ", b." + bg + ") as disjoint, " +
        "st_overlaps(a." + ag + ", b." + bg + ") as overlaps, " +
        "st_contains(a." + ag + ", b." + bg + ") as contains, " +
        "st_within(a." + ag + ", b." + bg + ") as inside, " +
        "st_touches(a." + ag + ", b." + bg + ") as meet, " +
        "st_covers(a." + ag + ", b." + bg + ") as covers, " +
        "st_covers(b." + bg + ", a." + ag + ") as covered_by, " +
        "st_equals(a." + ag + ", b." + bg + ") as equal " +
        "from " + a.name + " as a, " +
        b.name + " as b where a." + ai + " = " +
        id1.getId() + " and b." + bi + " = " + id2.getId();
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
