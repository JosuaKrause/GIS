package gis.gui;

import gis.data.datatypes.Table;
import gis.data.db.Query;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

public class TableSelectionCheckBox extends JCheckBox {

  public TableSelectionCheckBox(final GisPanel gisPanel, final Table table) {
    super(table.name);
    addActionListener(new ActionListener() {

      private final Query q = new Query("SELECT gid, geom FROM " + table.name
          + " LIMIT 10000");

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

}
