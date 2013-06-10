package gis.gui;

import gis.data.datatypes.GeoMarker;
import gis.data.datatypes.Table;
import gis.data.db.BrandenburgQuery;
import gis.data.db.BrandenburgTorQuery;
import gis.data.db.Query;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class GisControlPanel extends JPanel {

  private static final long serialVersionUID = -54745563278130023L;

  private final List<QueryCheckBox> checkBoxes = new ArrayList<>();
  private final GisPanel gisPanel;
  private final SelectionManager selectionManager;

  public GisControlPanel(final GisPanel gisPanel) {
    selectionManager = new SelectionManager();
    setFocusable(true);
    final SelectionManagerOverlayComponent smoc =
        new SelectionManagerOverlayComponent(gisPanel);
    selectionManager.setSelector(smoc);
    gisPanel.registerOverlayComponent(smoc);
    smoc.setVisible(true);
    this.gisPanel = Objects.requireNonNull(gisPanel);
    addQuery(new BrandenburgQuery(1000, "Brandenburger"));
    addQuery(new BrandenburgTorQuery("Tor"));
    add(new FlickrChloroplethQueryCheckbox(gisPanel));
    addTableSelectionCheckBox(gisPanel, Table.FLICKR, "All Flickr Photos");
    add(new CommercialRatioQueryCheckbox(gisPanel));
    addQuery(new Query<Object>(
        "select distinct b.gid as gid,  b.geom as geom, b.name as name " +
            "from berlin_administrative as a, buildings as b " +
            "where b.type = 'commercial'",
        Table.BUILDINGS, "All Commercial") {

      @Override
      protected void addFlavour(final GeoMarker m, final Object o) {
        m.setColor(new Color(55, 126, 184));
      }

    });
    add(new ParksNearWaterQueryCheckBox(gisPanel));
    addTableSelectionCheckBox(gisPanel, Table.BERLIN_WATER, "Water");
    addTableSelectionCheckBox(gisPanel, Table.BERLIN_POI, "All Points of Interest");
    addTableSelectionCheckBox(gisPanel, Table.BUILDINGS, "All Buildings");
    setSize(getMinimumSize());
    addForeignListeners(gisPanel);
  }

  private void addTableSelectionCheckBox(final GisPanel gisPanel, final Table table,
      final String name) {
    add(QueryCheckBox.createTableQuery(gisPanel, table, name));
  }

  private void addQuery(final Query<?> query) {
    add(new QueryCheckBox(gisPanel, query));
  }

  public Component add(final QueryCheckBox box) {
    checkBoxes.add(box);
    return add((JComponent) box);
  }

  private void addForeignListeners(final GisPanel gisPanel) {
    final SelectionManager sm = selectionManager;
    final MouseSelectionListener l = new MouseSelectionListener(gisPanel, this);
    gisPanel.addMouseListener(l);
    gisPanel.addMouseMotionListener(l);
    final Action d = new AbstractAction() {

      private static final long serialVersionUID = -8298142767434938419L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        sm.deselectAll();
      }

    };
    gisPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), d);
    gisPanel.getActionMap().put(d, d);
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), d);
    getActionMap().put(d, d);
    final Action q = new AbstractAction() {

      private static final long serialVersionUID = -8020987881253926797L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        GisFrame.getInstance().dispose();
      }

    };
    gisPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), q);
    gisPanel.getActionMap().put(q, q);
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), q);
    getActionMap().put(q, q);
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
