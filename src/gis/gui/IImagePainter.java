package gis.gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface IImagePainter {

  public void paint(BufferedImage image);

  public void paint(Graphics2D g);

}
