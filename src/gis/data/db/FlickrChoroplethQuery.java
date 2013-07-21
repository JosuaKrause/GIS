package gis.data.db;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.gui.color_map.ColorMap;
import gis.gui.color_map.IntervalIntensityMapping;

import java.awt.Color;
import java.util.List;

public class FlickrChoroplethQuery extends Query {

  private static final String query() {
    final Table t = Table.BERLIN_ADMINISTRATIVE;
    final String tid = t.idColumnName;
    final String info = t.infoColumnName;
    final String geom = t.geomColumnName;
    final Table f = Table.FLICKR;
    final String point = f.geomColumnName;
    return "select a." + tid + " as " + tid + ", a." + info + " as " + info +
        ", a." + geom + " as " + geom + ", " +
        "count(a." + tid + ") as num " +
        "from " + t.name + " as a, " + f.name + " as b where " +
        "st_contains(a." + geom + ", b." + point + ") " +
        "group by a." + tid;
  }

  private static final String queryWNulls() {
    final String inner = query();
    final Table t = Table.BERLIN_ADMINISTRATIVE;
    final String tid = t.idColumnName;
    final String info = t.infoColumnName;
    final String geom = t.geomColumnName;
    return "select a." + tid + " as " + tid + ", a." + info + " as " + info +
        ", a." + geom + " as " + geom + ", " +
        "b.num as num " +
        "from " + t.name + " as a left outer join (" + inner + ") as b " +
        "on a." + tid + " = b." + tid;
  }

  public FlickrChoroplethQuery(final String name) {
    super(queryWNulls(), Table.BERLIN_ADMINISTRATIVE, name, "num");
  }

  private final IntervalIntensityMapping mapping =
      new IntervalIntensityMapping(0, 0, 1, 1);

  private final ColorMap colorCode = new ColorMap(mapping,
      new Color[] { new Color(67, 162, 202),
          new Color(168, 221, 181), new Color(224, 243, 219)},
      new double[] { 0, 0.5, 1});

  @Override
  protected void finishLoading(final List<GeoMarker> ms) {
    double maxNum = Double.NEGATIVE_INFINITY;
    for(final GeoMarker m : ms) {
      final double v = m.getQueryValue();
      if(v > maxNum) {
        maxNum = v;
      }
    }
    if(maxNum > 0) {
      mapping.setMapping(0, 0, maxNum, 1);
    }
    for(final GeoMarker m : ms) {
      m.setColor(colorCode.getColor(m.getQueryValue()));
      m.setAlphaSelected(0.6f);
      m.setAlphaNotSelected(.8f);
      m.setOutlineColor(Color.BLACK);
    }
  }

  public ColorMap getColorCode() {
    return colorCode;
  }

}
