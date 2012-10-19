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

public class FMeasureMetric implements Metric {

	public String getName() {
		return MetricFactory.FMEASURE;
	}

	public float getScore(Clustering truth, Clustering experiment, boolean includeGrabBag) {
		// TODO more efficient implementation?
		int N = truth.getVertexCount();
		assert (truth.getVertexCount() == experiment.getVertexCount());
		
		float max, sum = 0, f_score;
		for (Module l_i : truth.getModules()) {
			// exclude grab bag from comparison
			if (!includeGrabBag && l_i.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
			max = 0;
			for (Module c_j : experiment.getModules()) {
				if (!includeGrabBag && c_j.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
				f_score = fScore(l_i, c_j);
				if (f_score > max)
					max = f_score;
			}
			sum += max * l_i.getSize();
		}
		return sum * 2.0f / N;
	}

	private float fScore(Module l, Module c) {
		return (recall(l, c) * precision(l, c)) / (recall(l, c) + precision(l, c));
	}
	
	private float precision(Module c, Module l) {
		return c.getIntersection(l).size() * 1.0f / c.getSize();
	}
	
	private float recall(Module c, Module l) {
		return precision(l, c);
	}
	
	public String toString() {
		return MetricFactory.FMEASURE;
	}

	public String getAnnotation() {
		return "<html><b>F-measure</b> - measure based on recall and precision assuming<br> " +
				"that one of the clsuterings is a true decomposition</html>";
	}

}
