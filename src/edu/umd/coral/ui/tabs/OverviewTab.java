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
package edu.umd.coral.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.RenderedImage;

import javax.swing.JTabbedPane;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.panel.BarChartPanel;
import edu.umd.coral.ui.panel.HasSaveableImage;

public class OverviewTab extends JPanelExt implements HasSaveableImage {
	
	private static final long serialVersionUID = -2234248829627364985L;
	
	private JTabbedPane tabbedPane;

	/**
	 * Initializes the chart properties
	 */
	public OverviewTab(DataModel model) {
		super(model);
		_dataModel = model;
		initUI();
	}

	// generate tabs
	private void initUI() {
		setLayout(new BorderLayout());
		
		// add tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		add(tabbedPane);
		
		// add charts
		BarChartPanel panel = new BarChartPanel(_dataModel, // model to listen
					Clustering.MODULE_COUNT, 				// prop to vis
					DataModel.CLUSTERINGS_CHANGED, 			// event to listen for
					BarChartPanel.INT);						// data type for values
		tabbedPane.addTab("Mod cnt", null, panel, "Module count in each clustering");
		
		panel = new BarChartPanel(_dataModel, 
					Clustering.AVG_MODULE_SIZE, 
					DataModel.CLUSTERINGS_CHANGED, 
					BarChartPanel.FLOAT);
		tabbedPane.addTab("Avg Mod size", null, panel, "Average module size per clustering");
		
		panel = new BarChartPanel(_dataModel, 
					Clustering.AVG_DENSITY, 
					DataModel.NETWORK_LOADED, 
					BarChartPanel.FLOAT);
		tabbedPane.addTab("Avg Mod Dens", null, panel, "Average module density (available only if network information is loaded)");
		
		panel = new BarChartPanel(_dataModel, 
					Clustering.VERTEX_COUNT, 
					DataModel.CLUSTERINGS_CHANGED, 
					BarChartPanel.INT);
		tabbedPane.addTab("Vtx cnt", null, panel, "Vertex count per clustering");
		
		panel = new BarChartPanel(_dataModel, // model to listen
				Clustering.ENTROPY, 				// prop to vis
				DataModel.CLUSTERINGS_CHANGED, 			// event to listen for
				BarChartPanel.FLOAT);						// data type for values
		tabbedPane.addTab("Entropy", null, panel, "Entropy for each clustering");
		
		panel = new BarChartPanel(_dataModel, // model to listen
				Clustering.OVERLAP, 				// prop to vis
				DataModel.CLUSTERINGS_CHANGED, 			// event to listen for
				BarChartPanel.FLOAT);						// data type for values
		tabbedPane.addTab("Overlap", null, panel, "Percent of vertices that are in more than one cluster");
		
		// select clusterings
//		SelectClusteringsPanel selectPanel = new SelectClusteringsPanel(_dataModel);
//		tabbedPane.addTab("Clust", null, selectPanel, "Clusterings included in analysis");
	}

	public RenderedImage getImage() {
		Component c = this.tabbedPane.getSelectedComponent();
		if (c instanceof BarChartPanel) {
			BarChartPanel bcp = (BarChartPanel) c;
			return bcp.getImage();
		}
		return null;
	}
}
