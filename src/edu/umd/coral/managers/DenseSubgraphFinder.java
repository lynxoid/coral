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
package edu.umd.coral.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Matrix;

public class DenseSubgraphFinder implements CliqueManager {

	public DenseSubgraphFinder() {
	}

	/**
	 * max is ignored
	 * @param cutoff 
	 */
	public ArrayList<Clique> getCliques(Matrix m, boolean cutoff) {
		// calculate distribution of co-occurrences (edge weights histogram)
		Map<Integer, Integer> histogram = new HashMap<Integer, Integer>();
		double sum = 0;
		int count = 0, size = m.getColumnCount();
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				double value = m.getElement(i, j);
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

		// calculate density matrix
		double [][] density = getDensityMatrix(m.getData());
		
		// find dense subgraphs
		Set<Integer> vertex_indices = findMaxDensity(density);
		ArrayList<Integer> blah = new ArrayList<Integer>(vertex_indices);
		Collections.sort(blah);
		
		// construct cliques
		ArrayList<Clique> cliques = constructCliques(vertex_indices, m, density, avg_edge_weight, true);
		return cliques;
	}
	
	/**
	 * Compute densities for all ordered [i..j] subgraphs
	 * 
	 * @param m
	 * @return
	 */
	public double[][] getDensityMatrix(double [][] M) {
		int n = M.length;
		double [][] density = new double[n][n];
		double old_sum;
		
		int s, i, j, k;

		for (s = 2; s < n+1; s++) {
			for (i = 0; i < n - s + 1; i++) {// # interval start
				k = i + s;			// number of vertices in S
				double new_sum = 0;		// sum of edge weights in S
				for (j = i; j < k - 1; j++) {
					new_sum += M[j][k - 1];
				}

				//old_sum = density[i][k - 2] * (s - 1) * (s - 2) / 2;
				// edge density
				//density[i][k - 1] = (old_sum + new_sum) * 2 /  s / (s - 1);
				
				old_sum = density[i][k - 2] * (s - 1);
				//density definition by Samir Khuller 
				density[i][k - 1] = (old_sum + new_sum) / s;
			}
		}
		return density;
	}
	
	/**
	 * Dynamic program to compute optimal arrangement of subgraphs to maximize 
	 * the sum of densities
	 * 
	 * @param density
	 * @return
	 */
	public Set<Integer> findMaxDensity(double [][] density) {
		// initialize
		int n = density.length;
		double [] d_opt = new double[n];
		Set<Integer> vertex_indices = new HashSet<Integer>();
		Arrays.fill(d_opt, -1);			// populate w/ -1
		
		
		d_opt[0] = 0;
		for (int i = 1; i < n; i++) {
			d_opt[i] = getDense(i, d_opt, density, vertex_indices);
		}
		return vertex_indices;
	}
	
	/**
	 * Pair class to hold pairs of density, vertex index for [0..i] subproblem
	 * @author lynxoid
	 */
	private class Pair implements Comparable<Pair> {
		public int t;			// index
		public double density;	// optimal density for this subproblem
		
		// constructor
		public Pair(int t, double d) {this.t = t; this.density = d;}

		// comparator
		public int compareTo(Pair a) {
			if (this.density < a.density) return -1;
			if (this.density > a.density) return 1;
			return 0;
		}
		
		public String toString() {
			return String.valueOf(t) + ": " + String.valueOf(density);
		}
	}

	/**
	 * Step in dynamic program to compute an optimal dense subgraph arrangement
	 * for [0..i] vertices.
	 * @param dOpt 
	 * 
	 * @param i
	 * @param density
	 * @param vertexIndices
	 * @return
	 */
	public double getDense(int k, double [] d_opt, double[][] density, Set<Integer> vertexIndices) {
		if (k <= 0)
			return 0;
		
		ArrayList<Pair> dtk = new ArrayList<Pair>();
		double d2;
		
		for (int t = 0; t < k+1; t++) {
			if (t > 0 && d_opt[t-1] != 1)
				d2 = d_opt[t-1];
			else
				d2 = getDense(t-1, d_opt, density, vertexIndices);
			
			Pair p = new Pair(t, density[t][k] + d2);
			dtk.add(p);
		}
		
		// get max from dtk
		Pair maxPair = Collections.max(dtk);
//		Pair maxPair = dtk.get(0);
		vertexIndices.add(maxPair.t);
		
		return maxPair.density;
	}
	
	/**
	 * 
	 * @param set
	 * @param m
	 * @param map
	 * @param density
	 * @param avg_edge_weight 
	 * @return
	 */
	public ArrayList<Clique> constructCliques(Set<Integer> set, Matrix m, double[][] density, double avg_edge_weight, boolean do_cutoff) {
		ArrayList<Integer> list = new ArrayList<Integer>(set);
		Collections.sort(list);
		Clique cliq;
		if (m == null)
			return null;
		
		ArrayList<Clique> cliques = new ArrayList<Clique>();
		double clique_density;
		
		int prevIndex = list.get(0), currentIndex;
		for (int i = 1; i < set.size(); i++) {
			currentIndex = list.get(i);
			clique_density = density[prevIndex][currentIndex];
			
			// cliques w/ density less than avg_edge_density * (n choose 2) * 0.5 / v don't make the cut
			int size = currentIndex - prevIndex;
			if (clique_density > avg_edge_weight * (size) / 2 || !do_cutoff) {
				cliq = new Clique();
				cliq.setQualityValue(clique_density);
				for (int j = prevIndex; j < currentIndex; j++) {
					cliq.addIndex(j);
				}
				cliques.add(cliq);
			}
			prevIndex = currentIndex;
		}
		
		currentIndex = m.getColumnCount() - 1;
		clique_density = density[prevIndex][currentIndex];
		
		int size = currentIndex - prevIndex;
		if (clique_density > avg_edge_weight * (size) / 2 || !do_cutoff) {
			cliq = new Clique();
			cliq.setQualityValue(clique_density);
			for (int j = prevIndex; j < currentIndex; j++) {
				cliq.addIndex(j);
			}
			cliques.add(cliq);
		}
		
		return cliques;
	}
	

}
