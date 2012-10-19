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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import edu.umd.coral.model.data.Cooccurrence;
import edu.umd.coral.model.data.Vertex;

public class Vertex2VertexTableModel extends AbstractTableModel {
	
	/**
	 * Columns that V2V table can show
	 */
	public static final String ITEM_NAME = "u";
	public static final String ITEM_PAIR_NAME = "v";
	public static final String CO_OCCUR_COUNT = "# co-occur";
	public static final String SIGNATURE = "signature";
	public static final String NET_NEIGHB = "u's neighbors";
	public static final String NET_PAIR_NEIGHB = "v's neighbors";
	
	public static final String V2V_TABLE = "v2vTable";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7020319317613100863L;
	
//	private Edge<Vertex> selectedPair;
	
	private Cooccurrence [] data;
	
	
	private ArrayList<String> columnNames = new ArrayList<String>();
	
	private boolean compareCustomVertex = false;
	private boolean compareCustomPair = false;
	
	private Vertex u, v;
	
	public Vertex2VertexTableModel() {
		String [] arr = {ITEM_NAME,
				ITEM_PAIR_NAME, 
				CO_OCCUR_COUNT, 
				SIGNATURE};
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
	
	public int getColumnCount() {
		return columnNames.size();
	}
	
	public String getColumnName(int col) {
		if (columnNames != null)
			return columnNames.get(col);
		return null;
    }

	public int getRowCount() {
		if (data != null)
			return data.length;
		return 0;
	}
	
	public Cooccurrence getRow(int row) {
		if (data == null || row >= data.length || row < 0)
			return null;
		return data[row];
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || columnIndex < 0 || columnNames == null || data == null)
			return null;
		
		Cooccurrence pair = data[rowIndex];
		String column = this.columnNames.get(columnIndex);
		if (column.equals(ITEM_NAME))
			return pair.u.getName();
		if (column.equals(ITEM_PAIR_NAME))
			return pair.v.getName();
		if (column.equals(CO_OCCUR_COUNT))
			return pair.getCooccurrenceCount();
		if (column.equals(SIGNATURE))
			return pair;
		if (column.equals(NET_NEIGHB))
			return pair.u.getNeighbors();
		if (column.equals(NET_PAIR_NEIGHB))
			return pair.v.getNeighbors();
		
		return null;
	}
	
	public Comparator<?> getColumnComparator(int columnIndex) {
		String column = this.columnNames.get(columnIndex);
		
		if (compareCustomVertex) {
			compareCustomVertex = false;
			return new VertexCustomComparator(u);
		}
		if (this.compareCustomPair) {
			this.compareCustomPair = false;
			return new PairCustomComparator(u, v);
		}
		
		if (column.equals(ITEM_NAME))
			return new StringComparator();
		if (column.equals(ITEM_PAIR_NAME))
			return new StringComparator();
		if (column.equals(CO_OCCUR_COUNT))
			return new IntComparator(); // TODO why does IntComparator fail?
//			return new StringComparator();
//		if (column.equals(SIGNATURE))
//			return new SignatureComparator();
		if (column.equals(NET_NEIGHB))
			return new StringComparator();
		if (column.equals(NET_PAIR_NEIGHB))
			return new StringComparator();
		return null;
	}

	/**
	 * 
	 * @param vertexPairs
	 */
	public void setData(Map<Vertex, TreeMap<Vertex, Cooccurrence>> vertexPairs) {
		data = toCooccurrenceArray(vertexPairs);
		
		Map<Integer, Integer> hist = new HashMap<Integer, Integer>();
		for (Cooccurrence c : data) {
			int value = c.getCooccurrenceCount();
			if (hist.containsKey(value))
				hist.put(value, hist.get(value) + 1);
			else
				hist.put(value, 1);
		}
		
		// print hist
		System.out.println("Co-cluster : counts");
		for (int value : hist.keySet()) {
			System.out.println(value + " : " + hist.get(value));
		}
		
		fireTableDataChanged();
	}

	private Cooccurrence [] toCooccurrenceArray(
			Map<Vertex, TreeMap<Vertex, Cooccurrence>> vertexPairs) {
		if (vertexPairs == null)
			return null;
		int size = 0;
		for (Map<Vertex, Cooccurrence> map : vertexPairs.values()) {
			for (Cooccurrence c : map.values()) {
				if (!c.selfReferential()) {
					size++;
				}
			}
		}// now we know size
		Cooccurrence [] cooccurrences = new Cooccurrence[size];
		
		int i = 0;
		for (Map<Vertex, Cooccurrence> map : vertexPairs.values()) {
			for (Cooccurrence c : map.values()) {
				if (!c.selfReferential()) {
					cooccurrences[i] = c;
					i++;
				}
			}
		}
		// sorted so that we can use BinarySort on it later
		Arrays.sort(cooccurrences); // sorts in place
		return cooccurrences;
	}
	
