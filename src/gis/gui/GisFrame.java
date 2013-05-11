package gis.gui;


import java.awt.Dimension;

import javax.swing.JFrame;

public class GisFrame extends JFrame {
	
	private final GisPanel mapViewer;
	
	public GisFrame() {
		//initialize
		super("GIS Viewer");
		mapViewer = new GisPanel();
		add(mapViewer);
		
		//set normal frame size and maximize
		setSize(new Dimension(800, 600));
		setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		
		//other stuff
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}
}
