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
package edu.umd.coral.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.Vertex;

public class CuthillMcKeeReordering implements MatrixReordering {

	/**
	 * 
	 * Cuthill-McKee matrix bandwidth minimization
	 * 
	 * 1. Treat co-occurrence matrix as an adjacency matrix w/ entries as edge 
	 * weights (corresponding graph may be disconnected
	 * 
	 * 2. Pick a min degree node and build a spanning tree by expanding nodes in
	 * a frontier w/ min degree
	 * 
	 * 3. Remember the order of expansion - this would be the relabeling for 
	 * original row/columns
	 * 
	 * @param matrix
	 * @return
	 */
	public Matrix reorder(Matrix matrix) {
		if (matrix == null)
			return null;

		// holds all "vertex" objects and produces the one with min degree
		PriorityQueue<Vertex> graph = buildCooccurrenceGraph(matrix);
		PriorityQueue<Vertex> frontier = new PriorityQueue<Vertex>(10, new DegreeComparator());
		Set<Vertex> expandedVertices = new HashSet<Vertex>();
		
		/**
		 * index in the list == new index
		 * olToNewIndex[i] == old index
		 */
		ArrayList<Integer> oldToNewIndex = new ArrayList<Integer>();

		// create a map from vertex name to its original row index in the matrix
		Vertex v;
		int rowCount = matrix.getRowCount();
		
		// may have disconnected components - so expand each component, but 
		// allow to jump across components
		while (!graph.isEmpty()) {
			// get vertex w/ min degree
			v = graph.poll();
			oldToNewIndex.add(v.getIndex());
			
			//  initialize frontier w/ min degree vertex
			frontier.add(v);
			
			traverseComponent(frontier, expandedVertices, oldToNewIndex, graph);
		}
		
		frontier.clear();
		frontier = null;
		graph = null;
		expandedVertices.clear();
		expandedVertices = null;
		
		// get back to reordering rows/columns
		int i, j;
		String [] names = new String[rowCount];
		double [][] data = new double[rowCount][rowCount];
		int oldRowIndex, oldColumnIndex;
		
		for (i = 0; i < rowCount; i++) {
			oldRowIndex = oldToNewIndex.get(i);
			names[i] = matrix.getRowName(oldRowIndex);
			
			for (j = 0; j < rowCount; j++) {
				oldColumnIndex = oldToNewIndex.get(j);
				data[i][j] = matrix.getElement(oldRowIndex, oldColumnIndex);
			}
		}

		Matrix m = new Matrix(data, names, names, matrix.getMax());

		return m;
	}
		
	/**
	 * Builds a graph from cooccurrence matrix (uses it as adjacency matrix)
	 * 
	 * @param matrix
	 * @return
	 */
	private PriorityQueue<Vertex> buildCooccurrenceGraph(Matrix matrix) {
		PriorityQueue<Vertex> graph = new PriorityQueue<Vertex>(10, new DegreeComparator());
		HashMap<String, Vertex> vertices = new HashMap<String, Vertex>();
		Vertex v;
		int row, column;
		int rowCount = matrix.getRowCount();

		for (row = 0; row < rowCount; row++) {
			v = new Vertex(matrix.getRowName(row), row);
			vertices.put(matrix.getRowName(row), v);
		}

		double value;
		for (row = 0; row < rowCount; row++) {
			v = vertices.get(matrix.getRowName(row));

			// add "neighbors" (co-occurred vertices)
			// do not add self
			for (column = 0; column < rowCount; column++) {
				value = matrix.getElement(row, column);
				if (value != 0 && (row != column))
					v.addNeighbor(vertices.get(matrix.getRowName(column)), value);
			}
			graph.add(v);
		}
		
		return graph;
	}
	
	
	/**
	 * Builds a reordering for a connected component
	 * 
	 * @param frontier
	 * @param expandedVertices
	 * @param indices
	 * @param graph
	 */
	private void traverseComponent(
			PriorityQueue<Vertex> frontier,
			Set<Vertex> expandedVertices,
			ArrayList<Integer> indices,
			PriorityQueue<Vertex> graph) {
		
		Vertex v, u;
		Collection<Vertex> neighbors;
		PriorityQueue<Vertex> q = new PriorityQueue<Vertex>(10, new DegreeComparator());
		
		while ( !frontier.isEmpty() ) {
			
			v = frontier.poll();
			graph.remove(v);
//			System.out.println("expanding " + v.getName());
			
			// if have not expanded v yet
			if ( !expandedVertices.contains(v) ) {
				// look at all of v's neighbors
				neighbors = v.getNeighbors(); 
//				System.out.println(neighbors);
				
				q.addAll(neighbors);
				
				// relabel unexpanded neighbors in increasing degree order
				while ( ! q.isEmpty() ) {
					u = q.poll();
					if ( !expandedVertices.contains(u) && !frontier.contains(u)) {
						frontier.add(u);
						indices.add(u.getIndex());
					}
					
				}
			}
			expandedVertices.add(v);
			// q - empty
		}
	}
	
	/**
	 * Compares vertices by degree 
	 * 
	 * @author aashish
	 */
	public class DegreeComparator implements Comparator<Vertex> {
		/**
		 * Compares vertices by degree and returns 0 if degrees match, -1 if 
		 * degree(v1) < degree(v2), and 1 otherwise.
		 */
		public int compare(Vertex v1, Vertex v2){
			if(v1.getDegree() == v2.getDegree()){
				return 0;
			}
			
			if(v1.getDegree() < v2.getDegree()){
				return -1;
			}
			return 1;
		}

	}
}
