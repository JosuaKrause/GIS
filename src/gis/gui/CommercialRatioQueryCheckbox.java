package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.color_map.ColorMap;
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
          // to initialize heat map and and overlay component
          q.getResult();
        } else {
          gisPanel.removeQuery(q);
        }

        final ColorMap heatMap = ((CommercialRatioQuery) getQuery()).getHeapMap();
        if(heatMap.getHeatMapOverlayComponent() == null) {
          heatMap.initOverlayComponent(gisPanel);
        }
        final IOverlayComponent hmoc = heatMap.getHeatMapOverlayComponent();
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

    private ColorMap heatMap;
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
              "on a.gid = b.gid " +
              "order by gid;",
          Table.BERLIN_ADMINISTRATIVE, "commercial ratio");
    }

    public ColorMap getHeapMap() {
      return heatMap;
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
        heatMap = ColorMap.getColorMap(0, maxRatio);
        maxRatio = Double.NEGATIVE_INFINITY;
      }
      m.setColor(heatMap.getColor(f));
      m.setAlphaSelected(0.9f);
      m.setAlphaNotSelected(1.0f);
      m.setOutlineColor(Color.BLACK);
    }

  }
}
