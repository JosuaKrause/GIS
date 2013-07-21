package gis.gui.hsv_color_map;

import gis.gui.overlay.Overlay;

import java.awt.Color;

public class PubAtmColorMap implements HsvColorMap {

  private double dim1Min = Double.NaN;
  private double dim1Max = Double.NaN;
  private double dim2Min = Double.NaN;
  private double dim2Max = Double.NaN;

  private Overlay legend;

  @Override
  public void setMapping(final double dim1Min, final double dim1Max,
      final double dim2Min, final double dim2Max) {
    this.dim1Min = dim1Min;
    this.dim1Max = dim1Max;
    this.dim2Min = dim2Min;
    this.dim2Max = dim2Max;
    if(legend == null) {
      legend = new PubAtmLegendOverlay(dim1Min, dim1Max, dim2Min, dim2Max, this);
    }
  }

  @Override
  public Color getColor(final double a, final double b) {
    final double dim1 = (a - dim1Min) / (dim1Max - dim1Min);
    final double dim2 = (b - dim2Min) / (dim2Max - dim2Min);
    return getColorFromIntensities(dim1, dim2);
  }

  public Color getColorFromIntensities(final double a, final double b) {
    final float h = (float) (1 - a) / 3;
    final float s = (float) ((1 - b) * 0.8 + 0.2);
    final float br = 0.9f;
    return Color.getHSBColor(h, s, br);
  }

  @Override
  public Overlay getLegend() {
    return legend;
  }
}
