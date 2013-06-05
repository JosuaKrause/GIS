package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.db.Database;
import gis.gui.overlay.AbstractOverlayComponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class SelectionManagerOverlayComponent extends AbstractOverlayComponent {

  protected static final int WIDTH = 200;
  protected static final int HEIGHT = 0;

  private final SelectionManager selectionManager;
  private final Database db = Database.getInstance();

  public SelectionManagerOverlayComponent(final GisPanel gisPanel,
      final SelectionManager selectionManager) {
    super(gisPanel, new Dimension(WIDTH, HEIGHT), Integer.MIN_VALUE);
    this.selectionManager = selectionManager;
  }

  @Override
  public void paint(final Graphics2D g) {
    final GeoMarker[] selected = selectionManager.getSelection();
    final StringBuilder sb = new StringBuilder();
    if(selected.length > 0) {
      sb.append(selected[0].getInfo());
    }
    if(selected.length == 2) {
      sb.append(" ");
      sb.append(db.getNineCutDescription(selected[0].getId(), selected[1].getId()));
      sb.append(" ");
      sb.append(selected[1].getInfo());
    }

    g.setColor(Color.BLACK);
    g.drawString(sb.toString(), position.x, position.y);
  }
}
