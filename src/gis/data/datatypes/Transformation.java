package gis.data.datatypes;

import java.awt.geom.Point2D;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public interface Transformation {

  Point2D convert(Coordinate c);

}
