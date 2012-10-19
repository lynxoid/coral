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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.MatrixSelectionMode;
import edu.umd.coral.ui.JPanelExt;

public class SelectablePanel extends JPanelExt implements 
			MouseMotionListener, MouseListener, PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2322335918777051414L;

	/**
	 * When started dragging, record the point where mouse clicked
	 */
	protected Point zoomRegionStart;
	
	/**
	 * When finished dragging, record the point where ended dragging
	 */
	protected Point zoomRegionEnd;
	
	/**
	 * 
	 * Holds a transparent rectangle for current selection
	 * 
	 */
	protected BufferedImage selection;
	
//	private BufferedImage cross;
	
	protected BufferedImage vertSelection;
	
	protected String selectionMode = MatrixSelectionMode.SELECT_SINGLE_VERTEX;

	/**
	 * 
	 * Default constructor - set bg to black, adds listeners
	 * 
	 * @param model
	 */
	public SelectablePanel(DataModel model) {
		super(model);
		
		setBackground(Color.BLACK);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	protected AffineTransform getTranslate() {
		if (zoomRegionStart != null && zoomRegionEnd != null) {
			int minX = (int) Math.min(zoomRegionStart.getX(), zoomRegionEnd.getX());
			int minY = (int) Math.min(zoomRegionStart.getY(), zoomRegionEnd.getY());
			return AffineTransform.getTranslateInstance(
					minX, 
					minY);
		}
		
		return AffineTransform.getTranslateInstance(0, 0);
	}
	
	/**
	 * 
	 * Repaint
	 * 
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2D = (Graphics2D)g;
		
//		if (selectionMode == MatrixSelectionMode.SELECT_ZOOM_REGION && selection != null) {
//			AffineTransform translateSel = getTranslate();
//
//			g2D.drawImage(selection, translateSel, null);
//			System.out.println("drawing zoom");
//		}
//		else 
		if (selectionMode == MatrixSelectionMode.SELECT_MULT_VERTICES &&
				vertSelection != null) {
			g2D.drawImage(vertSelection, null, null);
			System.out.println("drawing mutl vert");
		}
		else if (selectionMode == MatrixSelectionMode.SELECT_SINGLE_VERTEX) {
			// TODO: draw selection?
			System.out.println("drawing single vert");
		}
	}
	
	
	/**
	private void drawVertSelection() {
		flushImage();

		System.out.println("drawing vertex selection " + zoomRegionStart + " " + zoomRegionEnd);
		
		if (this.zoomRegionStart == null || this.zoomRegionEnd == null) {
			return;
		}
		

		int width = (int)  Math.abs(zoomRegionStart.getX() - zoomRegionEnd.getX());
		
		if (width <= 0 ) {
			return;
		}
		
		vertSelection = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D imageGraphics = vertSelection.createGraphics();
		Color c = new Color(0x00, 0x9D, 0xFF);
		imageGraphics.setColor(c);
		imageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));
		imageGraphics.fillRect(zoomRegionStart.x, zoomRegionStart.y, width, 3);
		// TODO: show tooltip w/ info on selection
	}
	*/

	
    
	
	/********************************************************
	 * 
	 * An implementation of a MouseListener interface
	 * 
	 ********************************************************/

	
	public void mouseClicked(MouseEvent e) {
		System.out.println("mouse clicked");
		
		this.selectionMode = MatrixSelectionMode.SELECT_SINGLE_VERTEX;
		
		int clkCount = e.getClickCount();
		
		if (clkCount == 1) {
			// TODO: select vertex
			this.selectionMode = MatrixSelectionMode.SELECT_SINGLE_VERTEX;
		}
	}

	
	public void mouseEntered(MouseEvent e) {
		this.requestFocus();
		requestFocusInWindow();
	}

	
	public void mouseExited(MouseEvent e) {	
	}

	
	public void mousePressed(MouseEvent e) {
		System.out.println("mouse pressed");
		
		this.requestFocus();
		requestFocusInWindow();
		flushImage();
		
//		if (this.selectionMode == MatrixSelectionMode.SELECT_ZOOM_REGION) {
//			this.zoomRegionStart = e.getPoint();
//		}
//		else 
		//if (this.selectionMode == MatrixSelectionMode.SELECT_MULT_VERTICES) {
			// remember where to start drawing the line
			this.zoomRegionStart = e.getPoint();
		//}
		
		repaint();
	}

	
	public void mouseReleased(MouseEvent e) {
		System.out.println("mouse released");
		flushImage();
		
		if (this.selectionMode == MatrixSelectionMode.SELECT_MULT_VERTICES) {
			// draw a line
			this.zoomRegionEnd= e.getPoint();
			selectVertices(zoomRegionStart, zoomRegionEnd);
		}
		repaint();
	}

	// match vertices within the segment 
	private void selectVertices(Point zoomRegionStart2, Point zoomRegionEnd2) {
		// TODO calculate what vertices to select - pass it to the parent
		//flushImage();
	}
	
	private void flushImage() {
		this.selection = null;
		selection = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		vertSelection = null;
		vertSelection = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	//==========================================================================
	//
	// Implementation for PropertychangeListener
	//
	//==========================================================================
	public void propertyChange(PropertyChangeEvent ev) {
		this.selectionMode = _dataModel.getSelectionMode();
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Testing only
	//
	////////////////////////////////////////////////////////////////////////////
	
	 /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("SimpleTableDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        DataModel dm = new DataModel(frame);
        SelectablePanel newContentPane = new SelectablePanel(dm);
        newContentPane.setPreferredSize(new Dimension(400, 400));
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

	
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
