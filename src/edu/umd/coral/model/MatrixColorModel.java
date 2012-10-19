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
package edu.umd.coral.model;

import java.awt.Color;

/**
 * This class is responsible for assigning colors to individual matrix values
 * 
 * @author lynxoid
 *
 */
public class MatrixColorModel {
	
	public static final int [] COLORS = new int [] {
		0xFFFFD4, 0xFED98E, 0xFE9929, 0xD95F0E, 0x993404
	};
	public static final int [] COLORS_DIVERGING = new int [] {
		0x1A9850, 0x91CF60, 0xFEE08B, 0xFC8D59, 0xD73027
	};
	
	public static final Color [] COLORS_DIVERGING_RGB = new Color [] {
		new Color(0x000000),
		new Color(0x1A9850), 
		new Color(0x91CF60), 
		new Color(0xFEE08B), 
		new Color(0xFC8D59), 
		new Color(0xD73027)
	};
	/**
	 * Scaleable color model based on maxValue (the highest number of co-occurences).
	 * The colors vary from green to red, green being 1 co-occurence, and red being the maxValue 
	 * number of co-occurences. The color spectrum increases as the max value does 
	 * (going green-yellow-orange-red).
	 * @param value
	 * @param maxValue
	 * @param selected
	 * @return color based on "value"
	 */
	public static Color getRGBColor(double value, double maxValue,
			boolean selected) {
		double saturation = 1f;
		double brightness = .9f;
		double index = 120 - (value - 1) * (120 / (maxValue - 1));
		if (value == 0) {
			brightness = 0;
			saturation = 0;
		}
		if (maxValue == 1) {
			index = 120;
		}
		if (selected) {
			saturation = .7f;
			brightness = 1f;
			if (value == 0) {
				value = 360;
				saturation = .2f;
				brightness = 1f;
			}
		}
		Color c = Color.getHSBColor((float)index / 360, (float)saturation, (float) brightness);
		return c;
	}
	
	
	public static Color getGrayScaleColor(double value, double maxValue) {
		double saturation = 0f;
		double brightness = .9f;
		
		double index = 120 - (value - 1) * (120 / (maxValue - 1));
		if (value == 0) {
			brightness = 0;
			saturation = 0;
		}
		
		if (maxValue == 1) {
			index = 120;
		}
		
		Color c = Color.getHSBColor(0.f, (float)saturation, (float) (brightness * index / 360));
		return c;
	}
}
