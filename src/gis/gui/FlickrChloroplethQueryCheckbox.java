package gis.gui;

import gis.data.db.FlickrChloroplethQuery;
import gis.gui.color_map.ColorMap;
import gis.gui.overlay.IOverlayComponent;

import java.util.Objects;

public class FlickrChloroplethQueryCheckbox extends QueryCheckBox {

  private static final long serialVersionUID = -6635608292308665608L;
  private final FlickrChloroplethQuery q;

  public FlickrChloroplethQueryCheckbox(final GisPanel gisPanel) {
    this(gisPanel, new FlickrChloroplethQuery("Flickr Ratio"));
  }

  public FlickrChloroplethQueryCheckbox(
      final GisPanel gisPanel, final FlickrChloroplethQuery q) {
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
    final IOverlayComponent hmoc = colorMap.getColorMapOverlayComponent();
    hmoc.setVisible(isSelected());
  }

}
