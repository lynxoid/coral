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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.umd.coral.model.data.BaseMatrix;
import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Matrix;

/**
 * This class finds an arrangement of optimal dense subgraphs on an weighted 
 * adjacency matrix. Filters out subgraphs that are less dense than expected.
 * 
 * @author lynxoid
 *
 */
public class FastDenseSubgraphFinder implements CliqueManager {

	// default constructor
	public FastDenseSubgraphFinder() {
		
	}
	
	/**
	 * 
	 */
	public ArrayList<Clique> getCliques(Matrix m, boolean cutoff) {		
		// calculate distribution of co-occurrences (edge weights histogram)
		double sum = 0;
		int n = m.getColumnCount();
		int z = 0;
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				sum += m.getElement(i, j);
				if (m.getElement(i, j) != 0)
					z++;
			}
		}
//		double avg_edge_weight = 2 * sum / ( (n-1) * (n-2) );
		double avg_edge_weight = sum / z;

		// calculate density matrix
		double [][] sums;
		if (m instanceof BaseMatrix)
			sums = getSumsFlexMatrix( ((BaseMatrix)m).getBaseMatrix() );
		else
			sums = getSumsMatrix(m.getData());
		
		
		// find dense subgraphs
		Set<Integer> vertex_indices = findMaxDensity(sums);
		
		ArrayList<Integer> blah = new ArrayList<Integer>(vertex_indices);
		Collections.sort(blah);
		
		// construct cliques
		ArrayList<Clique> cliques = constructCliques(vertex_indices, sums, avg_edge_weight, true, n);
		return cliques;
	}
	
	private double[][] getSumsFlexMatrix( Matrix matrix) {
		int n = matrix.getColumnCount();
		double [][] sums = new double[n][n];
		
		int i, j;
		double [] column_sums;
		
		// init densities
		for (i = 0; i < n; i++) {
			sums[i][i] = 0.0f;//M[i][i]; ignore self co-occurrences
		}

		for (i = 1; i < n; i++) { // column
			column_sums = new double [i+1];
			column_sums[i] = 0;//M[i][i]; // ignore co-occurrences
			for (j = i-1; j >= 0; j--) { // row
				column_sums[j] = (double) (column_sums[j+1] + matrix.getElement(j,i));
				sums[j][i] = sums[j][i-1] + column_sums[j];
			}
		}
//		System.out.println(sums);
		return sums;
	}

	/**
	 * Given a matrix M, computes all S_{ij} for all i < j.
	 * 
	 * Compute sums of weights for all ordered [i..j] subgraphs
	 * ii to ij
	 *   ......
	 *       jj
	 * 
	 * @param m
	 * @return
	 */
	public double [][] getSumsMatrix(double [][] M) {
		int n = M.length;
		double [][] sums = new double[n][n];
		
		int i, j;
		double [] column_sums;
		
		// init densities
		for (i = 0; i < n; i++) {
			sums[i][i] = 0.0f;//M[i][i]; ignore self co-cluster items
		}

		for (i = 1; i < n; i++) { // column
			column_sums = new double [i+1];
			column_sums[i] = 0;//M[i][i]; // ignore self co-cluster items
			for (j = i-1; j >= 0; j--) { // row
				column_sums[j] = column_sums[j+1] + M[j][i];
				sums[j][i] = sums[j][i-1] + column_sums[j];
			}
		}
		return sums;
	}
	
	/**
	 * Dynamic program to compute optimal arrangement of subgraphs to maximize 
	 * the sum of densities.
	 * TODO: ignore subgraphs of size < 2
	 * 
	 * @param density
	 * @return
	 */
	public Set<Integer> findMaxDensity(double [][] density) {
		// initialize
		Set<Integer> vertex_indices = new HashSet<Integer>();
		int i, j, n = density.length;
		double [] d_opt = new double[n];
		double s;
		int [] arrows = new int[n];
		
		// init density matrix
		for (i = 0; i < n; i++) {
			d_opt[i] = density[i][i]; // density/1.0f
		}
		
		// DP to compute optimal decomposition into subgraphs/patches/areas
		// suppose we only allow subgraphs of size 2 and greater
		arrows[0] = -1;
		for (i = 1; i < n; i++) {
			d_opt[i] = density[0][i] / (i+1);
			arrows[i] = -1;
			for (j = 1; j < i; j++) {
				s = d_opt[j] + density[j+1][i] / (i-j);
				if (s > d_opt[i]) {
					d_opt[i] = s;
					arrows[i] = j; // traceback to [i][j]
				}
			}
		}
		// max is at d_opt[n-1] - traceback from there
		vertex_indices.add(n-1);
		j = arrows[n-1];
		while (j > 0) {
			vertex_indices.add(j);
			j = arrows[j];
		}
		
		return vertex_indices;
	}
	
	/**
	 * Given a list of indices where the matrix is cut, returns a set of Clique
	 * that each represent a subgraph. Each subgraph contains indices that ended
	 * up in it.
	 * If do_cutoff is true, then the function returns only those subgraphs that
	 * have density higher than expected (weight * S_size / 2).
	 * 
	 * @param set
	 * @param sums
	 * @param weight
	 * @param do_cutoff
	 * @return
	 */
	public ArrayList<Clique> constructCliques(Set<Integer> set, double[][] sums, double weight, boolean do_cutoff, int n) {
		ArrayList<Integer> list = new ArrayList<Integer>(set);
		Collections.sort(list);
		Clique cliq;
		ArrayList<Clique> cliques = new ArrayList<Clique>();
		double clique_density, avg_density, s_pq;
		int h_pq;
		
		int prevIndex = 0, currentIndex;
		for (int i = 0; i < set.size(); i++) {
			currentIndex = list.get(i);
			s_pq = sums[prevIndex][currentIndex];
			h_pq = currentIndex - prevIndex + 1;
			int size = currentIndex - prevIndex + 1; // TODO: + 1 or no 1?
			clique_density = s_pq / size;
			avg_density = (h_pq - 1) * s_pq / ( (n-1) * (n-2) );
			// cliques w/ density less than avg_edge_density * (k choose 2) / v don't make the cut
			// if (clique_density > weight * size || !do_cutoff) { // OLD
			if (clique_density > avg_density || !do_cutoff) {
				cliq = new Clique();
				cliq.setQualityValue(clique_density);
				for (int j = prevIndex; j <= currentIndex; j++) {
					cliq.addIndex(j);
				}
				cliques.add(cliq);
			}
			prevIndex = currentIndex + 1;
		}
		
		System.out.println("Total cliques: " + list.size() + ", after filtering: " + cliques.size());
		
		return cliques;
	}
}
