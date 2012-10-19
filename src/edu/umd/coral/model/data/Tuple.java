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
 * A pair of things. T can be a vertex, string or clustering
 * @author darya
 *
 * @param <T>
 */

public class Tuple<T> implements Comparable<T> {
	
	public T u;
	
	public T v;
	
	public String toString(){
		return u.toString() + ", " + v.toString();
	}

	public int hashCode(){
		return this.u.hashCode() * this.v.hashCode();
	}
	public boolean equals(Tuple<T> t){
		return (this.u.equals(t.u) && this.v.equals(t.v));
	}

	public int compareTo(T arg0) {
		return 0;
	}

}
