package gis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseSelectionListener extends MouseAdapter {

  private final GisPanel gisPanel;
  private final GisControlPanel gisControlPanel;

  public MouseSelectionListener(final GisPanel gisPanel,
      final GisControlPanel gisControlPanel) {
    this.gisPanel = gisPanel;
    this.gisControlPanel = gisControlPanel;
  }

  @Override
  public void mouseClicked(final MouseEvent e) {
    if(e.getButton() == MouseEvent.BUTTON1) {
      if(gisControlPanel.processSelectionClick(e.getPoint())) {
        gisPanel.repaint();
      }
    }
  }

}
