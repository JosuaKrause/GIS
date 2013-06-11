package gis.data.db;

import gis.data.GeometryConverter;
import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
public abstract class Query {
  /** The actual SQL query. */
  private final String query;
  /** The table. */
  private final Table table;
  /** The name of the query. */
  private final String name;
  /** The value column if any. */
  private final String valueCol;

  /**
   * Creates a query.
   * 
   * @param query The SQL query. The column names used are taken from the
   *          {@link Table} definition.
   * @param table The table whose elements are returned. (At least for which the
   *          ids count)
   * @param name The name of the query.
   * @param valueCol The column of the extra value. May be <code>null</code>.
   */
  public Query(final String query, final Table table, final String name,
      final String valueCol) {
    this.name = Objects.requireNonNull(name);
    this.table = Objects.requireNonNull(table);
    this.query = Objects.requireNonNull(query);
    this.valueCol = valueCol;
  }

  public String uniqueHash() {
    try {
      final MessageDigest crypt = MessageDigest.getInstance("SHA-1");
      crypt.reset();
      crypt.update(query.getBytes("UTF-8"));
      return byteArrayToHexString(crypt.digest());
    } catch(UnsupportedEncodingException | NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  private static String byteArrayToHexString(final byte[] b) {
    final StringBuilder res = new StringBuilder();
    for(int i = 0; i < b.length; i++) {
      res.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
    }
    return res.toString();
  }

  /**
   * Getter.
   * 
   * @return The table.
   */
  public Table getTable() {
    return table;
  }

  /**
   * Getter.
   * 
   * @return The name.
   */
  public String getName() {
    return name;
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
  public synchronized List<GeoMarker> getResult() {
    if(!hasContent) {
      final long start = System.currentTimeMillis();
      final File cacheFile = Database.getQueryCacheFileFor(this);
      if(cacheFile.exists()) {
        loadFromCache(cacheFile);
      } else {
        fetchDatabase();
        writeToCache(cacheFile);
      }
      System.out.println("query took: " + (System.currentTimeMillis() - start) + "ms");
    }
    if(hasContent) {
      finishLoading(markers);
    }
    return markers;
  }

  private static final int MAGIC_NUMBER = 0xF00BAA0;

  private synchronized void loadFromCache(final File cacheFile) {
    if(hasContent) return;
    System.out.println("using cache: " + query);
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(cacheFile))) {
      markers.clear();
      if(in.readInt() != MAGIC_NUMBER) throw new IOException("incorrect magic number");
      final int size = in.readInt();
      for(int i = 0; i < size; ++i) {
        final GeoMarker m = (GeoMarker) in.readObject();
        markers.add(m);
        m.getId().setQuery(this);
      }
      hasContent = true;
    } catch(final IOException | ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  private synchronized void writeToCache(final File cacheFile) {
    if(!hasContent) return;
    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
      out.writeInt(MAGIC_NUMBER);
      out.writeInt(markers.size());
      for(final GeoMarker m : markers) {
        out.writeObject(m);
      }
    } catch(final IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private synchronized void fetchDatabase() {
    if(hasContent) return;
    System.out.println("executing: " + query);
    try (Connection conn = Database.getInstance().getConnection()) {
      markers.clear();
      final List<PGgeometry> geom = new ArrayList<>();
      final List<String> ids = new ArrayList<>();
      final List<String> infos = new ArrayList<>();
      final List<Double> flavour = new ArrayList<>();
      try (Statement s = conn.createStatement(); ResultSet r = s.executeQuery(query)) {
        while(r.next()) {
          ids.add(r.getString(table.idColumnName));
          geom.add((PGgeometry) r.getObject(table.geomColumnName));
          infos.add(r.getString(table.infoColumnName));
          if(valueCol != null) {
            final Double d = r.getDouble(valueCol);
            flavour.add(d != null ? d : 0.0);
          }
        }
      }
      for(int i = 0; i < geom.size(); ++i) {
        final String info = infos.get(i);
        final GeoMarker m = GeometryConverter.convert(
            new ElementId(this, ids.get(i)), geom.get(i),
            info == null ? "" + ids.get(i) : info);
        if(valueCol != null) {
          m.setQueryValue(flavour.get(i));
        }
        markers.add(m);
      }
      hasContent = true;
    } catch(final SQLException e) {
      throw new IllegalStateException(e);
    }
  }

  protected abstract void finishLoading(final List<GeoMarker> ms);

  /** Clears the query cache. */
  public void clearCache() {
    hasContent = false;
    markers.clear();
  }

}
