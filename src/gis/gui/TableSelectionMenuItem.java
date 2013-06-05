package gis.gui;

import gis.data.datatypes.GeoMarker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;

public class TableSelectionMenuItem extends JMenuItem {

  public TableSelectionMenuItem(final GeoMarker marker,
      final SelectionManager selectionManager, final GisPanel panel) {
    final boolean selected = selectionManager.isSelected(marker);
    final String txt = marker.getId().getQuery().getName() + ": " + marker.getInfo();
    if(selected) {
      setText("<html><b>" + txt + "</b>");
    } else {
      setText(txt);
    }
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        selectionManager.clickedOn(marker);
      }

    });
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(final MouseEvent e) {
        marker.setSelected(!selected);
        panel.repaint();
      }

      @Override
      public void mouseExited(final MouseEvent e) {
        marker.setSelected(selected);
        panel.repaint();
      }

    });
  }
}
