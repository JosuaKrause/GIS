package gis.gui.hsv_color_map;

import gis.gui.overlay.Overlay;

import java.awt.Color;

public interface HsvColorMap {

  public Color getColor(double a, double b);

  public void setMapping(double dim1Min, double dim1Max, double dim2Min, double dim2Max);

  public Overlay getLegend();

}
