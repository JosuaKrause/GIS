package gis.gui.color_map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class TestColorMappingFrame extends JFrame {

  private static final long serialVersionUID = 1134514145861266874L;

  private final ColorMapping colorMapping;

  public TestColorMappingFrame(final ColorMapping colorMapping) {
    super();
    this.colorMapping = colorMapping;
    this.setSize(800, 50);
    setLocationRelativeTo(null);
    setUndecorated(true);
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(final MouseEvent arg0) {
        System.exit(0);
      }

    });
  }

  @Override
  public void paint(final Graphics g) {
    final int width = getWidth();
    final int height = getHeight();
    for(int x = 1; x < width; ++x) {
      final Color c = colorMapping.getColor(x / (double) (width - 2));
      g.setColor(c);
      g.drawLine(x, 1, x, height - 2);
    }
    g.setColor(Color.BLACK);
    g.drawLine(0, 0, width - 1, 0);
    g.drawLine(0, height - 1, width - 1, height - 1);
    g.drawLine(0, 0, 0, height - 1);
    g.drawLine(width - 1, 0, width - 1, height - 1);

  }

  public static void main(final String[] args) {
    final ColorMapping mapping = new ColorMap(new IntervalIntensityMapping(0, 0, 1, 1),
        new Color[] {
            new Color(248, 16, 0), new Color(252, 252, 0), Color.WHITE},
        new double[] { 0, 0.5, 1});
    final TestColorMappingFrame frame = new TestColorMappingFrame(mapping);
    frame.setVisible(true);
  }

}
