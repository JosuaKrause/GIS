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
  BERLIN_ADMINISTRATIVE("berlin_administrative", GeometryType.POLYGON, "lor",
      convert(Color.RED)),
  BERLIN_HIGHWAY("berlin_highway", GeometryType.LINESTRING, "name", convert(Color.GRAY)),
  BERLIN_LOCATION("berlin_location", GeometryType.POINT, "name", convert(Color.PINK)),
  BERLIN_NATURAL("berlin_natural", GeometryType.POLYGON, "name", convert(Color.GREEN)),
  BERLIN_POI("berlin_poi", GeometryType.POINT, "name", convert(Color.MAGENTA)),
  BERLIN_WATER("berlin_water", GeometryType.POLYGON, "name", convert(Color.BLUE)),
  BUILDINGS("buildings", GeometryType.POLYGON, "name", convert(Color.ORANGE)),
  LANDUSE("landuse", GeometryType.POLYGON, "name", convert(Color.YELLOW)),
  PARK("park", GeometryType.POLYGON, "name", convert(new Color(12, 203, 2)));
  ; // EOD

  public final String name;
  public final GeometryType geometryType;
  public final String infoColumnName;
  public final Color color;

  private Table(final String name, final GeometryType geometryType,
      final String infoColumnName, final Color color) {
    this.name = name;
    this.geometryType = geometryType;
    this.infoColumnName = infoColumnName;
    this.color = color;
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
