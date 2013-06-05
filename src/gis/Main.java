package gis;

import gis.gui.GisFrame;

/**
 * Starts the main application.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public class Main {

  /* Default config.txt */
  // hostname=134.34.225.25
  // port=5432
  // dbname=joschi_gis_db
  // user=postgres
  // password=admin

  /**
   * Starts the main application.
   * 
   * @param args No arguments are processed.
   */
  public static void main(final String[] args) {
    GisFrame.getInstance().setVisible(true);
  }

}
