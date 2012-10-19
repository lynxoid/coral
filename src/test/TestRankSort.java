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

public class TestRankSort extends TestCase {
	
	public void testRank() {
		float [] data = {0.5f, 0.3f, 0.2f, 0.1f, 0.4f};
		
		int [] ranks = {0, 1, 2, 3, 4};
		
		int i, j, size = ranks.length;
		int tempIndex;
		double value1, value2;
		for (i = 0; i < size; i++) {
			for (j = 0; j < size - i  - 1; j++) {
				value1 = data[ranks[j]];
				value2 = data[ranks[j+1]];
				if (value1 > value2) {	// if the same value - do not swap
					// swap indices
					tempIndex = ranks[j];
					ranks[j] = ranks[j+1];
					ranks[j+1] = tempIndex;
				}
			}
		}
		
		int [] ranks_pr = new int[5];
		for (i = 0; i < size; i++) {
			ranks_pr[ranks[i]] = i;
		}
		
		assertTrue(ranks_pr[0] == 4);
		assertTrue(ranks_pr[1] == 2);
		assertTrue(ranks_pr[2] == 1);
		assertTrue(ranks_pr[3] == 0);
		assertTrue(ranks_pr[4] == 3);
	}
}
