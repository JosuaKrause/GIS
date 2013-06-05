package gis.gui.color_map;

import gis.gui.GisPanel;
import gis.gui.overlay.AbstractOverlayComponent;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ColorMapOverlayComponent extends AbstractOverlayComponent {

  private static final int WIDTH = 16;
  private static final int BOTTOM = 5;
  private static final int HEIGHT = 100 + BOTTOM;

  private final ColorMap colorMap;

  public ColorMapOverlayComponent(final GisPanel gisPanel,
      final int horizontalAlignmentWeight, final ColorMap colorMap) {
    super(gisPanel, new Dimension(WIDTH, HEIGHT), horizontalAlignmentWeight);
    this.colorMap = colorMap;
  }

  @Override
  public void paint(final Graphics2D g) {
    g.setStroke(new BasicStroke(1));
    // outline
    g.setColor(Color.BLACK);
    g.drawRect(position.x, position.y, WIDTH, HEIGHT);
    // color mapping
    final int left = position.x + 1;
    final int right = position.x + WIDTH - 1;
    final int numLines = HEIGHT - 1 - BOTTOM;
    for(int line = 0; line < numLines + BOTTOM; ++line) {
      final double intensity = (line - BOTTOM) / (double) numLines;
      g.setColor(colorMap.intensityToColor(Math.max(intensity, 0)));
      final int y = position.y + numLines - line + BOTTOM;
      g.drawLine(left, y, right, y);
    }
    final String top = colorMap.formatValue(colorMap.getMax());
    final String bottom = colorMap.formatValue(colorMap.getMin());
    final FontMetrics fm = g.getFontMetrics();
    final int tw = fm.stringWidth(top);
    final int bw = fm.stringWidth(bottom);
    GisPanel.drawText(g, top, position.x - tw - 5, position.y);
    GisPanel.drawText(g, bottom, position.x - bw - 5, position.y + HEIGHT);
  }
}
