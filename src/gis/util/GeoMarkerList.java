package gis.util;

import java.awt.Graphics;
import java.awt.Point;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

public class GeoMarkerList extends SortedList<GeoMarker> {

	public GeoMarkerList() {
		super(GeoMarker.getComparator());
	}
	
	public GeoMarker get(ElementId id) {
		GeoMarker m = new DummyGeoMarker(id);
		int index = insertionIndexOf(m);
		if (index < size) {
			return (GeoMarker)elements[index];
		}
		return null;
	}
	
	public void removeAll(Table table) {
		ElementId id = new ElementId(table, 0);
		GeoMarker m = new DummyGeoMarker(id);
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
				m = new DummyGeoMarker(id);
				endIndex = insertionIndexOf(m) - 1;
			}
			if (endIndex >= startIndex) {
				int numElements = endIndex - startIndex + 1;
				shiftAllLeft(endIndex + 1, numElements);
				size -= numElements;
			}
		}
	}
	
	public static void main(String[] args) {
		GeoMarkerList list = new GeoMarkerList();
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_ADMINISTRATIVE, 0)));
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_ADMINISTRATIVE, 1)));
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_ADMINISTRATIVE, 3)));
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_HIGHWAY, 4)));
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_HIGHWAY, 0)));
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_POI, 8)));
		list.add(new DummyGeoMarker(new ElementId(Table.BERLIN_ADMINISTRATIVE, 9)));
		System.out.println(list);
		
		list.removeAll(Table.BERLIN_ADMINISTRATIVE);
		System.out.println(list);
	}
	
	private static class DummyGeoMarker extends GeoMarker {

		public DummyGeoMarker(ElementId id) {
			super(id);
		}

		@Override
		public void paint(Graphics g, Point p) {
			//do nothing
		}
		
	}
}
