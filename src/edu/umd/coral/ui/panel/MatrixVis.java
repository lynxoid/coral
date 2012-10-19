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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import no.uib.cipr.matrix.MatrixEntry;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.GradientColorModel;
import edu.umd.coral.model.MatrixColorModel;
import edu.umd.coral.model.data.BaseMatrix;
import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Cooccurrence;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.ui.JPanelExt;

/**
 * Matrix Panel: draw a matrix, handle matrix item selection
 * 
 * @author darya, aashish
 *
 */
public class MatrixVis extends JPanelExt implements PropertyChangeListener, 
		MouseWheelListener, Scrollable,
		MouseMotionListener, MouseListener {

	private static final long serialVersionUID = -5287455681570860214L;

	// holds the drawing of a matrix
	private BufferedImage image, lines, cliques;
	
	// draw a line when selecting multiple vertices
	private BufferedImage vertSelection;

	private int translateX = 0;
	
	private int translateY = 0;

	private int width = 10;
	
	private int height = 10;
	
	private int actualSize = 0;
	
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
	 * Define the {@link #pixelsPerVertex} to use per matrix cell
	 * 
	 */
	private final int pixelsPerVertex = 1;
	
	private int maxUnitIncrement = 1;
	
	private JPopupMenu popup;

//	protected String selectionMode = MatrixSelectionMode.SELECT_SINGLE_VERTEX;

	// for drawing user selection as they drag the mouse
	private int xStartClick;
	private int yStartClick;
	private int xEndClick;
	private int yEndClick;
	
	private ArrayList<Interval> selectionIndices = new ArrayList<Interval>();
	
	/**
	 * 
	 * @author lynxoid
	 *
	 */
	private class Interval {
		public int xStart;
		public int yStart;
		public int xEnd;
		public int yEnd;
		
		// an interval on the diagonal
		public Interval(int x, int y) {
			xStart = x;
			yStart = x;
			xEnd = y;
			yEnd = y;
		}
		
		// interval off diagonal
		public Interval(int xStart, int yStart, int xEnd, int yEnd) {
			this.xStart = xStart;
			this.yStart = yStart;
			this.xEnd = xEnd;
			this.yEnd = yEnd;
		}

		public String toString() {
			return "(" + xStart + "," + yStart  + ") - (" + xEnd + "," + yEnd + ")";
		}
	}

//	private Matrix localMatrix;

	/**
	 * Default constructor
	 * 
	 * @param model
	 */
	public MatrixVis(DataModel model) {
		super(model);

		this.setLayout(new BorderLayout());

		// show tooltips instantly
		ToolTipManager.sharedInstance().setInitialDelay(0);

		requestFocusInWindow(true);
		setFocusable(true);
		requestFocus();

		setMinimumSize(new Dimension((width/2), (height/2)));
		setPreferredSize(new Dimension(width, height));

		if (model != null) {
			// set a BG
			setBackground(model.getBackgroundColor());

			model.addPropertyChangeListener(DataModel.MATRIX_CHANGED, this);
			model.addPropertyChangeListener(DataModel.CLIQUES_MATRIX_CHANGED, this);
			model.addPropertyChangeListener(DataModel.ZOOM_CHANGED, this);
			model.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
			model.addPropertyChangeListener(DataModel.HIGHLIGHT_CLIQUES_CHANGED, this);
			model.addPropertyChangeListener(DataModel.OVERLAY_CLIQUES_CHANGED, this);
			model.addPropertyChangeListener(DataModel.SEARCH_ITEM_CHANGED, this);
		}

		// set a black border
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		
		// add mouse listeners
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
	}
	

	/**
	 * Overrides paintComponent method. Goes through the DataModel::currentMatrix
	 * row by row, column by column and uses MatrixColorModel to color each item
	 */
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (g instanceof Graphics2D && image != null) {
			Graphics2D g2D = (Graphics2D)g;
			float scale = _dataModel.getZoomValue();
			int destWidth = getWidth();
			int destHeight = getHeight();

			if (destWidth == 0 || destHeight == 0)
				return;
			
			AffineTransform translateAT = AffineTransform.getTranslateInstance(translateX, translateY);
			AffineTransform scaleAT = AffineTransform.getScaleInstance(scale, scale);
			scaleAT.concatenate(translateAT);

			g2D.drawImage(image, scaleAT, null);
			
			if (_dataModel.getHighlightCliques() && cliques != null) {
				g2D.drawImage(cliques, scaleAT, null);
			}

			if ( (selectionIndices != null || _dataModel.getSearchItem() != null ) 
					&& lines != null){
				g2D.drawImage(lines, scaleAT, null);
			}
			
			g2D.drawImage(vertSelection, scaleAT, null);
		}
	}
	
	/**
	 * As user drags the mouse for multi-selection, draw the rectangle 
	 * representing the selection
	 */
	private void drawVertSelection() {
		if (xStartClick < 0 || xEndClick < 0 || yStartClick < 0 || yEndClick < 0)
			return;
		
		int width = (int)  Math.abs(xStartClick - xEndClick);
		
		if (width <= 0 ) {
			return;
		}
		int minX = Math.min(xStartClick, xEndClick);
		
		vertSelection = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D imageGraphics = vertSelection.createGraphics();
		Color c = new Color(0x00, 0x9D, 0xFF);
		imageGraphics.setColor(c);
		imageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));
		imageGraphics.fillRect(minX, yStartClick, width, 1);
		// TODO: show tooltip w/ info on selection
	}
	
	private void flushImage() {
		vertSelection = null;
		vertSelection = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * Draw selection lines for a selected vertex pair
	 */
	private void drawLines() {
		Matrix localMatrix = _dataModel.getCurrentMatrix();
		if (localMatrix != null && selectionIndices != null && selectionIndices.size() > 0) {
			int size = localMatrix.getRowCount();
			lines = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = lines.createGraphics();
			
			g.setColor(Color.CYAN);
			g.setComposite(AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, 0.7f));
			
			for (Interval inter : selectionIndices) {
				// top
				int w = inter.xEnd - inter.xStart;
				int h = inter.yEnd - inter.yStart;
				g.fillRect(inter.xStart, 0, w, inter.yStart);
				// bottom
				g.fillRect(inter.xStart, inter.yEnd, w, size-inter.yEnd);
				// left
				g.fillRect(0, inter.yStart, inter.xStart, h);
				// right
				g.fillRect(inter.xEnd, inter.yStart, size - inter.xEnd, h);
			}
		} else {
			// remove lines
			if (lines != null) lines.flush();
		}
	}
	
	private void drawCliques() {
		Matrix localMatrix = _dataModel.getCurrentMatrix();
		if (localMatrix == null) return;
		// draw cliques
		int size = localMatrix.getRowCount();// * pixelsPerVertex;
		
		cliques = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D cliqueGraphics = cliques.createGraphics();
		cliqueGraphics.setBackground(_dataModel.getBackgroundColor());
//		cliqueGraphics.setColor(new Color(255, 150, 64));	// yellow
		cliqueGraphics.setColor(Color.GREEN);
//		cliqueGraphics.setColor(new Color(152, 237, 0)); 	// lighter green
		// TODO: make non transparent?
		cliqueGraphics.setComposite(AlphaComposite.getInstance(
				AlphaComposite.SRC_OVER, 0.7f));
		
		Collection<Clique> cliques = _dataModel.getCliques();
		if (cliques == null)
			return;
		
		int beginCliqInd = -1;
		int c;
		int in = 0;
		int out = 0, delta;
		float side;
		int ind;
		
		for (Clique clique : cliques) {
			beginCliqInd = size + 1;
			c = clique.size();
			for (Vertex v : clique.getVertices() ) {
				ind = localMatrix.getColumnIndex(v.getName());
				if (ind < beginCliqInd)
					beginCliqInd = ind;
			}
			// overlay a square
			cliqueGraphics.setColor(Color.GREEN);
			cliqueGraphics.fillRect(beginCliqInd, beginCliqInd, c, c);
			
			// given network information, calculate % links inside and % links outside
			// overlay on top of clique
			if (_dataModel.hasEdges() && _dataModel.getOverlayCliques()) {
				in = clique.getInEdges();
				out = clique.getOutEdges();
				
				/**
				 *  cliq_size ^ 2 == in + out
				 *  x^2 / cliq_size^2 == in / (in + out)
				 *  area = in / (in + out) * sizeSq;
				 */
				if (in != 0 && (in + out) != 0) {
					side = Math.round(c * in / (in + out));
					if (side == c) side = c - 1;
					if (side == 0) side = 1;
					delta = Math.round((c - side) / 2);
					cliqueGraphics.setColor(Color.BLUE);
					cliqueGraphics.fillRect(beginCliqInd + delta, beginCliqInd + delta, (int)side, (int)side);
				}
			}
		}
	}
	
	/**
	 * Draws matrix pixel by pixel using MatrixColorModel to calculate colors
	 */
	private void drawMatrix() {
		Matrix localMatrix = _dataModel.getCurrentMatrix();
		
		if (localMatrix != null) {
//			System.out.println("drawMatrix");
//			long before = System.currentTimeMillis();
			
			int rowCount = localMatrix.getRowCount();
			int size = localMatrix.getRowCount();// * pixelsPerVertex;
			int i, j;
			double value;

			image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D imageGraphics = image.createGraphics();
			imageGraphics.setBackground(_dataModel.getBackgroundColor());
			
			
			float cutoff = _dataModel.getLowerBound();
			Color c, ca;
			
			if (localMatrix instanceof BaseMatrix) {
				BaseMatrix bm = (BaseMatrix)localMatrix;
				Iterator<MatrixEntry> mi = bm.iterator();
				
				while (mi.hasNext()) {
					MatrixEntry e = mi.next();
					value = e.get();
					if (value == 0) continue;
					c = GradientColorModel.getMatrixColor( (float)value, (float)localMatrix.getMax(), false);
					
					if (bm.isBase(e.row(), e.column()))
						imageGraphics.setColor(c);
					else {
//						the cell is outside the base clustering, make it dark / gray
//						float [] values = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
						//ca = Color.getHSBColor(values[0], values[1] * 0.5f, values[2] * 0.5f);
						ca = new Color(128, 128, 128);
						imageGraphics.setColor(ca);
					}
					
					imageGraphics.fillRect(e.row(), e.column(), pixelsPerVertex, pixelsPerVertex);
				}
				/*
				float maxValue = (float) localMatrix.getMax();
				for (i = 0; i < rowCount; i++) {
					for (j = 0; j < rowCount; j++) {
						value = bm.getElement(i, j);
						
//						c = MatrixColorModel.getRGBColor(value, localMatrix.getMax(), false);
						c = GradientColorModel.getMatrixColor( (float) value, maxValue, false);
						
						if (bm.isBase(i, j))
							imageGraphics.setColor(c);
						else {
							float [] values = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
							ca = Color.getHSBColor(values[0], values[1] * 0.1f, values[2] * 0.7f);
							imageGraphics.setColor(ca);
						}
						
						imageGraphics.fillRect(i, j, pixelsPerVertex, pixelsPerVertex);
					}
				}
				*/
			}
			else {
				float maxValue = (float) localMatrix.getMax();
				for (i = 0; i < rowCount; i++) {
					for (j = 0; j < rowCount; j++) {
						value = localMatrix.getElement(i, j);
						
						if (value == 0) continue;
						
						if (value >= cutoff)
							imageGraphics.setColor(GradientColorModel.getMatrixColor( (float) value, maxValue, false));
						else
							// gray scale
							imageGraphics.setColor(MatrixColorModel.getGrayScaleColor(value, localMatrix.getMax()) );
						
						imageGraphics.fillRect(i, j, pixelsPerVertex, pixelsPerVertex);
					}
				}
			}
			
//			long after = System.currentTimeMillis();
//			System.out.println("Time to draw: " + (after - before));
			//image2 = new BufferedImage
		}
		else {
			// erase
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
			
			if (cliques != null)
				cliques.flush();//new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}
	}
	
	/********************************************************
	 * 
	 * Update max size
	 * 
	 ********************************************************/
	private void updateMatrixSize() {
		Dimension d = getParent().getPreferredSize();
		Matrix m = _dataModel.getCurrentMatrix();
		if (m == null)
			return;
		
		int columns = m.getColumnCount();
		float zoomValue = _dataModel.getZoomValue();
		actualSize = (int) Math.ceil(columns * zoomValue * pixelsPerVertex);
		
		// if does not fit into current size - increase size
		if (actualSize > d.width || actualSize > d.height) {
			width = actualSize;
			height = actualSize;
		}
		else  {
			width = d.width;
			height = d.height;
		}
	}
	
	/**
	 * @private
	 * 
	 * shows matrix popup menu- reorder matrix, properties (color scheme)
	 */
	private void showPopupMenu(MouseEvent e) {
	    if (popup == null)
	    	popup = new MatrixPopupMenu(_dataModel);
	    
	    popup.show(e.getComponent(), e.getX(), e.getY());
	}


	//--------------------------------------------------------------------------
	//
	// An implementation of a PropertyChangeListener interface
	// 
	//--------------------------------------------------------------------------
	public void propertyChange(PropertyChangeEvent evt) {
		String name = evt.getPropertyName();
		
		if (name.equals(DataModel.ZOOM_CHANGED)) {
			repaint();
		}
		else if (name.equals(DataModel.HIGHLIGHT_CLIQUES_CHANGED) || name.equals(DataModel.OVERLAY_CLIQUES_CHANGED)) {
			if (!_dataModel.getHighlightCliques())
				cliques = null;
			else
				drawCliques();
			repaint();
		}
		else if (name.equals(DataModel.MATRIX_CHANGED)){
			// update the image
			this.cliques = null;
			this.lines = null;
			this._dataModel.setHighlightCliques(false);
			this._dataModel.setOverlayCliques(false);
			drawMatrix();
			repaint();
		}
		else if (name.equals(DataModel.SEARCH_ITEM_CHANGED)) {
			// highlight a single node on the diagonal
			Vertex u = _dataModel.getSearchItem();
			Matrix m = _dataModel.getCurrentMatrix();
			if (m == null || u == null) {
				lines = null;
				repaint();
				return;
			}
			int i = m.getColumnIndex(u.getName());
			
			selectionIndices.clear();
			selectionIndices.add(new Interval(i, i+1));
			
			drawLines();
			repaint();
		}
		else if (name.equals(DataModel.SELECTED_VERTICES_CHANGED)){
			ArrayList<Vertex> collection = _dataModel.getSelectedVertices();
			Matrix m = _dataModel.getCurrentMatrix();
			selectionIndices.clear();
			if (m == null || collection == null) {
				lines = null;
				repaint();
				return;
			}
			Interval inter;
			if (collection.size() == 1) {
				Vertex u = collection.get(0);
				int x = m.getRowIndex(u.getName());
				selectionIndices.add(new Interval(x, x+1));
			} 
			else if (collection.size() == 2) {
				Vertex u = collection.get(0);
				Vertex v = collection.get(1);
				int x = m.getColumnIndex(u.getName());
				int y = m.getRowIndex(v.getName());
				inter = new Interval(x, y, x+1, y+1);
				println("Selecting a new interval: " + inter);
				selectionIndices.add(inter);
			}
			else {// possibly multiple intervals
				int i = 0, j;
				int [] rows = new int[collection.size()];
				for (Vertex v : collection) {
					j = m.getRowIndex(v.getName());
					rows[i] = j; i++;
				}
				Arrays.sort(rows); // print
//				System.out.print("Indices: ");
//				for (Integer r : rows) {
//					System.out.print(r + " ");
//				}
//				System.out.println();
				i = 0; j = 0;
				
				while (j < rows.length-1) {
					if (rows[j] + 1 == rows[j+1]) j++;
					else {
						inter = new Interval(rows[i], rows[j]);
						selectionIndices.add(inter);
//						System.out.println("added interval " + inter);
						i = j+1;
						j = i;
					}
				}
				// add the last interval
				inter = new Interval(rows[i], rows[j]);
				selectionIndices.add(inter);
//				System.out.println("added interval " + inter);
			}
			drawLines();
			repaint(5);
		}
		else if (name.equals(DataModel.BOUND_CHANGED)){
			// filter matrix
		}
		else if (name.equals(DataModel.CLIQUES_MATRIX_CHANGED)) {
			drawCliques();
			repaint(5);
		}
	}

	/********************************************************
	 * 
	 * Override - custom size
	 * 
	 ********************************************************/
	/**
	 * take up as much space as given by the parent
	 */
	
	public Dimension getPreferredSize() {
		Dimension d = getParent().getPreferredSize();

		// update width and height
		updateMatrixSize();

		if (width < d.width && height < d.height)
			return d;
		else {
			int w = Math.max(width, d.width);
			int h = Math.max(height, d.height);
			return new Dimension(w, h);
		}
	}

	/********************************************************
	 * 
	 * An implementation of a MouseWheelListener interface
	 * 
	 ********************************************************/
	// mouse wheel
	
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		float zoomValue = _dataModel.getZoomValue();
		Dimension size = getParent().getSize();

		if (notches > 0)
			zoomValue -= 0.2;
		else
			zoomValue += 0.2;
		
		updateMatrixSize();
		_dataModel.setZoomValue(zoomValue);
		size = new Dimension(width, height);
		setPreferredSize(size);
		revalidate();
	}

	/********************************************************
	 * 
	 * An implementation of a scrollable client interface
	 * 
	 ********************************************************/
	public Dimension getPreferredScrollableViewportSize() {
		return getParent().getSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - maxUnitIncrement;
		} else {
			return visibleRect.height - maxUnitIncrement;
		}
	}

	// enable vertical scrolling by returning false 
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	// enable horizontal scrolling by returning false
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public int getScrollableUnitIncrement(Rectangle e, int arg1, int arg2) {
		return 10;
	}
	
	/********************************************************
	 * 
	 * An override of a MouseMotionlListener interface
	 * 
	 ********************************************************/
	public void mouseDragged(MouseEvent e) {
		//System.out.println("mouse dragging " + e.getPoint());
		this.zoomRegionEnd = e.getPoint();
		float scale = _dataModel.getZoomValue();
		float matrixX = zoomRegionEnd.x / scale;
		float matrixY = zoomRegionEnd.y / scale;
		xEndClick = (int) Math.floor(matrixX);
		yEndClick = (int) Math.floor(matrixY);
		
		Matrix localMatrix = _dataModel.getCurrentMatrix();
		
		if (localMatrix != null) {
			int matrixSize = localMatrix.getColumnCount();
		
			if (xEndClick > matrixSize) {
				xEndClick = matrixSize;
			}
		}
		drawVertSelection();
		repaint();
	}
	
	//--------------------------------------------------------------------------
	// 
	// MouseListener interface
	// 
	//--------------------------------------------------------------------------
	/**
	 * On mouse click, finds the matrix item under the mouse and get the vertex
	 * pair corresponding to that item; sets DataModel::selectedVertexPair to 
	 * be that pair.
	 */
	public void mouseClicked(MouseEvent e) {
		Matrix localMatrix = _dataModel.getCurrentMatrix();
		
		// check what mouse button was clicked
		int modifiers = e.getModifiers();
		int rightBtn = (modifiers & InputEvent.BUTTON3_MASK);
		int leftBtn = (modifiers & InputEvent.BUTTON1_MASK);
		
		if (rightBtn == InputEvent.BUTTON3_MASK) {
			showPopupMenu(e);
		}
		else if (leftBtn == InputEvent.BUTTON1_MASK) {
			this.requestFocus();
			requestFocusInWindow();
			
			// tell if it is over the image
			int x = e.getX();
			int y = e.getY();

			float scale = _dataModel.getZoomValue();
			float matrixX = x / scale;
			float matrixY = y / scale;
			selectionIndices.clear();
			if (localMatrix == null) return;

			if (matrixX > localMatrix.getColumnCount() ||
					matrixY > localMatrix.getRowCount()) {
				matrixX = -1;
				matrixY = -1;
				_dataModel.setSelectedVertices(null);
				return;
			}

			if (matrixX < localMatrix.getColumnCount() &&
					matrixY < localMatrix.getRowCount()) {
				selectionIndices.add(new Interval((int)matrixX, (int)matrixY, (int)matrixX+1, (int)matrixY+1));
				String nameU = localMatrix.getColumnName((int) matrixX);
				String nameV = localMatrix.getRowName((int) matrixY);

				Map<String, Vertex> map = _dataModel.getVertices();
				Vertex u = map.get(nameU);
				Vertex v = map.get(nameV);
				ArrayList<Vertex> collection = new ArrayList<Vertex>();
				collection.add(u);
				collection.add(v);

				drawLines();
				repaint();
				_dataModel.removePropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
				_dataModel.setSelectedVertices(collection);
				_dataModel.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
			}
			else {
				_dataModel.setSelectedVertices(null);
			}
		}
		else {
			_dataModel.setSelectedVertices(null);
		}
	}
	
	//--------------------------------------------------------------------------
	//
	// MouseListener interface
	// 
	//--------------------------------------------------------------------------
	public void mouseMoved(MouseEvent e) {
//		System.out.println("mouseMoved");
		Matrix m = _dataModel.getCurrentMatrix();
		int x = e.getX();
		int y = e.getY();
		float scale = _dataModel.getZoomValue();
		float matrixX = x / scale;
		float matrixY = y / scale;

		if (m == null) return;

		if (matrixX < m.getColumnCount() &&
				matrixY < m.getRowCount()) {
			String nameU = m.getColumnName( (int)Math.floor(matrixX) );
			String nameV = m.getRowName( (int) Math.floor(matrixY) );
						
			Map<Vertex, TreeMap<Vertex, Cooccurrence>> cooccurMap = _dataModel.getVertexPairs();
			
			if (cooccurMap != null) {
				Map<String, Vertex> map = _dataModel.getVertices();
				Vertex u = map.get(nameU);
				Vertex v = map.get(nameV);
				
				Map<Vertex, Cooccurrence> cooccurred = cooccurMap.get(u);
				if (cooccurred == null)
					setToolTipText(nameU + "  and  " + nameV + ": " + m.getElement( (int) matrixX, (int) matrixY) );
				else {
					Cooccurrence c = cooccurred.get(v);
					
					if (c != null)
						setToolTipText(c.getNiceHTMLString());
					else
						setToolTipText(null);
				}
			}
			else
				setToolTipText(null);
		}
		else {
			setToolTipText(null);
		}
	}

	public void mouseEntered(MouseEvent e) {
		this.requestFocus();
		requestFocusInWindow();
	}

	public void mouseExited(MouseEvent e) {
	}
	
	// record where the dragging started
	public void mousePressed(MouseEvent e) {
		this.requestFocus();
		requestFocusInWindow();
		flushImage();
		this.zoomRegionStart = e.getPoint();
		
		float scale = _dataModel.getZoomValue();
		float matrixX = zoomRegionStart.x / scale;
		float matrixY = zoomRegionStart.y / scale;
		xStartClick = (int) Math.floor(matrixX);
		yStartClick = (int) Math.floor(matrixY);
//		System.out.println("mouse pressed " + xStartClick + " " + yStartClick);
		Matrix m = _dataModel.getCurrentMatrix();
		if (m == null) {
			xStartClick = yStartClick = xEndClick = yEndClick = -1;
			return;
		}
		// do not go outside matrix
		if (matrixX >= m.getColumnCount() ) {
			xStartClick = m.getColumnCount() - 1;
		}
		if (matrixY >= m.getRowCount()) {
			yStartClick = m.getRowCount()-1;
		}
	}
	
	// record where the dragging stopped
	public void mouseReleased(MouseEvent e) {
		flushImage();
		this.zoomRegionEnd= e.getPoint();
		float scale = _dataModel.getZoomValue();
		float matrixX = zoomRegionEnd.x / scale;
//		float matrixY = zoomRegionEnd.y / scale;
		xEndClick = yEndClick = (int) Math.floor(matrixX);
//		yEndClick = (int) Math.floor(matrixY);
		this.selectionIndices.clear();
		
		Matrix matrix = _dataModel.getCurrentMatrix();
		if ( xStartClick < 0 || xStartClick >= matrix.getColumnCount() )  
			return;
		if (xEndClick < 0)
			xEndClick = 0;
		if (xEndClick >= matrix.getColumnCount())
			xEndClick = matrix.getColumnCount() - 1;
		selectionIndices.add(new Interval(xStartClick, xEndClick));
		drawLines();
		selectVertices(xStartClick, xEndClick);
		repaint();
	}
	
	/** 
	 * Match vertices within the segment; calculate what vertices to select, 
	 * pass it to the parent
	 * 
	 * @param x1
	 * @param x2
	 */
	private void selectVertices(int x1, int x2) {
		Matrix localMatrix = _dataModel.getCurrentMatrix();
		Map<String, Vertex> map = _dataModel.getVertices();
		ArrayList<Vertex> selected = new ArrayList<Vertex>();
		
		int minX = Math.min(x1, x2);
		int maxX = Math.max(x1, x2);
		
		if (minX > localMatrix.getColumnCount())
			return;
		if (maxX >= localMatrix.getColumnCount() )
			maxX = localMatrix.getColumnCount() - 1;
		
		String vertexName;
		Vertex v;
		for (int i = minX; i < maxX; i++) {
			vertexName = localMatrix.getRowName(i);
			v = map.get(vertexName);
			selected.add(v);
		}

		// TODO: uncomment
		_dataModel.removePropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		_dataModel.setSelectedVertices(selected);
		_dataModel.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
	}
	
	public static void println(String s) {
//		System.out.println(s);
	}


	public RenderedImage getImage() {
		return this.image;
	}
}
