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
package edu.umd.coral.model.metrics;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.parse.ModuleFilesParser;

public class Entropy {
	/**
	 * 
	 * @param c
	 * @param includeGB - whether to include grab bag in calculations
	 * @return
	 */
	public static float getEntropy(Clustering c, boolean includeGB) {
		float entropy = 0.0f;
		float prob;
		int N = c.getVertexCount();
		for (Module m : c.getModules()) {
			if (!includeGB && m.getName().equals(ModuleFilesParser.GRAB_BAG))
				continue;
			prob = m.getSize() * 1.0f / N;
			if (prob > 0) // skipping an emty module
				entropy += prob * Math.log(prob) / Math.log(2);
		}
		
		return -entropy;
	}
}
