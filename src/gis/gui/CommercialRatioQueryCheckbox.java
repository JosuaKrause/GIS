package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.color_map.HeatMap;
import gis.gui.overlay.IOverlayComponent;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CommercialRatioQueryCheckbox extends QueryCheckBox {

  public CommercialRatioQueryCheckbox(final GisPanel gisPanel) {
    super("commercial ratio", gisPanel, new CommercialRatioQuery(gisPanel));

    for(final ActionListener al : getActionListeners()) {
      removeActionListener(al);
    }

    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent arg0) {
        @SuppressWarnings("rawtypes")
        final Query q = getQuery();
        if(isSelected()) {
          gisPanel.addQuery(q);
          q.getResult();// to initialize heat map and and overlay component

        } else {
          gisPanel.removeQuery(q);
        }

        IOverlayComponent hmoc = null;
        final HeatMap heatMap = ((CommercialRatioQuery) getQuery()).getHeapMap();
        if(heatMap.getHeatMapOverlayComponent() == null) {
          heatMap.initOverlayComponent(gisPanel);
          hmoc = heatMap.getHeatMapOverlayComponent();
          gisPanel.registerOverlayComponent(hmoc);
        }
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

    private HeatMap heatMap;
    private double maxRatio = Double.NEGATIVE_INFINITY;

    public CommercialRatioQuery(final GisPanel gisPanel) {
      super(
          "select a.gid as gid, lor as info, (select b_area / a_area) as ratio, geom "
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
          Table.BERLIN_ADMINISTRATIVE);
    }

    public HeatMap getHeapMap() {
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
        heatMap = HeatMap.getHeatMap(0, maxRatio);
        maxRatio = Double.NEGATIVE_INFINITY;
      }
      m.setColor(heatMap.getColor(f));
      m.setAlphaSelected(0.9f);
      m.setAlphaNotSelected(1.0f);
      m.setOutlineColor(Color.BLACK);
    }

  }
}
