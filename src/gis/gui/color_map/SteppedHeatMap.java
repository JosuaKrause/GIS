package gis.gui.color_map;

import java.awt.Color;

public class SteppedHeatMap extends HeatMap {

  public static SteppedHeatMap getSteppedHeatMap(final double min, final double max) {
    final IIntensityMapping intensityMapping = new IntervalIntensityMapping(min, 0, max,
        1);
    return new SteppedHeatMap(intensityMapping);
  }

  protected final Color minIntensityColor = Color.MAGENTA;

  protected SteppedHeatMap(final IIntensityMapping intensityMapping) {
    super(intensityMapping);
  }

  @Override
  public Color intensityToColor(final double value) {
    if(value == intensities[0]) return minIntensityColor;
    return super.intensityToColor(value);
  }
}
