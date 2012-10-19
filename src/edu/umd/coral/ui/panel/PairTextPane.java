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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.ui.JPanelExt;

//going to need PropertyChangeListening 
public class PairTextPane extends JPanelExt implements ActionListener, PropertyChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7911889871953305636L;
	private int width = 300;
	private int height = 250;
	
	private JTextField vertField;
	private JComboBox clusterBox;
//	private EdgeSlider slider;
	
	public PairTextPane(DataModel model){
		super(model);
		setMinimumSize(new Dimension((width/2), (height/2)));
		setPreferredSize(new Dimension(width, height));
		
		model.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		model.addPropertyChangeListener(DataModel.CLUSTERINGS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SELECTED_CLUSTERING_CHANGED, this);
//		model.addPropertyChangeListener(DataModel.WEIGHTED_CHANGED, this);
		
		add(new JLabel("Selected Vertex Pair\n", JLabel.CENTER));
		
		vertField = new JTextField(20);
		vertField.setAlignmentX(Component.CENTER_ALIGNMENT);
		vertField.setEditable(false);
		vertField.setHorizontalAlignment(JTextField.CENTER);
		add(vertField);
		
		add(new JLabel("Selected Clustering\n", JLabel.CENTER));
		
		clusterBox = new JComboBox();
		clusterBox.addItem("");
		/*for (String s : _dataModel.getClusterings().keySet()){
			clusterBox.addItem(s);
		}*/
		clusterBox.setSelectedIndex(0);
		add(clusterBox);	
		
		clusterBox.addActionListener(this);
		
		/*slider = new EdgeSlider(model);
		if (model.isWeighted()){
			add(slider);
		}*/

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		if (name.equals(DataModel.SELECTED_VERTICES_CHANGED)){
			if (_dataModel.getSelectedVertices() == null){
				vertField.setText("");
			}else{
				vertField.setText(_dataModel.getSelectedVertices().toString());
			}
		}else if (name.equals(DataModel.CLUSTERINGS_CHANGED)){
			clusterBox.removeAllItems();
			clusterBox.addItem("");
			if (_dataModel.getClusterings() != null){
				for (String s : _dataModel.getClusterings().keySet()){
					clusterBox.addItem(s);
				}
			}
		}else if (name.equals(DataModel.SELECTED_CLUSTERING_CHANGED)){
			Clustering selected = _dataModel.getSelectedClustering();
			if (selected == null){
				clusterBox.setSelectedItem("");
			}else {
				clusterBox.setSelectedItem(_dataModel.getSelectedClustering().getName());
			}
		}/* else if (name.equals(DataModel.WEIGHTED_CHANGED)){
			if (slider != null) remove(slider);
			if(_dataModel.isWeighted()){
				slider = new EdgeSlider(_dataModel);
				add(slider);
			}
		}*/
	}

	public void actionPerformed(ActionEvent evt) {
		JComboBox cb = (JComboBox)evt.getSource();
		String clusterName = (String)cb.getSelectedItem();
		if (clusterName == null || clusterName.equals("")){
			_dataModel.setSelectedClustering(null);
		}else{
			_dataModel.setSelectedClustering(_dataModel.getClusterings().get(clusterName));
		}
		//reusing the selectedClustering variable 
	}

} 
