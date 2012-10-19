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
 * In statistics, and in particular in data clustering, the Rand index or Rand
 * measure is a measure of the similarity between two data clusterings. 
 * Possible alternatives for the Rand and Adjusted Rand index are the 
 * information theoretic based measures, namely the Mutual Information (MI) and
 * the Adjusted Mutual Information (AMI).
 * 
 * R = (a + b) / (a + b + c + d) = (a + b) / (n choose 2)
 * 
 * where:
 * a - #pairs in the same module in C1, C2
 * b - #pairs in different modules in C1, C2
 * c - #pairs that are in the same module in C1, but in different modules in C2
 * d - #pairs that are in different modules in C1, but in the same module in C2
 * 
 * (Wikipedia - http://en.wikipedia.org/wiki/Rand_index)
 * 
 * @author Darya Filippova
 *
 */

public class RandIndex implements Metric {
	public int a;
	public int b;
	public int c;
	public int d;

	// calculated according to http://en.wikipedia.org/wiki/Rand_index
	// same as Saket's code
	// assuming that vertices in C1 are the same as vertices in C2
	public float getScore(Clustering c1, Clustering c2, boolean includeGrabBag) throws Exception {
		float r = BitSymmetricMatrix.rand(c1.getCoocurenceMatrix(), c2.getCoocurenceMatrix(), includeGrabBag);
		return r;
	}
	
	public float getScore(int a, int b, int c, int d) {
		return (a + b) * 1.0f / (a+b+c+d);
	}
	
	public String getName() {
		return MetricFactory.RAND;
	}
	
	public String toString() {
		return MetricFactory.RAND;
	}

	public String getAnnotation() {
		return "<html><b>Rand index</b> - a measure of aggrement between two clusterings<br>" +
				"on how many pairs were placed in the same cluster and pairs<br> " +
				"that were placed in different clusters.</html>";
	}

}
