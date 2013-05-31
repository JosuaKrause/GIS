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
  BERLIN_ADMINISTRATIVE("berlin_administrative"),
  BERLIN_HIGHWAY("berlin_highway"),
  BERLIN_LOCATION("berlin_location"),
  BERLIN_NATURAL("berlin_natural"),
  BERLIN_POI("berlin_poi"),
  BERLIN_WATER("berlin_water"),
  BUILDINGS("buildings"),
  LANDUSE("landuse"),

  ; // EOL

  public final String name;

  private Table(final String name) {
    this.name = name;
  }

  private static final Map<Table, Integer> mapping = new HashMap<Table, Integer>();
  static {
    final Table[] values = values();
    for(int i = 0; i < values.length; ++i) {
      mapping.put(values[i], Integer.valueOf(i));
    }
  }

  private static final Comparator<Table> COMPARATOR = new Comparator<Table>() {

    @Override
    public int compare(final Table t1, final Table t2) {
      final int i1 = indexOf(t1);
      final int i2 = indexOf(t2);
      if(i1 < i2) return -1;
      if(i1 > i2) return 1;
      return 0;
    }

  };

  public static Comparator<Table> getComparator() {
    return COMPARATOR;
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
