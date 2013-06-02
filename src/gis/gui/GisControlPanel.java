package gis.gui;

import gis.data.datatypes.Table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class GisControlPanel extends JPanel {

  private final List<JCheckBox> tableSelectionCheckBoxes = new ArrayList<JCheckBox>();
  private final GisPanel gisPanel;

  public GisControlPanel(final GisPanel gisPanel) {
    this.gisPanel = gisPanel;
    for(final Table t : Table.values()) {
      addTableSelectionCheckBox(gisPanel, t);
    }
    setSize(getMinimumSize());
  }

  private void addTableSelectionCheckBox(final GisPanel gisPanel, final Table table) {
    final TableSelectionCheckBox box = new TableSelectionCheckBox(gisPanel, table);
    tableSelectionCheckBoxes.add(box);
    add(box);
  }

}
