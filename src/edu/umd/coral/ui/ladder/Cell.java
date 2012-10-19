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
package edu.umd.coral.ui.ladder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;

public class Cell extends JComponent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3335015641146449912L;
	
	private int cellWidth;
	
	private int cellHeight;
	
	private boolean drawBorder = false;
	
//	private Score<T> value;
	
//	private float maxValue = 1;

	private Color color;

	public Cell(int width, int height) {
		cellWidth = width;
		cellHeight = height;
		this.setPreferredSize(new Dimension(width, height));
		if (drawBorder)
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		// show tooltips immediately
		ToolTipManager.sharedInstance().setInitialDelay(0); 
	}
	
	/**
	 * Set preferred size
	 */
	public Dimension getPreferredSize() {
        return new Dimension(cellWidth,cellHeight);
    }
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color c) {
		color = c;
	}
	
	public boolean isDrawBorder() {
		return drawBorder;
	}

	public void setDrawBorder(boolean drawBorder) {
		this.drawBorder = drawBorder;
		
		if (drawBorder)
			setBorder(BorderFactory.createLineBorder(Color.GRAY));
		else
			setBorder(null);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Color c;
		if (color == null) {
			c = Color.GRAY;
		}
		else {
			c = color;
		}
		g.setColor(c);
		
		Dimension size = this.getSize();
		int x = (size.width - cellWidth) / 2;
		int y = (size.height - cellHeight) / 2;
		
		g.fillRect(
				x+1,
				y+1,
				cellWidth-1,
				cellHeight-1);
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
		repaint();
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
		repaint();
	}
}
