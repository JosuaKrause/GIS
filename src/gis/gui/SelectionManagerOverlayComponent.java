package gis.gui;

import gis.gui.overlay.AbstractOverlayComponent;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

public class SelectionManagerOverlayComponent extends AbstractOverlayComponent {

  protected static final int WIDTH = 200;
  protected static final int HEIGHT = 20;

  public SelectionManagerOverlayComponent(final GisPanel gisPanel) {
    super(gisPanel, new Dimension(WIDTH, HEIGHT), Integer.MIN_VALUE);
  }

  private String text = "";

  public void setText(final String text) {
    this.text = Objects.requireNonNull(text);
    gisPanel.repaint();
  }

  @Override
  public void paint(final Graphics2D g) {
    if(text.isEmpty()) return;
    final Graphics2D g2 = (Graphics2D) g.create();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f));
    g2.setColor(Color.BLACK);
    final FontMetrics fm = g2.getFontMetrics();
    final int w = fm.stringWidth(text);
    final int h = fm.getHeight();
    System.out.println(position);
    final double border = 5;
    g2.fill(new Rectangle2D.Double(position.x - border, position.y - border,
        w + 2 * border, h + 2 * border));
    g2.dispose();
    GisPanel.drawText(g, text, position.x, (float) (position.y + h - border * .5f));
  }

}
