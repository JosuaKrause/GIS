package gis.gui;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.BrandenburgQuery;
import gis.data.db.BrandenburgTorQuery;
import gis.data.db.Database;
import gis.data.db.Query;
import gis.gui.color_map.HeatMap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
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

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class GisControlPanel extends JPanel {

  private final List<QueryCheckBox> checkBoxes = new ArrayList<>();
  private final GisPanel gisPanel;
  private final SelectionManager selectionManager;

  public GisControlPanel(final GisPanel gisPanel) {
    selectionManager = new SelectionManager();
    this.gisPanel = Objects.requireNonNull(gisPanel);
    add(new QueryCheckBox("brandenburg", gisPanel, new BrandenburgQuery(1000)));
    add(new QueryCheckBox("tor", gisPanel, new BrandenburgTorQuery()));
    for(final Table t : Table.values()) {
      addTableSelectionCheckBox(gisPanel, t);
    }

    add(new QueryCheckBox(
        "border buildings",
        gisPanel,
        new Query<Double>(
            "select distinct b.gid as gid,  b.geom as geom, b.name as info "
                +
                "from berlin_administrative as a, buildings as b "
                +
                "where b.type = 'commercial' and st_intersects(a.geom, b.geom) and "
                +
                "st_area(st_intersection(a.geom, b.geom), true) < 0.99 * st_area(b.geom, true)",
            Table.BUILDINGS) {

          @Override
          protected Double getFlavour(final ResultSet r) throws SQLException {
            return (double) r.getInt("gid");
          }

          @Override
          protected void addFlavour(final GeoMarker m, final Double f) {
            m.setColor(Color.RED);
          }

        }));
    add(new QueryCheckBox(
        "commercial ratio",
        gisPanel,
        new Query<Double>(
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
            Table.BERLIN_ADMINISTRATIVE) {

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

        }));
    setSize(getMinimumSize());
    addGisPanelListeners(gisPanel);
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

  private List<Table> getSelectedTables() {
    final List<Table> tables = new ArrayList<>();
    for(final QueryCheckBox box : checkBoxes) {
      if(box.isSelected()) {
        tables.add(box.getTable());
      }
    }
    return tables;
  }

  public boolean processSelectionClick(final Point p, final Coordinate c) {
    final List<Table> tables = getSelectedTables();
    final GisPanel panel = gisPanel;
    if(tables.isEmpty()) return false;
    final List<ElementId> ids = Database.getInstance().getByCoordinate(
        c, getSelectedTables(), panel.getMeterPerPixel() * 5);
    final List<GeoMarker> markers = new ArrayList<>();
    for(final ElementId id : ids) {
      final GeoMarker m = panel.getGeoMarker(id);
      if(m != null) {
        markers.add(m);
      }
    }
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
    menu.show(panel, p.x, p.y);
    return true;
  }

}
