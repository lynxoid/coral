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
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

	/**
	 * The module data structure holds allthe vertices in one "cluster." Modules make up the clustering.
	 * @author Darya
	 *
	 */
public class Module implements Named, Comparable<Module> {
	
	private String _name;
	
	private Set<Vertex> _vertices = new TreeSet<Vertex>();
	
//	private Map<String, Vertex> _vertices = new TreeMap<String, Vertex>();
	
//	private Vertex [] sortedVertices;

	private Vertex [] vertexOrder;
	
//	private boolean _isSingleton;
	
	private Clustering containingClustering;
	
	public Module (String name, Clustering c) {
		_name = name;
		this.containingClustering = c;
	}
	
	public Module(Collection<Vertex> coll, Clustering c) {
		for (Vertex v : coll) {
			_vertices.add(v);
		}
		this.containingClustering = c;
	}

	public Module(String name, boolean isSingleton, Clustering c) {
		_name = name;
//		this._isSingleton = isSingleton;
		this.containingClustering = c;
	}

	/**
	 * Returns module name
	 * @return module name
	 */
	public String getName() {
		return _name;
	}
	
	public Clustering getClustering() {
		return this.containingClustering;
	}
	
	/**
	 * Returns the number of vertices in this module
	 * @return the number of vertices in this module
	 */
	public int getSize() {
		return _vertices.size();
	}
	
	public void addVertex(Vertex u) {
		boolean hasVertex = false;
		for (Vertex v : _vertices)
			if (v.getName().equals(u.getName()))
				hasVertex = true;
		if (_vertices.contains(u) || hasVertex) {
			System.out.println("ERROR: can not add a duplicate vertex: " + this.getName());
			return;
		}
		_vertices.add(u);
	}
	
	public ArrayList<Vertex> getVertices() {
		ArrayList<Vertex> blah = new ArrayList<Vertex>();
		blah.addAll(_vertices);
		return blah;
	}
	
	public Set<Vertex> getVertexMap() {
		return _vertices;
	}
	
	public String toString() {
		String s = "";
		for (Vertex v : _vertices) {
			s += v.getName() + ", ";
		}
		return s.trim();
	}
	/**
	 * @return collection of vertices that are in this and not in "module."
	 */
	public ArrayList<Vertex> getDifference(Module module) {
		ArrayList<Vertex> diff = new ArrayList<Vertex>();
		diff.addAll(_vertices);
		
		Iterator<Vertex> iterator = module._vertices.iterator();
		while (iterator.hasNext()) {
			diff.remove(iterator.next());
		}
		
		return diff;
	}
	
	/**
	 * 
	 * @param module
	 * @return a Collection with vertices from this and "module."
	 */
	public ArrayList<Vertex> getUnion(Module module) {
		ArrayList<Vertex> union = new ArrayList<Vertex>();
		union.addAll(_vertices);
		
		Iterator<Vertex> iterator = module._vertices.iterator();
		Vertex v;
		while (iterator.hasNext()) {
			v = iterator.next();
			if (!union.contains(v))
				union.add(v);
		}
		
		return union;
	}
	
	/**
	 * Collection is empty (size = 0) if there are no vertices in the intersection
	 * 
	 * @param m2
	 * @return a Collection of vertices that are in both modules
	 */
	public ArrayList<Vertex> getIntersection(Module m2) {
		ArrayList<Vertex> intersection = new ArrayList<Vertex>();
		
		for (Vertex v : _vertices) {
			if (m2._vertices.contains(v))
				intersection.add(v);
		}
		
		return intersection;
	}
	
	public int [][] getIntersectionIndices(Module m2) {
		Collection<Vertex> coll = this.getIntersection(m2);
		int [][] indices = new int [coll.size()][2];
		Vertex [] us =  this.getVertexMapping();
		Vertex [] vs =  m2.getVertexMapping();
		
		int k = 0;
		for (int i = 0; i < us.length; i++) {
			for (int j = 0; j < vs.length; j++) {
				if (us[i] == null) {
					System.out.println("us[i] is null " + this._name);
				}
				if (us[i].equals(vs[j])) {
					indices[k][0] = i;
					indices[k][1] = j;
					k++;
				}
			}
		}
		return indices;
	}
	
	/**
	 * Calculates the ratio of edges within the module (an edge connecting two vertices in the module)
	 * to edges going out of the module (an edge from a vertex in the module to one outside of it).
	 * @return ratio of in edges to out edges.
	 */
	public float getInOutRatio() {
		if (this._vertices == null)
			return 0.0f;
		
		int inEdges = 0;
		int outEdges = 0;
		Vertex u;
		Collection<Vertex> neighbors;
		Iterator<Vertex> iterator = _vertices.iterator();
		
		while (iterator.hasNext()) {
			u = iterator.next();
			neighbors = u.getNeighbors();
			for (Vertex v : neighbors) {
				if (_vertices.contains(v))
					inEdges++;
				else
					outEdges++;
			}
		}
		
		inEdges /= 2;
		
		if (outEdges == 0)
			return 0.0f;
		
		return inEdges/outEdges;
	}

	public int getEdgeCount() {
		int count = 0;
		for (Vertex v : _vertices) {
			for (Vertex u : v.getNeighbors()) {
				if (_vertices.contains(u))
					count++;
			}
		}
		return count/2;
	}

	public float getExpectedEdgeCount() {
		float sum = 0.0f;
		int m = 0;
		
		// TODO: include only the edges inside the module or all?
		for (Vertex v : _vertices) {
			m += v.getDegree();
		}
		
		for (Vertex v : _vertices) {
			for (Vertex u : v.getNeighbors()) {
				if (_vertices.contains(u)) {
					sum += v.getDegree() * u.getDegree() * 1.0f / m;
				}
			}
		}
		return sum;
	}

//	public boolean contains(String name) {
//		if (_vertices == null)
//			return false;
//		return _vertices.containsKey(name);
//	}
	
	public boolean contains(Vertex v) {
		if (_vertices == null)
			return false;
		return _vertices.contains(v);
	}

	/**
	 * Returns vertex index in a sortex vertex array
	 * @param v
	 * @return
	 */
	public int getIndex(Vertex v) {
		if (this.vertexOrder == null)
			return -1;
		// cant make teh assumption that the array is sorted
//		int i = Arrays.binarySearch(this.vertexOrder, v);
		int i = 0;
		for (Vertex u : this.vertexOrder) {
			if (u.equals(v))
				return i;
			else
				i++;
		}
		return -1;
	}

	public Vertex [] getVertexMapping() {
		return this.vertexOrder;
	}

	public void setVertexMapping(ArrayList<Pair<Vertex, Integer>> l) {
		vertexOrder = new Vertex[this.getSize()];
		int i = 0;
		for (Pair<Vertex,Integer> p : l)
			vertexOrder[i++] = p.getKey();
	}

	public int compareTo(Module o) {
		return this._name.compareTo(o._name);
	}

//	public boolean isSingleton() {
//		return _isSingleton;
//	}

	public boolean containsAny(ArrayList<Vertex> selectedVerts) {
		boolean contains = false;
		for (Vertex v : selectedVerts)
			if (_vertices.contains(v)) {
				contains = true; break;
				}
		return contains;
	}
}
