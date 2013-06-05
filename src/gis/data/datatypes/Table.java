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
  BERLIN_ADMINISTRATIVE("berlin_administrative", GeometryType.POLYGON,
      "lor", convert(Color.RED), "gid", "geom"),
  // BERLIN_HIGHWAY("berlin_highway", GeometryType.LINESTRING,
  // "name", convert(Color.GRAY), "gid", "geom"),
  BERLIN_LOCATION("berlin_location", GeometryType.POINT,
      "name", convert(Color.PINK), "gid", "geom"),
  BERLIN_NATURAL("berlin_natural", GeometryType.POLYGON,
      "name", convert(Color.GREEN), "gid", "geom"),
  BERLIN_POI("berlin_poi", GeometryType.POINT,
      "name", convert(Color.MAGENTA), "gid", "geom"),
  BERLIN_WATER("berlin_water", GeometryType.POLYGON,
      "name", convert(Color.BLUE), "gid", "geom"),
  BUILDINGS("buildings", GeometryType.POLYGON,
      "name", convert(Color.ORANGE), "gid", "geom"),
  LANDUSE("landuse", GeometryType.POLYGON,
      "name", convert(Color.YELLOW), "gid", "geom"),
  FLICKR("flickr", GeometryType.POINT,
      "phototitle", convert(Color.ORANGE), "photoid", "poly_geom"),

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

  private Table(final String name, final GeometryType geometryType,
      final String infoColumnName, final Color color, final String id, final String geom) {
    this.name = name;
    this.geometryType = geometryType;
    this.infoColumnName = infoColumnName;
    this.color = color;
    idColumnName = id;
    geomColumnName = geom;
  }

  private static final Map<Table, Integer> mapping = new HashMap<Table, Integer>();
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

  private static Color convert(final Color base) {
    final float[] hsb = Color.RGBtoHSB(
        base.getRed(), base.getGreen(), base.getBlue(), null);
    return Color.getHSBColor(hsb[0], hsb[1] * .8f, hsb[2] * .8f);
  }

}
