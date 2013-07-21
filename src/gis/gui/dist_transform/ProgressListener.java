package gis.gui.dist_transform;

import java.awt.Graphics;
import java.awt.Graphics2D;

public interface ProgressListener {

  boolean stillAlive();

  Graphics2D commitProgress(Graphics old);

}
