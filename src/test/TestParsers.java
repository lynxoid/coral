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

import java.util.Map;

import javax.swing.JFrame;

import junit.framework.TestCase;
import edu.umd.coral.clustering.LAPAwesomeReordering;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.BitSymmetricMatrix;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.MyMTJMatrix;
import edu.umd.coral.model.parse.ModuleFilesParser;

public class TestParsers extends TestCase {
	
	public void testModuleParser() throws Exception {
		DataModel model = new DataModel(new JFrame("blah!"));
		ModuleFilesParser parser = new ModuleFilesParser();
		String path = "/home/lynxoid/Dropbox/coral/svn/coral_cluster/data/test/";
		String [] files = {path + "clustering1", path + "clustering2"};
		
		parser.parse(files, model);
		
		// verify that clusterings and modules look ok
		Map<String, Clustering> clusterings = model.getClusterings();
		Clustering c1 = clusterings.get("clustering1");
		Clustering c2 = clusterings.get("clustering2");
		
		assertEquals(2, c1.getModuleCount());
		assertEquals(3, c2.getModuleCount());
		
		// test modules
//		checkC1(c1);
//		checkC2(c2);
		
		// test matrices
		try {
			c1.createCooccurenceMatrix();
			c2.createCooccurenceMatrix();
			
			BitSymmetricMatrix bsm = c1.getCoocurenceMatrix();
			BitSymmetricMatrix bsm2 = c2.getCoocurenceMatrix();
			
			checkRowValues(bsm);
			
			// check row and column sums
			int rowSum = 0;
			String [] vertexNames = {"v1", "v2", "v3", "v4", "v5", "v6", "v7", "v8", "v9", "v10"};
			for (String u : vertexNames) {
				rowSum = 0;
				for (String v : vertexNames)
					rowSum += bsm.getElement(bsm.getRowIndex(u), bsm.getRowIndex(v));
				assertEquals(5, rowSum);
			}
			
			// check two rows from the other matrix
			checkRowValues2(bsm2);
			
			// test rowSums
			int [] rowSums = {3,3,3,3,3,3,4,4,4,4};
			for (int i = 0; i < 10; i++) {
				String u = vertexNames[i];
				rowSum = 0;
				for (String v : vertexNames)
					rowSum += bsm2.getElement(bsm2.getRowIndex(u), bsm2.getRowIndex(v));
				assertEquals(rowSums[i], rowSum);
			}
			
			
			MyMTJMatrix sum = new MyMTJMatrix(10);
			sum.addMatrix(bsm);
			sum.addMatrix(bsm2);
			sum.rowNames = sum.columnNames = bsm.rowNames;
			
			checkSumsRows(sum);
			
			// reorder and check the same thing
			LAPAwesomeReordering so = new LAPAwesomeReordering(1, 0);
			Matrix rMatrix = so.reorder(sum);
			
			int [] sumRowSums = {8,8,8,8,8,8,9,9,9,9};
			for (int i = 0; i < 10; i++) {
				String u = vertexNames[i];
				rowSum = 0;
				for (String v : vertexNames)
					rowSum += rMatrix.getElement(rMatrix.getRowIndex(u), rMatrix.getRowIndex(v));
				assertEquals(sumRowSums[i], rowSum);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void checkSumsRows(MyMTJMatrix sum) {
		assertEquals(2.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v2")));
		assertEquals(2.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v3")));
		assertEquals(1.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v4")));
		assertEquals(1.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v5")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v6")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v7")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v8")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v9")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v1"), sum.getRowIndex("v10")));
		
		assertEquals(2.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v1")));
		assertEquals(2.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v3")));
		assertEquals(1.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v4")));
		assertEquals(1.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v5")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v6")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v7")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v8")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v9")));
		assertEquals(0.0f, sum.getElement(sum.getRowIndex("v2"), sum.getRowIndex("v10")));
	}

	private void checkRowValues2(BitSymmetricMatrix bsm2) {
		assertEquals(1.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v2")));
		assertEquals(1.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v3")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v4")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v5")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v6")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v7")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v8")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v9")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v1"), bsm2.getRowIndex("v10")));
		
		assertEquals(1.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v1")));
		assertEquals(1.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v3")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v4")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v5")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v6")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v7")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v8")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v9")));
		assertEquals(0.0f, bsm2.getElement(bsm2.getRowIndex("v2"), bsm2.getRowIndex("v10")));
	}

	private void checkRowValues(BitSymmetricMatrix bsm) {
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v2")));
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v3")));
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v4")));
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v5")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v6")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v7")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v8")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v9")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v1"), bsm.getRowIndex("v10")));
		
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v1")));
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v3")));
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v4")));
		assertEquals(1.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v5")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v6")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v7")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v8")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v9")));
		assertEquals(0.0f, bsm.getElement(bsm.getRowIndex("v2"), bsm.getRowIndex("v10")));
	}

	/**
	private void checkC2(Clustering c2) {
		Module m;
		m = c2.getModule("m1");
		assertTrue(m != null);
		assertEquals(3, m.getSize());
		assertTrue(m.contains("v1"));
		assertTrue(m.contains("v2"));
		assertTrue(m.contains("v3"));
		
		m = c2.getModule("m2");
		assertTrue(m != null);
		assertEquals(3, m.getSize());
		assertTrue(m.contains("v4"));
		assertTrue(m.contains("v5"));
		assertTrue(m.contains("v6"));
		
		m = c2.getModule("m3");
		assertTrue(m != null);
		assertEquals(4, m.getSize());
		assertTrue(m.contains("v7"));
		assertTrue(m.contains("v8"));
		assertTrue(m.contains("v9"));
		assertTrue(m.contains("v10"));
	}

	private void checkC1(Clustering c1) {
		Module m = c1.getModule("m1");
		assertTrue(m != null);
		assertEquals(5, m.getSize());
		assertTrue(m.contains("v1"));
		assertTrue(m.contains("v2"));
		assertTrue(m.contains("v3"));
		assertTrue(m.contains("v4"));
		assertTrue(m.contains("v5"));
		
		m = c1.getModule("m2");
		assertTrue(m != null);
		assertEquals(5, m.getSize());
		assertTrue(m.contains("v6"));
		assertTrue(m.contains("v7"));
		assertTrue(m.contains("v8"));
		assertTrue(m.contains("v9"));
		assertTrue(m.contains("v10"));
	}
	*/
}
