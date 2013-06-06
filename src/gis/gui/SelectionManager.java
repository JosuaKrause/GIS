package gis.gui;

import gis.data.NineCut;
import gis.data.datatypes.GeoMarker;
import gis.data.db.Database;

import java.util.Formatter;
import java.util.Locale;

public class SelectionManager {

  private final GeoMarker[] selection = new GeoMarker[2];
  private int numSelected = 0;

  // return true if repaint required
  public synchronized boolean clickedOn(final GeoMarker marker) {
    switch(numSelected) {
      case 2:
        final int index = indexOf(marker);
        if(index >= 0) {
          deselect(index);
          return true;
        }
        break;
      case 1:
        if(selection[0].equals(marker)) {
          deselect(0);
        } else {
          select(marker);
        }
        return true;
      case 0:
        select(marker);
        return true;
      default:
        break;
    }
    return false;
  }

  public boolean isSelected(final GeoMarker m) {
    return indexOf(m) >= 0;
  }

  /**
   * Getter.
   * 
   * @param m The geo marker.
   * @return the marker's index in {@link #selection} or -1.
   */
  private int indexOf(final GeoMarker m) {
    for(int i = 0; i < numSelected; ++i) {
      if(selection[i].equals(m)) return i;
    }
    return -1;
  }

  private void deselect(final int index) {
    selection[index].setSelected(false);
    if(index == 0 && numSelected == 2) {
      selection[0] = selection[1];
    }
    --numSelected;
    onSelection();
  }

  private void select(final GeoMarker m) {
    m.setSelected(true);
    selection[numSelected] = m;
    ++numSelected;
    onSelection();
  }

  private SelectionManagerOverlayComponent sel;

  public void setSelector(final SelectionManagerOverlayComponent sel) {
    this.sel = sel;
  }

  private void onSelection() {
    if(sel == null) return;
    final Database db = Database.getInstance();
    final StringBuilder sb = new StringBuilder();
    NineCut nc = null;
    sel.setNineCut(nc);
    if(numSelected == 1) {
      sb.append(selection[0].getInfo());
    } else if(numSelected == 2) {
      final double d = db.getDistance(selection[0].getId(), selection[1].getId());
      sb.append(String.format("Distance: %.5fm", d));
      nc = db.getNineCutDescription(
          selection[0].getId(), selection[1].getId());
      final boolean ap = selection[0].isPoint();
      final boolean bp = selection[1].isPoint();
      if(!(ap && bp)) {
        sb.append(" - \"");
        final Formatter formatter = new Formatter(sb, Locale.US);
        if(!ap && !bp) {
          formatter.format(nc.getFormat(),
              selection[0].getInfo(), selection[1].getInfo());
          formatter.close();
          sel.setNineCut(nc);
        } else {
          formatter.format(nc.getPointPolyFormat(),
              selection[0].getInfo(), selection[1].getInfo());
          formatter.close();
          sb.append("\"");
        }
      }
    }
    sel.setText(sb.toString());
  }

  public synchronized GeoMarker[] getSelection() {
    final GeoMarker[] markers = new GeoMarker[numSelected];
    for(int i = 0; i < markers.length; ++i) {
      markers[i] = selection[i];
    }
    return markers;
  }

  public synchronized int getNumSelected() {
    return numSelected;
  }

  public synchronized void deselectAll() {
    for(int i = 0; i < numSelected; ++i) {
      selection[i].setSelected(false);
    }
    numSelected = 0;
    onSelection();
  }

}
