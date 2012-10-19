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

import java.util.Iterator;

import no.uib.cipr.matrix.MatrixEntry;

public class BaseMatrix extends Matrix {
	/**
	 * All data
	 */
	private MyMTJMatrix data;
	
	/**
	 * Cells that represent the base clustering are set to 1, the rest is 0's
	 */
	private MyMTJMatrix isInBase;
	
	/**
	 * Wraps data and binary base matrix into a single BaseMatrix object
	 * 
	 * @param data
	 * @param base
	 * @throws Exception
	 */
	public BaseMatrix(MyMTJMatrix data, MyMTJMatrix base) throws Exception {
		super(data.rowNames, data.columnNames);
		// check that row order is the same
		for (int i = 0; i < data.getColumnCount(); i++)
			if (! data.getRowName(i).equals(base.getRowName(i)) )
				throw new Exception("matrix order should be the same");
		
		this.setMax(data.getMax());
		this.data = data;
		this.isInBase = base;
		
		
	}
	
	@Override
	public double getElement(int i, int j) {
		return data.getElement(i, j);
	}
	
	public Iterator<MatrixEntry> iterator() {
		return data.getMatrix().iterator();
	}
	
	public boolean isBase(int i, int j) {
		return isInBase.getElement(i, j) > 0;
	}

	/**
	 * Returns the full matrix (presumably in the same order as the base matrix)
	 * @return
	 */
	public Matrix getDataMatrix() {
		return data;
	}

	/**
	 * Returns a binary matrix where the cells are only set to 1 if the 
	 * corresponding items co-clustered in the selected reordering
	 * @return
	 */
	public Matrix getBaseMatrix() {
		return isInBase;
	}
}
