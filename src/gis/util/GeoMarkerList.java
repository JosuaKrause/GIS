package gis.util;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

public class GeoMarkerList extends SortedList<GeoMarker> {

  public GeoMarkerList() {
    super(GeoMarker.getComparator());
  }

  public GeoMarker get(final ElementId id) {
    final GeoMarker m = new GeoMarker(id);
    final int index = insertionIndexOf(m);
    if(index < size) return (GeoMarker) elements[index];
    return null;
  }

  public void removeAll(final Table table) {
    ElementId id = new ElementId(table, 0);
    GeoMarker m = new GeoMarker(id);
    final int startIndex = insertionIndexOf(m);
    if(startIndex < size) {
      int endIndex;
      // find last element with same table type
      final int tableIndex = Table.indexOf(table);
      final Table[] tables = Table.values();
      if(tableIndex == tables.length - 1) {
        endIndex = size - 1;
      } else {
        final Table nextTable = tables[tableIndex + 1];
        id = new ElementId(nextTable, 0);
        m = new GeoMarker(id);
        endIndex = insertionIndexOf(m) - 1;
      }
      if(endIndex >= startIndex) {
        final int numElements = endIndex - startIndex + 1;
        shiftAllLeft(endIndex + 1, numElements);
        size -= numElements;
      }
    }
  }

}
