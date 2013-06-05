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
        "SELECT gid, geom, " + table.infoColumnName + " as info FROM " + table.name,
        table) {

      @Override
      protected void addFlavour(final GeoMarker m, final Object f) {
        m.setColor(table.color);
      }

    };
    return new QueryCheckBox(table.name, panel, q);
  }

  public QueryCheckBox(
      final String name, final GisPanel gisPanel, final Query<?> q) {
    super(name);
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

  public Table getTable() {
    return q.getTable();
  }

  Query getQuery() {
    return q;
  }
}
