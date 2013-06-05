package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class QueryCheckBox extends JCheckBox {

  private final Query<?> q;

  public static final QueryCheckBox createTableQuery(
      final GisPanel panel, final Table table) {
    final Query<?> q = new Query<Object>(
        "SELECT " + table.idColumnName + ", " + table.geomColumnName + ", "
            + table.infoColumnName + " FROM " + table.name,
        table, table.name) {

      @Override
      protected void addFlavour(final GeoMarker m, final Object f) {
        m.setColor(table.color);
      }

    };
    return new QueryCheckBox(panel, q);
  }

  public QueryCheckBox(final GisPanel gisPanel, final Query<?> q) {
    super(q.getName());
    this.q = q;
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        if(isSelected()) {
          gisPanel.addQuery(q);
        } else {
          gisPanel.removeQuery(q);
        }
        gisPanel.repaint();
      }

    });
  }

  public Query<?> getQuery() {
    return q;
  }

}
