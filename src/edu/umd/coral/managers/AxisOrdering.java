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

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.ComparisonScores;

/**
 * La!
 * 
 * @author lynxoid
 *
 */
public class AxisOrdering {
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Clustering> computeOrdering(ComparisonScores<Clustering> scores) {

		// put in a q? and keep taking things out until
		if (scores == null)
			return null;
		
		ArrayList<Clustering> ordering = scores.getGreedyOrdering();
		
		return ordering;
	}
}
