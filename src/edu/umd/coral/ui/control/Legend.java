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
 * Continuous Legend - a color gradient w/ two labels for min and max values
 * 
 * @author dfilippo
 */
package edu.umd.coral.ui.control;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Legend extends JComponent {
	
	/**
	 * ?
	 */
	public static final int DELAY = 5;
	
	/**
	 * geenrated ID
	 */
	private static final long serialVersionUID = -2439066653684227000L;
	
	/**
	 * Gap between legend items
	 */
	private int gap = 0;
	
	/**
	 * all legend items
	 */
	private LegendItem [] legendItems;
	
	private Dimension preferredSize = new Dimension();

	public Legend() {
		this.setOpaque(true);
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	/**
	 * 
	 * @param items
	 */
	public void setLegendItems(LegendItem [] items) {
		if (items == null) {
			this.removeAll();
			legendItems = null;
			repaint();
			return;
		}
		
		legendItems = items;
		
		// position components
		Dimension size;
		int w = 0, h = 0;
		int x = 0;
		for (LegendItem li : legendItems) {
			size = li.getPreferredSize();
			li.setBounds(
					x, 
					0, 
					size.width, 
					size.height);
			add(li);
			x += size.width + gap;
			
			w += size.width + gap;
			h = li.getHeight();
		}
		
		w -= gap;
		this.preferredSize.width = w;
		this.preferredSize.height = h;
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return preferredSize; 
	}
	
	//////////////////////////////////////////////////////////////////////////
	//
	// testing
	//
	////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame f = new JFrame("Swing Paint Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Legend legend = new Legend();
		LegendItem [] li = new LegendItem[5];
		li[0] = new LegendItem(Color.BLACK, "0.0");
		li[1] = new LegendItem(Color.GRAY, "1.0");
		li[2] = new LegendItem(Color.BLUE, "2.0");
		li[3] = new LegendItem(Color.GREEN, "3.0");
		li[4] = new LegendItem(Color.YELLOW, "4.0");
		
		legend.setLegendItems(li);
		
		f.add(legend);
        f.pack();
		f.setVisible(true);
	}
}
