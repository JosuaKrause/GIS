package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;

public abstract class QueryCheckBox extends JCheckBox {

  private static final long serialVersionUID = 8146119965206278027L;

  public static final QueryCheckBox createTableQuery(
      final GisPanel panel, final Table table, final String name) {
    final Query q = new Query(
        "SELECT " + table.idColumnName + ", " + table.geomColumnName + ", "
            + table.infoColumnName + " FROM " + table.name,
        table, name, null) {

      @Override
      protected void finishLoading(final List<GeoMarker> ms) {
        for(final GeoMarker m : ms) {
          m.setColor(table.color);
        }
      }

    };
    return new QueryCheckBox(panel, q) {

      private static final long serialVersionUID = 1009355284434125327L;

      @Override
      public void onAction(final GisPanel p) {
        // nothing to do
      }

    };
  }

  public QueryCheckBox(final GisPanel gisPanel, final Query q) {
    super(q.getName());
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        if(isSelected()) {
          gisPanel.addQuery(q);
        } else {
          gisPanel.removeQuery(q);
        }
        onAction(gisPanel);
        gisPanel.repaint();
      }

    });
  }

  public abstract void onAction(GisPanel gisPanel);

}
