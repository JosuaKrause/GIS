package gis.data.datatypes;

import java.util.Comparator;

public abstract class GeoMarker {
	
	public final ElementId id;
	public boolean selected;
	
	public GeoMarker(ElementId id) {
		this.id = id;
		selected = false;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof GeoMarker) {
			GeoMarker m = (GeoMarker)o;
			return id.equals(m.id);
		}
		return false;
	}
	
	
	private static Comparator<GeoMarker> comparator;
	static {
		comparator = new Comparator<GeoMarker>() {
			
			private final Comparator<ElementId> comparator = ElementId.getComparator();
			
			@Override
			public int compare(GeoMarker g1, GeoMarker g2) {
				return comparator.compare(g1.id, g2.id);
			}
			
		};
	}
	
	public static Comparator<GeoMarker> getComparator() {
		return comparator;
	}
	
	@Override
	public String toString() {
		return id.toString();
	}
}
