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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.border.TitledBorder;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.ui.JPanelExt;

	/**
	 * Creates a panel that allows the user to change the background color of the matrix display.
	 * However 0 co-occurences remains black.
	 * @author darya
	 *
	 */
public class BGColorChooser extends JPanelExt implements ActionListener {
	
	private JButton colorButton;
	
	public BGColorChooser(DataModel model) {
		super(model);
		
//		setPreferredSize(new Dimension(400, 100));
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		
		TitledBorder leftBorder = BorderFactory.createTitledBorder("Background color:");
	    leftBorder.setTitleJustification(TitledBorder.LEFT);
		this.setBorder(leftBorder);
		
//		JLabel label = new JLabel("Background color:");
//		label.setAlignmentX(LEFT_ALIGNMENT);
//		add(label);

		colorButton = new JButton("Change");
		colorButton.setOpaque(true);
		colorButton.setBackground(model.getBackgroundColor());
		colorButton.addActionListener(this);
		colorButton.setAlignmentX(LEFT_ALIGNMENT);
		add(colorButton);
	}

	/**
	 * generated
	 */
	private static final long serialVersionUID = -6616046460574976598L;

	/************************************************************
	 * 
	 * Event handling
	 * 
	 ***********************************************************/
	public void actionPerformed(ActionEvent arg0) {
		Color c = JColorChooser.showDialog(this, 
				"Choose background color", _dataModel.getBackgroundColor());
		
		colorButton.setBackground(c);
		
		_dataModel.setBackgroundColor(c);
	}

}
