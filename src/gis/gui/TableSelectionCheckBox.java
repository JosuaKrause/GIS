package gis.gui;

import gis.data.datatypes.Table;
import gis.data.db.Database;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class TableSelectionCheckBox extends JCheckBox {

  private final GisPanel gisPanel;
  private final Database db;
  private final Table table;

  public TableSelectionCheckBox(
      final GisPanel gisPanel, final Database db, final Table table) {
    super(table.name);
    this.gisPanel = gisPanel;
    this.db = db;
    this.table = table;
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        if(isSelected()) {
          gisPanel.addGeoMarkerList(db.getGeometry(table));
        } else {
          // FIXME find all objects for the given query/table
          gisPanel.removeGeoMarkers(db.getGeometry(table));
        }
        gisPanel.repaint();
      }

    });
  }

}
