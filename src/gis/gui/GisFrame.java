package gis.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GisFrame extends JFrame {

  public GisFrame() {
    // initialize
    super("GIS Viewer");
    final GisPanel gisPanel = new GisPanel();
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
  }
}
