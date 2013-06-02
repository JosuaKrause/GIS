package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JPanel;

public class GisControlPanel extends JPanel {

  public GisControlPanel(final GisPanel gisPanel) {
    add(new QueryCheckBox("brandenburg", gisPanel,
        new Query<Double>("SELECT gid, geom, 1 as distance FROM " + Table.BERLIN_POI
            + " LIMIT 100;", Table.BERLIN_POI) {

          @Override
          protected Double getFlavour(final ResultSet r) throws SQLException {
            return r.getDouble("distance");
          }

          @Override
          protected void addFlavour(final GeoMarker m, final Double f) {
            m.setRadius(f);
          }

        }));
    for(final Table t : Table.values()) {
      addTableSelectionCheckBox(gisPanel, t);
    }
    setSize(getMinimumSize());
  }

  private void addTableSelectionCheckBox(final GisPanel gisPanel, final Table table) {
    final QueryCheckBox box = QueryCheckBox.createTableQuery(gisPanel, table);
    add(box);
  }

}
