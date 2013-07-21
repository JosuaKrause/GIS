package gis.gui.dist_transform;

import java.awt.Color;
import java.util.Objects;

public class DistanceTransformationCombiner implements DistanceColorMapping {

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

  public static final DistanceColorMapping DISTANCE = new DistanceTransformationCombiner(
      COLORS, THRESHOLDS, 500, "m", false);

  public static final DistanceColorMapping HOTS = new DistanceTransformationCombiner(
      new int[] {
          0x002b8cbe | (162 << 24),

          0x00c994c7 | (168 << 24),

          0x00e34a33 | (174 << 24),

          0x00fdbb84 | (180 << 24),

          0x00fee8c8 | (186 << 24),

          0x00ffffff | (192 << 24),
      }, new double[] {
          30.0 / 4.0,
          30.0 / 2.0,
          30.0,
          30.0 * 2.0,
          30.0 * 4.0
      }, 30.0, "px", true);

  private final int[] colors;
  private final double[] thresholds;
  private final double max;
  private final String unit;
  private final boolean interpolate;

  private DistanceTransformationCombiner(final int[] colors, final double[] thresholds,
      final double max, final String unit, final boolean interpolate) {
    this.max = max;
    this.unit = unit;
    this.interpolate = interpolate;
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
      if(distance >= thresholds[i]) {
        if(!interpolate || i >= thresholds.length - 1) return colors[i + 1];
        final double start = thresholds[i];
        final double end = thresholds[i + 1];
        final Color r = interpolate(new Color(colors[i], true),
            new Color(colors[i + 1], true),
            (distance - start) / (end - start));
        return r.getRGB();
      }
    }
    return colors[0];
  }

  @Override
  public double maxDistance() {
    return max;
  }

  @Override
  public Color intensityToColor(final double intensity) {
    final int cInt = distanceToColor(intensity * getMax());
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
    return new Color(color, false);
  }

  @Override
  public double getMax() {
    return thresholds[thresholds.length - 1];
  }

  @Override
  public double getMin() {
    return 0;
  }

  @Override
  public String formatValue(final double value) {
    return value + unit;
  }

  /**
   * Interpolates between two colors.
   * 
   * @param from The color to interpolate from.
   * @param to The color to interpolate to.
   * @param t The interpolation value from <code>0</code> to <code>1</code>.
   * @return The interpolated color.
   */
  public static Color interpolate(final Color from, final Color to, final double t) {
    final float[] fromRGBA = new float[4];
    final float[] toRGBA = new float[4];
    from.getRGBComponents(fromRGBA);
    to.getRGBComponents(toRGBA);
    final double r = fromRGBA[0] * (1 - t) + toRGBA[0] * t;
    final double g = fromRGBA[1] * (1 - t) + toRGBA[1] * t;
    final double b = fromRGBA[2] * (1 - t) + toRGBA[2] * t;
    final double a = fromRGBA[3] * (1 - t) + toRGBA[3] * t;
    return new Color((float) r, (float) g, (float) b, (float) a);
  }

}
