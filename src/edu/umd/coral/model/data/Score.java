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
/**
 * Awesome comments go here
 * 
 * Darya Filippova
 * 
 */

package edu.umd.coral.model.data;

public class Score<T extends Named> implements ComparisonItem<Named> {
	
	/*
	 * First item being compared 
	 */
	private T itemX;
	
	/*
	 * Second item being compared
	 */
	private T itemY;
	
	/*
	 * comparison score
	 */
	private float score;
	
	/**
	 * 
	 * 
	 * @param s1 - first component for comparison
	 * @param s2 - second component for comparison
	 * @param score -comparison score
	 */
	public Score(T s1, T s2, float score) {
		this.itemX = s1;
		this.itemY = s2;
		this.score = score;
	}	

	/**
	 * Returns comparison score
	 */
	public float getValue() {
		return score;
	}

	/**
	 * Returns first item being compared
	 */
	public T getXItem() {
		return itemX;
	}

	/**
	 * Returns second item being compared
	 */
	public T getYItem() {
		return itemY;
	}
	
	public String toString() {
		return itemX.getName() + ", " + itemY.getName() + ": " + score;
	}
}

