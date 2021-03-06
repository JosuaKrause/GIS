package gis.data.db;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

import java.util.List;

public class BrandenburgQuery extends Query {

  private final double maxMeter;

  private static String query(final double maxDistance) {
    final String id = Table.FLICKR.idColumnName;
    final String info = Table.FLICKR.infoColumnName;
    final String ageom = Table.FLICKR.geomColumnName;
    final String bgeom = Table.BUILDINGS.geomColumnName;
    final String aname = Table.FLICKR.name;
    final String bname = Table.BUILDINGS.name;
    return "SELECT a." + id + " as " + id + ", a." + info + " as " + info + "," +
        " a." + ageom + " as " + ageom + ", ST_DISTANCE(b.geom, a." + ageom +
        ", true) as distance FROM " + aname + " as a, " +
        " (SELECT " + bgeom + " FROM " + bname +
        " WHERE name = 'Brandenburger Tor') as b" +
        " WHERE ST_DISTANCE(b." + bgeom + ", a." + ageom + ", true) <= " + maxDistance;
  }

  public BrandenburgQuery(final double maxMeter, final String name) {
    super(query(maxMeter), Table.FLICKR, name, "distance");
    this.maxMeter = maxMeter;
  }

  @Override
  protected void finishLoading(final List<GeoMarker> ms) {
    for(final GeoMarker m : ms) {
      final double r = m.getQueryValue() / maxMeter * 0.0005 + 0.00005;
      m.setRadius(r);
      m.setColor(getTable().color);
    }
  }

}
