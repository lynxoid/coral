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
package edu.umd.coral.ui;

import javax.swing.JPanel;

import edu.umd.coral.model.DataModel;

public abstract class JPanelExt extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5942766878019489460L;
	
	protected DataModel _dataModel;
	
	public JPanelExt(DataModel model) {
		_dataModel = model;
	}
}
