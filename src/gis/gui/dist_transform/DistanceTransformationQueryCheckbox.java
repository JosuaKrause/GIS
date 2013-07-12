package gis.gui.dist_transform;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.GisPanel;
import gis.gui.TileLoaderCheckBox;
import gis.tiles.PainterTileLoader;

import java.util.List;

public class DistanceTransformationQueryCheckbox extends TileLoaderCheckBox {

  private static final long serialVersionUID = -7175701018571538127L;

  public DistanceTransformationQueryCheckbox(final GisPanel gisPanel) {
    this(gisPanel, new Query(
        "select gid, name, geom from buildings where type = 'museum';", Table.BUILDINGS,
        "Museum Distances", null, false) {

      @Override
      protected void finishLoading(final List<GeoMarker> ms) {
        // nothing here, yet
      }

    });
  }

  private DistanceTransformationQueryCheckbox(final GisPanel gisPanel, final Query q) {
    super(gisPanel, q, new PainterTileLoader(gisPanel,
        new ErgisDistanceTransformationPainter(gisPanel, q)));
  }

}
