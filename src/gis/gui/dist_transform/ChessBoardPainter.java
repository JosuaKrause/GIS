package gis.gui.dist_transform;


import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ChessBoardPainter implements IImagePainter {

  @Override
  public void paint(final BufferedImage image) {
    final int alpha = 32;
    final int tileSize = 8;
    final int whiteRgb = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
    final int blackRgb = (alpha << 24);
    for(int y = 0; y < image.getHeight(); ++y) {
      for(int x = 0; x < image.getWidth(); ++x) {
        if((x / tileSize + y / tileSize) % 2 == 0) {
          image.setRGB(x, y, whiteRgb);
        } else {
          image.setRGB(x, y, blackRgb);
        }
      }
    }
  }

  @Override
  public void paint(final Graphics2D g) {
    // TODO Auto-generated method stub

  }

}
