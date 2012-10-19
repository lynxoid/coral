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

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

import edu.umd.coral.ui.ladder.Cell;

public class LegendItem extends JComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4720546039864614933L;

	private Cell cell;
	
	private Color color;

	public LegendItem(Color c, String l) {
		this.color = c;
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		cell = new Cell(16,16);
		cell.setDrawBorder(false);
		cell.setToolTipText(l);
		cell.setColor(color);
		cell.setAlignmentX(CENTER_ALIGNMENT);
		add(cell);
		
		JLabel label = new JLabel(l);
		label.setToolTipText(l);
		label.setAlignmentX(CENTER_ALIGNMENT);
		add(label);
	}
	
	public Color getColor() {
		return color;
	}
}
