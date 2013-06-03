package gis.data.db;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BrandenburgQuery extends Query<Double> {

  private static String query(final double maxDistance) {
    return "SELECT a.photoid as gid, a." + Table.FLICKR.infoColumnName + " as info," +
        " a.poly_geom as geom, ST_DISTANCE(b.geom, a.poly_geom) as distance " +
        "FROM " + Table.FLICKR.name + " as a, " +
        " (SELECT geom FROM " + Table.BERLIN_POI +
        " WHERE name = 'Attraction:Brandenburger Tor') as b" +
        " WHERE ST_DISTANCE(b.geom, a.poly_geom) <= " + maxDistance;
  }

  public BrandenburgQuery() {
    super(query(50), Table.FLICKR);
  }

  @Override
  protected Double getFlavour(final ResultSet r) throws SQLException {
    System.out.println(r.getDouble("distance"));
    return r.getDouble("distance");
  }

  @Override
  protected void addFlavour(final GeoMarker m, final Double f) {
    m.setRadius(f);
    m.setColor(getTable().color);
  }

}
