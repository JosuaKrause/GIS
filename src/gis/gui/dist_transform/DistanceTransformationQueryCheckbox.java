package gis.gui.dist_transform;

import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.GisPanel;
import gis.gui.IImagePainter;
import gis.gui.QueryCheckBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DistanceTransformationQueryCheckbox extends QueryCheckBox {

  final IImagePainter imagePainter;

  public DistanceTransformationQueryCheckbox(final GisPanel gisPanel) {
    super(gisPanel, new Query<Double>(
        "select gid, name, geom from buildings where type = 'museum';", Table.BUILDINGS,
        "Museum Distances", false) {
      // nothing here, yet
    });
    imagePainter = new DistanceTransformationPainter(gisPanel, getQuery());
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
}
