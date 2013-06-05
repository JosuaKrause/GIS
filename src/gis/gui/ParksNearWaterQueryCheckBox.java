package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParksNearWaterQueryCheckBox extends QueryCheckBox {

  public ParksNearWaterQueryCheckBox(final GisPanel gisPanel) {
    super(
        gisPanel,
        new Query<Double>(
            "select p.gid, p.name, p.geom, min(st_distance(w.geom, p.geom, true)) "
                +
                "as water_dist from berlin_water as w, ("
                +
                "select * from berlin_poi "
                +
                "where category = 'Leisure'and (((name like 'Park%' or name like '%park%') "
                +
                "and not name like 'Parking%') or name like '%arten%') and not name like '%layground%' "
                +
                "and not name like 'Theme park%' and not name like 'Theater%' and " +
                "not name = 'Common:Gartenamt') as p " +
                "group by p.gid, p.name, p.geom;"
            , Table.BERLIN_POI, "Parks near Water") {

          @Override
          protected Double getFlavour(final ResultSet r) throws SQLException {

            return r.getDouble("water_dist");
          }

          @Override
          protected void addFlavour(final GeoMarker m, final Double waterDist) {
            if(waterDist < 50) {
              m.setColor(new Color(90, 180, 172));
            } else {
              m.setColor(new Color(216, 179, 101));
            }
            m.setFixedSize(true);
            m.setRadius(8);
            m.setOutlineColor(Color.BLACK);
            m.setAlphaNotSelected(0.9f);
            m.setAlphaSelected(0.6f);
          }

        });
  }

}
