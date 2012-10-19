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
import edu.umd.coral.model.data.Matrix;

public class TestMatrix extends TestCase {
	
	public void testConnectedComponentsDisjoint() {
		double [][] data = { 
				{1, 2, 0, 0, 0}, 
				{2, 1, 0, 0, 0},
				{0, 0, 3, 2, 1},
				{0, 0, 2, 3, 2},
				{0, 0, 1, 2, 3}
				};
		String [] names = {"a","b","c","d","e"};
		Matrix m = new Matrix(data, names, names, 3);
		
		Matrix [] comp = Matrix.getConnectedComponents(m);
		
		m = comp[0];
		assertTrue(m.getElement(0, 0) == 1);
		assertTrue(m.getElement(0, 1) == 2);
		assertTrue(m.getElement(1, 0) == 2);
		assertTrue(m.getElement(1, 1) == 1);
		
		m = comp[1];
		assertTrue(m.getElement(0, 0) == 3);
		assertTrue(m.getElement(0, 1) == 2);
		assertTrue(m.getElement(0, 2) == 1);
		assertTrue(m.getElement(1, 0) == 2);
		assertTrue(m.getElement(1, 1) == 3);
		assertTrue(m.getElement(1, 2) == 2);
		assertTrue(m.getElement(2, 0) == 1);
		assertTrue(m.getElement(2, 1) == 2);
		assertTrue(m.getElement(2, 2) == 3);
		
		
		double [][] data2 = { 
				{1, 0, 0, 2, 0}, 
				{0, 3, 2, 0, 2},
				{0, 2, 3, 0, 1},
				{2, 0, 0, 1, 0},
				{0, 2, 1, 0, 3}
				};
		String [] names2 = {"a","d","c","b","e"};
		m = new Matrix(data2, names2, names2, 3);
		
		comp = Matrix.getConnectedComponents(m);
		
		m = comp[0];
		assertTrue(m.getElement(0, 0) == 1);
		assertTrue(m.getElement(0, 1) == 2);
		assertTrue(m.getElement(1, 0) == 2);
		assertTrue(m.getElement(1, 1) == 1);
		
		m = comp[1];
		assertTrue(m.getElement(0, 0) == 3);
		assertTrue(m.getElement(0, 1) == 2);
		assertTrue(m.getElement(0, 2) == 2);
		assertTrue(m.getElement(1, 0) == 2);
		assertTrue(m.getElement(1, 1) == 3);
		assertTrue(m.getElement(1, 2) == 1);
		assertTrue(m.getElement(2, 0) == 2);
		assertTrue(m.getElement(2, 1) == 1);
		assertTrue(m.getElement(2, 2) == 3);
	}
	
	public void testMergeComponents() {
		double [][] data = { 
				{1, 2, }, 
				{3, 4, }
				};
		String [] names = {"a","b"};
		Matrix m = new Matrix(data, names, names);
		
		double [][] data2 = { 
				{5, 6 },
				{7, 8 }
				};
		String [] names2 = {"c","d"};
		Matrix m2 = new Matrix(data2, names2, names2);
		
		Matrix [] array = new Matrix[2];
		array[0] = m;
		array[1] = m2;
		
		Matrix m12 = Matrix.mergeComponents(array, 8);
		
		assertTrue(m12.getElement(0, 0) == 1);
		assertTrue(m12.getElement(0, 1) == 2);
		assertTrue(m12.getElement(1, 0) == 3);
		assertTrue(m12.getElement(1, 1) == 4);
		
		assertTrue(m12.getElement(0, 2) == 0);
		assertTrue(m12.getElement(0, 3) == 0);
		assertTrue(m12.getElement(1, 2) == 0);
		assertTrue(m12.getElement(1, 3) == 0);
		
		assertTrue(m12.getElement(2, 0) == 0);
		assertTrue(m12.getElement(2, 1) == 0);
		assertTrue(m12.getElement(3, 0) == 0);
		assertTrue(m12.getElement(3, 1) == 0);
		
		assertTrue(m12.getElement(2, 2) == 5);
		assertTrue(m12.getElement(2, 3) == 6);
		assertTrue(m12.getElement(3, 2) == 7);
		assertTrue(m12.getElement(3, 3) == 8);
	}
	
