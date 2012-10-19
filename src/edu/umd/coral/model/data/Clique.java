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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// collection of vertices that have edges between all of them
public class Clique {
	
	private Map<String, Vertex> vertices;
	
	private ArrayList<Integer> indicies = new ArrayList<Integer>();
	
	private double value = 0.0f;
	
	public Clique() {
	}
	
	public Clique(Collection<Vertex> vis) {
		if (vis == null)
			vertices = new HashMap<String, Vertex>();
			
		for (Vertex v : vis) {
			vertices.put(v.getName(), v);
		}
	}
	
	public void addVertex(Vertex v) {
		if (vertices == null)
			vertices = new HashMap<String, Vertex>();
		vertices.put(v.getName(), v);
	}
	
	public boolean hasVertex(String name) {
		return vertices.containsKey(name);
	}
	
	public Collection<Vertex> getVertices() {
		return vertices.values();
	}

	public String getAggregateName() {
		if (vertices == null)
			return "";
		String str = "";
		for (String s : vertices.keySet()) {
			str += s + "_";
		}
		return str;
	}
	
	public String toString() {
		if (vertices == null) {
			return this.value + " " + this.indicies.toString();
		}
		return vertices.size() + " " + vertices.toString();
	}

	public int size() {
		if (this.vertices == null)
			return 0;
		else
			return vertices.size();
	}

	/**
	 * 
	 * @return Number of edges between nodes in the clique
	 */
	public int getInEdges() {
		int count = 0;
		
		for (Vertex v : this.vertices.values()) {
			Collection<Vertex> neigh = v.getNeighbors();
			
			for (Vertex u : neigh)
				if (vertices.containsValue(u))
					count++;
		}
		return count / 2;
	}

	/**
	 * 
	 * @return Number of edges that go from nodes in the clique to the outside
	 * nodes
	 */
	public int getOutEdges() {
		int count = 0;
		// A = vertices.values();
		for (Vertex v : this.vertices.values()) {
			// N
			Collection<Vertex> neigh = v.getNeighbors();
			// N \ A
			neigh.removeAll(vertices.values());
			count += neigh.size();
		}
		return count;
	}

	public void setQualityValue(double value) {
		this.value = value;
	}

	public double getQualityValue() {
		return value;
	}

	public void addName(String name) {
		if (vertices == null)
			vertices = new HashMap<String, Vertex>();
		vertices.put(name, null);
	}

	public void addIndex(int j) {
		indicies.add(j);
	}

	public ArrayList<Integer> getIndices() {
		return this.indicies;
	}
}
