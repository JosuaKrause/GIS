package gis.gui.color_map;

import java.awt.Color;

public class SteppedColorMap extends ColorMap {

  public static SteppedColorMap getSteppedHeatMap(final double min, final double max) {
    final IIntensityMapping intensityMapping = new IntervalIntensityMapping(min, 0, max,
        1);
    return new SteppedColorMap(intensityMapping);
  }

  protected final Color minIntensityColor = Color.MAGENTA;

  protected SteppedColorMap(final IIntensityMapping intensityMapping) {
    super(intensityMapping);
  }

  @Override
  public Color intensityToColor(final double value) {
    if(value == intensities[0]) return minIntensityColor;
    return super.intensityToColor(value);
  }

}
