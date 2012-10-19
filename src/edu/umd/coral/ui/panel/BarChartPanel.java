/*******************************************************************************
 * Copyright (c) 2012 Darya Filippova.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Darya Filippova - initial API and implementation
 ******************************************************************************/
package edu.umd.coral.ui.panel;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.ColoredPair;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.control.SimpleBarChart;

public class BarChartPanel extends JPanelExt implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7946476817932958622L;
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Private variables
	//
	////////////////////////////////////////////////////////////////////////////
	
	private String type;
	
	/**
	 * Datatype - float or int. Determines label formatting.
	 */
	private String dtype;
	
	private SimpleBarChart chart;
	
	
	public final static String FLOAT = "float";
	public final static String INT = "int";

	/**
	 * 
	 * Default constructor
	 * 
	 * @param model
	 * @param type
	 * @param event 
	 */
	public BarChartPanel(DataModel model, String type, String event, String dtype) {
		super(model);
		this.type = type;
		this.dtype = dtype;
		
		if (_dataModel != null)
			_dataModel.addPropertyChangeListener(event, this);
		/*
		_dataModel.addPropertyChangeListener(DataModel.CLUSTERING_ORDERING_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.NETWORK_LOADED, this);
		*/
		_dataModel.addPropertyChangeListener(DataModel.EDGES_CHANGED, this);
		/*
		_dataModel.addPropertyChangeListener(DataModel.VERTICES_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.CLUSTERINGS_CHANGED, this);
		*/
		initUI();
	}

	private void initUI() {
		setLayout(new BorderLayout());
		
//		JPanel header = new JPanel();
//		header.setLayout(new BorderLayout());
//		header.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
		
//		ImageIcon icon = new ImageIcon(EXPORT_ICON);
//		export = new JButton(null, icon);
//		export.setToolTipText("Save as a png");
//		export.setAlignmentX(RIGHT_ALIGNMENT);
//		header.add(export, BorderLayout.LINE_END);
//		add(header, BorderLayout.PAGE_START);
		
		chart = new SimpleBarChart(dtype);
		if (type.equals(Clustering.AVG_DENSITY))
			chart.showMessage();
		add(chart);
	}
	
	private ArrayList<ColoredPair<String, Float>> createDataset(String value) {
		// column keys...
		Map<String, Clustering> map = _dataModel.getClusterings();
		if (map == null)
			return null;
		
		Collection<Clustering> clusterings = map.values();
		ArrayList<Clustering> clustList = new ArrayList<Clustering>();
		clustList.addAll(clusterings);
		
		// TODO: use this as a default clustering comparator
		Collections.sort(clustList, new Comparator<Clustering>() {
			public int compare(Clustering s1, Clustering s2) {
				try {
					int i1 = Integer.valueOf(s1.getName());
					int i2 = Integer.valueOf(s2.getName());
					
					if (i1 < i2) return -1;
					if (i1 > i2) return 1;
					return 0;
				}
				catch (Exception e) {
					return s1.compareTo(s2);
				}
			}
		});
		
		if (type.equals(Clustering.AVG_DENSITY) && _dataModel.getEdges() == null) return null;
		
		// create the dataset...
		ArrayList<ColoredPair<String, Float>> dataset = new ArrayList<ColoredPair<String, Float>>();

		ColoredPair<String, Float> p;
		for (Clustering c : clustList) {
			p = new ColoredPair<String, Float>(c.getName(), c.getValue(value, _dataModel.getIncludeGrabBag()));
			p.setColor(c.getColor());
			p.setOverlap(c.hasOverlap());
			dataset.add(p);
		}
		return dataset;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Event handling
	//
	////////////////////////////////////////////////////////////////////////////

	public void propertyChange(PropertyChangeEvent e) {
//		System.out.println("chart " + e.getPropertyName());
		ArrayList<ColoredPair<String, Float>> dp = createDataset(type);
		chart.setDataProvider(dp);
		repaint();
	}

	public BufferedImage getImage() {
		BufferedImage img = new BufferedImage(
				chart.getWidth(), 
				chart.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		chart.paint(img.getGraphics());
		return img;
	}
}
