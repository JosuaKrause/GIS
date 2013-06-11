package gis.gui.color_map;

public interface IntensityMapping {

  /**
   * Returns the intensity value mapped to a specific value.
   * 
   * @param value the input value
   * @return mapped value between [0,1]
   */
  double getIntensity(double value);

  double getMax();

  double getMin();

}
