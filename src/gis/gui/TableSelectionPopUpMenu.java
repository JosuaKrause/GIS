package gis.gui;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class TableSelectionPopUpMenu extends JPopupMenu {

  @SuppressWarnings("unused")
  private final GisPanel gisPanel;

  public TableSelectionPopUpMenu(final GisPanel gisPanel) {
    this.gisPanel = gisPanel;

    addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(final PopupMenuEvent arg0) {
        // do nothing
      }

      @Override
      public void popupMenuWillBecomeInvisible(final PopupMenuEvent arg0) {
        gisPanel.repaint();
      }

      @Override
      public void popupMenuCanceled(final PopupMenuEvent arg0) {
        // do nothing
      }

    });
  }

}
