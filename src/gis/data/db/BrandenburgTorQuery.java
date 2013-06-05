package gis.data.db;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;

import java.awt.Color;

public class BrandenburgTorQuery extends Query<Object> {

  private static final String gid = Table.BUILDINGS.idColumnName;
  private static final String info = Table.BUILDINGS.infoColumnName;
  private static final String geom = Table.BUILDINGS.geomColumnName;
  private static final String name = Table.BUILDINGS.name;

  public BrandenburgTorQuery(final String str) {
    super("SELECT " + gid + ", " + info + ", " + geom + " FROM " + name +
        " WHERE name = 'Brandenburger Tor'", Table.BUILDINGS, str);
  }

  @Override
  protected void addFlavour(final GeoMarker m, final Object f) {
    m.setColor(Color.CYAN);
  }

}
