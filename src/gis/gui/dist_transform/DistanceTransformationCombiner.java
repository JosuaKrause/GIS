package gis.gui.dist_transform;

import java.awt.Color;

public class DistanceTransformationCombiner implements Combiner {

  static final int[] COLORS = new int[6];// ARGB
  static final double[] THRESHOLDS = new double[5];
  static {
    THRESHOLDS[0] = 100;
    THRESHOLDS[1] = 200;
    THRESHOLDS[2] = 300;
    THRESHOLDS[3] = 400;
    THRESHOLDS[4] = 500;

    COLORS[0] = (162 << 24) | (255 << 16) | (255 << 8) | 255; // 0
    COLORS[1] = (168 << 24) | (204 << 16) | (229 << 8) | 229; // 86
    COLORS[2] = (174 << 24) | (178 << 16) | (216 << 8) | 216; // 121
    COLORS[3] = (180 << 24) | (103 << 16) | (177 << 8) | 179; // 148
    COLORS[4] = (186 << 24) | (91 << 16) | (175 << 8) | 133; // 171
    COLORS[5] = (192 << 24) | (67 << 16) | (169 << 8) | 40; // 192
  }

  @Override
  public Color getColor(final double value) {
    return new Color(distanceToColor(value), true);
  }

  @Override
  public int distanceToColor(final double distance) {
    if(distance >= THRESHOLDS[4]) return COLORS[5];
    if(distance >= THRESHOLDS[3]) return COLORS[4];
    if(distance >= THRESHOLDS[2]) return COLORS[3];
    if(distance >= THRESHOLDS[1]) return COLORS[2];
    if(distance >= THRESHOLDS[0]) return COLORS[1];
    return COLORS[0];
  }

  @Override
  public Color intensityToColor(final double intensity) {
    final int i = (int) (intensity * COLORS.length);
    final int cInt = COLORS[i];
    final int a = (cInt >> 24) & 255;
    final float alpha = a / 255.0f;
    int r = (cInt >> 16) & 255;
    int g = (cInt >> 8) & 255;
    int b = cInt & 255;
    final float invAlpha = 1 - alpha;
    r = Math.round(invAlpha * 255 + alpha * r);
    g = Math.round(invAlpha * 255 + alpha * g);
    b = Math.round(invAlpha * 255 + alpha * b);

    final int color = (255 << 24) | (r << 16) | (g << 8) | b;
    return new Color(color, true);
  }

  @Override
  public double getMax() {
    return THRESHOLDS[4];
  }

  @Override
  public double getMin() {
    return THRESHOLDS[0];
  }

  @Override
  public String formatValue(final double value) {
    return value + "m";
  }

}
