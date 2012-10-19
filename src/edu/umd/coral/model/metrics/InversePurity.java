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

public class InversePurity implements Metric {

	public String getName() {
		return MetricFactory.INV_PURITY;
	}

	public float getScore(Clustering experiment, Clustering truth, boolean includeGrabBag) {
		float max, precision, sum;
		sum = 0;
		for (Module l_i : truth.getModules() ) {
			if (!includeGrabBag && l_i.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
			precision = 0; max = 0;
			for (Module c_j : experiment.getModules() ) {
				if (!includeGrabBag && c_j.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
				precision = c_j.getIntersection(l_i).size() * 1.0f; 
				if (precision > max)
					max = precision;
			}
			sum += max;
		}
		return sum / experiment.getVertexCount();
	}

	public String toString() {
		return MetricFactory.INV_PURITY;
	}

	public String getAnnotation() {
		return "<html><b>Inverse purity</b> - assumming that one clustering is obtained through an experiment <br>" +
				"and another one represents the truth, shows how whether the elements from the same category<br>" +
				"tend to cluster in the same experimental module</html>";
	}
}
