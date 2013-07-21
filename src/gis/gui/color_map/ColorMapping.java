package gis.gui.color_map;

import java.awt.Color;

/**
 * A color mapping.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 */
public interface ColorMapping {

  /**
   * Returns the color mapped to a specific value.
   * 
   * @param value the input value
   * @return mapped color
   */
  public Color getColor(double value);

  public Color intensityToColor(double max);

  public double getMax();

  public double getMin();

  public String formatValue(double value);

}
