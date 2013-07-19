package gis.gui;

import gis.data.DedicatedLoader;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * The GIS frame shows the main application.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@googlemail.com>
 */
public class GisFrame extends JFrame {
  private static final long serialVersionUID = 8941331188193766017L;
  /** The instance. */
  private static final GisFrame INSTANCE = new GisFrame();

  /**
   * Getter.
   * 
   * @return The instance.
   */
  public static final GisFrame getInstance() {
    return INSTANCE;
  }

  private final GisPanel gisPanel;

  /** Creates the GIS frame. */
  private GisFrame() {
    // initialize
    super("GIS Viewer");
    gisPanel = new GisPanel();
    final GisControlPanel gisControlPanel = new GisControlPanel(gisPanel);

    // set layout
    setLayout(new GridBagLayout());

    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 10;
    gbc.weighty = 100;
    gbc.fill = GridBagConstraints.BOTH;
    gisPanel.setPreferredSize(new Dimension(800, 600));
    add(gisPanel, gbc);

    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add(gisControlPanel, gbc);

    // set normal frame size and maximize
    pack();
    // setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
    setLocationRelativeTo(null);
    // other stuff
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    gisPanel.setDisplayPositionByLatLon(52.5, 13.4, 10);
    gisPanel.grabFocus();
  }

  @Override
  public void dispose() {
    InfoFrame.getInstance().dispose();
    DedicatedLoader.shutdown();
    super.dispose();
  }

  public GisPanel getGisPanel() {
    return gisPanel;
  }
}
