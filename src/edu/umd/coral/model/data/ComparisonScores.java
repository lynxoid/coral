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
import java.util.HashSet;
import java.util.Set;

/**
 * Holds score matrix and ordering of the rows (columns) of the scores objects 
 * 
 * 
 * @author lynxoid
 *
 * @param <T>
 */
public class ComparisonScores<T extends Named> {
	
	/**
	 * symmetric matrix, using only upper triangle (see max/min)
	 */
	private float [][] scores;
	
	private ArrayList<T> ordering;
	
	public ComparisonScores(float [][] scores, ArrayList<T> list) {
		this.scores = scores;
		this.ordering = list;
	}
	
	/**
	 * Returns comparison score
	 */
	public float getValue(int i, int j) {
		return scores[i][j];
	}

	/**
	 * Returns first item being compared
	 */
	public T getXItem(int i) {
		return ordering.get(i);
	}

	/**
	 * Returns second item being compared
	 */
	public T getYItem(int j) {
		return ordering.get(j);
	}

	public int getSize() {
		return scores.length;
	}

	public float getMin() {
		float min = Float.MAX_VALUE;
		int size = scores.length;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (scores[i][j] < min)
					min = scores[i][j];
			}
		}
		return min;
	}

	public float getMax() {
		float max = Float.MIN_VALUE;
		int size = scores.length;
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				if (scores[i][j] > max)
					max = scores[i][j];
			}
		}
		return max;
	}

	public float[][] getScores() {
		return this.scores;
	}

	public ArrayList<T> getGreedyOrdering() {
		ArrayList<T> o = new ArrayList<T>();
		Set<Integer> visited = new HashSet<Integer>();
		
		// find max
		float max = Float.MIN_VALUE;
		int size = scores.length;
		int t1 = 0, t2 = 0, i, j;
		
		for (i = 0; i < size - 1; i++) {
			for (j = i + 1; j < size; j++) {
				if (scores[i][j] > max) {
					max = scores[i][j];
					t1 = i; t2 = j;
				}
			}
		}
		
		visited.add(t1);
		visited.add(t2);
		
		// t1 is always smaller than t2
		// now explore which has a larger score
		float t1Max = Float.MIN_VALUE, t2Max = Float.MIN_VALUE;
		int index = -1, index2 = -1;
		for (i = 0; i < size; i++) {
			if (scores[t1][i] > t1Max && !visited.contains(i)) {	// look for a first max
				t1Max = scores[t1][i]; index = i;
			}
		}
		
		for (i = 0; i < size; i++) {
			if (scores[t2][i] > t2Max && !visited.contains(i)) { // look for second max
				t2Max = scores[t2][i]; index2 = i;
			}
		}
		
		if (size <= 2) {
			o.add(ordering.get(t1));
			o.add(ordering.get(t2));
			return o;
		}
		
		if (t1Max > t2Max) {
			// t2 - t1 - index
			o.add(this.ordering.get(t2));
			o.add(this.ordering.get(t1));
			o.add(this.ordering.get(index));
		}
		else {
			// t1 - t2 - index2
			o.add(this.ordering.get(t1));
			o.add(this.ordering.get(t2));
			o.add(this.ordering.get(index2));
			index = index2;
		}
		visited.add(index2);
		
		
		// look for maxes
		while (o.size() < size) {
			max = Float.MIN_VALUE;
			for (i = 0; i < size; i++) {
				if (scores[index][i] > max && !visited.contains(i)) {
					max = scores[index][i];
					index2 = i;
				}
			}
			visited.add(index2);
			o.add(this.ordering.get(index2));
			index = index2;
		}
		
		return o;
	}

	public int getIndex(String s) {
		if (ordering == null)
			return -1;
		
		int size = ordering.size();
		for (int i = 0; i < size; i++)
			if (ordering.get(i).getName().equals(s) )
				return i;
		
		return -1;
	}
	
	public String toString() {
		if (ordering == null)
			return "empty";
		
		String s = "";
		for (int i = 0; i < ordering.size(); i++) {
			s += ordering.get(i).getName();
		}
		
		return s + " scores: " + this.scores.toString();
	}
}
