package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.color_map.ColorMap;
import gis.gui.color_map.IIntensityMapping;
import gis.gui.color_map.IntervalIntensityMapping;
import gis.gui.overlay.IOverlayComponent;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class CommercialRatioQueryCheckbox extends QueryCheckBox {

  public CommercialRatioQueryCheckbox(final GisPanel gisPanel) {
    super(Objects.requireNonNull(gisPanel), new CommercialRatioQuery());
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

        final ColorMap colorMap = ((CommercialRatioQuery) q).getColorMap();
        if(colorMap.getColorMapOverlayComponent() == null) {
          colorMap.initOverlayComponent(gisPanel);
        }
        final IOverlayComponent hmoc = colorMap.getColorMapOverlayComponent();
        if(isSelected()) {
          hmoc.setVisible(true);
        } else {
          hmoc.setVisible(false);
        }
        gisPanel.repaint();
      }

    });
  }

  public static class CommercialRatioQuery extends Query<Double> {

    private ColorMap colorMap;
    private double maxRatio = Double.NEGATIVE_INFINITY;

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
          Table.BERLIN_ADMINISTRATIVE, "Commercial Ratio");
    }

    public ColorMap getColorMap() {
      return colorMap;
    }

    @Override
    protected Double getFlavour(final ResultSet r) throws SQLException {
      Double ratio = r.getDouble("ratio");
      if(ratio == null) {
        ratio = 0.0;
      }
      if(ratio > maxRatio) {
        maxRatio = ratio;
      }
      return ratio;
    }

    @Override
    protected void addFlavour(final GeoMarker m, final Double f) {
      if(maxRatio > 0) {
        final IIntensityMapping intensityMapping =
            new IntervalIntensityMapping(0, 0, maxRatio, 1);
        colorMap = new ColorMap(intensityMapping, new Color[] {
            new Color(240, 59, 32), new Color(254, 178, 76), new Color(255, 237, 160)},
            new double[] { 0, 0.5, 1}) {

          @Override
          public String formatValue(final double value) {
            return String.format("%.3f\u2030", value * 1000);
          }

        };
        maxRatio = Double.NEGATIVE_INFINITY;
      }
      m.setColor(colorMap.getColor(f));
      m.setAlphaSelected(0.6f);
      m.setAlphaNotSelected(0.9f);
      m.setOutlineColor(Color.BLACK);
    }

  } // CommercialRatioQuery

}
