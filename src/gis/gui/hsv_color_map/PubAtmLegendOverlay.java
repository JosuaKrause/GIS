package gis.gui.hsv_color_map;

import gis.gui.GisPanel;
import gis.gui.overlay.Overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

public class PubAtmLegendOverlay implements Overlay {

  private final double dim1Min;
  private final double dim1Max;
  private final double dim2Min;
  private final double dim2Max;

  private final PubAtmColorMap parent;

  private boolean visible = false;
  private Point position = new Point(0, 0);

  private final int innerBoxOffsetX = 106;
  private final int innerBoxOffsetY = 100;
  private final int boxWidth = 50;
  private final int boxHeight = 50;

  private final Dimension dimension = new Dimension(
      innerBoxOffsetX + boxWidth,
      innerBoxOffsetY + boxHeight);

  PubAtmLegendOverlay(final double dim1Min, final double dim1Max,
      final double dim2Min, final double dim2Max, final PubAtmColorMap parent) {
    this.dim1Min = dim1Min;
    this.dim1Max = dim1Max;
    this.dim2Min = dim2Min;
    this.dim2Max = dim2Max;
    this.parent = parent;
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
    this.visible = visible;
  }

  @Override
  public void paint(final Graphics2D graphics) {
    final Graphics2D g = (Graphics2D) graphics.create(position.x, position.y,
        dimension.width, dimension.height);
    g.setColor(Color.BLACK);
    g.drawRect(innerBoxOffsetX, innerBoxOffsetY, boxWidth - 1, boxHeight - 1);
    final int width = boxWidth - 2;
    final int height = boxHeight - 2;
    for(int y = 0; y < height; ++y) {
      for(int x = 0; x < width; ++x) {
        final double v1 = x / (double) width;
        final double v2 = 1 - (y / (double) height);
        final Color c = parent.getColorFromIntensities(v1, v2);
        g.setColor(c);
        g.fillRect(innerBoxOffsetX + 1 + x, innerBoxOffsetY + 1 + y, 1, 1);
      }
    }
    g.setColor(Color.BLACK);
    final FontMetrics fm = g.getFontMetrics();
    final String s1Min = Math.round(dim1Min) + "m";
    final String s1Max = Math.round(dim1Max) + "m";
    final String s2Min = Math.round(dim2Min) + "m";
    final String s2Max = Math.round(dim2Max) + "m";
    final String[] label1 = new String[] { "avg. min.", "pub to pub", "distance"};
    final String[] label2 = new String[] { "avg. min.", "pub to ATM", "distance"};
    final int sep = 2;
    // s2Max
    int w = fm.stringWidth(s2Max);
    int h = fm.getAscent();
    int x = innerBoxOffsetX - w - sep;
    int y = innerBoxOffsetY + h;
    GisPanel.drawText(g, s2Max, x, y);
    // s2Min
    w = fm.stringWidth(s2Min);
    x = innerBoxOffsetX - w - sep;
    y = innerBoxOffsetY + boxHeight - 2;
    GisPanel.drawText(g, s2Min, x, y);
    // label2
    x = 0;
    y = innerBoxOffsetY + 18;
    for(int i = 0; i < label2.length; ++i, y += h) {
      GisPanel.drawText(g, label2[i], x, y);
    }
    // s1Min
    w = h;
    h = fm.stringWidth(s1Min);
    x = innerBoxOffsetX + w - 3;
    y = innerBoxOffsetY - sep;
    g.translate(x, y);
    g.rotate(-Math.PI / 2);
    GisPanel.drawText(g, s1Min, 1, 0);
    g.rotate(Math.PI / 2);
    g.translate(-x, -y);
    // s1Max
    h = fm.stringWidth(s1Max);
    x = innerBoxOffsetX + boxWidth - 2;
    y = innerBoxOffsetY - sep;
    g.translate(x, y);
    g.rotate(-Math.PI / 2);
    GisPanel.drawText(g, s1Max, 1, 0);
    g.rotate(Math.PI / 2);
    g.translate(-x, -y);
    // label1
    x = innerBoxOffsetX + 14;
    y = 0;
    for(int i = 0; i < label1.length; ++i, x += w) {
      y = fm.stringWidth(label1[i]);
      g.translate(x, y);
      g.rotate(-Math.PI / 2);
      GisPanel.drawText(g, label1[i], 0, 1);
      g.rotate(Math.PI / 2);
      g.translate(-x, -y);
    }

    g.dispose();
  }

  @Override
  public void setPosition(final Point position) {
    this.position = position;
  }

  @Override
  public int getHorizontalAlignmentWeight() {
    return 1;
  }

}
