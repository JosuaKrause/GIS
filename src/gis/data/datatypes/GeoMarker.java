package gis.data.datatypes;

import gis.gui.GisPanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.Objects;

/**
 * A geo marker holds an element reference.
 * 
 * @author Andreas Ergenzinger <andreas.ergenzinger@gmx.de>
 * @author Joschi <josua.krause@gmail.com>
 */
public abstract class GeoMarker {
  /** The reference. */
  private final ElementId id;
  /** The element info. */
  private final String info;
  /** The color. */
  private Color color = new Color(239, 138, 98, 255 / 3);
  /** Whether the geo marker is currently selected. */
  private boolean selected;

  /**
   * Creates a geo marker.
   * 
   * @param info The element info.
   * @param id The reference id.
   */
  public GeoMarker(final String info, final ElementId id) {
    this.id = Objects.requireNonNull(id);
    selected = false;
    this.info = Objects.requireNonNull(info);
  }

  /**
   * Getter.
   * 
   * @return The element info.
   */
  public String getInfo() {
    return info;
  }

  /**
   * Getter.
   * 
   * @return The reference id to reference the element in an SQL query.
   */
  public ElementId getId() {
    return id;
  }

  /**
   * Getter.
   * 
   * @return Whether this geo marker is selected.
   */
  public boolean isSelected() {
    return selected;
  }

  /**
   * Setter.
   * 
   * @param selected Whether this geo marker is selected.
   */
  public void setSelected(final boolean selected) {
    this.selected = selected;
  }

  /**
   * Getter.
   * 
   * @return The world coordinate bounding box.
   */
  public abstract Rectangle2D getLatLonBBox();

  /**
   * Transforms a rectangle in longitude, latitude representation to paint
   * coordinates.
   * 
   * @param panel The panel for coordinate transformation.
   * @param box The rectangle to convert.
   * @return The converted rectangle.
   */
  protected Rectangle2D transformRect(final GisPanel panel, final RectangularShape box) {
    final Point2D tl = panel.getMapPosition(box.getMinY(), box.getMinX(), false);
    final Point2D br = panel.getMapPosition(box.getMaxY(), box.getMaxX(), false);
    final double minX = Math.min(tl.getX(), br.getX());
    final double maxX = Math.max(tl.getX(), br.getX());
    final double minY = Math.min(tl.getY(), br.getY());
    final double maxY = Math.max(tl.getY(), br.getY());
    return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
  }

  /**
   * Paints the element.
   * 
   * @param g The graphics context.
   * @param panel The panel to convert coordinates.
   * @param simple Whether the simple representation should be used.
   */
  public abstract void paint(Graphics2D g, GisPanel panel, boolean simple);

  /**
   * Paints a simple representation of the geo marker. Note that graphics object
   * must be set accordingly first.
   * 
   * @param g The graphics context.
   * @param panel The panel for coordinate transformation.
   */
  protected void paintSimple(final Graphics2D g, final GisPanel panel) {
    final Rectangle2D r = transformRect(panel, getLatLonBBox());
    g.fill(r);
    g.draw(r);
  }

  /**
   * Checks whether the polygon is in the current viewport. Note that this
   * method may return weird results when applied near the International Date
   * Line. But since our data points lie nowhere near the International Date
   * Line this is no problem.
   * 
   * @param latLonViewport The viewport of the map viewer.
   * @return Whether the marker must be drawn.
   */
  public boolean inViewport(final Rectangle2D latLonViewport) {
    return latLonViewport.intersects(getLatLonBBox());
  }

  /**
   * Setter.
   * 
   * @param radius Sets the radius of the element.
   */
  public abstract void setRadius(final double radius);

  /**
   * Setter.
   * 
   * @param color Sets the color of the element.
   */
  public void setColor(final Color color) {
    this.color = color;
  }

  /**
   * Getter.
   * 
   * @return The current color.
   */
  public Color getColor() {
    return color;
  }

}
