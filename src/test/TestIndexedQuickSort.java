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
import edu.umd.coral.util.IndexedQuickSort;

public class TestIndexedQuickSort extends TestCase {
	
	public void testOutOfOrder() {
		String [] data = {"green","dog","blue"};
		int [] indices = {0,1,2};
		IndexedQuickSort.quicksort(data, indices);
		assertEquals(indices[0], 2);
		assertEquals(indices[1], 1);
		assertEquals(indices[2], 0);
	}
	
	public void testInOrder() {
		String [] data = {"blue","dog","green"};
		int [] indices = {0,1,2};
		IndexedQuickSort.quicksort(data, indices);
		assertEquals(indices[0], 0);
		assertEquals(indices[1], 1);
		assertEquals(indices[2], 2);
	}
}
