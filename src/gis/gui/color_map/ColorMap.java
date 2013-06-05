package gis.gui.color_map;

import gis.gui.GisPanel;
import gis.gui.overlay.IOverlayComponent;

import java.awt.Color;

public class ColorMap extends AbstractColorMapping {

  protected IOverlayComponent heatMapOverlayComponent;

  public static ColorMap getHeatMap(final double min, final double max) {
    final IIntensityMapping intensityMapping = new IntervalIntensityMapping(
        min, 0, max, 1);
    return new ColorMap(intensityMapping);
  }

  protected ColorMap(final IIntensityMapping intensityMapping) {
    super(intensityMapping,
        new Color[] { new Color(248, 16, 0), new Color(252, 252, 0),
            Color.WHITE},
        new double[] { 0, 0.5, 1});
    // new Color[] { new Color(198, 13, 0), new Color(248, 16, 0),
    // new Color(252, 252, 0), Color.WHITE},
    // new double[] { 0, 0.0001, 0.5, 1});
  }

  @Override
  public Color intensityToColor(final double intensity) {
    if(intensity == 0) return colors[0].darker();
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

  public void initOverlayComponent(final GisPanel gisPanel) {
    heatMapOverlayComponent = new ColorMapOverlayComponent(gisPanel, 1, this);
    gisPanel.registerOverlayComponent(heatMapOverlayComponent);
  }

  public IOverlayComponent getHeatMapOverlayComponent() {
    return heatMapOverlayComponent;
  }

}
