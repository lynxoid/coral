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
import java.util.Comparator;
import java.util.PriorityQueue;


public class ComparisonScoresQ extends ComparisonScores<Clustering> {

	PriorityQueue<Score<Clustering>> q = new PriorityQueue<Score<Clustering>>(10, new ScoreComparator());
	
	public ComparisonScoresQ(float[][] scores, ArrayList<Clustering> list) {
		super(scores, list);
		
		// put scores in a queue
		int len = scores.length;
		int i,j;
		Clustering u;
		for (i = 0; i < len-1; i++) {
			u = list.get(i);
			for (j = i+1; j < len; j++) {
				q.add( new Score<Clustering>(u, list.get(j), scores[i][j]) );
			}
		}
	}
	
	private class ScoreComparator implements Comparator<Score<Clustering>> {

		public int compare(Score<Clustering> o1, Score<Clustering> o2) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	
	public ArrayList<Clustering> getGreedyOrdering() {
		
		return null;
	}

}
