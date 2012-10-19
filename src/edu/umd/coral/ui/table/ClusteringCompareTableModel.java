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
package edu.umd.coral.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.ModulePair;
import edu.umd.coral.model.data.Score;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.metrics.JaccardMetric;

public class ClusteringCompareTableModel extends AbstractTableModel {
	
	public static final float SCORE_CUTOFF = 0.3f;

	/**
	 * Columns that CC table can show
	 */	
	public static final String ITEM_NAME = "m1";
	public static final String ITEM_PAIR_NAME = "m2";
	public static final String SCORE = "Jaccard";
	public static final String INTERSECTION = "m1 \u2229 m2";
	public static final String INT_SIZE = "|m1 \u2229 m2|";
	public static final String LEFT_DIFF = "m1 \\ m2";
	public static final String LEFT_DIFF_SIZE = "|m1 \\ m2|";
	public static final String RIGHT_DIFF = "m2 \\ m1";
	public static final String RIGHT_DIFF_SIZE = "|m2 \\ m1|";
	public static final String UNION = "m1 \u222A m2";
	public static final String UNION_SIZW = "|m1 \u222A m2|";
	
	public static final String C2C_TABLE = "c2cTable";
	
	private float lowerCutoff;
	
	private float upperCutoff;
	
	private float lowerModulesizeCutoff = 2;
//	private float upperModulesizeCutoff = Integer.MAX_VALUE;

	private Score<Clustering> selectedPair;
	
	private ArrayList<ModulePair> data;
	
	private ArrayList<ModulePair> filteredData;
	
	private ArrayList<String> columnNames = new ArrayList<String>();
	
	public ClusteringCompareTableModel() {
		String [] arr = {ITEM_NAME,
				ITEM_PAIR_NAME, 
				SCORE, 
				INTERSECTION,
				LEFT_DIFF,
				RIGHT_DIFF};
		for (String s : arr)
			columnNames.add(s);
	}
	
	//////////////////////////////////////////////////
	//
	// Update the columns collection
	//
	//////////////////////////////////////////////////
	public void setColumns(ArrayList<String> columns) {
		this.columnNames = columns;
		this.fireTableStructureChanged();
	}

	//////////////////////////////////////////////////
	//
	// Abstract table model methods
	//
	//////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1253338653386968636L;

	

	
	public int getColumnCount() {
		if (columnNames != null)
			return columnNames.size();
		return 0;
	}
	
	public String getColumnName(int col) {
		if (columnNames != null)
			return columnNames.get(col);
		return "blah";
    }
	
	public int getRowCount() {
		if (filteredData != null)
			return filteredData.size();
		return 0;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || columnIndex < 0 || columnNames == null || filteredData == null)
			return null;
		
		String column = this.columnNames.get(columnIndex);
		ModulePair pair = this.filteredData.get(rowIndex);
		Object value = null;
		Collection<Vertex> collection;
		
		if (column.equals(ITEM_NAME)) {
			value = pair.getM1().getName();
		}
		else if (column.equals(ITEM_PAIR_NAME)) {
			value = pair.getM2().getName();
		}
		else if (column.equals(INTERSECTION)) {
			collection = pair.getIntersection();
			value = collectionToString(collection);
		}
		else if (column.equals(LEFT_DIFF)) {
			collection = pair.getLeftDifference();
			value = collectionToString(collection);
		}
		else if (column.equals(RIGHT_DIFF)) {
			collection = pair.getRightDifference();
			value = collectionToString(collection);
		}
		else if (column.equals(SCORE)) {
			value = pair.getScore();
		}
		else if (column.equals(INT_SIZE)){
			value = pair.getIntersection().size();
		}
		else if (column.equals(LEFT_DIFF_SIZE)) {
			value = pair.getLeftDifference().size();
		}
		else if (column.equals(RIGHT_DIFF_SIZE)) {
			value = pair.getRightDifference().size();
		}
		
