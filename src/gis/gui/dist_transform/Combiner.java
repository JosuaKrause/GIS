package gis.gui.dist_transform;

import gis.gui.color_map.ColorMapping;

import java.awt.Color;

public interface Combiner extends ColorMapping {

  int distanceToColor(double distance);

  static double MAX_DIST = 100;

  static Combiner DISTANCE = new Combiner() {

    @Override
    public int distanceToColor(final double distance) {
      final int t = (int) Math.round(255 * Math.min(distance, MAX_DIST) / MAX_DIST);
      final int i = 255 - Math.min(t, 255);
      // ARGB
      return (((255 - i) * 3 / 4) << 24) | (i << 16) | (i << 8) | i;
    }

    @Override
    public Color getColor(final double value) {
      return new Color(distanceToColor(value), true);
    }

  };

  static Combiner HOTS = new Combiner() {

    @Override
    public int distanceToColor(final double distance) {
      final int t = (int) Math.round(255 * Math.min(distance, MAX_DIST) / MAX_DIST);
      final int i = 255 - Math.min(t, 255);
      // ARGB
      return (Color.HSBtoRGB(0, 1, i / 255f) & 0x00ffffff) | (i << 24);
    }

    @Override
    public Color getColor(final double value) {
      return new Color(distanceToColor(value), true);
    }

  };

}
