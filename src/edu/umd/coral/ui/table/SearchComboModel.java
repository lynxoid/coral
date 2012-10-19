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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.DefaultComboBoxModel;
import javax.swing.event.ListDataListener;

import edu.umd.coral.model.data.Vertex;

public class SearchComboModel extends DefaultComboBoxModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8830257121425997964L;

	private ArrayList<Vertex> data;
	
	public void setData(Collection<Vertex> verts) {
		// sort
		int len = 0;
		if (verts != null) {
			// sort
			ArrayList<Vertex> list = new ArrayList<Vertex>();
			list.addAll(verts);
			Collections.sort(list);
			data = list;
			len = data.size();
		}
		
		this.fireContentsChanged(this, 0, len);
	}

	public void addListDataListener(ListDataListener arg0) {
		super.addListDataListener(arg0);
	}

	public Object getElementAt(int rowIndex) {
		if (data == null || data.size() == 0 || rowIndex < 0)
			return null;
		
		Vertex o = (Vertex)data.get(rowIndex);
		return o;
	}

	public int getSize() {
		if (data != null)
			return data.size();
		return 0;
	}

	public void removeListDataListener(ListDataListener arg0) {
		super.removeListDataListener(arg0);
	}

	public Object getSelectedItem() {
		return super.getSelectedItem();
	}

	public void setSelectedItem(Object arg0) {
		super.setSelectedItem(arg0);
	}
}
