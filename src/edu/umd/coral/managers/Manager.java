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
package edu.umd.coral.managers;

import edu.umd.coral.model.DataModel;


public abstract class Manager implements IManager {
	
	private DataModel _dataModel;
	
	public Manager(DataModel m) {
		_dataModel = m;
	}
	
	public DataModel getModel() {
		return _dataModel;
	}

	abstract public void execute();
}
