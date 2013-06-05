package gis.gui;

import gis.data.datatypes.ElementId;
import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.Database;
import gis.data.db.Query;

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
    add(new QueryCheckBox("brandenburg", gisPanel,
        new Query<Double>("SELECT gid, geom, 1 as distance, "
            + Table.BERLIN_POI.infoColumnName + " as info FROM " + Table.BERLIN_POI
            + " LIMIT 100;", Table.BERLIN_POI) {

          @Override
          protected Double getFlavour(final ResultSet r) throws SQLException {
            return r.getDouble("distance");
          }

          @Override
          protected void addFlavour(final GeoMarker m, final Double f) {
            m.setRadius(f);
          }

        }));

    // for(final Table t : Table.values()) {
    // addTableSelectionCheckBox(gisPanel, t);
    // }

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
    add(new CommercialRatioQueryCheckbox(gisPanel));
    add(new ParksNearWaterQueryCheckBox(gisPanel));

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
    if(tables.size() > 0) {
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
      panel.repaint();
    }
    return false;
  }

}
