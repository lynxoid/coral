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
package edu.umd.coral.ui.tabs;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.JPanel;

import edu.umd.coral.model.data.Vertex;

/**
 * 
 * Data model for vis
 * 
 */
public class PPDataModel extends PropertyChangeSupport  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5359631650292502162L;
	/**
	 * 
	 */
	public final static String SELECTED_VERTICES_CHANGED = "selectedVertChangeFromPP";
	
	public PPDataModel(JPanel c) {
		super(c);
	}
	
	public void setSelectedVertices(ArrayList<Vertex> value){
		ArrayList<Vertex> oldValue = _blah;
		_blah = value;
		
		this.firePropertyChange(SELECTED_VERTICES_CHANGED, oldValue, value);
	}
	public ArrayList<Vertex> getSelectedVertices() {
		return _blah;
	}
	private ArrayList<Vertex> _blah;
}
