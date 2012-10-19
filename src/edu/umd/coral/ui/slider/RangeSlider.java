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
 * http://blog.limewire.org/?p=340
 *
 * 09/03/2010
 */

package edu.umd.coral.ui.slider;



import java.awt.Color;

import javax.swing.JSlider;

import edu.umd.coral.model.GradientColorModel;

/**
 * An extension of JSlider to select a range of values using two thumb controls.
 * The thumb controls are used to select the lower and upper value of a range
 * with pre-determined minimum and maximum values.
 * 
 * <p>RangeSlider makes use of the default BoundedRangeModel, which supports an
 * inner range defined by a value and an extent.  The upper value returned by
 * RangeSlider is simply the lower value plus the extent.</p>
 * 
 * @author Ernie Yu, LimeWire LLC
 */
public class RangeSlider extends JSlider {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6911210572427014201L;
	
	/**
	 * Start color for the gradient in the track
	 */
	private Color startColor = GradientColorModel.getLadderColor(0, 1, false);;

	/**
	 * End color for the gradient in the track
	 */
	private Color endColor = GradientColorModel.getLadderColor(1, 1, false);

	private boolean drawGradient = false;

	/**
     * Constructs a RangeSlider with default minimum and maximum values of 0
     * and 100.
     */
    public RangeSlider() {
    }

    /**
     * Constructs a RangeSlider with the specified default minimum and maximum 
     * values.
     */
    public RangeSlider(int min, int max) {
        super(min, max);
    }

    /**
     * Overrides the superclass method to install the UI delegate to draw two
     * thumbs.
     */
    
    public void updateUI() {
        setUI(new RangeSliderUI(this));
        // Update UI for slider labels.  This must be called after updating the
        // UI of the slider.  Refer to JSlider.updateUI().
        updateLabelUIs();
    }

    /**
     * Returns the lower value in the range.
     */
    
    public int getValue() {
        return super.getValue();
    }

    /**
     * Sets the lower value in the range.
     */
    
    public void setValue(int value) {
        int oldValue = getValue();
        if (oldValue == value) {
            return;
        }

        // Compute new value and extent to maintain upper value.
        int oldExtent = getExtent();
        int newValue = Math.min(Math.max(getMinimum(), value), oldValue + oldExtent);
        int newExtent = oldExtent + oldValue - newValue;

        // Set new value and extent, and fire a single change event.
        getModel().setRangeProperties(newValue, newExtent, getMinimum(), 
            getMaximum(), getValueIsAdjusting());
    }

    /**
     * Returns the upper value in the range.
     */
    public int getUpperValue() {
        return getValue() + getExtent();
    }

    /**
     * Sets the upper value in the range.
     */
    public void setUpperValue(int value) {
        // Compute new extent.
        int lowerValue = getValue();
        int newExtent = Math.min(Math.max(0, value - lowerValue), getMaximum() - lowerValue);
        
        // Set extent to set upper value.
        setExtent(newExtent);
    }
    
    public Color getStartColor() {
		return startColor;
	}

	public void setStartColor(Color startColor) {
		this.startColor = startColor;
		
		this.invalidate();
	}

	public Color getEndColor() {
		return endColor;
	}

	public void setEndColor(Color endColor) {
		this.endColor = endColor;
		this.invalidate();
	}
	
	
	public boolean isDrawGradient() {
		return drawGradient;
	}
	
	public void setDrawGradient(boolean b) {
		drawGradient = b;
		invalidate();
	}
}
