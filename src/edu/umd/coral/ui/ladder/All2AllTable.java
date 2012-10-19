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
 * 
 * @author dfilippo
 */

package edu.umd.coral.ui.ladder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.umd.coral.model.GradientColorModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.ComparisonScores;
import edu.umd.coral.model.data.Score;
import edu.umd.coral.ui.ISelectable;

public class All2AllTable extends JPanel implements PropertyChangeListener, ComponentListener {

	/**
	 * generated
	 */
	private static final long serialVersionUID = -2628664877354239849L;
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Private variables
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * ArrayList of items to display
	 */
	private ComparisonScores<Clustering> comparisonItems;
	
	/**
	 * Cell size in pixels
	 */
	private int cellSize = 20;
	
	/**
	 * X index of a selected cell
	 */
	private InteractiveCell<Float, Clustering> selectedCell = null;
	
	/**
	 * 
	 */
	private InteractiveCell<Float, Clustering> [] cells;

	private final float minValue = 0f;

	private float maxValue;
	
	private int minFontSize = 6;
	
	private int fontHeight = 0;
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor(s)
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * 
	 */
	public All2AllTable() {
		super();
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		// absolute positioning
        setLayout(null);
        // listen to resize events and size appropriately
        addComponentListener(this);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Methods
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Paints the component: the wireframe, selected and highlighted cells,
	 * labels
	 * 
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (comparisonItems == null) {
			g.drawString("Load data", 10, 20);
			return;
		}
		// draw labels only if they will be readable
		if (cellSize - 1 >= minFontSize) {
			// draw labels
			g.setColor(Color.BLACK);
			int i;
			String item;
			
			int labelX = 3;	
			int labelY = fontHeight - 3;			
			// draw the string for the first cell separately
//			item = comparisonItems.getXItem(0).getName();
//			g.drawString(item, cellSize/2, fontHeight-2);
			for (i = 0; i < comparisonItems.getSize(); i++) {
				item = comparisonItems.getXItem(i).getName();
				g.drawString(item, labelX+2, labelY-2);
				labelX += cellSize;
				labelY += cellSize;
			}
		}
	}

	private void updateCells() {
		removeCells();
		addCells(comparisonItems);
	}
	
	@SuppressWarnings("unchecked")
	private void addCells(ComparisonScores<Clustering> comparisonItems) {
		if (comparisonItems == null)
			return;
		
		int size = comparisonItems.getSize();
		cells = new InteractiveCell[size * (size - 1) / 2 ];
		
		InteractiveCell<Float, Clustering> cell;
		Color cellColor;
		float diff = maxValue - minValue;
		if (diff == 0)
			diff = 1;
		
//		System.out.println("adding cells: " + comparisonItems.toString());
//		System.out.println("cell size set to " + cellSize);
		
		if (comparisonItems.getSize() == 0) {
			//System.out.println("did not update cells");
			return;
		}
		
		int k = 0, index1, index2;
		String l1, l2;
		for (int i = 0; i < size; i++) {
			l1 = comparisonItems.getXItem(i).getName();
			index1 = comparisonItems.getIndex(l1);
			
			for (int j = i + 1; j < size ; j++) {
				l2 = comparisonItems.getXItem(j).getName();
				index2 = comparisonItems.getIndex(l2);
//				System.out.println("comparison items for " + labels[i] + "," + labels[j] + index1 + " " + index2);
				float value = comparisonItems.getValue(index1, index2);
				cell = new InteractiveCell<Float, Clustering>(cellSize, cellSize);
				
				cellColor = GradientColorModel.getLadderColor((value-minValue)/diff, 1, false); 
//				cellColor = GradientColorModel.getRGBColor( (value - minValue) / diff, 1, false, 210);
				cell.setValue(value);
				cell.setColor(cellColor);
				cell.setToolTipText(l1 + ", " + l2 + ": " + String.valueOf(value) );
				cell.setPair(comparisonItems.getXItem(index1), comparisonItems.getXItem(index2) );
				
				cell.setBounds(
						0 + i * cellSize, 
						0 + (j - 1) * cellSize + fontHeight, 
						cellSize, 
						cellSize);
				
				cell.addPropertyChangeListener(ISelectable.CELL_SELECTED_CHANGED, this);
				// add to the DisplayList
				add(cell);
				
				cells[k++] = cell;
			}
		}
	}

	private void removeCells() {
		if (cells != null) {
			for (InteractiveCell<Float, Clustering> c : cells) {
				remove(c);	// remove each cell from display list
				c = null;
			}
		}
		cells = null;
	}
	
	public void setComparisonItems(ComparisonScores<Clustering> clusteringScores) {
		this.comparisonItems = clusteringScores;
		
		if (comparisonItems == null) {
			maxValue = 0;
			updateCells();
			return;
		}
		this.maxValue = comparisonItems.getMax();
		this.updateCellSize(this);
		updateCells();
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// For testing
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
		
		All2AllTable panel = new All2AllTable();
		panel.setPreferredSize(new Dimension(200, 200));
		
		String [] items = new String[8];
		items[0] = "c1";
		items[1] = "c2";
		items[2] = "c3";
		items[3] = "c4";
		items[4] = "c5";
		items[5] = "c6";
		items[6] = "c7";
		items[7] = "c8";
		
//		panel.setLabels(items);
		
		f.add(panel);
        f.pack();
		f.setVisible(true);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Event handling
	//
	////////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		
		if (name.equals(ISelectable.CELL_SELECTED_CHANGED)) {
			boolean newSelectedValue = (Boolean) evt.getNewValue();
			Score<Clustering> oldValue;
			Score<Clustering> newValue;
			
			// deselect previous cell
			if (selectedCell != null) {
				if (newSelectedValue) {
					// selected something - need to deselect old cell
					oldValue = selectedCell.getScore();
					selectedCell.setSelected(false);
					// update selectedCell to the new cell
					selectedCell = (InteractiveCell<Float, Clustering>) evt.getSource();
					newValue = selectedCell.getScore();
				}
				else {
					// deselected something
					oldValue = selectedCell.getScore();
					newValue = null;
					selectedCell = null;
				}
			}
			else {
				// update selectedCell to the new cell
				oldValue = null;
				selectedCell = (InteractiveCell<Float, Clustering>) evt.getSource();
				newValue = selectedCell.getScore();
			}
			firePropertyChange(ISelectable.CELL_SELECTED_CHANGED, oldValue, newValue);
		}
		repaint();
	}
	

	private void updateCellSize(Component c) {
//		System.out.println("Updating cell size");
		Dimension d = c.getSize();
//		System.out.println(d);
		if (comparisonItems == null)
			return;
		
		Graphics g = this.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds = fm.getStringBounds("laA1234567890", g);
		fontHeight = (int) Math.floor(bounds.getHeight());
		
		int cellCount = comparisonItems.getSize();
		int h = d.height;
		
		if (cellCount == 1)
			cellSize = h - fontHeight;
		else
			cellSize = (int)Math.floor( (h - fontHeight) / (cellCount - 1) );
//		System.out.println(cellSize);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Implementation of the ComponentLIstener interface
	//
	////////////////////////////////////////////////////////////////////////////
	
	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	
	public void componentResized(ComponentEvent e) {
		updateCellSize( (Component)e.getSource());
		// update cell size with each cell
		updateCells();
		repaint();
	}


	public void componentShown(ComponentEvent arg0) {
	}
}
