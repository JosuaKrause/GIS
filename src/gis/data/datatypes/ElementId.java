package gis.data.datatypes;

import java.util.Comparator;

public class ElementId {
	
	public final Table table;
	public final int gid;
	
	public ElementId(final Table table, final int gid) {
		this.table = table;
		this.gid = gid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ElementId) {
			ElementId e = (ElementId)o;
			if (table == e.table && gid == e.gid) {
				return true;
			}
		}
		return false;
	}
	
	private static Comparator<ElementId> comparator;
	static {
		comparator = new Comparator<ElementId>() {

			@Override
			public int compare(ElementId e1, ElementId e2) {
				int comp = Table.getComparator().compare(e1.table, e2.table);
				if (comp == 0) {
					if (e1.gid < e2.gid)
						return -1;
					if (e1.gid > e2.gid)
						return 1;
					return 0;
				}
				return comp;
			}
			
		};
	}
	
	public static Comparator<ElementId> getComparator() {
		return comparator;
	}
	
	@Override
	public String toString() {
		return "[" + table.toString() + " " + gid + "]"; 
	}
}
