package gis.data.db;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.gui.color_map.ColorMap;
import gis.gui.color_map.IntervalIntensityMapping;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlickrChloroplethQuery extends Query<Double> {

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

  public FlickrChloroplethQuery(final String name) {
    super(queryWNulls(), Table.BERLIN_ADMINISTRATIVE, name);
  }

  private double maxNum = Double.NEGATIVE_INFINITY;

  private ColorMap colorCode;

  @Override
  protected Double getFlavour(final ResultSet r) throws SQLException {
    Double num = r.getDouble("num");
    // InfoFrame.getInstance().addText("" + num);
    if(num == null) {
      num = 0.0;
    }
    if(num > maxNum) {
      maxNum = num;
    }
    return num;
  }

  @Override
  protected void addFlavour(final GeoMarker m, final Double f) {
    if(maxNum > 0) {
      colorCode = new ColorMap(new IntervalIntensityMapping(0, 0, maxNum, 1),
          new Color[] { new Color(67, 162, 202),
              new Color(168, 221, 181), new Color(224, 243, 219)},
          new double[] { 0, 0.5, 1});
      maxNum = Double.NEGATIVE_INFINITY;
    }
    m.setColor(colorCode.getColor(f));
    m.setAlphaSelected(0.9f);
    m.setAlphaNotSelected(1.0f);
    m.setOutlineColor(Color.BLACK);
  }

  public ColorMap getColorCode() {
    return colorCode;
  }

}
