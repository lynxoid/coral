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
package edu.umd.coral.ui.control;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.GradientColorModel;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.slider.RangeSlider;

	/**
	 * Currently unimplemented. This class creates a slider that can be used to set a minimum or maximum threshold to display 
	 * in the matrix. Example: Only display vertices that co-occure between 4 or 8 times. 
	 * @author darya
	 *
	 */
public class CutOffValuesSlider extends JPanelExt implements PropertyChangeListener, ChangeListener {
	
	private RangeSlider slider;

	public CutOffValuesSlider(DataModel model) {
		super(model);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// listen for when new data is loaded
		_dataModel.addPropertyChangeListener(DataModel.ORIGINAL_MATRIX_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.MATRIX_CHANGED, this);
		
		TitledBorder leftBorder = BorderFactory.createTitledBorder("Filter matrix values");
	    leftBorder.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(leftBorder);
		
//		JLabel label = new JLabel("Filter matrix values:");
//		label.setAlignmentX(LEFT_ALIGNMENT);
//		add(label);
		
		slider = new RangeSlider();
		slider.setMinimum(0);
		slider.setMaximum(10);
		slider.setDrawGradient(true);
		
		slider.setValue(0);
		slider.setUpperValue(10);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.addChangeListener(this);
		slider.setAlignmentX(LEFT_ALIGNMENT);
		add(slider);
		
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4367885157464050088L;

	public void propertyChange(PropertyChangeEvent e) {
		// reset slider
		slider.setValue(0);
		
		Matrix matrix = _dataModel.getCurrentMatrix();
		if (matrix != null) {
			int max = (int)matrix.getMax();
			slider.setMaximum(max);
			slider.setUpperValue(max);
			System.out.println("curr matrix max: " + max);
		}
	}

	public void stateChanged(ChangeEvent arg0) {
		int lowerValue = slider.getValue();
		int upperValue = slider.getUpperValue();
		
		// update slider's start and end color
		Color startColor = GradientColorModel.getGradientColor(
				Color.RED, 
				Color.GREEN, 
				lowerValue / slider.getMaximum(),
				false);
		Color endColor = GradientColorModel.getGradientColor(
				Color.RED, 
				Color.GREEN, 
				upperValue / slider.getMaximum(),
				false);
		
		slider.setStartColor(startColor);
		slider.setEndColor(endColor);
		
		// update model
		System.out.println("slider: " + lowerValue + " " + upperValue);
		_dataModel.setLowerBound(lowerValue);
		_dataModel.setUpperBound(upperValue);
	}

}
