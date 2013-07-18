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

public class DistanceTransformationQueryCheckbox extends QueryCheckBox {

  private static final long serialVersionUID = -8643774480736311524L;

  final ImagePainter imagePainter;

  public DistanceTransformationQueryCheckbox(final GisPanel gisPanel) {
    super(gisPanel, new Query(
        "select gid, name, geom from buildings where type = 'museum';", Table.BUILDINGS,
        "Museum Distances", null, false) {

      @Override
      protected void finishLoading(final List<GeoMarker> ms) {
        // nothing here, yet
      }

    });
    // imagePainter = new DistanceTransformationPainter(gisPanel,
    // getQuery());//TODO
    imagePainter = new ErgisDistanceTransformationPainter(getQuery(), Combiner.DISTANCE);// TODO
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        if(isSelected()) {
          gisPanel.setImagePainter(imagePainter);
        } else {
          gisPanel.setImagePainter(null);
        }
      }

    });

  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    // nothing to do
  }

}
