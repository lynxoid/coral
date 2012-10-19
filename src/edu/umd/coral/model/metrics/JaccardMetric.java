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

import java.util.Collection;
import java.util.Iterator;

import edu.umd.coral.model.data.BitSymmetricMatrix;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Vertex;

public class JaccardMetric implements Metric {	

	public float getScore(Clustering c1, Clustering c2, boolean includeGrabBag) {
//		System.out.println("Jacc " + includeGrabBag);
		
		float jac = BitSymmetricMatrix.jaccard(c1.getCoocurenceMatrix(), c2.getCoocurenceMatrix(), includeGrabBag);
		return jac;
	}

	
	public float getScore(Collection<Vertex> c1, Collection<Vertex> c2) {
		if (c1 == null || c2 == null)
			return 0.0f;
		
		Iterator<Vertex> iterator = c1.iterator();
		
		int intersectionSize = 0;
		
		while (iterator.hasNext()) {
			if (c2.contains(iterator.next()))
				intersectionSize++;
		}
		
		int unionSize = c1.size() + c2.size() - intersectionSize;
		float score = intersectionSize * 1.0f / unionSize;
		
		return score;
//		return formatter.format("float   = %1.4f %n", score);
	}

	public String getName() {
		return MetricFactory.JACCARD;
	}
	
	public String toString() {
		return MetricFactory.JACCARD;
	}


	public String getAnnotation() {
		return "<html><b>Jaccard</b> - a measure of how many pairs were placed in the same<br>" +
				"cluster in both clusterings</html>";
	}

}
