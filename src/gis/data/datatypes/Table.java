package gis.data.datatypes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public enum Table {
	BERLIN_ADMINISTRATIVE("berlin_administrative", GeometryType.POLYGON, "lor"),
	BERLIN_HIGHWAY("berlin_highway", GeometryType.LINESTRING, "name"),
	BERLIN_LOCATION("berlin_location", GeometryType.POINT, "name"),
	BERLIN_NATURAL("berlin_natural", GeometryType.POLYGON, "name"),
	BERLIN_POI("berlin_poi", GeometryType.POINT, "name"),
	BERLIN_WATER("berlin_water", GeometryType.POLYGON, "name"),
	BUILDINGS("buildings", GeometryType.POLYGON, "name"),
	LANDUSE("landuse", GeometryType.POLYGON, "name");
	
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
