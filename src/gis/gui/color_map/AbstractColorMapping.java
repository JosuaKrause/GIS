package gis.gui.color_map;

import java.awt.Color;

public abstract class AbstractColorMapping implements IColorMapping {

  protected final IIntensityMapping intensityMapping;
  protected final Color[] colors;
  protected final double[] intensities;

  public AbstractColorMapping(final IIntensityMapping intensityMapping,
      final Color[] colors, final double[] intensities) {
    this.intensityMapping = intensityMapping;
    this.colors = colors;
    this.intensities = intensities;
  }

  @Override
  public final Color getColor(final double value) {
    final double intensity = intensityMapping.getIntensity(value);
    return intensityToColor(intensity);
  }

  protected abstract Color intensityToColor(double intensity);

  public static Color linearInterpolation(final Color a, final Color b,
      final double aRatio) {
    final float[] ca = a.getRGBColorComponents(null);
    final float[] cb = b.getRGBColorComponents(null);
    interpolateColorChannel(ca, cb, (float) aRatio);
    return new Color(ca[0], ca[1], ca[2], ca.length < 4 ? 1f : ca[3]);
  }

  private static void interpolateColorChannel(final float[] a, final float[] b,
      final float aRatio) {
    for(int i = 0; i < Math.min(a.length, b.length); ++i) {
      a[i] = a[i] * aRatio + b[i] * (1 - aRatio);
    }
  }

  public double getMax() {
    return intensityMapping.getMax();
  }

  public double getMin() {
    return intensityMapping.getMin();
  }

}
