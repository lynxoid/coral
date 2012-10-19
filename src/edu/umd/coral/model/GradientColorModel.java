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

public class GradientColorModel {

	public static final float MAX_SATURATION = 1.0f;

	public static final float MAX_BRIGHTNESS = MAX_SATURATION;

	public static final float EPSILON = 0.001f;
	
	public static Color getMatrixColor(double value, double maxValue, boolean selected) {
		float hue = 10;
		return getRGBColor(value, maxValue, selected, hue);
	}
	
	public static Color getLadderColor(float value, float maxValue, boolean isSelected) {
		float hue = 210;
		return getRGBColor(value, maxValue, isSelected, hue);
	}

	/**
	 * Given a hue, varies saturation and brightness based on maxValue and value
	 * 
	 * @param value - a value, goes from 0 to 1
	 * @param maxValue  -1
	 * @param selected
	 * @param hue - value from 1 to 360, color hue
	 * @return
	 */
	public static Color getRGBColor(double value, double maxValue, boolean selected, float hue) {
		double saturation = value / maxValue;
		if (value < EPSILON)
			saturation = EPSILON;
		float brightness = 1;
		float index = hue;

		if(maxValue == value) {
			//			index = 120;
			brightness = MAX_BRIGHTNESS;
			saturation = MAX_SATURATION;
		}

		if(selected) {
			saturation = .7f;
			brightness = 1f;
		}

		Color c = Color.getHSBColor(index/360, (float)saturation, brightness);

		return c;
	}

	/**
	 * 
	 * @param c1
	 * @param c2
	 * @param offset - value between 0 and 1
	 * @param selected
	 * @return
	 */
	public static Color getGradientColor(Color c1, Color c2, float offset, boolean selected) {
		float ratio = offset;
		int red = 	(int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
		int green = 	(int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
		int blue = 	(int)(c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));
		Color color = new Color(red, green, blue);

		return color;
	}

}
