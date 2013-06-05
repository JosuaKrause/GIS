package gis.gui.overlay;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

public interface IOverlayComponent {

  public Dimension getDimension();

  public boolean isVisible();

  public void setVisible(boolean visible);

  public void paint(Graphics2D g);

  /**
   * Assigns the components top left bounding box position.
   * 
   * @param position top left corner of the bounding box.
   */
  public void setPosition(Point position);

  /**
   * A weight specifying a preference regarding the horizontal position of the
   * overlay component. All components will be rendered at the bottom of the
   * GisPanel. Overlay components with a negative (positive) weight will be
   * stacked starting from the left (right) border. The higher the absolute
   * value, the closer the respective border the component will be painted.
   * 
   * @return a weigth specifying a preference regarding the horizontal postion
   *         of the overlay component
   */
  public int getHorizontalAlignmentWeight();
}
