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

public class Pair<T1, T2> {
	
	private T1 key;
	
	private T2 value;
	
	
	
	public Pair(T1 k, T2 v) {
		key = k;
		value = v;
	}
	public T1 getKey() {
		return key;
	}
	public void setKey(T1 key) {
		this.key = key;
	}
	public T2 getValue() {
		return value;
	}
	public void setValue(T2 value) {
		this.value = value;
	}
	
	
	
	public String toString() {
		return key.toString() + ":" + value.toString();
	}
}
