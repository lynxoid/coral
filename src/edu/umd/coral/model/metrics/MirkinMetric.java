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

import edu.umd.coral.model.data.BitSymmetricMatrix;
import edu.umd.coral.model.data.Clustering;

public class MirkinMetric implements Metric {
	
	public float getScore(Clustering c1, Clustering c2, boolean includeGrabBag) throws Exception {
		float m = BitSymmetricMatrix.mirkin(c1.getCoocurenceMatrix(), c2.getCoocurenceMatrix(), includeGrabBag);
		return m;
	}
	
	public String getName() {
		return MetricFactory.MIRKIN;
	}
	
	public String toString() {
		return MetricFactory.MIRKIN;
	}

	public String getAnnotation() {
		return "<html><b>Mirkin</b> - a measure of how many pairs were placed in different<br>" +
				"clusters in one clustering, but in the same cluster in another<br>" +
				"clustering. A measure of disagreement between the two clusterings</html>";
	}

}
