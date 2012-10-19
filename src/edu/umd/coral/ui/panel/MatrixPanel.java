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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.control.DiscreteLegend;
import edu.umd.coral.ui.table.SearchComboModel;

public class MatrixPanel extends JPanelExt implements ComponentListener, 
	PropertyChangeListener, ActionListener, HasSaveableImage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7050849554117696495L;
	
	private MatrixVis matrixVis;
	
	private DiscreteLegend legend;

	private JLabel selectedLabel;
	
	private JComboBox searchCombo;
	
	private JButton searchButton;
	
	private SearchComboModel comboModel;
		
	public MatrixPanel(DataModel model) {
		super(model);
		
		this._dataModel = model;
		
		// get vertices
//		Collection<Vertex> coll = model.getVertices().values();
//		Vertex [] vertices = coll.toArray(new Vertex[0]);
		
		// listen to changes to the matrix
		model.addPropertyChangeListener(DataModel.VERTICES_CHANGED, this);
		model.addPropertyChangeListener(DataModel.MATRIX_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		
		// set border layout
		setLayout(new BorderLayout());		
		
		matrixVis = new MatrixVis(_dataModel);
		JScrollPane pane = new JScrollPane(matrixVis);
		pane.addComponentListener(this);
		add(pane, BorderLayout.CENTER);
		
		JPanel det = new JPanel();
		det.setLayout(new BoxLayout(det, BoxLayout.Y_AXIS));
		
		JPanel search = new JPanel();
		search.setLayout(new BoxLayout(search, BoxLayout.X_AXIS));
//		search.setBorder(BorderFactory.createLineBorder(Color.RED));
		search.setAlignmentX(LEFT_ALIGNMENT);
		search.add(new JLabel("Search:"));
		
		comboModel = new SearchComboModel(); 
		searchCombo = new JComboBox(comboModel);
		searchCombo.setEditable(true);
		AutoCompleteDecorator.decorate(searchCombo);
		search.add(searchCombo);
		searchButton = new JButton("Go");
		searchButton.addActionListener(this);
		search.add(searchButton);
//		search.add(Box.createHorizontalGlue());
		
		det.add(search);
		
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setAlignmentX(LEFT_ALIGNMENT);
		det.add(sep);
		
		JPanel detailPanel = new JPanel();
		detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.X_AXIS));
//		detailPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		
		legend = new DiscreteLegend(5);
//		legend.setBorder(BorderFactory.createLineBorder(Color.blue));
		legend.setPreferredSize(new Dimension(DiscreteLegend.minBlockSize * (5+1), DiscreteLegend.gradientWidth));
		legend.setMaximumSize(new Dimension(DiscreteLegend.minBlockSize * (5+1), DiscreteLegend.gradientWidth));
		legend.setAlignmentY(Component.CENTER_ALIGNMENT);
		detailPanel.add(legend);
		
		detailPanel.add(Box.createRigidArea(new Dimension(4, 1)));
		
		JLabel label = new JLabel("Selected:");
		label.setAlignmentY(Component.CENTER_ALIGNMENT);
		detailPanel.add(label);
		
		selectedLabel = new JLabel();
		detailPanel.add(selectedLabel);
		selectedLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		detailPanel.setAlignmentX(LEFT_ALIGNMENT);
		det.add(detailPanel);
		
		add(det, BorderLayout.PAGE_END);
		//add(detailPanel, BorderLayout.PAGE_END);
		
		addComponentListener(this);
	}
	
	private void updateLegend(int maxValue) {
		// float diff = (maxValue - minValue);
		
		// configure legend
		// System.out.println("legend min max " + minValue / diff + " " + maxValue / diff);
		legend.setMaxValue(maxValue);
		legend.revalidate();
	}

	//--------------------------------------------------------------------------
	// 
	// ComponentListener interface
	// 
	//--------------------------------------------------------------------------
	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
		matrixVis.revalidate();
		matrixVis.repaint();
	}

	public void componentShown(ComponentEvent e) {
	}
	
	//--------------------------------------------------------------------------
	// 
	// PropertyChangeListener interface
	// 
	//--------------------------------------------------------------------------
	public void propertyChange(PropertyChangeEvent e) {
		String propName = e.getPropertyName();
		if (propName.equals(DataModel.MATRIX_CHANGED)) {
			Matrix m = _dataModel.getCurrentMatrix();
			if (m != null) {
				updateLegend((int)m.getMax());
			}
		}
		else if (propName.equals(DataModel.SELECTED_VERTICES_CHANGED)) {
			Collection<Vertex> verts = _dataModel.getSelectedVertices();
			// TODO: wrap into a nice string
			if (verts != null) {
				selectedLabel.setText(verts.toString());
				this.selectedLabel.setToolTipText(verts.toString());
			}
			else { 
				selectedLabel.setText("");
				selectedLabel.setToolTipText("Nothing selected");
			}
		}
		else if (propName.equals(DataModel.VERTICES_CHANGED)) {
			Map<String, Vertex> map = _dataModel.getVertices();
			if (map == null) {
				comboModel.setData(null);
			}
			else {
				Collection<Vertex> coll =  map.values();
				comboModel.setData(coll);
			}
		}
	}

	//--------------------------------------------------------------------------
	// 
	// ActionListener interface
	// 
	//--------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchButton) {
			Vertex v = (Vertex) this.searchCombo.getSelectedItem();
			_dataModel.setSearchItem(v);
		}
	}

	public RenderedImage getImage() {
		return this.matrixVis.getImage();
	}
}
