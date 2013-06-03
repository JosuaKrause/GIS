package gis.gui;

import gis.data.datatypes.GeoMarker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

@SuppressWarnings("unused")
public class TableSelectionMenuItem extends JMenuItem {

  private final GeoMarker marker;
  private final SelectionManager selectionManager;

  public TableSelectionMenuItem(final GeoMarker marker,
      final SelectionManager selectionManager) {
    this.marker = marker;
    this.selectionManager = selectionManager;
    setText(marker.getInfo());
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent arg0) {
        selectionManager.clickedOn(marker);
      }

    });
  }

}
