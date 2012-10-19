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

import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.ui.control.DiscreteLegend;
//import edu.umd.coral.ui.network.NetworkThumbnailPanel;
import edu.umd.coral.ui.panel.MatrixPanel;
import edu.umd.coral.ui.panel.Vertex2VertexPanel;

public class CooccurrenceMatrixPanel extends JSplitPane implements PropertyChangeListener {

	private static final long serialVersionUID = -6127497841545203496L;
	
	private DataModel _dataModel;
	
	private JSplitPane top;
	
//	private NetworkThumbnailPanel netView;
	
	private DiscreteLegend legend;
	 
	public CooccurrenceMatrixPanel(DataModel model) {
		super(JSplitPane.VERTICAL_SPLIT);
		_dataModel = model;
		
		model.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		model.addPropertyChangeListener(DataModel.MATRIX_CHANGED, this);
		
		setResizeWeight(600.0/850.0);
		
		
		top = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		top.setResizeWeight(1.0);
//		top.setDividerLocation(0.75);
		top.setPreferredSize(new Dimension(750,600));
		top.add(new MatrixPanel(_dataModel));
		
		setResizeWeight(1.0);
//		setDividerLocation(0.75);
		setPreferredSize(new Dimension(750,600));
		
		
		// TODO: organize V2V and legend
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		legend = new DiscreteLegend(5);
		legend.setAlignmentX(Component.LEFT_ALIGNMENT);
		sidePanel.add(legend);
				
		Vertex2VertexPanel v2v = new Vertex2VertexPanel(_dataModel);
		v2v.setPreferredSize(new Dimension(150, 500));
		v2v.setAlignmentX(Component.LEFT_ALIGNMENT);
		sidePanel.add(v2v);
		
		top.add(sidePanel);
	
		add(top);
		
//		if (_dataModel.hasEdges()) {
//			netView = new NetworkThumbnailPanel(_dataModel);
//			netView.setPreferredSize(new Dimension(0,0));
//			add(netView);
//		}
	}
	
	private void updateLegend(int maxValue) {
		// float diff = (maxValue - minValue);
		
		// configure legend
		// System.out.println("legend min max " + minValue / diff + " " + maxValue / diff);
		legend.setMaxValue(maxValue);
		legend.revalidate();
	}

	
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(DataModel.MATRIX_CHANGED)) {
			Matrix m = _dataModel.getCurrentMatrix();
			if (m != null)
				this.updateLegend((int)m.getMax());
		}
		else if (e.getPropertyName().equals(DataModel.SELECTED_VERTICES_CHANGED)) {
//			Collection<Vertex> pair = _dataModel.getSelectedVertices();
			// only add when selected a pair
//			if (pair != null && netView != null) {
//				netView.setPreferredSize(new Dimension(600, 100));
//				validate();
//				repaint();
//			}
		}
	}	
}
