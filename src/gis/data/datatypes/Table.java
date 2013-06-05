package gis.data.datatypes;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all tables in the database.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public enum Table {
  /** Administrative regions. */
  BERLIN_ADMINISTRATIVE("berlin_administrative", GeometryType.POLYGON,
      "lor", new Color(228, 26, 28), "gid", "geom"),
  /** Locations in berlin. */
  BERLIN_LOCATION("berlin_location", GeometryType.POINT,
      "name", new Color(247, 129, 191), "gid", "geom"),
  /** Natural locations in berlin. */
  BERLIN_NATURAL("berlin_natural", GeometryType.POLYGON,
      "name", new Color(77, 175, 74), "gid", "geom"),
  /** Points-of-interest ini berlin. */
  BERLIN_POI("berlin_poi", GeometryType.POINT,
      "name", new Color(166, 86, 40), "gid", "geom"),
  /** Water in berlin. */
  BERLIN_WATER("berlin_water", GeometryType.POLYGON,
      "name", new Color(55, 126, 184), "gid", "geom"),
  /** Buildings. */
  BUILDINGS("buildings", GeometryType.POLYGON,
      "name", new Color(152, 78, 163), "gid", "geom"),
  /** Land use. */
  LANDUSE("landuse", GeometryType.POLYGON,
      "name", new Color(77, 175, 74), "gid", "geom"),
  /** Flickr data. */
  FLICKR("flickr", GeometryType.POINT,
      "phototitle", new Color(255, 255, 51), "photoid", "poly_geom"),
  PARK("park", GeometryType.POINT, "name", new Color(12, 203, 2), "gid",
      "geom"),

  ; // EOD
  /** The table name. */
  public final String name;
  /** The geometry type. */
  public final GeometryType geometryType;
  /** The column for the info. */
  public final String infoColumnName;
  /** The default color. */
  public final Color color;
  /** The id column. */
  public final String idColumnName;
  /** The geom column. */
  public final String geomColumnName;

  /**
   * Creates a table.
   * 
   * @param name The name of the table.
   * @param geometryType The geometry type.
   * @param infoColumnName The info col.
   * @param color The default color.
   * @param id The id col.
   * @param geom The geom col.
   */
  private Table(final String name, final GeometryType geometryType,
      final String infoColumnName, final Color color, final String id, final String geom) {
    this.name = name;
    this.geometryType = geometryType;
    this.infoColumnName = infoColumnName;
    this.color = color;
    idColumnName = id;
    geomColumnName = geom;
  }

  /** The reverse lookup. */
  private static final Map<Table, Integer> mapping = new HashMap<>();

  static {
    final Table[] values = values();
    for(int i = 0; i < values.length; ++i) {
      mapping.put(values[i], Integer.valueOf(i));
    }
  }

  /**
   * Return the index of <i>table</i> in {@link Table#values()}.
   * 
   * @param table a Table object
   * @return The index of the table.
   */
  public static int indexOf(final Table table) {
    return mapping.get(table);
  }

}
