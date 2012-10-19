package edu.umd.coral.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Pair;
import edu.umd.coral.model.data.Vertex;

public class CrossOverModuleOrderManager extends ModuleOrderManager {
	
	public ArrayList<Module> getOrdering(Clustering clustering, String [] names, int sizeCutoff) {	
		return this.getModuleOrderings(clustering, names, sizeCutoff);
	}
	
	private ArrayList<Module> getModuleOrderings(Clustering c, String [] vertexNames, int sizeCutoff) {
		if (c == null || vertexNames == null)
			return null;

		ArrayList<Module> order;
		Module module;
		Vertex v;
		Map<Module, Integer> mapping;
		Map<Module, ArrayList<Pair<Vertex,Integer>>> vertexMapping;
		ArrayList<Pair<Vertex, Integer>> l;
		
		int size = vertexNames.length;

		mapping = new HashMap<Module, Integer>();
		vertexMapping = new HashMap<Module, ArrayList<Pair<Vertex, Integer>>>();

		Pair<Vertex, Integer> p;
		Collection<Module> matchedModules;
		Iterator<Module> iter;
		
		// go through each vertex
		for (int i = 0; i < size; i++) {
			v = c.getVertex(vertexNames[i]);
			matchedModules = c.getVertexModule(v);
			iter = matchedModules.iterator();
			
			// for all the modules that the vertex might belong to
			// add vert locations to their rankings
			while (iter.hasNext()) {
				module = iter.next();
				
				if (module.getSize() >= sizeCutoff) { // map
					// if a vertex was in some module in this clustering
					p = new Pair<Vertex, Integer>(v, i);
					if (mapping.containsKey(module)) {
						mapping.put(module, mapping.get(module) + i);
						l = vertexMapping.get(module);
					}
					else {
						mapping.put(module, i);
						l = new ArrayList<Pair<Vertex, Integer>>();
						vertexMapping.put(module, l);
					}
					l.add(p);
				}
			}
		}

		// sort each module
		for (Map.Entry<Module, ArrayList<Pair<Vertex, Integer>>> entry : vertexMapping.entrySet()) {
			l = entry.getValue();
			module = entry.getKey();
			
			// check that the size of l is the same as the size of module
			if (l.size() != module.getSize())
				System.out.println("I am going to fail soon...");
			
			Collections.sort(l, new PairComparator());
			
			// set this vertex order on this module
//			assert !l.contains(null);
			
			module.setVertexMapping(l);
			// System.out.println(l);
		}

		ArrayList<Map.Entry<Module, Integer>> list = new ArrayList<Map.Entry<Module, Integer>>();
		list.addAll(mapping.entrySet());

		// rank
		Collections.sort(list, new EntryComparator());

		order = new ArrayList<Module>();
		for (Map.Entry<Module, Integer> e : list)
			order.add(e.getKey());

		return order;
	}

}
