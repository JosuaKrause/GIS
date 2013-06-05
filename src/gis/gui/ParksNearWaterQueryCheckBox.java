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
        "Parks near Water",
        gisPanel,
        new Query<Boolean>(
            "select p.gid, p.name as info, st_dwithin(p.geom, w.geom, 50, true) as near_water, "
                +
                "p.geom from park as p, berlin_water as w where st_dwithin(p.geom, w.geom, 50, true) "
            , Table.PARK) {

          @Override
          protected Boolean getFlavour(final ResultSet r) throws SQLException {

            return r.getBoolean("near_water");
          }

          @Override
          protected void addFlavour(final GeoMarker m, final Boolean nearWater) {
            if(nearWater) {
              m.setColor(Table.PARK.color.brighter());
            } else {
              m.setColor(Table.PARK.color);
            }

            m.setOutlineColor(Color.BLACK);
            m.setAlphaNotSelected(0.9f);
            m.setAlphaSelected(0.5f);
          }

        });
  }

}
