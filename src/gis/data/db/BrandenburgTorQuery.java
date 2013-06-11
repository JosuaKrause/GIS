package gis.data.db;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

import java.awt.Color;
import java.util.List;

public class BrandenburgTorQuery extends Query {

  private static final String gid = Table.BUILDINGS.idColumnName;
  private static final String info = Table.BUILDINGS.infoColumnName;
  private static final String geom = Table.BUILDINGS.geomColumnName;
  private static final String name = Table.BUILDINGS.name;

  public BrandenburgTorQuery(final String str) {
    super("SELECT " + gid + ", " + info + ", " + geom + " FROM " + name +
        " WHERE name = 'Brandenburger Tor'", Table.BUILDINGS, str, null);
  }

  @Override
  protected void finishLoading(final List<GeoMarker> ms) {
    for(final GeoMarker m : ms) {
      m.setColor(Color.CYAN);
    }
  }

}
