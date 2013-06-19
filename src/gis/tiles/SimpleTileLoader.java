package gis.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;

/**
 * A simple custom tile loader to show its use.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class SimpleTileLoader extends ImageTileLoader {

  public SimpleTileLoader(final TileLoaderListener listener, final TileLoader parent) {
    super(listener, parent);
  }

  @Override
  protected BufferedImage createImageFor(final TileInfo tile) throws IOException {
    // create image with size of tile
    final BufferedImage img = tile.createImage();
    final Graphics2D g = (Graphics2D) img.getGraphics();
    // convert latitude and longitude of top left position of the tile
    final String s = String.format("Lat: %.2f Lon: %.2f",
        tile.getLatForY(0), tile.getLonForX(0));
    g.setColor(Color.BLACK);
    g.drawString(s, 5 - 1, img.getHeight() - 5);
    g.drawString(s, 5, img.getHeight() - 5 - 1);
    g.drawString(s, 5 + 1, img.getHeight() - 5);
    g.drawString(s, 5, img.getHeight() - 5 + 1);
    g.setColor(Color.WHITE);
    g.drawString(s, 5, img.getHeight() - 5);
    // draw border around tile
    final Rectangle2D r = new Rectangle2D.Double(0, 0, img.getWidth(), img.getHeight());
    g.setColor(Color.RED);
    g.draw(r);
    g.dispose();
    return img;
  }

}
