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
/**
 * 
 * Each vertex holds a list of its neighbors, as well as what module and 
 * what clustering its in.
 * 
 * @author darya, meghan
 *
 */
package edu.umd.coral.model.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
	
public class Vertex implements Comparable<Vertex>, Named {
	
	////////////////////////////////////////////////////////////////////////////
	//
	// private variables
	//
	////////////////////////////////////////////////////////////////////////////
	
	private String _name;
	
	private int _index = 0;
	
	private Map<Vertex, Double> _neighbors = new TreeMap<Vertex, Double>();
	
	/**
	 * 
	 * Default constructors
	 * 
	 * @param name
	 * @param index
	 */
	public Vertex(String name, int index) {
		_name = name;
		_index = index;
	}

	public Vertex(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}
	
	public int getIndex() {
		return _index;
	}
	
	/**
	 * Adds a symmetric edge (this, v) , (v, this)
	 * @param v
	 */
	public void addNeighbor(Vertex v) {
		if (_neighbors == null)
			_neighbors = new HashMap<Vertex, Double>();
		_neighbors.put(v, 1.0);//1.0f);
		if (v._neighbors == null)
			v._neighbors = new HashMap<Vertex, Double>();
		v._neighbors.put(this, 1.0);
	}
	
	/**
	 * Adds an edge from this vertex to <code>v</code> w/ weight <code>f</code>
	 * @param v - adjacent vertex
	 * @param f - edge weight
	 */
	public void addNeighbor(Vertex v, double f) {
		_neighbors.put(v, f);
	}
	
	public void setEdgeWeight(Vertex v, double f){
		_neighbors.put(v, f);
	}
	
	/**
	 * 
	 * @param v
	 * @return - may return null if vertex v is not a neighbor to this vertex
	 */
	public double getEdgeWeight(Vertex v){
		return _neighbors.get(v);
	}
	
	/**
	 * Returns a collection of vertices that are adjacent to this vertex
	 * @return
	 */
	public Collection<Vertex> getNeighbors() {
		return _neighbors.keySet();
	}
	
//	public void addClusteringModule(Clustering c, Module m) {
//		_clusteringToModule.put(c, m);
//	}
	
//	public Module getModuleByClustering(Clustering c) {
//		return _clusteringToModule.get(c);
//	}
	
	/**
	 *  compares vertices: two vertices are equal if their names and indices 
	 *  match
	 */
	public boolean equals(Object o){
		if (o == null)
			return false;
		if (!(o instanceof Vertex))
			return false;
		
		Vertex v = (Vertex)o;
		if (_name == null)
			return v.getName() == null;
		return _name.equals(v.getName()) && this._index == v._index;
	}
	
	/**
	 * to enable sorting vertices by the name
	 */
	public int compareTo(Vertex u) {
		if (u == null)
			return 1;
		return (this._name).compareTo(u._name);
	}
	
	public int hashCode(){
		return _name.hashCode();
	}
	
	public String toString() {
		return /*_index + ":" + */_name;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Static methods
	//
	////////////////////////////////////////////////////////////////////////////
//	public static boolean sameModule(Vertex u, Vertex v, Clustering c) {
//		Module moduleName1 = u.getModuleByClustering(c);
//		Module moduleName2 = v.getModuleByClustering(c);
//		if (moduleName1 == null || moduleName2 == null) {
//			System.out.println("Module name NULL");
//			return false;
//		}
//		return moduleName1.getName().equals(moduleName2.getName());
//	}

	public int getDegree() {
		return _neighbors.size();
	}

	public boolean hasNeighbor(Vertex v) {
		return this._neighbors.containsKey(v);
	}

	/**
	 * Returns the sum of all edge weights
	 * @return
	 */
	public double getEdgeSum() {
		double sum = 0;
		if (this._neighbors != null) {
			for (double weight : _neighbors.values()) {
				sum += weight;
			}
		}
		return sum;
	}
}
