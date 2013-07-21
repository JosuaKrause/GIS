package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Query;
import gis.gui.color_map.ColorMap;
import gis.gui.hsv_color_map.HsvColorMap;
import gis.gui.hsv_color_map.PubAtmColorMap;
import gis.gui.overlay.Overlay;

import java.awt.Color;
import java.util.List;
import java.util.Objects;

public class PubCrawlQueryCheckbox extends QueryCheckBox {

  private static final long serialVersionUID = -2974461807420712240L;
  private final PubCrawlQuery q;

  public PubCrawlQueryCheckbox(final GisPanel gisPanel) {
    this(gisPanel, new PubCrawlQuery());
  }

  private PubCrawlQueryCheckbox(
      final GisPanel gisPanel, final PubCrawlQuery q) {
    super(Objects.requireNonNull(gisPanel), q);
    this.q = q;
  }

  @Override
  public void onAction(final GisPanel gisPanel) {
    final Overlay overlay = q.getLegend();
    if(overlay != null) {
      // order matters
      overlay.setVisible(isSelected());
      gisPanel.registerOverlayComponent(overlay);
    }
  }

  public static class PubCrawlQuery extends Query {

    public PubCrawlQuery() {
      super(
          "select quarter as lor, a.quarter_gid as gid, quarter_geom as geom, avg_min_dist as avg_min_pub_pub_dist, avg_min_pub_atm_dist "
              +
              "from "
              +
              "  (select quarter, quarter_gid, quarter_geom, avg(min_dist) as avg_min_dist "
              +
              "  from "
              +
              "    (select quarter, quarter_gid, quarter_geom, a_gid, min(dist) as min_dist "
              +
              "    from "
              +
              "      (select a.gid as a_gid, b.gid as b_gid, c.lor as quarter, c.gid as quarter_gid, c.geom as quarter_geom, st_distance(a.geom, b.geom, true) as dist "
              +
              "      from berlin_poi as a, berlin_poi as b, berlin_administrative as c "
              +
              "      where a.gid != b.gid "
              +
              "      and a.category = 'Eating&Drinking' and a.name like 'Pub%'and st_contains(c.geom, a.geom) "
              +
              "      and b.category = 'Eating&Drinking' and b.name like 'Pub%'and st_contains(c.geom, b.geom)) as distances "
              +
              "    group by quarter, quarter_gid, quarter_geom, a_gid) as min_distances "
              +
              "  group by quarter, quarter_gid, quarter_geom) as a, "
              +
              "  (select quarter_gid, avg(min_dist) as avg_min_pub_atm_dist "
              +
              "  from "
              +
              "    (select quarter_gid, pub_gid, min(dist) as min_dist "
              +
              "    from "
              +
              "      (select c.gid as quarter_gid, a.gid as pub_gid, st_distance(a.geom, b.geom, true) as dist "
              +
              "      from berlin_poi as a, berlin_poi as b, berlin_administrative as c "
              +
              "      where a.category = 'Eating&Drinking' and a.name like 'Pub%'and st_contains(c.geom, a.geom) "
              +
              "      and b.name like 'ATM%' and st_contains(c.geom, b.geom)) as distances "
              +
              "    group by quarter_gid, pub_gid) as min_distances " +
              "  group by quarter_gid) as b " +
              "where a.quarter_gid = b.quarter_gid;",
          Table.BERLIN_ADMINISTRATIVE, "Pub Crawl", "avg_min_pub_pub_dist",
          "avg_min_pub_atm_dist");
    }

    private final HsvColorMap colorMap = new PubAtmColorMap();// TODO initialize

    @Override
    protected void finishLoading(final List<GeoMarker> ms) {
      // dim1 is avg. min. inter-pub distance, dim2 is avg. min pub-atm distance
      double dim1Min = Double.MAX_VALUE;
      double dim1Max = Double.MIN_VALUE;
      double dim2Min = Double.MAX_VALUE;
      double dim2Max = Double.MIN_VALUE;
      for(final GeoMarker m : ms) {
        final double v1 = m.getQueryValue();
        final double v2 = m.getQueryValue2();
        if(v1 < dim1Min) {
          dim1Min = v1;
        }
        if(v1 > dim1Max) {
          dim1Max = v1;
        }
        if(v2 < dim2Min) {
          dim2Min = v2;
        }
        if(v2 > dim2Max) {
          dim2Max = v2;
        }
      }
      System.out.println(dim1Min + " " + dim1Max + " " + dim2Min + " " + dim2Max);// TODO
      colorMap.setMapping(dim1Min, dim1Max, dim2Min, dim2Max);
      for(final GeoMarker m : ms) {
        m.setColor(colorMap.getColor(m.getQueryValue(), m.getQueryValue2()));
        m.setAlphaSelected(0.6f);
        m.setAlphaNotSelected(0.8f);
        m.setOutlineColor(Color.BLACK);
      }
    }

    public ColorMap getColorCode() {
      return null;
    }

    public Overlay getLegend() {
      return colorMap.getLegend();
    }

  }

}
