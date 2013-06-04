package gis.gui.color_map;

import java.awt.Color;

public class HeatMap extends AbstractColorMapping {

  public static HeatMap getHeatMap(final double min, final double max) {
    final IIntensityMapping intensityMapping = new IntervalIntensityMapping(min, 0, max,
        1);
    return new HeatMap(intensityMapping);
  }

  private HeatMap(final IIntensityMapping intensityMapping) {
    super(intensityMapping,
        // new Color[] { Color.RED, Color.YELLOW, Color.WHITE},
        // new double[] { 0, 0.6, 1});
        new Color[] { new Color(248, 16, 0), new Color(252, 252, 0), Color.WHITE},
        new double[] { 0, 0.5, 1});
  }

  @Override
  protected Color intensityToColor(final double intensity) {
    final int size = intensities.length;
    for(int interval = 0; interval < size - 1; ++interval) {
      if(intensities[interval + 1] < intensity) {
        continue;
      }
      final Color a = colors[interval];
      final Color b = colors[interval + 1];
      final double aRatio = 1 - ((intensity - intensities[interval])
          / (intensities[interval + 1] - intensities[interval]));
      return AbstractColorMapping.linearInterpolation(a, b, aRatio);
    }
    return colors[size - 1];
  }

}
