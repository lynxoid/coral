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

/**
 * Metric annotation - from Meila,
 * "Comparing Clusterings by the Variation of Information", LNAI 2777, 
 * pp. 173-187, 2003.
 * 
 * @author lynxoid
 *
 */
public class VariationOfInformation implements Metric{

	public float getScore(Clustering c1, Clustering c2, boolean includeGrabBag) throws Exception {
		float H1 = Entropy.getEntropy(c1, includeGrabBag);
		float H2 = Entropy.getEntropy(c2, includeGrabBag);
		
		MutualInformation mutual = new MutualInformation();
		float I12 = mutual.getScore(c1, c2, includeGrabBag);
		
		return H1 + H2 - 2 * I12;
	}
	
	public String getName() {
		return MetricFactory.VI;
	}
	
	public String toString() {
		return MetricFactory.VI;
	}

	public String getAnnotation() {
		return "<html><b>Variation of Information</b> - a measure of the amount of information<br>" +
				"lost and gained when changing from one clustering to the other</html>";
	}
}
