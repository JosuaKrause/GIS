package gis.gui;

import gis.gui.dist_transform.ViewInfo;

import java.awt.Graphics2D;

public interface ImagePainter {

  public void paint(Graphics2D g, ViewInfo info);

}
