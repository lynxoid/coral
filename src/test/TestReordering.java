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
package test;

import junit.framework.TestCase;
import edu.umd.coral.clustering.LAPAwesomeReordering;
//import edu.umd.coral.clustering.SpectralReordering;
import edu.umd.coral.model.data.Matrix;

public class TestReordering extends TestCase {
	
	/*
	public void testSpectralPreserveValues() {
		double [][] data = {
				{0, 0, 1, 1},
				{0, 0, 1, 1},
				{1, 1, 1, 0},
				{1, 1, 0, 0}
		};
		String [] names = {"a", "b", "c", "d"};
		Matrix matrix = new Matrix(data, names, names);
		SpectralReordering so = new SpectralReordering();
		Matrix rMatrix = so.reorder(matrix);
		
		// make sure a-a is 0, a-b is 1
		String [] rowNames = rMatrix.rowNames;
		
		int i, j, k, index1 = -1, index2 = -1;
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				for (k = 0; k < 4; k++) {
					if (names[k].equals(rowNames[i]))
						index1 = k;
					if (names[k].equals(rowNames[j]))
						index2 = k;
				}
				assertTrue(rMatrix.getElement(i, j) == matrix.getElement(index1, index2) );
			}
		}
	}
	*/
	
	/*
	public void testSpectralTen() {
		double [][] data = {
				{0, 0, 1, 1},
				{0, 0, 1, 1},
				{1, 1, 1, 0},
				{1, 1, 0, 0}
		};
		String [] names = {"a", "b", "c", "d"};
		Matrix matrix = new Matrix(data, names, names);
		SpectralReordering so = new SpectralReordering();
		Matrix rMatrix = so.reorder(matrix);
		
		// make sure a-a is 0, a-b is 1
		String [] rowNames = rMatrix.rowNames;
		
		int i, j, k, index1 = -1, index2 = -1;
		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				for (k = 0; k < 4; k++) {
					if (names[k].equals(rowNames[i]))
						index1 = k;
					if (names[k].equals(rowNames[j]))
						index2 = k;
				}
				assertTrue(rMatrix.getElement(i, j) == matrix.getElement(index1, index2) );
			}
		}
	}
	*/
	
	
	public void testLAP() {
		double [][] data = {
				{0, 0, 1, 2},
				{0, 0, 3, 4},
				{1, 3, 0, 0},
				{2, 4, 0, 0}
		};
		String [] names = {"a", "b", "c", "d"};
		Matrix matrix = new Matrix(data, names, names);
		LAPAwesomeReordering so = new LAPAwesomeReordering(11, 0);
		Matrix rMatrix = so.reorder(matrix);
		
		int i, j, r, c;
		for (i = 0; i < 4; i++) {
			r = rMatrix.getRowIndex(matrix.getRowName(i));
			for (j = 0; j < 4; j++) {
				c = rMatrix.getColumnIndex(matrix.getColumnName(j));
				assertEquals(matrix.getElement(i, j), rMatrix.getElement(r, c));
			}
		}
	}
	
	public void testRowColumnSums() {
		
	}
}
