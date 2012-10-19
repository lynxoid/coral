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
package edu.umd.coral.clustering;

import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import javaasp.LinearAssignmentSolver;
import no.uib.cipr.matrix.MatrixEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 * Greedy linear assignment solver
 * 
 * @author lynxoid
 *
 */
public class GreedyLAP extends LinearAssignmentSolver {
	
	Set<Integer> paired_columns;
	Set<Integer> paired_rows;
	
	/**
	 * TODO: explain
	 * will not fill out the newIntColOrder
	 */
	public void solve(int N, FlexCompRowMatrix cost, int [] newIntColOrder, int [] newIntRowOrder) {
		Triplet t;
		paired_columns = new HashSet<Integer>();
		paired_rows = new HashSet<Integer>();
		
		long before = System.currentTimeMillis();
		
		cost.compact();
		PriorityQueue<Triplet> q = new PriorityQueue<Triplet>();
		
		Iterator<MatrixEntry> iter = cost.iterator();
		MatrixEntry me;
		while (iter.hasNext()) {
			me = iter.next();
			if (me.get() != 0)
				q.add(new Triplet(me.row(), me.column(), me.get()));
		}
		
		// pick from the queue
		while (!q.isEmpty() && paired_rows.size() < N) {
			t = q.poll();
			if (paired_rows.contains(t.row) || paired_columns.contains(t.column)) continue;
			paired_rows.add(t.row);
			paired_columns.add(t.column);
			newIntRowOrder[t.row] = t.column;
		}
		
		long after = System.currentTimeMillis();
		System.out.println("        Greedy interation: " + (after - before));
	}
	
	private class Triplet implements Comparable<Triplet> {
		public int row;
		public int column;
		public double cost;
		
		public Triplet(int r, int c, double cost) {
			this.row = r;
			this.column = c;
			this.cost = cost;
		}

		// sort elements in decreasing order
		public int compareTo(Triplet t) {
			if (this.cost < t.cost)
				return -1;
			
			if (t.cost == this.cost)
				return 0;
			
			return 1;
		}
	}
}
