package gis.data.datatypes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public enum Table {
	BERLIN_ADMINISTRATIVE("berlin_administrative"),
	BERLIN_HIGHWAY("berlin_highway"),
	BERLIN_LOCATION("berlin_location"),
	BERLIN_NATURAL("berlin_natural"),
	BERLIN_POI("berlin_poi"),
	BERLIN_WATER("berlin_water"),
	BUILDINGS("buildings"),
	LANDUSE("landuse");
	
	public final String name;
	
	private Table(String name) {
		this.name = name;
	}
	
	private static final Map<Table, Integer> mapping = new HashMap<Table, Integer>();
	static {
		Table[] values = values();
		for (int i = 0; i < values.length; ++i) {
			mapping.put(values[i], Integer.valueOf(i));
		}
	}
	
	private static Comparator<Table> comparator;
	static {
		comparator = new Comparator<Table>() {
			
			@Override
			public int compare(Table t1, Table t2) {
				//return t1.name.compareTo(t2.name);
				int i1 = indexOf(t1);
				int i2 = indexOf(t2);
				if (i1 < i2) {
					return -1;
				}
				if (i1 > i2) {
					return 1;
				}
				return 0;
			}
			
		};
	}
	
	public static Comparator<Table> getComparator() {
		return comparator;
	}
	
	/**
	 * Return the index of <i>table</i> in {@link Table#values()}.
	 * @param table a Table object
	 */
	public static int indexOf(Table table) {
		return (int)mapping.get(table);
	}
}
