package gis.gui;

import gis.data.db.FlickrChloroplethQuery;
import gis.data.db.Query;
import gis.gui.color_map.ColorMap;
import gis.gui.overlay.IOverlayComponent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class FlickrChloroplethQueryCheckbox extends QueryCheckBox {

  public FlickrChloroplethQueryCheckbox(final GisPanel gisPanel) {
    super(Objects.requireNonNull(gisPanel), new FlickrChloroplethQuery(
        "Flickr Ratio"));
    for(final ActionListener al : getActionListeners()) {
      removeActionListener(al);
    }
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final Query<?> q = getQuery();
        if(isSelected()) {
          gisPanel.addQuery(q);
          q.getResult();
        } else {
          gisPanel.removeQuery(q);
        }
        final ColorMap colorMap = ((FlickrChloroplethQuery) q).getColorCode();
        if(colorMap.getColorMapOverlayComponent() == null) {
          colorMap.initOverlayComponent(gisPanel);
        }
        final IOverlayComponent hmoc = colorMap.getColorMapOverlayComponent();
        hmoc.setVisible(isSelected());
        gisPanel.repaint();
      }

    });
  }

}
