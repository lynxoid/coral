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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.umd.coral.model.data.Named;
import edu.umd.coral.model.data.Score;
import edu.umd.coral.ui.IDataRenderer;
import edu.umd.coral.ui.IHighlightable;
import edu.umd.coral.ui.ISelectable;


public class InteractiveCell<T, C extends Named> extends Cell implements 
			ISelectable, IHighlightable, IDataRenderer<T>,
			MouseListener, MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3335015641146449912L;

	private boolean selected = false;

	private boolean highlighted = false;

	private T value;
	
	private C left;
	
	private C right;

	private static final int DELAY = 0;

	public InteractiveCell(int width, int height) {
		super(width, height);
		this.setPreferredSize(new Dimension(width, height));

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * Set preferred size to 250 by 250 pixels
	 */
	public Dimension getPreferredSize() {
		return new Dimension(getCellWidth(), getCellHeight());
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (selected == this.selected)
			return;

		this.selected = selected;
		repaint();

		// fire change event
		this.firePropertyChange(ISelectable.CELL_SELECTED_CHANGED, !selected, selected);
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		if (highlighted == this.highlighted)
			return;

		this.highlighted = highlighted;
		repaint(DELAY);
	}

	/**
	 * Draws teh cell of specified width and height, paiting it in _color and
	 * w/ a black (blue if selected) border
	 */
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (! (g instanceof Graphics2D) )
			return;			
		
		Graphics2D g2 = (Graphics2D)g;

		if (getValue() == null)
			return;
		
		g2.clearRect(0, 0, getCellWidth(), getCellHeight());
		
		// color the cell
		Color c = getColor();
		if (getColor() == null)
			c = Color.RED;
		//c = GradientColorModel.getRGBColor(value.getValue(), 1, false, 210);
		g2.setColor(c);
		g.fillRect(
				1,
				1,
				getCellWidth()-1,
				getCellHeight()-1);

		// draw border in a different color if selected or highlighted
		if (highlighted) {
			g2.setColor(Color.YELLOW);
		}
		else if (selected) {
			g2.setColor(Color.RED);
		}
		g2.setStroke(new BasicStroke(4));
		
		if (selected || highlighted)
			g2.drawRect(
					0,
					0,
					getCellWidth(),
					getCellHeight());
		
		// check if label will fit
		String v = new DecimalFormat("#0.00").format(value);
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D bounds = fm.getStringBounds(v, g2);
		
		g2.setColor(Color.BLACK);
		if (getCellWidth() > bounds.getWidth()+2 && getCellHeight() > bounds.getHeight()+2)
			g.drawString(String.valueOf(v), 
					(int)(getCellWidth() -  bounds.getWidth() ) / 2, 
					(int)(getCellWidth() + bounds.getHeight()-2 ) / 2);
		
	}

	
	public void mouseDragged(MouseEvent e) {		
	}

	
	public void mouseMoved(MouseEvent e) {
		// highlight
	}

	
	public void mouseClicked(MouseEvent arg0) {
		setSelected(!selected);
	}

	
	public void mouseEntered(MouseEvent arg0) {
		setHighlighted(true);
	}

	
	public void mouseExited(MouseEvent arg0) {
		setHighlighted(false);
	}

	
	public void mousePressed(MouseEvent arg0) {
	}

	
	public void mouseReleased(MouseEvent arg0) {
	}

	
	public T getValue() {
		return value;
	}

	
	public void setValue(T v) {
		value = v;
	}

	public void setPair(C c1, C c2) {
		// TODO Auto-generated method stub
		left = c1;
		right = c2;
	}
	
	public C getLeft() {return left;}
	
	public C getRight() {return right;}

	public Score<C> getScore() {
		return new Score<C>(left, right, 0);
	}
}
