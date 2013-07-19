package gis.gui;

import gis.data.db.Query;
import gis.gui.dist_transform.Combiner;
import gis.gui.dist_transform.ProgressListener;
import gis.gui.dist_transform.ViewInfo;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JCheckBox;

public abstract class ImagePainter {

  protected final Query query;

  protected final Combiner combiner;

  private final JCheckBox box;

  public ImagePainter(final Query query, final Combiner combiner, final JCheckBox box) {
    this.box = Objects.requireNonNull(box);
    this.query = Objects.requireNonNull(query);
    this.combiner = Objects.requireNonNull(combiner);
  }

  public abstract void paint(Graphics2D g, ViewInfo info, ProgressListener prog);

  public JCheckBox getCheckBox() {
    return box;
  }

  public ActionListener createActionListener(final GisPanel gisPanel) {
    return new ActionListener() {

      @Override
      public void actionPerformed(final ActionEvent e) {
        final ImagePainter old = gisPanel.getImagePainter();
        if(getCheckBox().isSelected()) {
          if(old != null) {
            old.getCheckBox().setSelected(false);
          }
          gisPanel.setImagePainter(ImagePainter.this);
        } else if(old == ImagePainter.this) {
          gisPanel.setImagePainter(null);
        }
      }

    };
  }

}
