package gis.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A simple custom tile loader to show its use.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class SimpleTileLoader extends ImageTileLoader<SimpleTileLoader> {

  public SimpleTileLoader(final ResetableTileListener listener) {
    super(listener);
  }

  @Override
  protected BufferedImage createImageFor(final TileInfo<SimpleTileLoader> tile)
      throws IOException {
    // create image with size of tile
    final BufferedImage img = tile.createImage();
    final Graphics2D g = (Graphics2D) img.getGraphics();
    // convert latitude and longitude of top left position of the tile
    final String s = String.format("Lon: %.2f Lat: %.2f",
        tile.getLonForX(0), tile.getLatForY(0));
    g.setColor(Color.BLACK);
    final int h = g.getFontMetrics().getHeight();
    g.drawString(s, 5 - 1, h + 5);
    g.drawString(s, 5, h + 5 - 1);
    g.drawString(s, 5 + 1, h + 5);
    g.drawString(s, 5, h + 5 + 1);
    g.setColor(Color.WHITE);
    g.drawString(s, 5, h + 5);
    // draw border around tile
    final Rectangle2D r = new Rectangle2D.Double(0, 0, img.getWidth(), img.getHeight());
    g.setColor(Color.RED);
    g.draw(r);
    g.setColor(Color.BLACK);
    g.fill(new Rectangle2D.Double(0, 0, 1, 1));
    g.dispose();
    return img;
  }

}
