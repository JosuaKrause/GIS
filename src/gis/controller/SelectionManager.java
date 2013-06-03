package gis.controller;

import gis.data.datatypes.GeoMarker;

import java.util.Arrays;

/**
 * Handles selections.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class SelectionManager {
  /** An array holding two selected geo markers. */
  private final GeoMarker[] selection = new GeoMarker[2];
  /** The number of elements in the selection array. */
  private int numSelected = 0;

  /**
   * Handles selections.
   * 
   * @param marker The geo marker the user clicked on.
   * @return Whether a repaint is required.
   */
  public synchronized boolean clickedOn(final GeoMarker marker) {
    switch(numSelected) {
      case 2:
        final int index = isSelected(marker);
        if(index >= 0) {
          deselect(index);
          return true;
        }
        return false;
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
        return false;
    }
  }

  /**
   * Whether the given geo marker is selected.
   * 
   * @param m The geo marker.
   * @return the marker's index in {@link #selection} or -1.
   */
  private int isSelected(final GeoMarker m) {
    for(int i = 0; i < numSelected; ++i) {
      if(selection[i].equals(m)) return i;
    }
    return -1;
  }

  /**
   * Clears the selection of the given index.
   * 
   * @param index The index.
   */
  private void deselect(final int index) {
    selection[index].selected = false;
    if(index == 0 && numSelected == 2) {
      selection[0] = selection[1];
      selection[1] = null;
    }
    --numSelected;
  }

  /**
   * Selects a geo marker.
   * 
   * @param m The geo marker.
   */
  private void select(final GeoMarker m) {
    m.selected = true;
    selection[numSelected] = m;
    ++numSelected;
  }

  /**
   * Getter.
   * 
   * @return The current selection.
   */
  public synchronized GeoMarker[] getSelection() {
    return Arrays.copyOf(selection, numSelected);
  }

}
