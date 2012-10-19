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

/**
 * Folkes and Mallows measure
 * 
 * from "A comparison of extrinsic clustering evalution metrics based on formal
 * constraints" by E. Amigo, J. Gonzalo, J. Artiles, F. Verdejo. Information
 * Retrieval. 2009. 12:461-486.
 * 
 * sqrt( a/(a+b) * a/(a+c)  )
 * 
 * @author lynxoid
 *
 */
public class FolkesMetric implements Metric {

	public String getName() {
		return MetricFactory.FOLKES;
	}

	public float getScore(Clustering c1, Clustering c2, boolean includeGrabBag)
			throws Exception {
		float score = BitSymmetricMatrix.fm(c1.getCoocurenceMatrix(), c2.getCoocurenceMatrix(), includeGrabBag);
		return score;
	}
	
	public String toString() {
		return MetricFactory.FOLKES;
	}

	public String getAnnotation() {
		return "<html><b>Folkes-Mallows metric</b> - pair-counting based metric; geometric<br>" +
				"mean between a/(a+b) and a/(a+c)</html>";
	}

}
