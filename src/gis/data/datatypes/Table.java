package gis.data.datatypes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all tables in the database.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public enum Table {
	BERLIN_ADMINISTRATIVE("berlin_administrative", GeometryType.POLYGON, "lor"),
	BERLIN_HIGHWAY("berlin_highway", GeometryType.LINESTRING, "name"),
	BERLIN_LOCATION("berlin_location", GeometryType.POINT, "name"),
	BERLIN_NATURAL("berlin_natural", GeometryType.POLYGON, "name"),
	BERLIN_POI("berlin_poi", GeometryType.POINT, "name"),
	BERLIN_WATER("berlin_water", GeometryType.POLYGON, "name"),
	BUILDINGS("buildings", GeometryType.POLYGON, "name"),
	LANDUSE("landuse", GeometryType.POLYGON, "name"),

	; // EOL
	
  public final String name;
	public final GeometryType geometryType;
	public final String infoColumnName;
	
	private Table(final String name, final GeometryType geometryType, final String infoColumnName) {
		this.name = name;
		this.geometryType = geometryType;
		this.infoColumnName = infoColumnName;
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

}
