package gis.gui.color_map;

import java.util.ArrayList;
import java.util.List;

public class IntervalIntensityMapping implements IntensityMapping {

  protected List<Double> input = new ArrayList<>();
  protected List<Double> output = new ArrayList<>();

  /**
   * The input parameters must be a sequence of pairs (in_i, out_i), such that
   * the sequence of input and output values is non-descending.
   * 
   * @param values sequence of (input value, output value) pairs
   */
  public IntervalIntensityMapping(final double... values) {
    setMapping(values);
  }

  public void setMapping(final double... values) {
    input.clear();
    output.clear();
    // check constraints
    if(values.length < 4 || values.length % 2 != 0) throw new AssertionError();
    for(int i = 0; i < values.length - 4; i += 2) {
      if(values[i] > values[i + 2] || values[i + 1] > values[i + 3]) throw new AssertionError();
    }
    for(int i = 1; i < values.length; i += 2) {
      if(values[i] < 0 || values[i] > 1) throw new AssertionError();
    }
    // save parameters
    for(int i = 0; i < values.length - 1; i += 2) {
      input.add(values[i]);
      output.add(values[i + 1]);
    }
  }

  @Override
  public double getMin() {
    return input.get(0);
  }

  @Override
  public double getMax() {
    return input.get(input.size() - 1);
  }

  @Override
  public double getIntensity(final double value) {
    final int size = input.size();
    if(value >= input.get(size - 1)) return output.get(size - 1);
    for(int interval = size - 2; interval >= 0; --interval) {
      if(input.get(interval) < value) {
        final double minIn = input.get(interval);
        final double maxIn = input.get(interval + 1);
        final double minOut = output.get(interval);
        final double maxOut = output.get(interval + 1);
        final double intervalRatio = (value - minIn) / (maxIn - minIn);
        return minOut + (maxOut - minOut) * intervalRatio;
      }
    }
    return output.get(0);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("[");
    for(int i = 0; i < input.size(); ++i) {
      sb.append("[");
      sb.append(input.get(i));
      sb.append("->");
      sb.append(output.get(i));
      sb.append("]");
    }
    sb.append("]");
    return sb.toString();
  }

}
