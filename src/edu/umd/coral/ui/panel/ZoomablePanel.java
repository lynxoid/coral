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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Displays clusterings and correlations between modules in different 
 * clusterings.
 * 
 * Vertex order should be the same as in the reordered co-occurrence matrix - 
 * this way crossover between bands would be minimized
 * 
 * @author lynxoid
 *
 */
public class ZoomablePanel extends JPanel implements MouseWheelListener, 
		Scrollable, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = -747286996950904094L;
	
	protected float scale = 1.0f, translateX = 0, translateY = 0;
	
	protected final float MAX_SCALE = 15, MIN_SCALE = 0.2f, INCR = 0.2f;
	
	protected int width = 100, height = 100, maxUnitIncrement = 10;
	
	protected int pixelsPerVertex = 1;
	
	// image of the clustering bands
//	private BufferedImage image;

	public ZoomablePanel() {
		setLayout(null);
//		this.setLayout(new BorderLayout());
		setMinimumSize(new Dimension((width/2), (height/2)));
		setPreferredSize(new Dimension(width, height));
		
		// add listeners
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		// create a test image
		/*
		image = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.drawLine(0, 0, 1000, 1000);
		g.setColor(Color.YELLOW);
		g.drawLine(0, 100, 1000, 100);
		g.setColor(Color.RED);
		g.drawLine(900, 0, 900, 1000);
		g.setColor(Color.BLUE);
		g.drawLine(0, 900, 1000, 900);
		*/
	}
	
	/*
	public Dimension getPreferredSize() {
		// update width and height
		Dimension d = super.getPreferredSize();
		updateMatrixSize();

//		if (width < d.width && height < d.height)
//			return d;
//		else {
			int w = Math.max(width, d.width);
			int h = Math.max(height, d.height);
			return new Dimension(w, h);
//		}
	}
	*/
	
	/********************************************************
	 * 
	 * Update max size
	 * @return 
	 * 
	 ********************************************************/
	protected Dimension updateMatrixSize() {
		// TODO: width is ~ longest clustering band
		int columns = 1000;
		int actualSize = (int) Math.ceil(columns * scale * pixelsPerVertex);
		
		// if does not fit into current size - increase size
//		if (actualSize > d.width || actualSize > d.height) {
			width = actualSize;
			height = actualSize;
		return new Dimension(width, height);
//		}
//		else  {
//			width = d.width;
//			height = d.height;
//		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (g instanceof Graphics2D) {
			if (getWidth() == 0 || getHeight() == 0)
				return;
			
			AffineTransform translateAT = AffineTransform.getTranslateInstance(translateX, translateY);
			AffineTransform scaleAT = AffineTransform.getScaleInstance(scale, scale);
			scaleAT.concatenate(translateAT);

//			g2D.drawImage(image, scaleAT, null);
		}
	}

	//--------------------------------------------------------------------------
	//
	// Mouse listener
	//
	//--------------------------------------------------------------------------
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

	
	//--------------------------------------------------------------------------
	//
	// MouseMotion listener
	//
	//--------------------------------------------------------------------------
	public void mouseDragged(MouseEvent arg0) {
		
	}

	public void mouseMoved(MouseEvent arg0) {
		
	}
	
	//--------------------------------------------------------------------------
	//
	// Scrollable listener
	//
	//--------------------------------------------------------------------------

	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - maxUnitIncrement;
		} else {
			return visibleRect.height - maxUnitIncrement;
		}
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return 10;
	}

	
	//--------------------------------------------------------------------------
	//
	// MouseWheel listener
	//
	//--------------------------------------------------------------------------
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		Dimension size = getParent().getSize();

		if (notches > 0)
			scale -= INCR;
		else
			scale += INCR;
		
		if (scale < MIN_SCALE)
			scale = MIN_SCALE;
		else if (scale > MAX_SCALE)
			scale = MAX_SCALE;
		
		updateMatrixSize();
		size = new Dimension(width, height);
		setPreferredSize(size);
		revalidate();
	}
	
}
