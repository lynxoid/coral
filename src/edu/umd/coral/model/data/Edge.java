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

/**
 * Class edge extends Tuple - an edge in a network w/ weight and direction
 * if directed = true, u is the source, v - destination, T - a vertex.
 * Also used as an edge between Rows of the matrix when reordering is 
 * used. The weight describes the "distance" between rows defined by some
 * metric and then the rows are hierarchically clustered. 
 * @author darya
 *
 * @param <T>
 */
public class Edge<T> extends Tuple<T> {
	
	public float weight = 1;
	
	public boolean directed = false;
	
	public Edge() {}
	
	public Edge(T u, T v) {
		this.u = u;
		this.v = v;
	}
	public String toString(){
		String str = "(" + u.toString() + ", " + v.toString() + ")";
		return str;
	}
	
	public boolean equals(Edge<T> edge){
		return ( (this.u.equals(edge.u) && this.v.equals(edge.v) ) || 
				(this.v.equals(edge.u) && this.u.equals(edge.v)));
	}	
}
