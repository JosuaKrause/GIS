package gis.tiles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.TileJob;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoaderListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;

/**
 * Defines the content of tiles by itself. It is possible to draw upon another
 * tile loader. The images of the underlying tile loader are not being
 * overwritten. This loader does not provide caching by itself and using
 * {@link org.openstreetmap.gui.jmapviewer.OsmFileCacheTileLoader} may lead to
 * unexpected behavior.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public abstract class ImageTileLoader implements TileLoader {
  /** The listener to be notified when finished loading. */
  private final TileLoaderListener listener;
  /** The optional parent loader. */
  private final TileLoader parent;

  /**
   * Creates a custom image tile loader.
   * 
   * @param listener The listener that is notified when the tile has been
   *          loaded.
   * @param parent The optional parent tile loader on which the image is drawn.
   *          May be <code>null</code>.
   */
  public ImageTileLoader(final TileLoaderListener listener, final TileLoader parent) {
    this.listener = Objects.requireNonNull(listener);
    this.parent = parent;
  }

  @Override
  public TileJob createTileLoaderJob(final Tile tile) {
    return new ImageTileJob(tile, parent);
  }

  /**
   * Creates an image for the given tile.
   * 
   * @param info Informations about the tile.
   * @return The image.
   * @throws IOException I/O Exception.
   */
  protected abstract BufferedImage createImageFor(TileInfo info) throws IOException;

  protected TileLoaderListener getListener() {
    return listener;
  }

  public TileLoader getParent() {
    return parent;
  }

  /**
   * Provides information about tiles.
   * 
   * @author Joschi <josua.krause@googlemail.com>
   */
  public static final class TileInfo {
    /** The tile. */
    private final Tile tile;
    /** The source. */
    private final TileSource source;

    /**
     * Creates a tile info.
     * 
     * @param tile The tile.
     */
    public TileInfo(final Tile tile) {
      this.tile = tile;
      source = tile.getSource();
    }

    public int getWidth() {
      return source.getTileSize();
    }

    public int getHeight() {
      return source.getTileSize();
    }

    public int tileX() {
      return tile.getXtile();
    }

    public int tileY() {
      return tile.getYtile();
    }

    public int zoom() {
      return tile.getZoom();
    }

    /**
     * Getter.
     * 
     * @return Creates a newly allocated image with the bounds of the tile. The
     *         image is initially transparent.
     */
    public BufferedImage createImage() {
      final int size = source.getTileSize();
      return new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Getter.
     * 
     * @param lon The longitude.
     * @return The x coordinate of the longitude in tile image coordinates.
     */
    public double getXForLon(final double lon) {
      return source.lonToTileX(lon, tile.getZoom()) - tile.getXtile();
    }

    /**
     * Getter.
     * 
     * @param lat The latitude.
     * @return The y coordinate of the latitude in tile image coordinates.
     */
    public double getYForLat(final double lat) {
      return source.latToTileY(lat, tile.getZoom()) - tile.getYtile();
    }

    /**
     * Getter.
     * 
     * @param x The x position in tile image coordinates.
     * @return The corresponding longitude.
     */
    public double getLonForX(final int x) {
      return source.tileXToLon(tile.getXtile() + x, tile.getZoom());
    }

    /**
     * Getter.
     * 
     * @param y The y position in tile image coordinates.
     * @return The corresponding latitude.
     */
    public double getLatForY(final int y) {
      return source.tileYToLat(tile.getYtile() + y, tile.getZoom());
    }

    public void prepareTile(final TileLoader parent) {
      if(parent == null) return;
      tile.initLoading();
      final TileJob job = parent.createTileLoaderJob(tile);
      job.run();
    }

    public void setImage(final BufferedImage img,
        final TileLoaderListener listener, final TileLoader parent) {
      if(img == null) {
        tile.setError("image is null");
        listener.tileLoadingFinished(tile, false);
      } else {
        if(parent == null) {
          tile.setImage(img);
        } else {
          // image of tile is set
          final BufferedImage tileImg = tile.getImage();
          final BufferedImage real = new BufferedImage(tileImg.getWidth(),
              tileImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
          final Graphics g = real.getGraphics();
          g.drawImage(tileImg, 0, 0, null);
          g.drawImage(img, 0, 0, null);
          g.dispose();
          tile.setImage(real);
        }
        listener.tileLoadingFinished(tile, true);
      }
    }

  } // TileInfo

  /**
   * The tile job responsible for loading the custom tiles.
   * 
   * @author Joschi <josua.krause@googlemail.com>
   */
  private class ImageTileJob implements TileJob {
    /** The parent tile loader if any. */
    private final TileLoader p;
    /** The tile to load. */
    private final Tile tile;

    /**
     * Creates a tile job.
     * 
     * @param tile The tile.
     * @param parent The parent if any.
     */
    public ImageTileJob(final Tile tile, final TileLoader parent) {
      this.tile = Objects.requireNonNull(tile);
      p = parent;
    }

    @Override
    public void run() {
      if(p == null) {
        synchronized(tile) {
          if((tile.isLoaded() && !tile.hasError()) || tile.isLoading()) return;
          tile.initLoading();
        }
      } else {
        if((tile.isLoaded() && !tile.hasError()) || tile.isLoading()) return;
        final TileJob job = p.createTileLoaderJob(tile);
        job.run();
        if(!tile.isLoaded()) return;
      }
      try {
        final TileInfo info = new TileInfo(tile);
        final BufferedImage img = createImageFor(info);
        info.setImage(img, getListener(), p);
      } catch(final IOException e) {
        tile.setError(e.getMessage());
        getListener().tileLoadingFinished(tile, false);
      } finally {
        synchronized(tile) {
          tile.finishLoading();
        }
      }
    }

    @Override
    public Tile getTile() {
      return tile;
    }

  } // ImageTileJob

}
