package gis.data.datatypes;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

public class GeoMarkerLineString extends GeoMarker {

  public Coordinate[] points;
  public Color color = Color.BLUE;

  public GeoMarkerLineString(final String info, final ElementId id,
      final Coordinate[] points) {
    super(info, id);
    this.points = points;
  }

  @Override
  public boolean hasPolygon() {
    return true;
  }

  @Override
  public MapMarker[] getMarker() {
    throw new UnsupportedOperationException(); // no markers
  }

  @Override
  public MapPolygon[] getPolygons() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Rectangle2D getLatLonBBox() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRadius(final double radius) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setColor(final Color color) {
    throw new UnsupportedOperationException();
  }

}
