package gis.gui;

import gis.data.db.Query;
import gis.tiles.ImageTileLoader;

import java.util.Objects;

public class TileLoaderCheckBox extends QueryCheckBox {

  private static final long serialVersionUID = -2332610101951652353L;

  private final ImageTileLoader loader;

  public TileLoaderCheckBox(final GisPanel gisPanel, final Query q,
      final ImageTileLoader loader) {
    super(gisPanel, q);
    this.loader = Objects.requireNonNull(loader);
    loader.setParent(gisPanel.getTileController().getTileLoader());
    gisPanel.setTileLoader(loader);
  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    loader.setActive(isSelected());
  }

}
