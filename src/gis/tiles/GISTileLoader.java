package gis.tiles;

import java.io.File;

import org.lwjgl.opengl.ARBShaderObjects;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;

public class GISTileLoader extends ShaderTileLoader {

  public GISTileLoader(final ResetableTileListener listener, final TileLoader parent) {
    super(listener, parent, new File("shaders/screen.vert"),
        new File("shaders/screen.frag"));
  }

  @Override
  protected void settingVariables(final TileInfo info) {
    ARBShaderObjects.glUniform2fARB(attr("size"), info.getWidth(), info.getHeight());
    ARBShaderObjects.glUniform2fARB(attr("tile"), info.tileX(), info.tileY());
    ARBShaderObjects.glUniform1fARB(attr("zoom"), info.zoom());
  }

}
