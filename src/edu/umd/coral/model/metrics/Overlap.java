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

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.parse.ModuleFilesParser;

public class Overlap {
	
	public static float getOverlap(Clustering c, boolean includeGrabBag) {
		ArrayList<Module> mods = new ArrayList<Module>(c.getModules());
		Set<Vertex> overlapping = new TreeSet<Vertex>();
		int vert_cnt = c.getOriginalVertexCount();
		
		Module m1, m2;
		for (int i = 0; i < mods.size(); i++) {
			m1 = mods.get(i);
			if (!includeGrabBag && m1.getName().equals(
					ModuleFilesParser.GRAB_BAG)) continue;
			for (int j = i+1; j < mods.size(); j++) {
				m2 = mods.get(j);
				if (!includeGrabBag && m2.getName().equals(
						ModuleFilesParser.GRAB_BAG)) continue;
				overlapping.addAll(m1.getIntersection(m2));
			}
		}
		if (vert_cnt == 0) return 0;
		return overlapping.size() * 1.0f / vert_cnt;
	}
}
