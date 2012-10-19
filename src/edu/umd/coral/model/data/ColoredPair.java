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

import java.awt.Color;

public class ColoredPair<T1, T2> extends Pair<T1, T2> {

	private Color color;
	
	private boolean hasOverlap = false;
	
	public ColoredPair(T1 k, T2 v) {
		super(k, v);
	}
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public void setOverlap(boolean hasOverlap) {
		this.hasOverlap = hasOverlap;
	}
	
	public boolean getOverlap() {
		return hasOverlap;
	}

}
