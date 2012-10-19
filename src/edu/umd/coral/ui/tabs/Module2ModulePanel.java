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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.GradientColorModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.ModulePair;
import edu.umd.coral.model.data.Score;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.metrics.Metric;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.slider.RangeSlider;
import edu.umd.coral.ui.table.ClusteringCompareTableModel;
import edu.umd.coral.ui.table.ClusteringCompareTableSorter;
import edu.umd.coral.ui.table.renderer.JaccardCellRenderer;

public class Module2ModulePanel extends JPanelExt implements ActionListener, 
			PropertyChangeListener, ChangeListener, ListSelectionListener {

	private JTable table;
	
	private JLabel detail;
	
	private RangeSlider slider;
	
	private ClusteringCompareTableModel tableModel;

	/**
	 * generated
	 */
	private static final long serialVersionUID = -4344345902768705907L;
	
	/**
	 * Constructor. Initializes visual components.
	 * 
	 * @param model
	 */
	public Module2ModulePanel(DataModel model) {
		super(model);
		
		// listen for the property changes
		_dataModel.addPropertyChangeListener(DataModel.METRIC_SCORES_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.SELECTED_CLUSTERING_PAIR_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.MODULE_TABLE_COLUMNS_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.SEARCH_ITEM_CHANGED, this);
		
		initUI();
	}

	// set up the ui components
	private void initUI() {
		setLayout(new BorderLayout());
		
		initHeaderUI();
		initTableUI();
	}

	/**
	 * @private
	 * 
	 * set up the table holding model2module comparisons 
	 */
	private void initTableUI() {
		// table w/ details
		table = new JTable(){
			private static final long serialVersionUID = -4676300440118445967L;

			public TableCellRenderer getCellRenderer(int row, int column) {
				TableModel model = this.getModel();
				if (model instanceof ClusteringCompareTableModel) {
					ClusteringCompareTableModel v2vm = (ClusteringCompareTableModel)model;
					int index = v2vm.getColumnIndex(ClusteringCompareTableModel.SCORE);
					if (index == column)
					    return new JaccardCellRenderer(true);
				}
				
				return super.getCellRenderer(row, column);
			  }
			
			public String getToolTipText(MouseEvent e) {
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
		        int colIndex = columnAtPoint(p);
//		        int realColumnIndex = convertColumnIndexToModel(colIndex);
		        int realRowIndex = convertRowIndexToModel(rowIndex);
		        String colName = this.getColumnName(colIndex);
		        String tip;
		        if (colName.equals(ClusteringCompareTableModel.INTERSECTION) ||
		        	colName.equals(ClusteringCompareTableModel.LEFT_DIFF) ||
		        	colName.equals(ClusteringCompareTableModel.RIGHT_DIFF)) {
		        	tip = tableModel.getToolTipAt(realRowIndex, colName);
//		        	tip = ( (ClusteringCompareTableModel) ( (String)this.getModel() ) ).getTooltipAt(realRowIndex, colName);
		        	if (tip == null || tip.equals(""))
		        		tip = "Empty";
		        }
		        else tip = super.getToolTipText(e);
		        
				return tip;
			}
		};
		tableModel = new ClusteringCompareTableModel();
		ClusteringCompareTableSorter sorter = new ClusteringCompareTableSorter(tableModel);
		_dataModel.setModuleTableColumns(tableModel.getSelectedColumnNames());
		
		table.setModel(tableModel);
		table.setRowSorter(sorter);
		table.getSelectionModel().addListSelectionListener(this);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * @private
	 * add "selected items" label and a slider for filtering out the Jaccard 
	 * values in the table
	 */
	private void initHeaderUI() {
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		
		detail = new JLabel("Selected:");
		detail.setAlignmentX(LEFT_ALIGNMENT);
		header.add(detail);
		
		// slider to cut off modules below some Jaccard score
		slider = new RangeSlider();
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(5);
		slider.setDrawGradient(true);
		
		Dictionary<Integer, JLabel> d = new Hashtable<Integer, JLabel>();
		d.put(0, new JLabel("0.0"));
		d.put(10, new JLabel("0.1"));
		d.put(20, new JLabel("0.2"));
		d.put(30, new JLabel("0.3"));
		d.put(40, new JLabel("0.4"));
		d.put(50, new JLabel("0.5"));
		d.put(60, new JLabel("0.6"));
		d.put(70, new JLabel("0.7"));
		d.put(80, new JLabel("0.8"));
		d.put(90, new JLabel("0.9"));
		d.put(100, new JLabel("1.0"));
		
		slider.setLabelTable(d);
		
		slider.setMinimum(0);
		slider.setMaximum(100);
		slider.setValue(0);
        slider.setUpperValue(100);
        slider.setAlignmentX(LEFT_ALIGNMENT);
        slider.addChangeListener(this);
        header.add(slider);
        
        add(header, BorderLayout.PAGE_START);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Event handling
	//
	////////////////////////////////////////////////////////////////////////////
	
	public void actionPerformed(ActionEvent e) {
		// combobox argument changed
		JComboBox combo = (JComboBox) e.getSource();
		Metric selectedMetric = (Metric) combo.getSelectedItem();
		
		_dataModel.setSelectedMetric(selectedMetric);
	}

	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		if (name.equals(DataModel.MODULE_TABLE_COLUMNS_CHANGED)) {
			tableModel.setColumns(_dataModel.getModuleTableColumns());
		}
		else if (name.equals(DataModel.SEARCH_ITEM_CHANGED)) {
			ListSelectionModel selModel = this.table.getSelectionModel();
			selModel.clearSelection();
			
			if (_dataModel.getSelectedClusteringPair() == null || _dataModel.getSearchItem() == null)
				return;
			
			ArrayList<Vertex> arr = new ArrayList<Vertex>();
			arr.add(_dataModel.getSearchItem());
			highlightRows(selModel, arr);
		}
		else if (name.equals(DataModel.SELECTED_VERTICES_CHANGED) ) {
			ListSelectionModel selModel = this.table.getSelectionModel();
			selModel.clearSelection();
			
			ArrayList<Vertex> selectedVerts = _dataModel.getSelectedVertices();
			if (_dataModel.getSelectedClusteringPair() == null || selectedVerts == null)
				return;
			
			highlightRows(selModel, selectedVerts);
		}
		else if (name.equals(DataModel.SELECTED_CLUSTERING_PAIR_CHANGED)) {
			// update the table
			Score<Clustering> score = _dataModel.getSelectedClusteringPair();
			tableModel.setScore(score);
			
			if (score != null) {
				detail.setText("Selected: " + score.getXItem().getName() + " " + score.getYItem().getName());
			}
			else
				detail.setText("Selected: select clustering pair in ladder");
			detail.invalidate();
			detail.repaint();
			
			resetSlider();
		}
		else if (name.equals(DataModel.METRIC_SCORES_CHANGED)) {
			tableModel.setScore(null);
			resetSlider();
		}
	}

	private void highlightRows(ListSelectionModel selModel,
			ArrayList<Vertex> selectedVerts) {
		Set<Integer> rows = new TreeSet<Integer>();
		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);
		Score<Clustering> pair = this._dataModel.getSelectedClusteringPair();
		Clustering c = pair.getXItem();
		Collection<Module> mods = c.getModules();
		int row;
		for (Module m : mods)
			if (m.containsAny(selectedVerts)) {
				row = tableModel.getRowIndex(m);
				rows.add(row);
			}
		c = pair.getYItem();
		mods = c.getModules();
		for (Module m : mods)
			if (m.containsAny(selectedVerts)) {
				row = tableModel.getRowIndex(m);
				rows.add(row);
			}
		// select all these rows
		for (int r : rows)
			selModel.addSelectionInterval(r, r);
//		table.setCellSelectionEnabled(true);
	}

	private void resetSlider() {
		slider.setValue(0);
		slider.setUpperValue(100);
	}
	
	public void stateChanged(ChangeEvent e) {
		// slider's value has changed
		int lowerValue = slider.getValue();
		int upperValue = slider.getUpperValue();
		
		// update slider's start and end color
		Color startColor = GradientColorModel.getLadderColor(
				lowerValue / 100.0f, 1, false);
		Color endColor = GradientColorModel.getLadderColor(
				upperValue/100f, 1, false);
		
		slider.setStartColor(startColor);
		slider.setEndColor(endColor);
		
		this.tableModel.setLowerCutoff(lowerValue / 100.0f);
		this.tableModel.setUpperCutoff(upperValue / 100.0f);
	}
	
	//--------------------------------------------------------------------------
	//
	// List selection implementation
	//
	//--------------------------------------------------------------------------
	public void valueChanged(ListSelectionEvent e) {
		int col = this.table.getSelectedColumn();
		int row = this.table.getSelectedRow();
		
		if (row < 0 || col < 0) {
			return;
		}
		
		int adjRow = this.table.convertRowIndexToModel(row);
		int adjCol = this.table.convertColumnIndexToModel(col);
		
		ModulePair mp = tableModel.getRow(adjRow);
		String colName = table.getColumnName(adjCol);
		ArrayList<Vertex> value = mp.getValue(colName);
		
		_dataModel.removePropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		_dataModel.setSelectedVertices( (ArrayList<Vertex>) value);
		_dataModel.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
	}
}
