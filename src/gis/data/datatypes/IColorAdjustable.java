package gis.data.datatypes;

import java.awt.Color;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

public interface IColorAdjustable extends MapMarker {
	
	public void setColor(Color color);
	public void setAlpha(float alpha);
	
}
