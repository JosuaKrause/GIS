package gis.gui;

import gis.data.datatypes.Table;
import gis.data.db.Database;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class GisControlPanel extends JPanel {

  private final List<JCheckBox> tableSelectionCheckBoxes = new ArrayList<JCheckBox>();
  private final GisPanel gisPanel;

  public GisControlPanel(final Database db, final GisPanel gisPanel) {
    this.gisPanel = gisPanel;
    for(final Table t : Table.values()) {
      addTableSelectionCheckBox(gisPanel, db, t);
    }
    setSize(getMinimumSize());
  }

  private void addTableSelectionCheckBox(
      final GisPanel gisPanel, final Database db, final Table table) {
    final TableSelectionCheckBox box = new TableSelectionCheckBox(gisPanel, db, table);
    tableSelectionCheckBoxes.add(box);
    add(box);
  }

}
