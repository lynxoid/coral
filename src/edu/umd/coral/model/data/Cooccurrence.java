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
package edu.umd.coral.model.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cooccurrence implements Comparable<Cooccurrence> {
	
	public Vertex u;
	
	public Vertex v;
	
	/**
	 * 
	 * Clusterings in which these two vertices co-occur
	 * 
	 */
	private Set<Clustering> clusterings;// = new HashSet<Clustering>();
	
	private String signature;
	
	private String niceString;
	
	private List<Clustering> lastUsedOrdering;
	
	public Cooccurrence(Vertex u, Vertex v) {
		this.u = u;
		this.v = v;
	}
	
	public void setSignature(List<Clustering> ordering) {
		lastUsedOrdering = ordering;
		
		String s = "";
		if (clusterings == null)
			for (int i = 0; i < ordering.size(); i++) {
				s += "0";
			}
		else
			for (Clustering c : ordering) {
				if (clusterings.contains(c))
					s += "1";
				else
					s += "0";
			}
		signature = s;
	}
	
	public String getSignature() {
		return signature;
	}

	public void addClustering(Clustering clustering) {
		if (clusterings == null)
			clusterings = new HashSet<Clustering>();
		clusterings.add(clustering);
	}

	public Set<Clustering> getClusterings() {
		return clusterings;
	}

	public void addClusterings(Set<Clustering> set) {
		if (clusterings == null)
			clusterings = new HashSet<Clustering>();
		clusterings.addAll(set);
	}

	public int getCooccurrenceCount() {
		if (clusterings != null)
			return clusterings.size();
		return 0;
	}

	public String getNiceHTMLString() {
		if (niceString != null)
			return niceString;
		if (lastUsedOrdering == null)
			return null;
		
		int i = 1;
		int cutoff = 10;
		
		String s = "<html>(" + u.getName() + ", " + v.getName() + "): <b>" + clusterings.size() + "</b><br>";
		for (Clustering c : lastUsedOrdering) {
			if (i >= cutoff) {
				s += "...and more";
				break;
			}
			else
				s += c.getName() + ": " + (clusterings.contains(c) ? "<b>1</b>" : "0") + "<br>";
			i++;
		}
		s += "</html>";
		niceString = s;
		return s;
	}

	public boolean selfReferential() {
		return u.getName().equals(v.getName());
	}

	public Clustering getClusteringAt(int i) {
		if (this.lastUsedOrdering != null)
			return this.lastUsedOrdering.get(i);
		return null;
	}

	public int compareTo(Cooccurrence o) {
		int i = this.signature.compareTo(o.signature);
		if (i == 0) {
			i = u.compareTo(o.u);
		}
		if (i == 0) {
			i = v.compareTo(o.v);
		}
		return i; 
	}
	
	public String toString() {
		return signature;
	}
}
