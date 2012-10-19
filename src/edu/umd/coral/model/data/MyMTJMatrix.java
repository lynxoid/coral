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

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;


/**
 * Wrapper for matrix class T
 * 
 * @author lynxoid
 *
 * @param <T>
 */
public class MyMTJMatrix extends edu.umd.coral.model.data.Matrix {
	
	private FlexCompRowMatrix matrix;

	/**
	 * Create square matrix
	 * @param size
	 */
	public MyMTJMatrix(int size) {
		super(size, size);
		matrix = new FlexCompRowMatrix(size, size);
	}

	/**
	 * Crate square matrix with initial data, rowNames, columnNames
	 * 
	 * @param data
	 * @param names
	 * @param names2
	 * @param max
	 */
	public MyMTJMatrix(double [][] data, String[] rowNames, String[] columnNames, double max) {
		super(rowNames, columnNames);
		super.setMax((float)max);
		// create matrix object
		DenseMatrix dense = new DenseMatrix (data);
		matrix = new FlexCompRowMatrix(dense, true);
	}

	public MyMTJMatrix(double [][] data, int size) {
		DenseMatrix dense = new DenseMatrix (data);
		matrix = new FlexCompRowMatrix(dense, true);
	}

	public MyMTJMatrix(double [][] data, String [] rowNames, String [] columnNames) {
		super(rowNames, columnNames);
		// create matrix object
		DenseMatrix dense = new DenseMatrix(data);
		matrix = new FlexCompRowMatrix(dense, true);
	}
	
	public MyMTJMatrix(String [] rowNames, String [] columnNames) {
		// create matrix object
		super(rowNames, columnNames);
		matrix = new FlexCompRowMatrix(rowNames.length, columnNames.length);
	}

//	public MyFlexCompRowMatrixMTJ getLaplacianMatrix() {
//		int size = getSize();
//		double [][] data_copy = new double[size][size];
//		
//		int i, j;
//		
//		for (i = 0; i < size; i++) {
//			data_copy[i][i] = 0;//-this._data[i][i];
//			for (j = 0; j < size; j++) {
//				if (i != j) {
////					data_copy[i][j] = -this.matrix[i][j];// > 0 ? -1: 0;
//					data_copy[i][j] = -matrix.get(i, j);// > 0 ? -1: 0;
//					
//					// symmetry
//					data_copy[j][i] = data_copy[i][j];
//					//data_copy[i][i] += this.matrix[i][j];	// accumulate vertex degree on diag
//					data_copy[i][i] += matrix.get(i, j);	// accumulate vertex degree on diag
//				}
//								
//			}
//		}
//		
//		MyFlexCompRowMatrixMTJ m = new MyFlexCompRowMatrixMTJ(data_copy, size);
//		return m;
//	}

	public MyMTJMatrix(FlexCompRowMatrix m, String[] col_names, String[] row_names) {
		super.columnNames = col_names;
		super.rowNames = row_names;
		super.column_count = col_names.length;
		super.row_count = row_names.length;
		this.matrix = m;
	}

	public int getSize() {
		if (matrix != null)
			return matrix.numColumns();
		else 
			return 0;
	}

	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public double getElement(int i, int j) {
		return (float)matrix.get(i, j);
	}
	
	public void setElement(int r, int c, int value) {
		matrix.set(r, c, value);
	}

	/**
	 * @return 
	 */
	public MyMTJMatrix addMatrix(BitSymmetricMatrix m) {
		int i, j;
		for (i = 0; i < row_count; i++) {
			for (j = 0; j < column_count; j++) {
				this.matrix.set(i, j, matrix.get(i, j) + m.getElement(i, j) );
				if (this.matrix.get(i, j) > getMax())
					super.setMax( (float)this.matrix.get(i, j));
			}
		}
		
		return this;
	}
	
	public MyMTJMatrix addMatrix(MyMTJMatrix m) {
		if (m.getColumnCount() != this.column_count || m.getRowCount() != this.row_count)
			throw new IndexOutOfBoundsException();
		
//		Matrix sum = Matrix.generateEmptySymMatrix(row_count, m.rowNames);
//		Matrix sum = new Matrix(row_count, column_count);
//		sum.columnNames = this.columnNames;
//		sum.rowNames = this.rowNames;
		
		no.uib.cipr.matrix.Matrix sum = this.matrix.add(m.matrix);
		MyMTJMatrix temp = new MyMTJMatrix(this.column_count);
		temp.matrix = new FlexCompRowMatrix(sum, false);
		return temp;
	}

	public FlexCompRowMatrix getMatrix() {
		return this.matrix;
	}

	protected MyMTJMatrix makeMatrix(double[][] data, String[] rowNames, String[] colNames, double max) {
		MyMTJMatrix j = new MyMTJMatrix(data, rowNames, colNames, max);
		return j;
	}

	protected MyMTJMatrix makeMatrix(double[][] data, String[] rowNames, String[] columnNames) {
		MyMTJMatrix j = new MyMTJMatrix(data, rowNames, columnNames);
		return j;
	}
	
	public BufferedWriter saveToFileAsCSVMatrix(BufferedWriter bf, String delimiter) throws IOException {
		bf.write(delimiter);
		int i, j;
		
		// write column names
		for (j = 0; j < getSize(); j++) {
			bf.write("'" + getColumnName(j) + "'");
			if (j < getSize() - 1)
				bf.write(delimiter);
		}
		bf.write("\n");
		
		// write row data
		for (i = 0; i < getSize(); i++) {
			for (j = 0; j < getSize(); j++) {
				bf.write(String.valueOf(matrix.get(i,j)));
				if (j < getSize() - 1)
					bf.write(delimiter);
			}
			bf.write("\n");
		}
		
		return bf;
	}
	
	/**
	 * Reorder according to referenceMatrix rowOrder
	 * @param matrix
	 * @param referenceMatrix
	 * @return
	 */
	public static MyMTJMatrix reorderSubMatrix(Matrix matrix, Matrix referenceMatrix) {
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
		
		MyMTJMatrix m = new MyMTJMatrix(data, names, names, matrix.getMax());

		return m;
	}
}
