package gis.gui.dist_transform;

import gis.gui.color_map.ColorMapping;

public interface DistanceColorMapping extends ColorMapping {

  int distanceToColor(double distance);

  double maxDistance();

}
