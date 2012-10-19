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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.Vertex;

public class LooseCliqueFinder extends Manager {

	public LooseCliqueFinder(DataModel m) {
		super(m);	
	}

	
	public void execute() {
		DataModel m = this.getModel();
		Matrix matrix = m.getCurrentMatrix();
		
		Stack<Vertex> stack = getVertices(matrix);//new Stack<Vertex>();
		
		Set<Collection<Vertex>> cliques = findLooseCliques(stack, matrix.getMax() - 1);
		
		System.out.println(cliques);
	}

	private Set<Collection<Vertex>> findLooseCliques(Stack<Vertex> vertices, double minEdgeWeight) {
		Set<Collection<Vertex>> set = new HashSet<Collection<Vertex>>();
		Vertex u;
		Collection<Vertex> neighbors;
		Set<Vertex> toExpand = new HashSet<Vertex>();
		Collection<Vertex> cluster;
		
		while (vertices.size() > 0) {
			// pick a vertex
			u = vertices.pop();
			
			neighbors = u.getNeighbors();
			cluster = new ArrayList<Vertex>();
			
			// add all neighbors to expandList if edge weight > minEdgeWeight
			for (Vertex v : neighbors) {
				if (!toExpand.contains(v) && u.getEdgeWeight(v) >= minEdgeWeight) {
					toExpand.add(v);
					cluster.add(v);
					// remove vertex from  vertices
					vertices.remove(v);
				}
			}
			set.add(cluster);
		}		
		
		return set;
	}
	
	private Stack<Vertex> getVertices(Matrix m) {
		Stack<Vertex> stack = new Stack<Vertex>();
		Vertex [] vertices = new Vertex[m.getColumnCount()];
		
		int i, j, length = m.getColumnCount();
		for (i = 0; i < length; i++) {
			vertices[i] = new Vertex(m.columnNames[i]);
			stack.push(vertices[i]);
		}
		
		// set up neighbors
		double [][] data = m.getData();
		for (i = 0; i < length; i++) {
			for (j = i; j < length; j++) {
				if (data[i][j] > 0) {
					vertices[i].addNeighbor(vertices[j], data[i][j]);
				}
			}
		}
		
		return stack;
	}
}