	public class FloatComparator implements Comparator<Float> {
		
		public int compare(Float i1, Float i2) {
			if (i1 < i2) return -1;
			if (i1 > i2) return 1;
			return 0;
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public class IntComparator implements Comparator {
		
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
	
	public class StringComparator implements Comparator<String> {
		
		public int compare(String s1, String s2) {
//			try {
//				int i1 = Integer.parseInt(s1);
//				int i2 = Integer.parseInt(s2);
//				if (i1 < i2) return -1;
//				if (i1 > i2) return 1;
//				return 0;
//			}
//			catch (NumberFormatException e) {
				return s1.compareTo(s2);
//			}
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public class VertexCustomComparator implements Comparator {
		private Vertex v;
		
		public VertexCustomComparator(Vertex v) {
			this.v = v;
		}

		public int compare(Object o1, Object o2) {
			if (o1 instanceof String && o2 instanceof String) {
				String s1 = (String)o1, s2 = (String)o2;
				if (s1.equals(v.getName()))
					return -1;
				if (s2.equals(v.getName()))
					return -1;
				return s1.compareTo(s2);
			}
			return 0;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public class PairCustomComparator implements Comparator {
		private Vertex u, v;
		
		public PairCustomComparator(Vertex u, Vertex v) {
			this.u = u;
			this.v = v;
		}

		public int compare(Object o1, Object o2) {
			if (o1 instanceof String && o2 instanceof String) {
				String s1 = (String)o1, s2 = (String)o2;
				if (s1.equals(u.getName()) && s2.equals(v.getName()) ||
					s1.equals(v.getName()) && s2.equals(u.getName()))
					return -1;
				return s1.compareTo(s2);
			}
			return 0;
		}
	}

	public static ArrayList<String> getColumnNames() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(ITEM_NAME);list.add(ITEM_PAIR_NAME);list.add(CO_OCCUR_COUNT);
		list.add(SIGNATURE);list.add(NET_NEIGHB);list.add(NET_PAIR_NEIGHB);
		return list;
	}

	public int getColumnIndex(String name) {
		if (this.columnNames != null)
			return this.columnNames.indexOf(name);
		
		return -1;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getSelectedColmnNames() {
		return (ArrayList<String>)columnNames.clone();
	}
	
	public int setSortToTop(Collection<Vertex> col) {
		if (col == null) {
			this.compareCustomPair = false;
			this.compareCustomVertex = false;
			return 0;
		}
		else if (col.size() == 2) {
			Iterator<Vertex> iter = col.iterator();
			this.u = iter.next();
			this.v = iter.next();
			
			return 1;
		}
		return 0;
	}

	public int sortMatchesToTop(Vertex v) {
		return sortToTop(data, v);
	}
	
	/**
	 * Sorts data in place, returns the number of rows at the beginning of the list
	 * that contain u in the first or second column
	 * 
	 * @param data
	 * @param u
	 * @return
	 */
	private int sortToTop(Cooccurrence [] data, Vertex u) {
		final Vertex finalU = u;
		// float all rows w/ u in the first column to the top
		Arrays.sort(data, new Comparator<Cooccurrence>() {
			// sort on the first column
			public int compare(Cooccurrence o1, Cooccurrence o2) {
				if (o1.u.equals(finalU)) return -1;
				if (o2.u.equals(finalU)) return 1;
				return o1.u.compareTo(o2.u);
			}
		});
		
		int startIndex = 0;
		boolean notFound = true;
		while (startIndex < data.length && notFound) {
			if (data[startIndex].u.equals(u))
				startIndex++;
			else 
				notFound = false;
		}
		int endIndex = data.length;
		Cooccurrence [] sublis = new Cooccurrence[endIndex - startIndex + 1];
		for (int i = startIndex; i <= endIndex; i++)
			sublis[i-startIndex] = data[i];
		Arrays.sort(sublis, new Comparator<Cooccurrence>() {
			// sort on the second column
			public int compare(Cooccurrence o1, Cooccurrence o2) {
				if (o1.v.equals(finalU)) return -1;
				if (o2.v.equals(finalU)) return 1;
				return o1.v.compareTo(o2.v);
			}
		});
		
		notFound = true;
		while (startIndex < data.length && notFound) {
			if (data[startIndex].v.equals(u))
				startIndex++;
			else 
				notFound = false;
		}
		return startIndex;
	}

	public int getIndex(Cooccurrence c) {
		if (data == null)
			return -1;

		int index = Arrays.binarySearch(data, c);
		return index;
	}


}
