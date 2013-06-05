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
            "select gid, name, water_dist, geom from park"
            , Table.PARK, "Parks near Water") {

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
