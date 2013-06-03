package gis.data.datatypes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

/**
 * A marker for multi polygons.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GeoMarkerMultiPolygon extends GeoMarker {

    /** The style of the polygons. */
    private static final Style STYLE = new Style();

    static {
        STYLE.setColor(new Color(0, 0, 0, 255 * 2 / 5));
        STYLE.setBackColor(new Color(239, 138, 98, 255 / 3));
        STYLE.setStroke(new BasicStroke(3f));
    }

    /** The polygons. */
    private final MapPolygon[] polygons;
    /** The world coordinate bounding box. */
    private final Rectangle2D latLonBBox;

    /**
     * Creates a geo marker for the list of polygons.
     * 
     * @param id
     *            The reference id.
     * @param poly
     *            The list of polygons.
     */
    public GeoMarkerMultiPolygon(final ElementId id,
            final List<Coordinate[]> poly) {
        super(id);
        int pos = 0;
        polygons = new MapPolygon[poly.size()];
        double minLat = Double.NaN;
        double maxLat = Double.NaN;
        double minLon = Double.NaN;
        double maxLon = Double.NaN;
        for (final Coordinate[] coords : poly) {
            for (final Coordinate c : coords) {
                if (Double.isNaN(minLat) || minLat > c.getLat()) {
                    minLat = c.getLat();
                }
                if (Double.isNaN(maxLat) || maxLat < c.getLat()) {
                    maxLat = c.getLat();
                }
                if (Double.isNaN(minLon) || minLon > c.getLon()) {
                    minLon = c.getLon();
                }
                if (Double.isNaN(maxLon) || maxLon < c.getLon()) {
                    maxLon = c.getLon();
                }
            }
            final MapPolygonImpl p = new MapPolygonImpl(coords);
            p.setStyle(STYLE);
            polygons[pos++] = p;
        }
        latLonBBox = new Rectangle2D.Double(
                minLon, minLat, maxLon - minLon, maxLat - minLat);
    }

    @Override
    public Rectangle2D getLatLonBBox() {
        return latLonBBox;
    }

    @Override
    public boolean hasPolygon() {
        return true;
    }

    @Override
    public MapPolygon[] getPolygons() {
        return polygons;
    }

    @Override
    public MapMarker[] getMarker() {
        throw new UnsupportedOperationException(); // no marker
    }

    @Override
    public void setRadius(final double radius) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColor(final Color color) {
        // TODO set color
    }

}
