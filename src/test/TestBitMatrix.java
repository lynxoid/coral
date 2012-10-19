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
import edu.umd.coral.model.data.BitSymmetricMatrix;

public class TestBitMatrix extends TestCase {
	
	public void testPrint() {
		System.out.println(BitSymmetricMatrix.printBits(1));
		System.out.println(BitSymmetricMatrix.printBits(4));
		System.out.println(BitSymmetricMatrix.printBits(7));
	}
	
	public void testCreate() {
		try {
			BitSymmetricMatrix bsm = new BitSymmetricMatrix(7, new String[7]);
			assertTrue(bsm.getVectorCount() == 1);
			
			bsm = new BitSymmetricMatrix(8, new String[8]);
			double maxVectors = (8 * 8.0 / 2 ) / 63; // n^2 / 2 / vect_len
			assertTrue(bsm.getVectorCount() == (int) Math.ceil(maxVectors));
			
			bsm = new BitSymmetricMatrix(12, new String[12]);
			maxVectors = (12 * 12.0 / 2 ) / 63; // n^2 / 2 / vect_len
			assertTrue(bsm.getVectorCount() == (int) Math.ceil(maxVectors));
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	public void testGetElement() {
		try{
			BitSymmetricMatrix bsm = new BitSymmetricMatrix(12, new String[12]);
			int i, j;
			for (i = 0; i < 12; i++)
				for (j = 0; j < 12; j++)
					assertTrue(bsm.getElement(i, j) == 0);
		} catch (Exception e) {
			
		}
	}
	
	public void testGetSetElement() {
		try{
			BitSymmetricMatrix bsm = new BitSymmetricMatrix(12, new String[12]);
			double maxVectors = (12 * 12.0 / 2 ) / 63; // n^2 / 2 / vect_len
			assertTrue(bsm.getVectorCount() == (int) Math.ceil(maxVectors));
			bsm.setElement(0, 0, 1, false);
			assertTrue(bsm.getElement(0,0) == 1);
			for (int i= 0; i < 12; i++)
				for (int j = 0; j < 12; j++)
					if ( !(i == 0 && j == 0) )
						assertTrue(bsm.getElement(i,j) == 0);
			
			bsm.setElement(9, 9, 1, false);
			assertTrue(bsm.getElement(9,9) == 1);
			for (int i= 0; i < 12; i++)
				for (int j = 0; j < 12; j++)
					if ( !(i == 0 && j == 0) && !(i == 9 && j == 9))
						assertTrue(bsm.getElement(i,j) == 0);
			
			bsm.setElement(0, 0, 0, false);
			assertTrue(bsm.getElement(0,0) == 0);
			for (int i= 0; i < 12; i++)
				for (int j = 0; j < 12; j++)
					if ( !(i == 9 && j == 9))
						assertTrue(bsm.getElement(i,j) == 0);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testSymmetric() {
		BitSymmetricMatrix bsm = new BitSymmetricMatrix(12, new String[12]);
		bsm.setElement(3, 4, 1, false);
		assertTrue(bsm.getElement(3, 4) == 1);
		assertTrue(bsm.getElement(4, 3) == 1);
		
		bsm.setElement(5, 4, 1, false);
		assertTrue(bsm.getElement(5, 4) == 1);
		assertTrue(bsm.getElement(4, 5) == 1);
		
		bsm.setElement(4, 3, 0, false);
		assertTrue(bsm.getElement(3, 4) == 0);
		assertTrue(bsm.getElement(4, 3) == 0);
		
		bsm.setElement(4, 5, 0, false);
		assertTrue(bsm.getElement(5, 4) == 0);
		assertTrue(bsm.getElement(4, 5) == 0);
	}
	
	public void testRandomAccess() {
		int N = 10;
		BitSymmetricMatrix bsm = new BitSymmetricMatrix(N, new String[N]);
		System.out.println("Vectors: " + bsm.getVectorCount());
		
		// set all to 1
		for (int i = 0; i < N; i++) {
			for (int j = i; j < N; j++) {
				bsm.setElement(i, j, 1, false);
			}
		}
		
		for (int k = 0; k < 10000; k++) {
			int r = (int) Math.round(Math.random() * (N - 1));
			int c = (int) Math.round(Math.random() * (N - 1));
			assertEquals(bsm.getElement(r, c), 1.0, 0.00001);
		}
	}
	
	/**
	 * 0 1 0 0 0
	 * 1 0 0 0 0
	 * 0 0 0 1 1
	 * 0 0 1 0 1
	 * 0 0 1 1 0
	 * 
	 * 
	 */
	public void testGetJaccard() {
		BitSymmetricMatrix m1 = new BitSymmetricMatrix(5, new String[5]);
		BitSymmetricMatrix m2 = new BitSymmetricMatrix(5, new String[5]);
		// TODO: test jaccard
		// identical
		m1.setElement(0, 1, 1, false);
		m1.setElement(2, 3, 1, false);
		m1.setElement(2, 4, 1, false);
		m1.setElement(3, 4, 1, false);
		m1.setElement(4, 4, 1, false);

		m2.setElement(0, 1, 1, false);
		m2.setElement(2, 3, 1, false);
		m2.setElement(2, 4, 1, false);
		m2.setElement(3, 4, 1, false);

		assertTrue(BitSymmetricMatrix.jaccard(m1, m2, false) == 1.0f);

		m2.setElement(0, 1, 0, false);
		int inter, union;
		/* 23, 24, 34*/
		inter = 3;
		union = 4;// 01, 23, 23, 34
		assertTrue(BitSymmetricMatrix.jaccard(m1, m2, false) == inter * 1.0 / union );
	}
}
