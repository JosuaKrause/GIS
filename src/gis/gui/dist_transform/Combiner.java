package gis.gui.dist_transform;

import gis.gui.color_map.ColorMapping;

public interface Combiner extends ColorMapping {

  int distanceToColor(double distance);

  static double MAX_DIST = 100;

}