	public void testConnectedComponentsSingle() {
		double [][] data = {
				{0, 0, 1, 2},
				{0, 0, 3, 4},
				{1, 3, 0, 0},
				{2, 4, 0, 0}
		};
		String [] names = {"a", "b", "c", "d"};
		Matrix matrix = new Matrix(data, names, names);
		Matrix [] comp = Matrix.getConnectedComponents(matrix);
		assertTrue(comp.length == 1);
		
		assertTrue(comp[0].getElement(0, 0) == 0);
		assertTrue(comp[0].getElement(0, 1) == 0);
		assertTrue(comp[0].getElement(0, 2) == 1);
		assertTrue(comp[0].getElement(0, 3) == 2);
		
		assertTrue(comp[0].getElement(1, 0) == 0);
		assertTrue(comp[0].getElement(1, 1) == 0);
		assertTrue(comp[0].getElement(1, 2) == 3);
		assertTrue(comp[0].getElement(1, 3) == 4);
		
		assertTrue(comp[0].getElement(2, 0) == 1);
		assertTrue(comp[0].getElement(2, 1) == 3);
		assertTrue(comp[0].getElement(2, 2) == 0);
		assertTrue(comp[0].getElement(2, 3) == 0);
		
		assertTrue(comp[0].getElement(3, 0) == 2);
		assertTrue(comp[0].getElement(3, 1) == 4);
		assertTrue(comp[0].getElement(3, 2) == 0);
		assertTrue(comp[0].getElement(3, 3) == 0);
	}
	
	
	public void testConnectedComponentsSingle2() {
		double [][] data = {
				{0, 0, 1, 2},
				{0, 0, 3, 4},
				{1, 3, 0, 0},
				{2, 4, 0, 0}
		};
		String [] names = {"a", "b", "c", "d"};
		Matrix matrix = new Matrix(data, names, names);
		Matrix [] comp = Matrix.getConnectedComponents2(matrix);
		assertTrue(comp.length == 1);
		
		assertTrue(comp[0].getElement(0, 0) == 0);
		assertTrue(comp[0].getElement(0, 1) == 0);
		assertTrue(comp[0].getElement(0, 2) == 1);
		assertTrue(comp[0].getElement(0, 3) == 2);
		
		assertTrue(comp[0].getElement(1, 0) == 0);
		assertTrue(comp[0].getElement(1, 1) == 0);
		assertTrue(comp[0].getElement(1, 2) == 3);
		assertTrue(comp[0].getElement(1, 3) == 4);
		
		assertTrue(comp[0].getElement(2, 0) == 1);
		assertTrue(comp[0].getElement(2, 1) == 3);
		assertTrue(comp[0].getElement(2, 2) == 0);
		assertTrue(comp[0].getElement(2, 3) == 0);
		
		assertTrue(comp[0].getElement(3, 0) == 2);
		assertTrue(comp[0].getElement(3, 1) == 4);
		assertTrue(comp[0].getElement(3, 2) == 0);
		assertTrue(comp[0].getElement(3, 3) == 0);
	}
	
	public void testBandwidth() {
		double [][] data = { 
				{1, 2, 0, 1, 0}, 
				{2, 1, 0, 0, 0},
				{0, 0, 3, 2, 0},
				{1, 0, 2, 3, 2},
				{0, 0, 0, 2, 3}
				};
		String [] names = {"a", "b", "c", "d", "e"};
		Matrix matrix = new Matrix(data, names, names);
		int band = matrix.getBandwidth();
		System.out.println(band);
		assertTrue(band == 5);
	}
	
	
	public void testReorder() {
		double [][] data1 = {
				{1,2,3},
				{4,5,6},
				{7,8,9}
		};
		String [] names1 = {"a","b","c"};
		
		double [][] data2 = {
			//	  b a c
			/*b*/{5,4,6},
			/*a*/{2,1,3},
			/*c*/{8,7,9}
		};
		String [] names2 = {"b","a","c"};
		
		Matrix m1 = new Matrix(data1, names1, names1);
		Matrix m2 = new Matrix(data2, names2, names2);
		Matrix reorder = Matrix.reorderSubMatrix(m1, m2);
		
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				assertEquals(reorder.getElement(i, j), m2.getElement(i, j));
	}
	
	public void testReorder2() {
		double [][] data1 = {
				{1,2,3},
				{4,5,6},
				{7,8,9}
		};
		String [] names1 = {"a","b","c"};
		
		double [][] data2 = {
			//	  c a b
			/*c*/{9,7,8},
			/*a*/{3,1,2},
			/*b*/{6,4,5}
		};
		String [] names2 = {"c","a","b"};
		
		Matrix m1 = new Matrix(data1, names1, names1);
		Matrix m2 = new Matrix(data2, names2, names2);
		Matrix reorder = Matrix.reorderSubMatrix(m1, m2);
		
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				assertEquals(reorder.getElement(i, j), m2.getElement(i, j));
	}
}
