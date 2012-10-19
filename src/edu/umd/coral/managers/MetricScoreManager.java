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
import java.util.Collections;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.ComparisonScores;
import edu.umd.coral.model.metrics.Metric;

/**
 * 
 * @author lynxoid
 *
 */
public class MetricScoreManager {
	
	private boolean includeGrabBag;
	
	public MetricScoreManager(boolean include){
		this.includeGrabBag = include;
	}
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	public ComparisonScores<Clustering> computeScores(Metric m, Collection<Clustering> clusterings) {
		if (m == null || clusterings == null)
			return null;
		
		ComparisonScores<Clustering> scores = calculateScores(m, new ArrayList<Clustering>(clusterings));
		return scores;
	}
	
	private ComparisonScores<Clustering> calculateScores(Metric metric, ArrayList<Clustering> clusterings) {
		System.out.println("Calculating similarity scores");
//		long before = System.currentTimeMillis();
		
		//Clustering [] array = clusterings.toArray(new Clustering[0]);
		Collections.sort(clusterings);
		
		int size = clusterings.size();
		
		int i, j;
		Clustering c1, c2;
		
		// TODO: is sorted?
		float [][] data = new float[size][size];
		ArrayList<Clustering> list = new ArrayList<Clustering>();
		for (i = 0; i < size - 1; i++) {
			c1 = clusterings.get(i);
			list.add(c1);
			for (j = i + 1; j < size; j++) {
				try {
					c2 = clusterings.get(j);
					data[j][i] = data[i][j] = metric.getScore(c1, c2, includeGrabBag);
					
					// System.out.println(c1.getName() + " " + c2.getName() + ": " + data[i][j]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (i < size)
			list.add(clusterings.get(i));
		
//		long after = System.currentTimeMillis();
//		System.out.println("Time to cal similarit: " + (after - before) + "ms");

		return new ComparisonScores<Clustering>(data, list);
	}

}
