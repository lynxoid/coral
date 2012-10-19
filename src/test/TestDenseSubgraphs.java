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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import edu.umd.coral.managers.DenseSubgraphFinder;
import edu.umd.coral.managers.FastDenseSubgraphFinder;
import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Matrix;

public class TestDenseSubgraphs extends TestCase {
	
	public void testCalculateDensity() {
		double [][] data = { 
				{6,5,4,2,0,0}, 
				{5,6,5,3,0,0},
				{4,5,6,4,0,0},
				{2,3,4,6,2,2},
				{0,0,0,2,6,6},
				{0,0,0,2,6,6}};
		
		DenseSubgraphFinder dsf = new DenseSubgraphFinder();
		
		double [][] density = dsf.getDensityMatrix(data);
				
		assertTrue(density[0][1] == 2.5);
		assertTrue(density[0][2] == 14.0f/3);
		assertTrue(density[0][3] == 23.0f/4);
		assertTrue(density[0][4] == 5.0f);
		assertTrue(density[0][5] == 33.0f/6);
		
		assertTrue(density[1][2] == 5.0f/2);
		assertTrue(density[1][3] == 12.0f/3);
		assertTrue(density[1][4] == 14.0f/4);
		assertTrue(density[1][5] == 22.0f/5);
		
		assertTrue(density[2][3] == 4.0f/2);
		assertTrue(density[2][4] == 6.0f/3);
		assertTrue(density[2][5] == 14.0f/4);
		
		assertTrue(density[3][4] == 2.0f/2);
		assertTrue(density[3][5] == 10.0f/3);
		
		assertTrue(density[4][5] == 6.0f/2);
	}
	
	public void testGetDenseSubgraphs() {
		double [][] data = { 
				{6,5,4,2,0,0}, 
				{5,6,5,3,0,0},
				{4,5,6,4,0,0},
				{2,3,4,6,2,2},
				{0,0,0,2,6,6},
				{0,0,0,2,6,6}};
		
		DenseSubgraphFinder dsf = new DenseSubgraphFinder();
		double [][] density = dsf.getDensityMatrix(data);
		Set<Integer> set = dsf.findMaxDensity(density);
		ArrayList<Integer> list = new ArrayList<Integer>(set);
		Collections.sort(list);
		
		assertTrue(list.get(0) == 0);	// start vertex
		assertTrue(list.get(1) == 4);	// cut: [0..3], [4..5]
	}
	
	public void testGetDenseSubgraphs2() {
		System.out.println("---------------------------");
		double [][] data = { 
				{0,1,1,2,0,0,0,0,0,0}, 
				{0,0,5,3,0,0,0,0,0,0},
				{0,0,0,5,0,0,0,0,0,0},
				{0,0,0,0,2,2,6,0,6,0},
				{0,0,0,0,0,6,6,0,6,0},
				{0,0,0,0,0,0,2,0,0,0},
				{0,0,0,0,0,0,0,1,0,0},
				{0,0,0,0,0,0,0,0,6,0},
				{0,0,0,0,0,0,0,0,0,6},
				{0,0,0,0,0,0,0,0,0,0}};
		String [] columnNames = new String[10];
		Matrix m = new Matrix(data, columnNames, columnNames, 6);
		
		DenseSubgraphFinder dsf = new DenseSubgraphFinder();
		ArrayList<Clique> slowCliques = dsf.getCliques(m, false);
		System.out.println(slowCliques);
		
//		double [][] density = dsf.getDensityMatrix(data);
//		Set<Integer> set = dsf.findMaxDensity(density);
//		ArrayList<Integer> list = new ArrayList<Integer>(set);
//		Collections.sort(list);
//		System.out.println(list);
		
		
		FastDenseSubgraphFinder fdsf = new FastDenseSubgraphFinder();
		ArrayList<Clique> fastCliques = fdsf.getCliques(m, false);
		System.out.println(fastCliques);
		
//		double [][] density2 = fdsf.getSumsMatrix(data);
//		Set<Integer> set2 = fdsf.findMaxDensity(density2);
//		ArrayList<Integer> list2 = new ArrayList<Integer>(set2);
//		Collections.sort(list2);
//		System.out.println(list2);
	}
	
