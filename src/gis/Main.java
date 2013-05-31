package gis;

import gis.gui.GisFrame;

import javax.swing.JFrame;

/**
 * Starts the main application.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class Main {

  /**
   * Starts the main application.
   * 
   * @param args No arguments are processed.
   */
  public static void main(final String[] args) {
    final JFrame frame = new GisFrame();
    frame.setVisible(true);
  }

}
