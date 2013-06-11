package gis.gui.overlay;

import gis.gui.GisPanel;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class AbstractOverlayComponent implements Overlay {

  public static final int PADDING = 5;

  private final GisPanel gisPanel;
  private final Dimension dimension;
  private boolean visible;
  /**
   * Coordinates of the bounding box's top left corner.
   */
  protected Point position;
  protected int horizontalAlignmentWeight;

  public AbstractOverlayComponent(final GisPanel gisPanel, final Dimension dimension,
      final int horizontalAlignmentWeight) {
    this.gisPanel = gisPanel;
    this.dimension = dimension;
    this.horizontalAlignmentWeight = horizontalAlignmentWeight;
  }

  @Override
  public Dimension getDimension() {
    return dimension;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public void setVisible(final boolean visible) {
    if(this.visible != visible) {
      gisPanel.alignOverlayComponents();
      gisPanel.repaint();
    }
    this.visible = visible;
  }

  @Override
  public abstract void paint(final Graphics2D g);

  @Override
  public void setPosition(final Point position) {
    this.position = position;
  }

  @Override
  public int getHorizontalAlignmentWeight() {
    return horizontalAlignmentWeight;
  }

  public GisPanel getPanel() {
    return gisPanel;
  }

}
