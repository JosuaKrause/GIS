package gis.gui;

import gis.data.db.FlickrChoroplethQuery;
import gis.gui.color_map.ColorMap;
import gis.gui.overlay.Overlay;

import java.util.Objects;

public class FlickrChoroplethQueryCheckbox extends QueryCheckBox {

  private static final long serialVersionUID = -6635608292308665608L;
  private final FlickrChoroplethQuery q;

  public FlickrChoroplethQueryCheckbox(final GisPanel gisPanel) {
    this(gisPanel, new FlickrChoroplethQuery("Flickr Ratio"));
  }

  public FlickrChoroplethQueryCheckbox(
      final GisPanel gisPanel, final FlickrChoroplethQuery q) {
    super(Objects.requireNonNull(gisPanel), q);
    this.q = q;
  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    final ColorMap colorMap = q.getColorCode();
    if(colorMap == null) return;
    if(colorMap.getColorMapOverlayComponent() == null) {
      colorMap.initOverlayComponent(gisPanel);
    }
    final Overlay hmoc = colorMap.getColorMapOverlayComponent();
    hmoc.setVisible(isSelected());
  }

}