	public void testGetFastDenseSubgraphs() {
		double [][] data = { 
				{6,5,4,2,0,0}, 
				{5,6,5,3,0,0},
				{4,5,6,4,0,0},
				{2,3,4,6,2,2},
				{0,0,0,2,6,6},
				{0,0,0,2,6,6}};
		
		FastDenseSubgraphFinder dsf = new FastDenseSubgraphFinder();
		double [][] density = dsf.getSumsMatrix(data);
		
//		assertTrue(density[0][0] == 6);
//		assertTrue(density[1][1] == 6);
//		assertTrue(density[2][2] == 6);
//		assertTrue(density[3][3] == 6);
//		assertTrue(density[4][4] == 6);
//		assertTrue(density[5][5] == 6);
//		
//		assertTrue(density[0][1] == 17);
//		assertTrue(density[1][2] == 17);
//		assertTrue(density[2][3] == 16);
//		assertTrue(density[3][4] == 14);
//		assertTrue(density[4][5] == 18);
//		
//		assertTrue(density[0][2] == 32);
//		assertTrue(density[1][3] == 30);
//		assertTrue(density[2][4] == 24);
//		assertTrue(density[3][5] == 28);
//		
//		assertTrue(density[0][3] == 47);
//		assertTrue(density[1][4] == 38);
//		assertTrue(density[2][5] == 38);
//		
//		assertTrue(density[0][4] == 55);
//		assertTrue(density[1][5] == 52);
//		
//		assertTrue(density[0][5] == 69);
		
		assertTrue(density[0][1] == 5.0f);
		assertTrue(density[0][2] == 14.0f);
		assertTrue(density[0][3] == 23.0f);
		assertTrue(density[0][4] == 25.0f);
		assertTrue(density[0][5] == 33.0f);
		
		assertTrue(density[1][2] == 5.0f);
		assertTrue(density[1][3] == 12.0f);
		assertTrue(density[1][4] == 14.0f);
		assertTrue(density[1][5] == 22.0f);
		
		assertTrue(density[2][3] == 4.0f);
		assertTrue(density[2][4] == 6.0f);
		assertTrue(density[2][5] == 14.0f);
		
		assertTrue(density[3][4] == 2.0f);
		assertTrue(density[3][5] == 10.0f);
		
		assertTrue(density[4][5] == 6.0f);
		
		Set<Integer> set = dsf.findMaxDensity(density);
		ArrayList<Integer> list = new ArrayList<Integer>(set);
		Collections.sort(list);
		
		assertTrue(list.get(0) == 3);	// start vertex
		assertTrue(list.get(1) == 5);	// cut: [0..3], [4..5]
		
		Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		double sum = 0;
		int n = data.length;
		int count = (n-1) * (n-1);
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				double value = data[i][j];
//				System.out.println(i + " " + j);
				if (value > 0) {
					count++;
					sum += value;
				}
				if (histogram.containsKey((int)value)) {
					int h = histogram.get((int)value);
					histogram.put( (int)value, h+1);
				}
				else
					histogram.put((int)value, 1);
			}
		}
		double avg_edge_weight = sum / count;
		ArrayList<Clique> cliques = dsf.constructCliques(set, density, avg_edge_weight, false, n);
		assertTrue(cliques.size() == 2);
		assertTrue(cliques.get(0).getIndices().size() == 4);
		assertTrue(cliques.get(1).getIndices().size() == 2);
	}


	public void testSlowFastResults() {
		DenseSubgraphFinder dsf = new DenseSubgraphFinder();
		FastDenseSubgraphFinder fdsf = new FastDenseSubgraphFinder();
		
		// load test reordered matrix, run on both
		try {
			FileInputStream fstream = new FileInputStream(
//					"/Users/lynxoid/Documents/coral/coral_cluster/jSwingCoral/text_matrix.txt");
			"/Users/lynxoid/Dropbox/coral/svn/coral_cluster/jSwingCoral/nearopt.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			String [] parts;
			
			int i = 0, j;
			
			// read names
			line = br.readLine();
			parts = line.split(",");
			int n = parts.length, val;
			Matrix m = new Matrix(n,n);
			
			while ((line = br.readLine()) != null)   {
				parts = line.split(",");
				assertTrue(n == parts.length);
				for (j = 0; j < n; j++) {
					val = (int)Float.parseFloat(parts[j]);
					m.setElement(i, j, val);
				}
				i++;
			}
			br.close();
			
			int s;
			double [][] slowDensity = dsf.getDensityMatrix(m.getData());
			double [][] fastSums = fdsf.getSumsMatrix(m.getData());
			double [][] fastDensityMod = new double [n][n];
			for (i = 0; i < n; i++) {
				for (j = i; j < n; j++) {
					s = j-i+1;
					fastDensityMod[i][j] = fastSums[i][j] / s;
					if ( (slowDensity[i][j] - fastDensityMod[i][j]) > 0.00001f) {
//						System.out.println(i + " " + j + " " + 
//								slowDensity[i][j] + " " + 
//								fastDensityMod[i][j]);
					}
//					assertTrue( Math.abs(slowDensity[i][j] - fastDensity[i][j] / s) < 0.000001f );
				}
			}
			
//			Set<Integer> vertex_indices = dsf.findMaxDensity(fastDensityMod);
//			System.out.println("#Cuts w/ a diff matrix (from sums): " + vertex_indices.size());
//			ArrayList<Integer> blah = new ArrayList<Integer>(vertex_indices);
//			Collections.sort(blah);
//			System.out.println(blah);
			
			ArrayList<Clique> slowCliques = dsf.getCliques(m, true);
//			System.out.println("Slow cliques");
//			for (Clique c: slowCliques) {
//				System.out.println(c.getIndices().size() + " " + c.getQualityValue());
//			}
			
			ArrayList<Clique> fastCliques = fdsf.getCliques(m, true);
//			System.out.println("Fast cliques");
//			for (Clique c: fastCliques) {
//				System.out.println(c.getIndices().size() + " " + c.getQualityValue());
//			}
			
			assertTrue(slowCliques.size() == fastCliques.size());
			
			Clique slow, fast;
			for (i = 0; i < slowCliques.size(); i++) {
				slow = slowCliques.get(i);
				fast = fastCliques.get(i);
				assertTrue(slow.getIndices().containsAll(fast.getIndices()) );
				assertTrue(fast.getIndices().containsAll(slow.getIndices()) );
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	}
}
