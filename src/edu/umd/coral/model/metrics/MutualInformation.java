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
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.parse.ModuleFilesParser;


// I(X, Y)

public class MutualInformation implements Metric{

	public float getScore(Clustering c1, Clustering c2, boolean includeGrabBag) throws Exception {
		if (c1.getVertexCount() != c2.getVertexCount())
			throw new Exception("sizes don't match");
		
		// naive correction for overlapping clusters - count intersections separately
//		int sum = 0;
//		for (Module m : c1.getModules())
//			sum += m.getSize();
//		int N1 = sum;
//		sum = 0;
//		for (Module m : c2.getModules())
//			sum += m.getSize();
//		int N2 = sum;
		int N1 = c1.getVertexCount();
		int N2 = c2.getVertexCount();
		
		float I = 0.0f;
		float p_k1, p_k2, P_k1_k2;
		
		
		for (Module m1 : c1.getModules()) {
			if (!includeGrabBag && m1.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
			p_k1 = m1.getSize() * 1.0f / N1;
			for (Module m2 : c2.getModules()) {
				if (!includeGrabBag && m2.getName().equals(ModuleFilesParser.GRAB_BAG)) continue;
				p_k2 = m2.getSize() * 1.0f / N2;
				P_k1_k2 = getUnionSize(m1, m2) * 1.0f / (N1+N2);
				if (P_k1_k2 != 0)
					// should be log of 2
					I += P_k1_k2 * Math.log10( P_k1_k2 / ( p_k1 * p_k2 ));
			}
		}
		return I;
	}

	private int getUnionSize(Module m1, Module m2) {
		int size = 0;
		for (Vertex v : m1.getVertices()) {
			for (Vertex u : m2.getVertices()) {
				if (v.equals(u))
					size++;
			}
		}
		return size;
	}

	public String getName() {
		return "Mutual Information";
	}
	
	public String toString() {
		return "Mutual Information";
	}

	public String getAnnotation() {
		return "<html><b>Mutual information</b> - information that C1 and C2 share (how<br>" +
				"much knowing one of these variables reduces uncertainty<br>" +
				"about the other).</html>";
	}

}
