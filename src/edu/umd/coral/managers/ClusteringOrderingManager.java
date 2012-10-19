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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Cooccurrence;
import edu.umd.coral.model.data.Vertex;

public class ClusteringOrderingManager implements IManager {

	private DataModel _dataModel;
	
	public ClusteringOrderingManager(DataModel model) {
		_dataModel = model;
	}
	
	public void execute() {
		List<Clustering> sortedClusterings = _dataModel.getClusteringOrdering();
		Map<Vertex, TreeMap<Vertex, Cooccurrence>> allCooccurr = _dataModel.getVertexPairs();
		// DataModel would not dispatch an update - allCooccur has not changed
		fixSignatures(allCooccurr, sortedClusterings);		
		_dataModel.setVertexPairs(allCooccurr);
	}
	
	/**
	 * 
	 * 
	 * @param allCooccurr
	 * @param _clusterings2
	 */
	private void fixSignatures(Map<Vertex, TreeMap<Vertex, Cooccurrence>> allCooccurr,
			List<Clustering> clusterings) {
		Set<Vertex> keys = allCooccurr.keySet();
		Collection<Cooccurrence> entries;
		for (Vertex u : keys) {
			entries = allCooccurr.get(u).values();
			for (Cooccurrence cooccur : entries) {
				cooccur.setSignature(clusterings);
			}
		}
	}

}
