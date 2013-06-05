package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.BrandenburgQuery;
import gis.data.db.BrandenburgTorQuery;
import gis.data.db.FlickrChloroplethQuery;
import gis.data.db.Query;
import gis.gui.color_map.HeatMap;

import java.awt.Color;
import java.awt.Component;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class GisControlPanel extends JPanel {

  private final List<QueryCheckBox> checkBoxes = new ArrayList<>();
  private final GisPanel gisPanel;
  private final SelectionManager selectionManager;

  public GisControlPanel(final GisPanel gisPanel) {
    selectionManager = new SelectionManager();
    this.gisPanel = Objects.requireNonNull(gisPanel);
    addQuery(new BrandenburgQuery(1000, "brandenburg"));
    addQuery(new BrandenburgTorQuery("tor"));
    addQuery(new FlickrChloroplethQuery("flickr_admin"));
    addQuery(new Query<Double>(
        "select distinct b.gid as gid,  b.geom as geom, " +
            "b.name as name from berlin_administrative as a, " +
            "buildings as b where b.type = 'commercial' and " +
            "st_intersects(a.geom, b.geom) and " +
            "st_area(st_intersection(a.geom, b.geom), true) < " +
            "0.99 * st_area(b.geom, true)",
        Table.BUILDINGS, "border buildings") {

      @Override
      protected Double getFlavour(final ResultSet r) throws SQLException {
        return (double) r.getInt("gid");
      }

      @Override
      protected void addFlavour(final GeoMarker m, final Double f) {
        m.setColor(Table.convert(Color.RED));
      }

    });
    addQuery(new Query<Object>(
        "select distinct b.gid as gid,  b.geom as geom, b.name as name " +
            "from berlin_administrative as a, buildings as b " +
            "where b.type = 'commercial'",
        Table.BUILDINGS, "commercial") {

      @Override
      protected void addFlavour(final GeoMarker m, final Object o) {
        m.setColor(Table.convert(Color.CYAN));
      }

    });
    addQuery(new Query<Double>(
        "select a.gid as gid, lor as lor," +
            "(select b_area / a_area) as ratio, geom " +
            "from berlin_administrative as a left outer join " +
            "( select a.gid, st_area(a.geom, true) as a_area, " +
            "sum(st_area(st_intersection(a.geom, b.geom), true)) as b_area " +
            "from berlin_administrative as a, buildings as b " +
            "where b.type = 'commercial' and st_intersects(a.geom, b.geom) " +
            "group by a.gid ) as b " +
            "on a.gid = b.gid " +
            "order by gid;",
        Table.BERLIN_ADMINISTRATIVE, "commercial ratio") {

      private double maxRatio = Double.NEGATIVE_INFINITY;

      private HeatMap heatMap;

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
      }

    });
    for(final Table t : Table.values()) {
      addTableSelectionCheckBox(gisPanel, t);
    }
    setSize(getMinimumSize());
    addGisPanelListeners(gisPanel);
  }

  private void addQuery(final Query<?> query) {
    add(new QueryCheckBox(query.getName(), gisPanel, query));
  }

  private void addTableSelectionCheckBox(final GisPanel gisPanel, final Table table) {
    final QueryCheckBox box = QueryCheckBox.createTableQuery(gisPanel, table);
    add(box);
  }

  public Component add(final QueryCheckBox box) {
    checkBoxes.add(box);
    return add((JComponent) box);
  }

  private void addGisPanelListeners(final GisPanel gisPanel) {
    gisPanel.addMouseListener(new MouseSelectionListener(gisPanel, this));
  }

  private List<Query<?>> getSelectedQueries() {
    final List<Query<?>> queries = new ArrayList<>();
    for(final QueryCheckBox box : checkBoxes) {
      if(box.isSelected()) {
        queries.add(box.getQuery());
      }
    }
    return queries;
  }

  public boolean processSelectionClick(final Point2D pos) {
    final List<GeoMarker> markers = new ArrayList<>();
    final GisPanel panel = gisPanel;
    panel.pick(pos, markers);
    if(markers.isEmpty()) return false;
    if(markers.size() == 1) return selectionManager.clickedOn(markers.get(0));
    final JPopupMenu menu = new JPopupMenu();
    menu.addPopupMenuListener(new PopupMenuListener() {

      @Override
      public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
        // do nothing
      }

      @Override
      public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
        panel.repaint();
      }

      @Override
      public void popupMenuCanceled(final PopupMenuEvent e) {
        // do nothing
      }

    });
    for(final GeoMarker m : markers) {
      menu.add(new TableSelectionMenuItem(m, selectionManager, panel));
    }
    menu.show(panel, (int) pos.getX(), (int) pos.getY());
    return true;
  }

}
