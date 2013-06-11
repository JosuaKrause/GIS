package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.color_map.ColorMap;
import gis.gui.color_map.IntervalIntensityMapping;
import gis.gui.overlay.Overlay;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

public class CommercialRatioQueryCheckbox extends QueryCheckBox {

  private static final long serialVersionUID = -2974461807420712240L;
  private final CommercialRatioQuery q;

  public CommercialRatioQueryCheckbox(final GisPanel gisPanel) {
    this(gisPanel, new CommercialRatioQuery());
  }

  private CommercialRatioQueryCheckbox(
      final GisPanel gisPanel, final CommercialRatioQuery q) {
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

  public static class CommercialRatioQuery extends Query {

    public CommercialRatioQuery() {
      super(
          "select a.gid as gid, lor, (select b_area / a_area) as ratio, geom "
              +
              "from berlin_administrative as a left outer join "
              +
              "( select a.gid, st_area(a.geom, true) as a_area, sum(st_area(st_intersection("
              +
              "a.geom, b.geom), true)) as b_area " +
              "from berlin_administrative as a, buildings as b " +
              "where b.type = 'commercial' and st_intersects(a.geom, b.geom) " +
              "group by a.gid ) as b " +
              "on a.gid = b.gid",
          Table.BERLIN_ADMINISTRATIVE, "Commercial Ratio", "ratio");
    }

    private final IntervalIntensityMapping intensityMapping =
        new IntervalIntensityMapping(0, 0, 1, 1);

    private final ColorMap colorCode = new ColorMap(intensityMapping, new Color[] {
        new Color(240, 59, 32), new Color(254, 178, 76), new Color(255, 237,
            160)},
        new double[] { 0, 0.5, 1}) {

      @Override
      public String formatValue(final double value) {
        return String.format("%.3f\u2030", value * 1000);
      }

    };

    @Override
    protected void finishLoading(final List<GeoMarker> ms) {
      double maxRatio = Double.NEGATIVE_INFINITY;
      for(final GeoMarker m : ms) {
        final double v = m.getQueryValue();
        if(v > maxRatio) {
          maxRatio = v;
        }
      }
      if(maxRatio > 0) {
        intensityMapping.setMapping(0, 0, maxRatio, 1);
      }
      for(final GeoMarker m : ms) {
        m.setColor(colorCode.getColor(m.getQueryValue()));
        m.setAlphaSelected(0.6f);
        m.setAlphaNotSelected(0.8f);
        m.setOutlineColor(Color.BLACK);
      }
    }

    public ColorMap getColorCode() {
      return colorCode;
    }

  } // CommercialRatioQuery

}
