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
package test;

import junit.framework.TestCase;
import edu.umd.coral.clustering.GreedyLAP;

public class TestGreedySolver extends TestCase {
	
	public void testGreedyOn3() {
		GreedyLAP g = new GreedyLAP();
		double [][] cost = {
				{0, 5, 2},
				{4, 10, 2},
				{1, 3, 5}};
		int [] newIntColOrder = new int[3];
		int [] newIntRowOrder = new int[3];
		g.solve(3, cost, newIntColOrder, newIntRowOrder);
		
		// calc cost
		double sum = 0f;
		for (int i = 0; i < 3; i++) {
			sum += cost[i][newIntRowOrder[i]];
		}
		// 0 + 2 + 3
		assertEquals(5.0f, sum);
		
		int [] correct_order = {0, 2, 1};
		
		for (int i = 0; i < 3; i++) {
			assertEquals(correct_order[i], newIntRowOrder[i]);
			System.out.println(i + " => " + newIntRowOrder[i]);
		}
	}
}
