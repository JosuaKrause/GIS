package gis.gui.overlay;

import gis.data.db.Query;
import gis.gui.GisPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class DistanceThresholdSelector extends JTextField {

  private static final long serialVersionUID = 1568302050770485451L;

  private static final int TEXT_FIELD_WIDTH = 80;
  private static final int TEXT_FIELD_HEIGHT = 20;

  private final String labelLeft = "Threshold Distance:";
  private final String labelRight = "meters";
  private double distanceInMeters;

  public DistanceThresholdSelector(final Query query, final double distanceInMeters) {
    super(Double.toString(distanceInMeters));
    this.distanceInMeters = distanceInMeters;
    setSize(TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
    setHorizontalAlignment(SwingConstants.RIGHT);
    setBorder(new Border() {

      @Override
      public void paintBorder(final Component c, final Graphics g, final int x,
          final int y, final int width, final int height) {
        // do nothing
      }

      @Override
      public boolean isBorderOpaque() {
        return true;
      }

      @Override
      public Insets getBorderInsets(final Component c) {
        return new Insets(0, 0, 0, 0);
      }
    });
    setFocusable(true);
    setEditable(true);
    setEnabled(true);
    final Action a = new AbstractAction() {

      private static final long serialVersionUID = -4019313644858997850L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        final String s = getText();
        double d = 0;
        try {
          d = Double.parseDouble(s);
        } catch(final NumberFormatException ex) {
          // do nothing
        }

        final double oldDistance = distanceInMeters;
        setDistanceInMeters(d);
        if(oldDistance != d) {
          System.out.println("repaint parent");
          query.clearCache();
          getParent().repaint();
        } else {
          System.out.println("repaint");
          repaint();
        }
      }
    };
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), a);
    getActionMap().put(a, a);
    updateText();
  }

  public void paint(final Graphics2D g, final int topRightX, final int topRightY) {
    final FontMetrics fm = g.getFontMetrics();
    final int llw = fm.stringWidth(labelLeft);
    final int rlw = fm.stringWidth(labelRight);
    final int lh = fm.getHeight();
    final int padding = 5;
    // draw labels
    g.setColor(Color.BLACK);
    int x = topRightX - rlw - padding;
    final int y = topRightY + (TEXT_FIELD_HEIGHT + lh) / 2 - fm.getDescent();
    GisPanel.drawText(g, labelRight, x, y);
    x = topRightX - rlw - 3 * padding - llw - TEXT_FIELD_WIDTH;
    GisPanel.drawText(g, labelLeft, x, y);
    // draw text field
    setLocation(topRightX - TEXT_FIELD_WIDTH - 2 * padding - rlw, topRightY);
  }

  public double getDistanceInMeters() {
    return distanceInMeters;
  }

  private void updateText() {
    final long rounded = Math.round(distanceInMeters);
    if(rounded == distanceInMeters) {
      setText(Long.toString(rounded));
    } else {
      setText(Double.toString(distanceInMeters));
    }
  }

  void setDistanceInMeters(final double dist) {
    distanceInMeters = dist;
    updateText();
  }

}
