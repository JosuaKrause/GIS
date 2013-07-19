package gis.gui.dist_transform;

import java.awt.Color;
import java.util.Objects;

public class DistanceTransformationCombiner implements Combiner {

  private static final int[] COLORS = new int[6];// ARGB
  private static final double[] THRESHOLDS = new double[5];
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

  public static final Combiner DISTANCE = new DistanceTransformationCombiner(
      COLORS, THRESHOLDS);

  public static final Combiner HOTS = new DistanceTransformationCombiner(
      new int[] {
          0x002b8cbe | (162 << 24),

          0x00c994c7 | (168 << 24),

          0x00e34a33 | (174 << 24),

          0x00fdbb84 | (180 << 24),

          0x00fee8c8 | (186 << 24),

          0x00ffffff | (192 << 24),
      }, new double[] {
          50.0 / 16.0,
          50.0 / 8.0,
          50.0 / 4.0,
          50.0 / 2.0,
          50.0
      });

  private final int[] colors;
  private final double[] thresholds;

  private DistanceTransformationCombiner(final int[] colors, final double[] thresholds) {
    this.colors = Objects.requireNonNull(colors);
    this.thresholds = Objects.requireNonNull(thresholds);
  }

  @Override
  public Color getColor(final double value) {
    return new Color(distanceToColor(value), true);
  }

  @Override
  public int distanceToColor(final double distance) {
    for(int i = thresholds.length - 1; i >= 0; --i) {
      if(distance >= thresholds[i]) return colors[i + 1];
    }
    return colors[0];
  }

  @Override
  public double maxDistance() {
    return thresholds[thresholds.length - 1];
  }

}
