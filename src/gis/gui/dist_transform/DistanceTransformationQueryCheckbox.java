package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.GisPanel;
import gis.gui.ImagePainter;
import gis.gui.QueryCheckBox;

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
        // nothing here
      }

    });
    imagePainter = new ErgisDistanceTransformationPainter(getQuery(),
        DistanceTransformationCombiner.DISTANCE, this);
    addActionListener(imagePainter.createActionListener(gisPanel));
  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    // nothing to do
  }

}
