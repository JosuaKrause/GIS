package gis.gui.color_map;

import gis.gui.GisPanel;
import gis.gui.overlay.AbstractOverlayComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class HeatMapOverlayComponent extends AbstractOverlayComponent {

  private static final int WIDTH = 16;
  private static final int HEIGHT = 100;

  private final HeatMap heatMap;

  public HeatMapOverlayComponent(final GisPanel gisPanel,
      final int horizontalAlignmentWeight, final HeatMap heatMap) {
    super(gisPanel, new Dimension(WIDTH, HEIGHT), horizontalAlignmentWeight);
    this.heatMap = heatMap;
  }

  @Override
  public void paint(final Graphics2D g) {
    g.setStroke(new BasicStroke(1));
    // outline
    g.setColor(Color.BLACK);
    g.drawLine(position.x, position.y, position.x + WIDTH - 1, position.y);
    g.drawLine(position.x, position.y + HEIGHT - 1, position.x + WIDTH - 1, position.y
        + HEIGHT - 1);
    g.drawLine(position.x, position.y, position.x, position.y + HEIGHT - 1);
    g.drawLine(position.x + WIDTH - 1, position.y,
        position.x + WIDTH - 1, position.y + HEIGHT - 1);
    // heatmap
    final int left = position.x + 1;
    final int right = position.x + WIDTH - 2;
    final int numLines = HEIGHT - 2;
    for(int line = 0; line < numLines; ++line) {
      final double intensity = line / (double) numLines;
      g.setColor(heatMap.intensityToColor(intensity));
      final int y = position.y + numLines - line;
      g.drawLine(left, y, right, y);
    }
  }
}
