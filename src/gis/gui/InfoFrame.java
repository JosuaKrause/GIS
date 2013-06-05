package gis.gui;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * The info frame shows information.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public class InfoFrame extends JFrame {

  /** The instance. */
  private static final InfoFrame INSTANCE = new InfoFrame();
  /** The text area. */
  private final JTextArea text;
  /** The scroll pane. */
  private final JScrollPane scroll;

  /**
   * Getter.
   * 
   * @return The info frame.
   */
  public static final InfoFrame getInstance() {
    return INSTANCE;
  }

  /** Creates the info frame. */
  private InfoFrame() {
    super("Info");
    text = new JTextArea(25, 40);
    text.setEditable(false);
    scroll = new JScrollPane(text);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    add(scroll);
    pack();
    setDefaultCloseOperation(HIDE_ON_CLOSE);
  }

  /**
   * Adds text to the info frame.
   * 
   * @param str The string to add.
   */
  public void addText(final String str) {
    if(!GisFrame.getInstance().isVisible()) return;
    text.setText(text.getText() + str + "\n");
    final JScrollBar bar = scroll.getVerticalScrollBar();
    bar.setValue(bar.getMaximum());
    setVisible(true);
  }

}
