package gis.tiles;

import gis.tiles.ImageTileLoader.TileInfo;

import java.awt.image.BufferedImage;

public interface TilePainter {

  void paintTile(BufferedImage img, TileInfo<?> info);

}
