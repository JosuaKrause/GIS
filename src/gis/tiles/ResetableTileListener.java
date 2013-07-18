package gis.tiles;

import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

public interface ResetableTileListener extends TileLoaderListener {

  void clear();

}
