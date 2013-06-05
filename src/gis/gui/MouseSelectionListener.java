package gis.gui;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Database;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

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
    if(SwingUtilities.isLeftMouseButton(e)) {
      if(gisControlPanel.processSelectionClick(e.getPoint())) {
        gisPanel.repaint();
      }
    }
  }

  private ElementId curHover;

  protected volatile Thread cur;

  @Override
  public void mouseMoved(final MouseEvent e) {
    final Point2D pos = e.getPoint();
    final GisPanel gisPanel = this.gisPanel;
    final List<GeoMarker> picks = new ArrayList<>();
    gisPanel.pick(pos, picks);
    for(final GeoMarker m : picks) {
      if(m.getId().getQuery().getTable() != Table.FLICKR) {
        gisPanel.setToolTipText(m.getInfo());
        gisPanel.repaint();
        return;
      }
    }
    if(!e.isShiftDown()) return;
    final Thread s = new Thread() {

      @Override
      public void run() {
        for(final GeoMarker m : picks) {
          if(cur != this || isInterrupted()) return;
          if(m.getId().getQuery().getTable() != Table.FLICKR) {
            continue;
          }
          if(setCurHover(pos, m.getId())) {
            break;
          }
        }
      }

    };
    if(cur != null) {
      cur.interrupt();
    }
    cur = s;
    s.setDaemon(true);
    s.start();
  }

  protected boolean setCurHover(final Point2D pos, final ElementId id) {
    if(id == null) {
      if(curHover != null) {
        gisPanel.setHoverImage(null, pos);
      }
      curHover = null;
      return false;
    }
    if(id.equals(curHover)) return true;
    curHover = id;
    final Image img = Database.getInstance().getImage(curHover);
    if(img != null) {
      gisPanel.setHoverImage(img, pos);
    }
    return img != null;
  }

}
