package gis.gui;

import gis.data.datatypes.GeoMarker;

public class SelectionManager {

  private final GeoMarker[] selection = new GeoMarker[2];
  private int numSelected = 0;

  // return true if repaint required
  public synchronized boolean clickedOn(final GeoMarker marker) {
    switch(numSelected) {
      case 2:
        final int index = isSelected(marker);
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
    }
    return false;
  }

  /**
   * @param m
   * @return the marker's index in {@link selection} or -1.
   */
  private int isSelected(final GeoMarker m) {
    for(int i = 0; i < numSelected; ++i) {
      if(selection[i].equals(m)) return i;
    }
    return -1;
  }

  private void deselect(final int index) {
    selection[index].selected = false;
    final GeoMarker m = selection[index];
    if(index == 0 && numSelected == 2) {
      selection[0] = selection[1];
    }
    --numSelected;
    System.out.println("deselect " + m.getId());
  }

  private void select(final GeoMarker m) {
    m.selected = true;
    selection[numSelected] = m;
    ++numSelected;
    System.out.println("select " + m.getId());
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

}
