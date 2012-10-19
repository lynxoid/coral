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

import java.util.ArrayList;

import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Matrix;

public interface CliqueManager {
	
	//public ArrayList<Clique> getCliques(Matrix m, Map<String, Vertex> map);
	
	/**
	 * Each clique is a collection of vertices that define a subgraph
	 */
	public ArrayList<Clique> getCliques(Matrix m, boolean cutoff);
	
}
