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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import edu.umd.coral.model.metrics.Entropy;
import edu.umd.coral.model.metrics.Overlap;
import edu.umd.coral.model.parse.ModuleFilesParser;
import edu.umd.coral.util.IndexedQuickSort;

	/**
	 * Clustering's are representations of the data output by one clustering algorithm.
	 * Made up of "modules" (vertices clustered together).
	 */

public class Clustering implements Comparable<Clustering>, Named{
	
	public final static String MODULE_COUNT = "module count";
	public final static String AVG_DENSITY = "Avg. density";
	public final static String AVG_MODULE_SIZE = "Avg. module size";
	public final static String VERTEX_COUNT = "Vertex count";
	public static final String ENTROPY = "Entropy";
	public static final String OVERLAP = "Overlap";
	
	
	/***********************************************
	 * 
	 * Private
	 * 
	 **********************************************/
	
	private String _name;
	
	private Map<String, Module> _modules = new TreeMap<String, Module>();
	
//	private Map<String, Vertex> _vertices = new TreeMap<String, Vertex>();
	private Set<Vertex> _vertices = new TreeSet<Vertex>();
	
	private BitSymmetricMatrix _cooccurenceMatrix;
	
	private int originalVertexCount;
	
	private Color color;
	
	/**
	 * Some clusterings do not allow modules with single vertices, however, 
	 * single vertex modules will be added to clustering to make vertex 
	 * content across all clusterings the same.
	 * 
	 * When calculating module density, use the originalModuleCount
	 * 
	 */
	private int originalModuleCount = 0;
	
	/**
	 * True if there are modules that overlap
	 */
	private boolean _hasOverlap = false;
	
	/**
	 * 
	 */
	public Clustering() {
		_name = UUID.randomUUID().toString();
	}
	
