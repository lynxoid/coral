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
package edu.umd.coral.ui.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.ui.JPanelExt;

public class ZoomingPanel extends JPanelExt implements ChangeListener, PropertyChangeListener {

	JSlider slider;
	
	public ZoomingPanel(DataModel model) {
		super(model);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		TitledBorder leftBorder = BorderFactory.createTitledBorder("Matrix zoom");
	    leftBorder.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(leftBorder);
		
		_dataModel.addPropertyChangeListener(DataModel.ZOOM_CHANGED, this);
		
//		JLabel l = new JLabel("Zoom in/out:");
//		l.setAlignmentX(LEFT_ALIGNMENT);
//		add(l);
		slider = new JSlider(JSlider.HORIZONTAL /* orientation */, 
				0 /* min */, 
				1000 /* max */, 
				100 /* value */);
		slider.setMajorTickSpacing(200);
		slider.setMinorTickSpacing(50);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		slider.setAlignmentX(LEFT_ALIGNMENT);
		add(slider);
		
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -450976020731133267L;

	
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting()) {
			int zoomValue = (int)source.getValue();
			if (zoomValue > 20)
				_dataModel.setZoomValue(zoomValue / 100.0f);
			else
				_dataModel.setZoomValue(0.2f);
		}
	}

	
	public void propertyChange(PropertyChangeEvent arg0) {
		slider.setValue((int)(_dataModel.getZoomValue()* 100));
	}

}
