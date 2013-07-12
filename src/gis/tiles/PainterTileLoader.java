package gis.tiles;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class PainterTileLoader extends ImageTileLoader {

  private final TilePainter painter;

  public PainterTileLoader(final ResetableTileListener listener, final TilePainter painter) {
    super(listener);
    this.painter = Objects.requireNonNull(painter);
  }

  @Override
  protected BufferedImage createImageFor(final TileInfo info) throws IOException {
    final BufferedImage img = info.createImage();
    painter.paintTile(img, info);
    return img;
  }

}
