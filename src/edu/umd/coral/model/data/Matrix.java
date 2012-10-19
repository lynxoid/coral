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
package edu.umd.coral.model.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
//import Jama.EigenvalueDecomposition;

	/**
	 * Data structure for the co-occurence matrix. 
	 * @author amgadani
	 *
	 */
public class Matrix {
	
	public String [] rowNames;
	
	public String [] columnNames;
	
	protected int row_count = 0;
	
	protected int column_count = 0;

	private double _maximum = 0;
	
	/**
	 * matrix values
	 */
	private double [][] _data;
//	private FlexCompRowMatrix _data;
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	////////////////////////////////////////////////////////////////////////////
	
	public Matrix () {
		
	}
	
	/**
	 * Creates an empty NxM matrix  
	 * 
	 */
	public Matrix(int N, int M) {
		row_count = N;
		column_count = M;
		_data = new double [row_count][column_count];
		rowNames = new String[row_count];
		columnNames = new String[column_count];
	}
	
	public Matrix(String [] rowNames, String [] columnNames) throws NullPointerException {
		if (rowNames == null)
			throw new NullPointerException();
		if (columnNames == null)
			throw new NullPointerException();
		this.rowNames = rowNames;
		this.columnNames = columnNames;
		row_count = rowNames.length;
		column_count = columnNames.length;
		_data = new double[row_count][column_count];
	}
	
	// TODO: may not need this method
	public Matrix(double [][] data, String [] rowNames, String [] columnNames) {
		_data = data;
		this.rowNames = rowNames;
		this.columnNames = columnNames;
		
		if (rowNames != null)
			row_count = rowNames.length;
		if (columnNames != null)
			column_count = columnNames.length;
	}

	public Matrix(ArrayList<double[]> rowsArray, String[] rowNames, String[] columnNames, double f) {
		_data = rowsArray.toArray(new double[0][]);
		this.rowNames = rowNames;
		this.columnNames = columnNames;
		if (rowNames != null)
			row_count = rowNames.length;
		if (columnNames != null)
			column_count = columnNames.length;
		this._maximum = f;
	}
	
	public Matrix(double[][] data, 
			String[] rowNames, 
			String[] columnNames,
			double max) {
		
		_data = data;
		this.rowNames = rowNames;
		this.columnNames = columnNames;
		
		if (rowNames != null)
			row_count = rowNames.length;
		if (columnNames != null)
			column_count = columnNames.length;
		
		this._maximum = max;
	}

	public Matrix(FlexCompRowMatrix m, String[] col_names, String[] row_names) {
		int size = col_names.length;
		_data = new double[size][size];
		this.columnNames = col_names;
		this.rowNames = row_names;
		this.column_count = this.row_count = size;
		Iterator<MatrixEntry> iter = m.iterator();
		while (iter.hasNext()) {
			MatrixEntry me = iter.next();
			_data[me.row()][me.column()] = me.get();
		}
	}

	/**
	 * Returns an two-dimensional array w/ matrix values (not a copy or clone)
	 * @return
	 */
	public double [][] getData() {
		return _data;
	}
	
	/**
	 * Returns the value of the maximum element in the matrix
	 * @return
	 */
	public double getMax() {
		return _maximum;
	}
	
	/**
	 * Returns a value at (row, column)
	 * @param row
	 * @param column
	 * @return
	 */
	public double getElement(int row, int column) {
		if (row >= row_count || column >= column_count)
			throw new IndexOutOfBoundsException();
		return _data[row][column];
	}
	
	public void setElement(int row, int column, int value) {
		if (row >= row_count || column >= column_count)
			throw new IndexOutOfBoundsException();
		_data[row][column] = value;
		
		if (value > _maximum)
			_maximum = value;
	}
	public void setElement(String row, String column, int value){
		int r=0, c=0;
		// can not assume that rowNames and columnNames are sorted
		for (int i = 0; i < rowNames.length; i++)
			if (rowNames[i].equals(row)) {
				r = i; break;
			}
		for (int i = 0; i < columnNames.length; i++)
			if (columnNames[i].equals(row)) {
				c = i; break;
			}
		_data[r][c] = value;
	
	}
	
