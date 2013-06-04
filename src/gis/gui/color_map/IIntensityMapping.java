package gis.gui.color_map;

public interface IIntensityMapping {
  /**
   * Returns the intensity value mapped to a specific value.
   * 
   * @param value the input value
   * @return mapped value from [0,1]
   */
  public double getIntensity(double value);
}
