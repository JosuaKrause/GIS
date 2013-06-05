package gis.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class DeselectionListener extends KeyAdapter {

  final JComponent component;
  final JFrame frame;
  final SelectionManager selectionManager;

  public DeselectionListener(final JFrame frame,
      final SelectionManager selectionManager) {
    this.frame = frame;
    component = null;
    this.selectionManager = selectionManager;
  }

  public DeselectionListener(final JComponent component,
      final SelectionManager selectionManager) {
    frame = null;
    this.component = component;
    this.selectionManager = selectionManager;
  }

  @Override
  public void keyTyped(final KeyEvent e) {
    if(Character.toLowerCase(e.getKeyChar()) == 'd') {
      selectionManager.deselectAll();
      if(frame != null) {
        frame.repaint();
      }
      if(component != null) {
        component.repaint();
      }
    }
  }
}
