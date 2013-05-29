package gis.gui;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.SpringLayout;

public class GisFrame extends JFrame {
	
	private final GisPanel gisPanel = new GisPanel();
	private final GisControlPanel gisControlPanel = new GisControlPanel(gisPanel);
	
	
	public GisFrame() {
		//initialize
		super("GIS Viewer");
		
		//set layout
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 10;
		gbc.weighty = 100;
		gbc.fill = GridBagConstraints.BOTH;
		add(gisPanel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(gisControlPanel, gbc);
		
		//set normal frame size and maximize
		setSize(new Dimension(800, 600));
		setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		
		//other stuff
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
	}
}
