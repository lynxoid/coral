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
package edu.umd.coral.ui.table;

import java.util.Comparator;

import javax.swing.table.TableRowSorter;

public class Vertex2VertexTableSorter 
		extends	TableRowSorter<Vertex2VertexTableModel>{

	public Vertex2VertexTableSorter(Vertex2VertexTableModel tableModel) {
		super(tableModel);
		
	}
	
	public Comparator<?> getComparator(int index) {
		Comparator<?> c = this.getModel().getColumnComparator(index);
		return c;
	}
	
	
}
