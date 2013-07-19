package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.GisPanel;
import gis.gui.ImagePainter;
import gis.gui.QueryCheckBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class WiFiQueryCheckbox extends QueryCheckBox {

  private static final long serialVersionUID = -8643774480736311524L;

  final ImagePainter imagePainter;

  public WiFiQueryCheckbox(final GisPanel gisPanel) {
    super(
        gisPanel,
        new Query(
            "select b.gid, h.name, b.geom from buildings as b, hotspots as h where b.gid = h.gid;",
            Table.BUILDINGS,
            "WiFi", null, false) {

          @Override
          protected void finishLoading(final List<GeoMarker> ms) {
            // nothing here
          }

        });
    imagePainter = new HeatMapPainter(getQuery(), new DistanceTransformationCombiner());// TODO
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        gisPanel.setImagePainter(isSelected() ? imagePainter : null);
      }

    });

  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    // nothing to do
  }

}