	/**
	 * Returns a new matrix that is a sum of the current object and m.
	 * 
	 * @param m
	 * @return
	 */
	public Matrix addMatrix(Matrix m) {
		if (m.getColumnCount() != this.column_count || m.getRowCount() != this.row_count)
			throw new IndexOutOfBoundsException();
		
		Matrix sum = Matrix.generateEmptySymMatrix(row_count, m.rowNames);
//		Matrix sum = new Matrix(row_count, column_count);
//		sum.columnNames = this.columnNames;
//		sum.rowNames = this.rowNames;
		
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				sum._data[i][j] = _data[i][j] + m._data[i][j];
				if (sum._data[i][j] > sum._maximum)
					sum._maximum = sum._data[i][j];
			}
		}
		
		return sum;
	}
	
	public Matrix addMatrix(BitSymmetricMatrix m) {
		Matrix sum = Matrix.generateEmptySymMatrix(row_count, m.rowNames);
		
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				sum._data[i][j] = _data[i][j] + m.getElement(i, j);
				if (sum._data[i][j] > sum._maximum)
					sum._maximum = sum._data[i][j];
			}
		}
		
		return sum;
	}
	
	public static Matrix generateEmptySymMatrix(int rowCount,
			String[] names) {
		Matrix copy = new Matrix(rowCount, rowCount);
		copy.rowNames = copy.columnNames = names;
		return copy;
	}

	/**
	 * Subtracts a matrix <code>m</code> from the current matrix (this). The resulting matrix
	 * is in the form a[i,j] = this[i,j] - m[i,j]. Returns a new matrix 
	 * containing the result. Does not modify either of <code>m</code> or this.
	 * 
	 * @param m
	 * @return
	 */
	public Matrix subtractMatrix(Matrix m) {
		if (m.getColumnCount() != this.column_count || m.getRowCount() != this.row_count)
			throw new IndexOutOfBoundsException();
		Matrix diff = new Matrix(row_count, column_count);
		diff.columnNames = this.columnNames;
		diff.rowNames = this.rowNames;
		
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				diff._data[i][j] = _data[i][j] - m._data[i][j];
				if (diff._data[i][j] > diff._maximum)
					diff._maximum = diff._data[i][j];
			}
		}
		
		return diff;
	}
	
	public Matrix subtractMatrix(BitSymmetricMatrix m) {
		if (m.getColumnCount() != this.column_count || m.getRowCount() != this.row_count)
			throw new IndexOutOfBoundsException();
		Matrix diff = new Matrix(row_count, column_count);
		diff.columnNames = this.columnNames;
		diff.rowNames = this.rowNames;
		
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				diff._data[i][j] = _data[i][j] - m.getElement(i, j);
				if (diff._data[i][j] > diff._maximum)
					diff._maximum = diff._data[i][j];
			}
		}
		
		return diff;
	}
	
	public int getRowCount() {
		return row_count;
	}
	
	public int getColumnCount() {
		return column_count;
	}
	
	public String getRowName(int row) {
		if (row >= this.row_count)
			throw new IndexOutOfBoundsException();
		return rowNames[row];
	}
	
	public String getColumnName(int column) {
		if (column >= this.column_count)
			throw new IndexOutOfBoundsException();
		return columnNames[column];
	}

	public void setFilteredData(double [][] data, int lowCutOff, int highCutOff) {
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				if (data[i][j] >= lowCutOff && data[i][j] <= highCutOff) {
					_data[i][j] = data[i][j];
					if (_data[i][j] > _maximum)
						_maximum = _data[i][j];
				}
				else
					_data[i][j] = 0;				
			}
		}
	}

	/**
	 * Returns a row w/ data
	 * 
	 * @param i
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public double[] getRow(int i) throws IndexOutOfBoundsException {
		if (i < 0 || i >= this.row_count)
			throw new IndexOutOfBoundsException();
		// TODO: return a copy?
		return _data[i];
	}
	
	/**
	 * Prints matrix out in a nice format:
	 * _| a | b | c
	 * a| 1 | 0 | 0
	 * ------------
	 * b| 2 | 0 | 0
	 * ------------
	 * c| 2 | 1 | 0
	 * 
	 */
	public String toString() {
//		return "";
		String output = "\t";
		int i, j;
		
		for (j = 0; j < column_count; j++)
			output += columnNames[j] + "\t";
		output += "\n";
		
		for (i = 0; i < row_count; i++) {
			output += rowNames[i] + "\t";
			for (j = 0; j < column_count; j++){
				if(_data[i][j] == 0.0){
					output+="   " + "\t";
				}
				else{
					output += _data[i][j] + "\t";
				}
			}
			output += "\n";
		}
		
		return output;
	}
	
	/**
	 * Same as toString(), but no row names
	 * 
	 * a | b | c
	 * 1 | 0 | 0
	 * 2 | 0 | 0
	 * 2 | 1 | 0
	 * 
	 * @return
	 */	
	public StringBuilder toStringBuilder(String delimiter) {
		StringBuilder output = new StringBuilder(delimiter);
		int i, j;
		
		// write column names
		for (j = 0; j < column_count; j++)
			output.append(columnNames[j] + delimiter);
		output.append("\n");
		
		// write row data
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				output.append(_data[i][j]);
				output.append(delimiter);
			}
			output.append("\n");
		}
		
		return output;
	}
	
	public BufferedWriter saveToFileAsCSVMatrix(BufferedWriter bf, String delimiter) throws IOException {
//		bf.write("row_name" + delimiter);
		int i, j;
		
		// write column names
		for (j = 0; j < column_count; j++) {
			bf.write("'" + columnNames[j] + "'");
			if (j < column_count - 1)
				bf.write(delimiter);
		}
		bf.write("\n");
		
		// write row data
		for (i = 0; i < row_count; i++) {
//			bf.write(rowNames[i] + delimiter);
			for (j = 0; j < column_count; j++) {
				bf.write(String.valueOf(_data[i][j]));
				if (j < column_count - 1)
					bf.write(delimiter);
			}
			bf.write("\n");
		}
		
		return bf;
	}
	
	/**
	 * As adjacency list
	 * 
	 * @param bf
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public BufferedWriter saveToFileAsAdjacencyList(BufferedWriter bf, String delimiter) throws IOException {
//		bf.write(delimiter);
		int i, j;
		
		String a, b;
		
		// write row data
		for (i = 0; i < row_count; i++) {
			a = rowNames[i];
			
			for (j = i + 1; j < column_count; j++) {
				b = columnNames[j];
				
				if (_data[i][j] > 0) {
					// edge
					bf.write(a);
					bf.write(delimiter);
					bf.write(b);
					bf.write(delimiter);
					
					// edge weight
					bf.write(String.valueOf( (int) _data[i][j] ));
//					bf.write(delimiter);
					bf.write("\n");
				}
			}
//			bf.write("\n");
		}
		
		return bf;
	}

	public int getRowIndex(String name) {
		if (this.rowNames == null)
			return -1;
		for (int i = 0; i < rowNames.length; i++)
			if (rowNames[i].equals(name)) return i;
		return -1;
	}

	public int getColumnIndex(String name) {
		if (this.columnNames == null)
			return -1;
		for (int i = 0; i < columnNames.length; i++)
			if (columnNames[i].equals(name)) return i;
		return -1;
	}
	
	/**
	 * Returns a laplacian of a current matrix (==graph). Laplacian is a D - A
	 * where A is adjacency matrix, and D is a degree matrix, i.e. 
	 * d(i,i) = deg(v_i)
	 * 
	 * @return
	 */
	/*
	public Jama.Matrix getLaplacianMatrix() {
		int size = this.getColumnCount();
		double [][] data_copy = new double[size][size];
		
		int i, j;
		
		for (i = 0; i < size; i++) {
			data_copy[i][i] = 0;//-this._data[i][i];
			for (j = 0; j < size; j++) {
				if (i != j) {
					data_copy[i][j] = -this._data[i][j];// > 0 ? -1: 0;
					// symmetry
					data_copy[j][i] = data_copy[i][j];
					data_copy[i][i] += this._data[i][j];	// accumulate vertex degree on diag
				}
								
			}
		}
		
		
		Jama.Matrix jamaM = new Jama.Matrix(data_copy, row_count, column_count);
		return jamaM;
	}
	*/
	
	/**
	 * 
	 * @return
	 */
	/*
	public EigenvalueDecomposition getEigenDecomposition() {
		double [][] copy = new double[row_count][row_count];
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = i; j < row_count; j++) {
				copy[i][j] = copy[j][i] = this._data[i][j];
			}
		}
		
		Jama.Matrix jamaM = new Jama.Matrix(copy, row_count, column_count);
		EigenvalueDecomposition ed = new EigenvalueDecomposition(jamaM);
		
		return ed;
	}
	*/
	
	/**
	 * A sum of |diagonal - last non-zero element on the row| for all rows
	 * 
	 * @return
	 */
	public int getBandwidth() {
		int sum = 0;
		
		for (int i = 0; i < column_count - 1; i++) {
			for (int j = column_count - 1; j > i; j--)
				if (_data[i][j] > 0) {
					sum += j - i;
					break;
				}
		}
		return sum;
	}

	/**
	 * Finds connected components in the graph induced by the matrix m minus
	 * all edges of weight less than cutoffValue (min value = 0). Returns
	 * an array of Matrix objects for each connected component. 
	 * 
	 * @param m
	 * @return
	 */
	public static Matrix[] getConnectedComponents(Matrix m) {
		int i, size = m.row_count;
		Set<Integer> allVertices = new HashSet<Integer>();
		
		for (i = 0; i < size; i++) {
			allVertices.add(i);
		}
		
		Set<Integer> nodes_to_expand_now = new HashSet<Integer>();
		Set<Integer> nodes_to_expand_later = new HashSet<Integer>();
		Set<Integer> component_vertices = new HashSet<Integer>();
		List<Set<Integer>> vertex_sets = new ArrayList<Set<Integer>>();
		
		component_vertices.add(0);
		nodes_to_expand_now.add(0);
		
		Set<Integer> visited = new HashSet<Integer>();
		double value;
		
		while (visited.size() < size) {
			for (int node_index : nodes_to_expand_now) {
				// go through the row, add neighbors that are not yet in the set
				for (i = 0; i < size; i++) {
					value = m.getElement(node_index,i);
					if (value > 0 && node_index != i && !component_vertices.contains(i)) { // edge
						nodes_to_expand_later.add(i);
						component_vertices.add(i);
					}
				}
			}

			nodes_to_expand_now.clear();
			nodes_to_expand_now.addAll(nodes_to_expand_later);
			nodes_to_expand_later.clear();
			
			if (nodes_to_expand_now.size() == 0) {
				// flush nodes from visited to a component
				vertex_sets.add(new HashSet<Integer>(component_vertices));
				visited.addAll(component_vertices);
				component_vertices.clear();
				
				// get a new unvisited vertex
				Set<Integer> temp = new HashSet<Integer>();
				temp.addAll(allVertices);
				temp.removeAll(visited);
				
				if (temp.size() > 0) {
					// add one vertex to expand
					Iterator<Integer> iterator = temp.iterator();
					nodes_to_expand_now.add(iterator.next());
					component_vertices.addAll(nodes_to_expand_now);
					//nodes_to_expand_now.addAll(temp);
				}
			}
		}
		
		return componentsToMatrices(m, vertex_sets);
	}
	
	public static Matrix[] getConnectedComponents2(Matrix m) {
		Set<Integer> vertices = new HashSet<Integer>();
		Set<Integer> visited = new HashSet<Integer>();
		
		// add all vert
		for (int i = 0; i < m.getColumnCount(); i++) {
			vertices.add(i);
		}
		
		List<Set<Integer>> components = new ArrayList<Set<Integer>>();
		Set<Integer> component;
		
		// BFS
		while (!vertices.isEmpty()) {
			Iterator<Integer> iter = vertices.iterator();
			Integer v = iter.next(), t;
			
			component = new HashSet<Integer>();
			components.add(component);
			
			// BFS
			Set<Integer> Q = new HashSet<Integer>();
			Q.add(v); component.add(v);
			visited.add(v);
			while (!Q.isEmpty()) {
				Iterator<Integer> iterator = Q.iterator();
				t = iterator.next();
				Q.remove(t); // deq
				for (int i = 0; i < m.getColumnCount(); i++) { // for all t's neighbors
					int u = m.getElement(t, i) > 0 ? i : -1;
					if (u > 0)
						if (!visited.contains(u)) {
							visited.add(u);
							Q.add(u); component.add(u);
						}
				}
			}
			vertices.removeAll(component);
		}
		
		return componentsToMatrices(m, components);
	}
	
	private static Matrix [] componentsToMatrices(Matrix m, List<Set<Integer>> components2) {
		int i, size = components2.size();
		Matrix [] components = new Matrix[size];
		int component_size, l, k;
		for (i = 0; i < size; i++) {
			Set<Integer> component = components2.get(i);
			component_size = component.size();
			double [][] data = new double[component_size][component_size];
			String [] names = new String[component_size];
			
			// set data
			k = 0;
			for (int index1 : component) {
				l = 0;
				names[k] = m.getRowName(index1);
				for (int index2 : component) {
					data[k][l] = data[l][k] = m.getElement(index1, index2);
					l++;
				}
				k++;
			}
			components[i] = new Matrix(data, names, names /* should be a copy? */);
		}
		
		
		return components;
	}

	/**
	 * Given blocks A1, A2, A3, ..., constructs a block matrix A:
	 * 		|A1				|
	 * 		|   A2			|
	 * A = 	|      A3		|
	 * 		|  		  ...	|
	 * 		|			 An |
	 * 
	 * @param reorderedComponents
	 * @param max 
	 * @return
	 */
	public static Matrix mergeComponents(Matrix [] reorderedComponents, double max) {
		int smallSize;
		int bigSize = 0;
		
		for (Matrix m : reorderedComponents) {
			smallSize = m.row_count;
			bigSize += smallSize;
		}
		
		double [][] data = new double[bigSize][bigSize];
		String [] names = new String[bigSize];
		
		int offset = 0, i, j;
		for (Matrix m : reorderedComponents) {
			smallSize = m.row_count;
			for (i = 0; i < smallSize; i++) {
				names[offset + i] = m.getRowName(i);
				for (j = 0; j < smallSize; j++) {
					data[offset + i][offset + j] = m._data[i][j];
				}
			}
			offset += smallSize;
		}
		
		return new Matrix(data, names, names, max);
	}	
	
	public static Matrix reorderSubMatrix(Matrix matrix, Matrix referenceMatrix) {
		if (matrix == null || referenceMatrix == null)
			return null;
		
		int i, j;
		int rowCount = matrix.getRowCount();
		String [] names = new String[rowCount];
		double [][] data = new double[rowCount][rowCount];
		int oldRowIndex, oldColumnIndex;
		String rowName, columnName;
		
		for (i = 0; i < rowCount; i++) {
			rowName = referenceMatrix.getRowName(i);
			oldRowIndex = matrix.getRowIndex(rowName);
			names[i] = rowName;
			
			for (j = 0; j < rowCount; j++) {
				columnName = referenceMatrix.getColumnName(j);
				oldColumnIndex = matrix.getColumnIndex(columnName);
				data[i][j] = matrix.getElement(oldRowIndex, oldColumnIndex);
			}
		}
		
		Matrix m = new Matrix(data, names, names, matrix.getMax());

		return m;
	}

	protected void setMax(double d) {
		this._maximum = d;
	}
}