		return value;
	}
	
	//////////////////////////////////////////////////
	//
	// Getters, setters, aux methods
	//
	//////////////////////////////////////////////////
	
	private String collectionToString(Collection<Vertex> collection) {
		String str = "", name;
		for (Vertex v : collection) {
			name = v.getName();
			str += name + ",";
		}
		
		return str;
	}
	
	private String collectionToTooltipString(Collection<Vertex> collection) {
		String str = "<html>", name;
		int i = 0, line_length = 0, N = collection.size();
		final int MAX_LINE_LEN = 60;
		final int MAX_ITEMS = 40;
		
		if (N == 0) return null;
		
		for (Vertex v : collection) {
			if (i > MAX_ITEMS) {
				str = str.substring(0, str.length() - 2);
				str += " and " + (N - i) + " more";
				break;
			}
			name = v.getName();
			// if will overflow the string - new line
			if (name.length() + line_length > MAX_LINE_LEN) {
				str += "<br>" + name + ", ";
				line_length = name.length() + 2;
			}
			else {
				if (i < N - 1) {
					str += name + ", ";
					line_length += name.length() + 2;
				}
				else {
					str += name;
					line_length += name.length();
				}
			}
			i++;
		}
		
		return str + "</html>";
	}
	
	public Comparator<?> getColumnComparator(int columnIndex) {
		String column = this.columnNames.get(columnIndex);
		if (column.equals(ITEM_NAME))
			return new StringComparator();
		if (column.equals(ITEM_PAIR_NAME))
			return new StringComparator();
		if (column.equals(INT_SIZE))
			return new IntComparator();
		if (column.equals(LEFT_DIFF_SIZE))
			return new IntComparator();
		if (column.equals(RIGHT_DIFF_SIZE))
			return new IntComparator();
		return new StringComparator();
	}
	
	public float getLowerCutoff() {
		return lowerCutoff;
	}

	public void setLowerCutoff(float lowerCutoff) {
		if (lowerCutoff == this.lowerCutoff)
			return;
		
		this.lowerCutoff = lowerCutoff;
		updateData();
	}

	public float getUpperCutoff() {
		return upperCutoff;
	}

	public void setUpperCutoff(float upperCutoff) {
		if (upperCutoff == this.upperCutoff)
			return;
		
		this.upperCutoff = upperCutoff;
		updateData();
	}
	
	public Score<Clustering> getScores() {
		return selectedPair;
	}

	/**
	 * 
	 * 
	 * @param s
	 */
	public void setScore(Score<Clustering> s) {
		this.selectedPair = s;
		
		if (s == null) {
			filteredData = data = null;
			fireTableDataChanged();
			return;
		}
		
		// convert to ModulePairs
		Clustering c1 = s.getXItem();
		Clustering c2 = s.getYItem();
		
		if (c1 == null || c2 == null) {
			// empty the table
			filteredData = data = null;
			fireTableDataChanged();
			return;
		}
		
		Collection<Module> modules1 = c1.getModules();
		Collection<Module> modules2 = c2.getModules();
		
		Iterator<Module> iterator1 = modules1.iterator();
		Iterator<Module> iterator2;
		
		Module module1, module2;
		ModulePair pair;
		JaccardMetric metric = new JaccardMetric();
		ArrayList<ModulePair> collection = new ArrayList<ModulePair>();
		float score;
		
		while (iterator1.hasNext()) {
			module1 = iterator1.next();
			// filter off modules of small size
			if (module1.getSize() < lowerModulesizeCutoff)
				continue;
			
			iterator2 = modules2.iterator();
			while (iterator2.hasNext()) {
				module2 = iterator2.next();
				// filter off modules of small size				
				if (module2.getSize() < lowerModulesizeCutoff)
					continue;
				
				score = metric.getScore(module1.getVertices(), module2.getVertices());
				
				if (score >= SCORE_CUTOFF) {
					pair = new ModulePair(module1, module2, score);
					collection.add(pair);
				}				
			}
		}
		
		filteredData = data = collection;
		
		// notify table that the data has changed
		this.fireTableDataChanged();
	}
	
	@SuppressWarnings("unchecked")
	private void updateData() {
		if (data == null)
			return;
		
		filteredData = (ArrayList<ModulePair>) data.clone();
		
		float score;
		// maybe do the lazy delete - mark it deleted instead of rebuilding array each time
		for (ModulePair pair : data) {
			score = pair.getScore();
			if (score < this.lowerCutoff || score > this.upperCutoff)
				filteredData.remove(pair);
		}
		this.fireTableDataChanged();
	}
	
	public class StringComparator implements Comparator<String> {
		
		public int compare(String i1, String i2) {
			return i1.compareTo(i2);
		}
		
	}
	
	public class FloatComparator implements Comparator<Float> {
		
		public int compare(Float f1, Float f2) {
			if (f1 < f2) return -1;
			if (f1 > f2) return 1;
			return 0;
		}
	}
	
	public class IntComparator implements Comparator<Object> {
		
		public int compare(Integer i1, Integer i2) {
			if (i1 < i2) return -1;
			if (i1 > i2) return 1;
			return 0;
		}

		public int compare(Object o1, Object o2) {
			if (o1 instanceof String && o2 instanceof String) {
				int i1 = Integer.parseInt((String) o1);
				int i2 = Integer.parseInt((String) o2);
				if (i1 < i2) return -1;
				if (i1 > i2) return 1;
				return 0;
			}
			return 0;
		}
		
	}
	
	public int getColumnIndex(String name) {
		if (this.columnNames != null)
			return this.columnNames.indexOf(name);
		
		return -1;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getSelectedColumnNames() {
		return (ArrayList<String>)columnNames.clone();
	}

	public static ArrayList<String> getColumnNames() {
		ArrayList<String> l = new ArrayList<String>();
		l.add(ITEM_NAME);l.add(ITEM_PAIR_NAME);l.add(SCORE);l.add(INTERSECTION);
		l.add(INT_SIZE);l.add(LEFT_DIFF);l.add(LEFT_DIFF_SIZE);
		l.add(RIGHT_DIFF);l.add(RIGHT_DIFF_SIZE);
		return l;
	}

	/**
	 * Returns a row index that contains this module in one of the columns. If 
	 * no row contains it, the returned index is negative.
	 * @param m
	 * @return
	 */
	public int getRowIndex(Module m) {
		for (int i = 0; i < filteredData.size(); i++) {
			ModulePair mp = filteredData.get(i);
			if (mp.getM1().equals(m) || mp.getM2().equals(m) )
					return i;
		}
		return -1;
	}

	public ModulePair getRow(int row) {
		if (row < 0 || filteredData == null) return null;
		if (row >= this.filteredData.size()) return null;
	 	return filteredData.get(row);
	}

	public String getToolTipAt(int rowIndex, String column) {
		Collection<Vertex> collection;
		ModulePair pair = this.filteredData.get(rowIndex);
		String value = null;
		
		if (column.equals(INTERSECTION)) {
			collection = pair.getIntersection();
			value = collectionToTooltipString(collection);
		}
		else if (column.equals(LEFT_DIFF)) {
			collection = pair.getLeftDifference();
			value = collectionToTooltipString(collection);
		}
		else if (column.equals(RIGHT_DIFF)) {
			collection = pair.getRightDifference();
			value = collectionToTooltipString(collection);
		}
		return value;
	}
}
