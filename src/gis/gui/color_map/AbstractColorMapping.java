package gis.gui.color_map;

import java.awt.Color;

public abstract class AbstractColorMapping implements IColorMapping {

  protected final IIntensityMapping intensityMaping;
  protected final Color[] colors;
  protected final double[] intensities;

  public AbstractColorMapping(final IIntensityMapping intensityMapping,
      final Color[] colors, final double[] intensities) {
    intensityMaping = intensityMapping;
    this.colors = colors;
    this.intensities = intensities;
  }

  @Override
  public final Color getColor(final double value) {
    final double intensity = intensityMaping.getIntensity(value);
    return intensityToColor(intensity);
  }

  protected abstract Color intensityToColor(double intensity);

  public static Color linearInterpolation(final Color a, final Color b,
      final double aRatio) {
    final int aRed = a.getRed();
    final int aGreen = a.getGreen();
    final int aBlue = a.getBlue();
    final int aAlpha = a.getAlpha();
    final int bRed = b.getRed();
    final int bGreen = b.getGreen();
    final int bBlue = b.getBlue();
    final int bAlpha = b.getAlpha();
    final int resRed = interpolateColorChannel(aRed, bRed, aRatio);
    final int resGreen = interpolateColorChannel(aGreen, bGreen, aRatio);
    final int resBlue = interpolateColorChannel(aBlue, bBlue, aRatio);
    final int resAlpha = interpolateColorChannel(aAlpha, bAlpha, aRatio);
    return new Color(resRed, resGreen, resBlue, resAlpha);
  }

  private static int interpolateColorChannel(final int a, final int b, final double aRatio) {
    final double d = a * aRatio + b * (1 - aRatio);
    return (int) Math.round(d);
  }

}
