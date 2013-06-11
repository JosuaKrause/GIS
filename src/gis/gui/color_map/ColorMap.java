package gis.gui.color_map;

import gis.gui.GisPanel;
import gis.gui.overlay.Overlay;

import java.awt.Color;

public class ColorMap extends AbstractColorMapping {

  private Overlay colorMapOverlayComponent;

  public ColorMap(final IntensityMapping intensityMapping, final Color[] colors,
      final double[] vals) {
    super(intensityMapping, colors, vals);
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
    colorMapOverlayComponent = new ColorMapOverlayComponent(gisPanel, 1, this);
    gisPanel.registerOverlayComponent(colorMapOverlayComponent);
  }

  public Overlay getColorMapOverlayComponent() {
    return colorMapOverlayComponent;
  }

  public String formatValue(final double value) {
    return "" + value;
  }

}
