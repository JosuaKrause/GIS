package gis.util;


import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

public class GeoMarkerList extends SortedList<GeoMarker> {

	public GeoMarkerList() {
		super(GeoMarker.getComparator());
	}
	
	public GeoMarker get(ElementId id) {
		GeoMarker m = new GeoMarker(id) { };
		int index = insertionIndexOf(m);
		m = (GeoMarker)elements[index];
		if (index < size && id.equals(m.id)) {
			return m;
		}
		return null;
	}
	
	public void removeAll(Table table) {
		ElementId id = new ElementId(table, 0);
		GeoMarker m = new GeoMarker(id) { };
		int startIndex = insertionIndexOf(m);
		if (startIndex < size) {
			int endIndex;
			//find last element with same table type
			int tableIndex = Table.indexOf(table);
			Table[] tables = Table.values();
			if (tableIndex == tables.length - 1) {
				endIndex = size - 1;
			} else {
				Table nextTable = tables[tableIndex + 1];
				id = new ElementId(nextTable, 0);
				m = new GeoMarker(id) { };
				endIndex = insertionIndexOf(m) - 1;
			}
			if (endIndex >= startIndex) {
				int numElements = endIndex - startIndex + 1;
				shiftAllLeft(endIndex + 1, numElements);
				size -= numElements;
			}
		}
	}
	
}
