package gis.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class MouseSelectionListener extends MouseAdapter {
	
	private final GisPanel gisPanel;
	private final GisControlPanel gisControlPanel;
	
	public MouseSelectionListener(final GisPanel gisPanel,
			final GisControlPanel gisControlPanel) {
		this.gisPanel = gisPanel;
		this.gisControlPanel = gisControlPanel;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			final Coordinate c = gisPanel.getPosition(e.getPoint());
			if (gisControlPanel.processSelectionClick(e.getPoint(), c)) {
				gisPanel.repaint();
			}
		}
	}
	
}