	/**
	 * 
	 * @param name
	 */
	public Clustering(String name) {
		_name = name;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Returns clustering name
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Returns the number of modules
	 * @return
	 */
	public int size() {
		return _modules.size();
	}
	
	/**
	 * Adds a module to clustering. Updates vertex map to include vertices in 
	 * this module.
	 * 
	 * @param m
	 * @throws Exception 
	 */
	public void addModule(Module m) throws Exception {
		Module returned = _modules.put(m.getName(), m);
		if (returned != null) 
			throw new Exception("Module with this name already exists. Module: " + m.getName() + " in " + this._name);
//			System.out.println("ERROR: overwrote a module with the same name: " + m.getName());
		Set<Vertex> vSet = m.getVertexMap();
		for (Vertex v : vSet)
			if (_vertices.contains(v))
				this._hasOverlap = true;
		this._vertices.addAll(vSet);
	}
	
	/**
	 * Returns a collection of all modules in this clustering
	 * @return
	 */
	public Collection<Module> getModules() {
		return this._modules.values();
	}
	
	/**
	 * 
	 * @return An array of modules sorted by their name
	 */
//	public Module [] getSortedModules() {
//		Collection<Module> c = _modules.values();
//		Module [] array = c.toArray(new Module[0]);
//		Arrays.sort(array);
//		return array;
//	}
	
	/**
	 * Adds a vertex to a module of a given name. If a module does not exist,
	 * creates the module and adds this vertex to it.
	 * 
	 * @param moduleName
	 * @param v
	 * @throws Exception 
	 */
	public void addToModule(String moduleName, Vertex v) {
		Module m;
		if (_modules.containsKey(moduleName)) {
			m = _modules.get(moduleName);
		}
		else {
			System.out.println("No module " + moduleName + ". Skipping " + v);
			return;
		}
		m.addVertex(v);
		boolean alreadyPresent = !_vertices.add(v); // returns false if the vertex is already there
		_hasOverlap = _hasOverlap || alreadyPresent;
	}
	
	/**
	 * Creates a co-occurence matrix for the given clustering C with a 
	 * predetermined row and column ordering
	 * 
	 * each row and column represent a single vertex in the network
	 * the matrix element a[i,j] = 1 if vertices i, j are in the same cluster
	 * in clustering C, a[i,j] = 0 otherwise.
	 * @throws Exception 
	 */
	public HashMap<Vertex, TreeMap<Vertex, Cooccurrence>> createCooccurenceMatrix(ArrayList<Vertex> ordering) {		
		if (_vertices == null || _modules == null)
			return null;
		
		int size = _vertices.size();
		
		// set column, row names for the matrix
		String [] names = new String[ordering.size()];
		for (int i = 0; i < size; i++) {
			names[i] = ordering.get(i).getName();
		}
		return generateMatrix(size, names);
	}
	
	/**
	 * Creates a co-occurence matrix for the given clustering C
	 * each row and column represent a single vertex in the network
	 * the matrix element a[i,j] = 1 if vertices i, j are in the same cluster
	 * in clustering C, a[i,j] = 0 otherwise.
	 * @throws Exception 
	 */
	public HashMap<Vertex, TreeMap<Vertex, Cooccurrence>> createCooccurenceMatrix() {
		if (_vertices == null || _modules == null)
			return null;
		
		int size = _vertices.size();		

		// set column, row names for the matrix
		Vertex [] sortedVertices = getSortedVertexArray();
		String [] names = new String[sortedVertices.length];
		for (int i = 0; i < size; i++) {
			names[i] = sortedVertices[i].getName();
		}
		
		return generateMatrix(size, names);
	}

	private HashMap<Vertex, TreeMap<Vertex, Cooccurrence>> generateMatrix(int size,
			String[] names) {
		ArrayList<Vertex> vertices;
		int moduleSize;
		int i, j, u_i, v_i;
		Vertex u;
		Vertex v;
		Cooccurrence cooccur;
		HashMap<Vertex, TreeMap<Vertex, Cooccurrence>> hashMap = new HashMap<Vertex, TreeMap<Vertex, Cooccurrence>>();
		
		String [] sortedNames = names.clone();
		int [] indices = new int[size];
		for (i = 0; i < size; i++)
			indices[i] = i;
		IndexedQuickSort.quicksort(sortedNames, indices);
		
		BitSymmetricMatrix matrix = new BitSymmetricMatrix(size, names);
		
		// go through each cluster and put 1's for ever pair of vertices in a cluster
		boolean in = false;
		for (Module m : _modules.values()) {
//			if(m.getName().equals(ModuleFilesParser.GRAB_BAG)) in = true;
//			else in = false;
			if (m.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
			moduleSize = m.getSize();
			vertices = new ArrayList<Vertex>(m.getVertexMap());			
			for (i = 0; i < moduleSize-1; i++) {
				u = vertices.get(i);
				for (j = i + 1; j < moduleSize; j++) {
					v = vertices.get(j);
					
					// find the name in sorted array using fast log(n) search
					// look up the index in the original unsorted array and 
					// pass it on to BitMatrix
					u_i = indices[Arrays.binarySearch(sortedNames, u.getName())];
					v_i = indices[Arrays.binarySearch(sortedNames, v.getName())];
					matrix.setElement(u_i, v_i, 1, in);
					matrix.setElement(v_i, u_i, 1, in);
					
					// BUG FIX: N is too slow. need to use log(n)
//					matrix.setElement(u.getName(), v.getName(), 1);	// slow
//					matrix.setElement(v.getName(), u.getName(), 1);
					
					cooccur = new Cooccurrence(u, v);
					cooccur.addClustering(this);
					
					TreeMap<Vertex, Cooccurrence> set = hashMap.get(u);
					if (set == null) {
						set = new TreeMap<Vertex, Cooccurrence>();
						hashMap.put(u, set);
					}
					set.put(v, cooccur);
				}
			}
		}
		
		_cooccurenceMatrix = matrix;
		// System.out.println("created co-occurrence matrix");
		
		return hashMap;
	}
	
	public BitSymmetricMatrix getCoocurenceMatrix() {
		return _cooccurenceMatrix;
	}
	
	public String toString() {
//		String s = "### clustering " + this._name + " ###\n";
//		for (Module m : _modules.values()) {
//			s += m.getName() + ": " + m.toString() + "\n";
//		}
//		s += "######";
//		return s.trim();
		return this._name;
	}

	public boolean hasVertex(Vertex v) {
		return _vertices.contains(v);
	}
	
	/**
	 * Sorts vertices by name to provide a consistent ordering
	 * @return
	 */
	public Vertex [] getSortedVertexArray() {
		Vertex [] array = new Vertex[_vertices.size()];
		int index = 0;
		for (Vertex v : _vertices) {
			array[index++] = v;
		}
		// sort
		Arrays.sort(array);
		return array;
	}
	
	/**
	 * Compares clusterings by name
	 */
	public int compareTo(Clustering c) {
		if (c == null)
			return 1;
		if (_name == null)
			if (c._name == null)
				return 0;
			else
				return -1;
		return _name.compareTo(c._name);
	}
	
	/**
	 * Returns a module by name or null if there is no module with such name
	 * 
	 * @param name
	 * @return
	 */
	public Module getModule(String name) {
		if (this._modules == null)
			return null;
		return _modules.get(name);
	}
	
	/**
	 * Returns an array of vertices
	 * 
	 * @return
	 */
	public Vertex[] getVertices() {
		return this._vertices.toArray(new Vertex[0]);
	}

	/**
	 * 
	 * Returns vertex count for this clustering (for vertices that were initially
	 * clustered in this clustering)
	 * 
	 * @return - vertex count
	 */
	public int getVertexCount() {
		return this._vertices.size();
	}
	
	/**
	 * returns the number of modules in this clustering
	 * @param includeGrabBag 
	 * 
	 * @return
	 */
	public int getModuleCount() {
		if (this.originalModuleCount == 0)
			return this._modules.size();
		else
			return this.originalModuleCount;
	}

	/**
	 * 
	 * @return
	 */
	public float getAverageDensity(boolean includeGrabBag) {
		int edgeCount;
		float expectedEdgeCount;
		float sum = 0.0f;
		for (Module m : _modules.values()) {
			if (!includeGrabBag && m.getName().equals(ModuleFilesParser.GRAB_BAG))
				continue;
			edgeCount = m.getEdgeCount();
//			System.out.println("edge count in " + this._name + ": " + edgeCount);
			expectedEdgeCount = m.getExpectedEdgeCount();
			if (expectedEdgeCount != 0)
				sum += edgeCount / expectedEdgeCount;
		}
		return sum;
	}

	/**
	 * Returns an average module size: vertex_count / module_count
	 * @param includeGrabBag TODO
	 * 
	 * @return average module size for this clustering
	 */
	public float getAverageModuleSize(boolean includeGrabBag) {
		int mod_count = 0;
		int vert_cnt = 0;
		for (Module m : this._modules.values()) {
			if (!includeGrabBag && 
					m.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
				mod_count++;
				vert_cnt += m.getSize();
		}
		if (mod_count == 0) return 0;
		return vert_cnt * 1.0f / mod_count;
	}

//	public float getModularity() {
//		// need network information
//		if (_modules ==  null || _vertices == null)
//			return 0.0f;
//		int edgeCount = DataModel.getInstance().edges.size();
//		if (edgeCount == 0)
//			return 0.0f;
//		edgeCount *= 2;
//		
//		Collection<Module> modules = this._modules.values();
//		Collection<Vertex> verticesI;
//		Collection<Vertex> verticesJ;
//		float modularity = 0.0f;
//		
//		for (Module m : modules) {
//			verticesI = m.getVertices();
//			verticesJ = m.getVertices();
//			
//			for (Vertex u : verticesI) {
//				for (Vertex v : verticesJ) {
//					int Aij = u.hasNeighbor(v);
//					modularity += Aij - u.getDegree() * v.getDegree() * 1.0f / edgeCount;
//				}
//			}
//		}
//		
//		return modularity / edgeCount;
//	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public Float getValue(String value, boolean includeGrabBag) {
		Float count = 0.0f;
		
		if (value.equals(MODULE_COUNT)) {
			count = (float)this.getModuleCount();
		}
		else if (value.equals(AVG_DENSITY)) {
			count = this.getAverageDensity(includeGrabBag);
		}
		else if (value.equals(AVG_MODULE_SIZE)) {
			count = this.getAverageModuleSize(includeGrabBag);
		}
		else if (value.equals(VERTEX_COUNT)) {
//			count = (float)this.getVertexCount();
			count = (float)this.getOriginalVertexCount();
		}
		else if (value.equals(OVERLAP)) {
//			count = (float)this.getVertexCount();
			count = Overlap.getOverlap(this, includeGrabBag);
		}
		else if (value.equals(ENTROPY)) {
//			count = (float)this.getVertexCount();
			count = Entropy.getEntropy(this, includeGrabBag);
		}
		
		return count;
	}

	public void setOriginalVertexCount(int vertexCount) {
		originalVertexCount = vertexCount;
	}
	
	public int getOriginalVertexCount() {
		return this.originalVertexCount;
	}

	public boolean containsVertex(Vertex v) {
		return _vertices.contains(v);
	}

	public Set<Collection<Vertex>> getCollections() {
		Set<Collection<Vertex>> set = new HashSet<Collection<Vertex>>();
		Collection<Module> col = this.getModules();
		for (Module m : col) {
			set.add(m.getVertices());
		}
		
		return set;
	}

	/**
	 * Given a vertex name, give a module it belongs to.
	 * 
	 * @param vertexName
	 * @return Module containing the vertex
	 */
	public Collection<Module> getVertexModule(Vertex v) {
		ArrayList<Module> array = new ArrayList<Module>();
		
		for (Module m : this._modules.values()) {
			if (m.contains(v))
				array.add(m);
		}
//		Module m = v.getModuleByClustering(this);
//		array.add(m);

		return array;
	}

	public void recordOriginalModuleCount() {
		this.originalModuleCount = this._modules.size();
	}

	public boolean hasModule(String moduleName) {
		if (_modules == null)
			return false;
		return _modules.containsKey(moduleName);
	}

	public Vertex getVertex(String vName) {
		for (Vertex u : _vertices)
			if (u.getName().equals(vName)) return u;
		return null;
	}

	/**
	 * Clears all internal data structures making the clustering empty
	 */
	public void clear() {
		this._cooccurenceMatrix = null;
		this._modules.clear();
		this._vertices.clear();
	}

	public boolean hasOverlap() {
		return this._hasOverlap;
	}
}
