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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.parse.ModuleFilesParser;
import edu.umd.coral.ui.tabs.PPDataModel;

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
public class ZoomableParallelSets extends ZoomablePanel implements
		HasSaveableImage {

	private static final long serialVersionUID = -747286996950904094L;

	private final int pixelsPerVertex = 3;

	private int largestClustSize = 0;

	// image of the clustering bands
	private BufferedImage traces;

	private boolean showItemLabels = true, showPartitionLabels = true;

	// arrangement of modules in order that they should be drawn
	private List<ArrayList<Module>> orderings;

	private ArrayList<Clustering> clustCollection;

	private Collection<Vertex> selectedVertices;

	private int moduleSpacing = 6;// pixels

	private final int bandSpacing = 50;

	private final int cHeight = 20;

	private final int MIN_WIDTH = 100;

	private final int MIN_MOD_WIDTH = 1 * pixelsPerVertex;

	private BufferedImage[] clusteringBandImage, intersectionBandImage;
	
	private BufferedImage itemLabelsImage;

	private ArrayList<Vertex> highlightedVertices;
	
	private PPDataModel ppdm;

	private int avgLabelWidth = 0;

	public ZoomableParallelSets(PPDataModel ppdm) {
		setLayout(null);
		this.ppdm = ppdm;
		// this.setLayout(new BorderLayout());
		setMinimumSize(new Dimension((width / 2), (height / 2)));
		setPreferredSize(new Dimension(width, height));

		// add listeners
		this.addMouseWheelListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public Dimension getPreferredSize() {
		// update width and height
		Dimension d = super.getPreferredSize();
		Dimension d2 = updateMatrixSize();

		int w = Math.max(d2.width, d.width);
		int h = Math.max(d2.height, d.height);
		return new Dimension(w, h);
	}

	/********************************************************
	 * 
	 * Update max size
	 * 
	 ********************************************************/
	protected Dimension updateMatrixSize() {
		int actualSize = (int)Math.max(MIN_WIDTH, this.largestClustSize*scale);

		// if does not fit into current size - increase size
		int count;
		if (clustCollection == null) count = 1;
		else count = clustCollection.size();
		
		int w = actualSize;
		int h = cHeight * count + bandSpacing * (count - 1) + 15;
		Dimension preferred = new Dimension(w, h);
		return preferred;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (g instanceof Graphics2D ) {
			Graphics2D g2D = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(
		            RenderingHints.KEY_ANTIALIASING,
		            RenderingHints.VALUE_ANTIALIAS_ON);
			rh.put(	RenderingHints.KEY_INTERPOLATION, 
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2D.setRenderingHints(rh);
			
			if (getWidth() == 0 || getHeight() == 0)
				return;
			AffineTransform shear;

			AffineTransform translateAT = AffineTransform.getTranslateInstance(
					translateX, translateY);
			AffineTransform scaleAT = AffineTransform.getScaleInstance(scale,
					scale);
			scaleAT.concatenate(translateAT);
			
			FontMetrics fm = g2D.getFontMetrics();
			Clustering c;
			int y, labelY;
			if (clusteringBandImage != null) {
				y = labelY = 0;
				for (int i = 0; i < clusteringBandImage.length; i++) {
					translateAT = AffineTransform.getTranslateInstance(0, y);
					shear = AffineTransform.getScaleInstance(scale, 1);
					shear.concatenate(translateAT);
					g2D.drawImage(clusteringBandImage[i], shear, null);
					y += bandSpacing + cHeight;
					
					if (showPartitionLabels) {
						c = clustCollection.get(i);
						g2D.setColor(Color.BLACK);
						Rectangle2D bounds = fm.getStringBounds(c.getName(), g2D);
						g2D.drawString(c.getName(), 10,
								labelY + (int) (cHeight + bounds.getHeight()) / 2 - 3 + cHeight);
					}
					labelY += bandSpacing + cHeight;
				}
			}
			
			if (intersectionBandImage != null) {
				y = cHeight + 1;
				for (int i = 0; i < intersectionBandImage.length; i++) {
					translateAT = AffineTransform.getTranslateInstance(0, y);
					scaleAT = AffineTransform.getScaleInstance(scale, 1);
//					scaleAT = AffineTransform.getScaleInstance(scale, scale);
					translateAT.concatenate(scaleAT);
					g2D.drawImage(intersectionBandImage[i], translateAT, null);
					y += bandSpacing + cHeight;
				}
			}
			
			if (traces != null) {
				g.setColor(Color.RED);
				// zoom, shear
				scaleAT = AffineTransform.getScaleInstance(scale, 1);
				g2D.drawImage(traces, scaleAT, null);
			}
			
			if ((this.pixelsPerVertex * scale * 3 > avgLabelWidth) && showItemLabels) {
				Rectangle r = this.getVisibleRect();
				g2D.drawImage(itemLabelsImage, r.x, r.y, null);
			}
		}
	}

	/**
	 * find clust bands that are in the viewport
	 * @param r
	 */
	private void drawLabels(Rectangle r) {
		// adjust image's size
//		System.out.println("drawing labels in ppp");
		if (r.width < 1 && r.height < 1) return;
		if (orderings == null) return;
		itemLabelsImage = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = itemLabelsImage.createGraphics();
		g2d.setColor(Color.BLUE.darker()); // draw labels in blue
		Font font = new Font("SansSerif", Font.PLAIN, 9);// set font
		g2d.setFont(font);
		
		boolean found_module = false;
		double v = 0;
		float labelY, labelX = 0, x_module_len;
		int band_i, module_j;
		int diff_y = 0;
		ArrayList<Module> modules;
		Module m;
		
		band_i = (int)Math.ceil(r.y * 1.0 / (bandSpacing + cHeight) );
		labelY = band_i * (cHeight + bandSpacing) - r.y;
		
		// find the first visible band and module in the rectangle
		while (labelY < r.y + r.height && band_i < orderings.size()) {
			modules = orderings.get(band_i);
			m = modules.get(0);
			labelX = 0; module_j = 0; diff_y = 0; found_module = false;
			
			// find the first item block that is visible on the screen
			while (labelX < r.x && !found_module) {
				m = modules.get(module_j);
				x_module_len = m.getSize() * pixelsPerVertex * scale;
				if (labelX + x_module_len > r.x) { // stop within this module
					v = Math.ceil((r.x - labelX ) / (pixelsPerVertex * scale));
					labelX += v * pixelsPerVertex * scale;
					diff_y += v;
					found_module = true;
				}
				else {
					// skip to the next module
					labelX += (m.getSize() * pixelsPerVertex + moduleSpacing) * scale;
					diff_y += m.getSize();
					module_j++;
				}
			}
			if (m == null) { // next clustering band
				labelY += bandSpacing + cHeight;
				continue;
			}
			labelX -= r.x;
			diff_y = (diff_y % 2 == 0) ? 0 : 8;
			
			// label everything in the viewport on this band
			for (; module_j < modules.size(); module_j++) {
				m = modules.get(module_j);
				Vertex u;
				for (; v < m.getSize(); v++) {
					u = m.getVertexMapping()[(int)v];
					g2d.drawString(u.getName(), labelX+1, labelY+11 + diff_y);
					diff_y = diff_y == 0 ? 8 : 0;
					labelX += pixelsPerVertex * scale;
				}
				v = 0;
				labelX += moduleSpacing * scale;
			}
			labelY += bandSpacing + cHeight;
			band_i++;
		}
	}

	// --------------------------------------------------------------------------
	//
	// HasSaveableImage listener
	//
	// --------------------------------------------------------------------------
	public RenderedImage getImage() {
		BufferedImage image = new BufferedImage(
				this.getWidth(),
				this.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		this.paint(image.getGraphics());
		return image;
	}

	public void setShowPartitionItems(boolean showPItems) {
		this.showItemLabels = showPItems;
		repaint();
	}

	public void setShowPartitionLabels(boolean showPLabels) {
		this.showPartitionLabels = showPLabels;
		this.repaint();
	}

	public void setOrderings(List<ArrayList<Module>> orderings) {
		this.orderings = orderings;
		this.intersectionBandImage = null;
		itemLabelsImage = null;
		
		if (orderings != null && orderings.size() > 0){
			Graphics g = this.getGraphics();
			FontMetrics fm = g.getFontMetrics();
			ArrayList<Module> modules = orderings.get(0);
			double sum_width = 0;
			int i = 0;
			Rectangle2D r;
			for (Module m : modules) {
				for (Vertex v : m.getVertices()) {
					r = fm.getStringBounds(v.getName(), g);
					sum_width += r.getWidth();
					i++;
				}
			}
			avgLabelWidth = (int) Math.floor(sum_width / i);
			// System.out.println("Avg label width is "+ avgLabelWidth);
		}
		
		repaint();
	}

	public void setCollection(ArrayList<Clustering> axisOrdering) {
		this.clustCollection = axisOrdering;
		this.clusteringBandImage = null;
		this.traces = null;
		itemLabelsImage = null;
//		drawClusterings();
		repaint();
	}

	public void invalidateData() {
		drawClusterings();
		repaint();
	}

	public void setSelectedVertices(Collection<Vertex> selectedVertices) {
		this.selectedVertices = selectedVertices;
		traces = null;
		drawTraces(orderings, clustCollection);
		repaint();
	}

//	public void invalidateTraces() {
//		drawTraces(orderings, clustCollection);
//	}
	
	// --------------------------------------------------------------------------
	//
	// Mouse interaction (tooltips, selection)
	//
	// --------------------------------------------------------------------------
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// if clicked on a band - select a vertex
		// if ctrl was pressed - select a module
		boolean getModule = e.isShiftDown();
		ArrayList<Module> ordering = getClusteringBand(e.getY());
		if (ordering != null) {
			// find a module and an item
			this.selectedVertices = getItems(ordering, getModule, e.getX());
			ppdm.setSelectedVertices(highlightedVertices);
			drawTraces(orderings, clustCollection);
			repaint();
			return;
		}
		else {
			highlightedVertices = null;
		}
	}
	
	/**
	 *  get the module, clustering, number of elements in the module
	 *  
	 *  data item under the mouse
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		boolean getModule = e.isShiftDown();
		ArrayList<Module> ordering = getClusteringBand(e.getY());
		
		if (ordering == null) { 
			setToolTipText(null); 
			return;
		}
		
		// if shift is held down
		if (getModule) {
			Module m = getModule(ordering, e.getX());
			if (m != null)
				this.setToolTipText("<html>Module: <b>" + m.getName() + "</b> (" + m.getSize() + ")<br>" +
					"clustering: <b>" + m.getClustering().getName() + "</b><br>" +
					"</html>");
			else
				this.setToolTipText(null);
		}
		// else
		else {
			Module m = null;
			Vertex v = null;
			boolean isFound = false;
			int xxx = 0;
			for (int i = 0; i < ordering.size() && !isFound; i++) {
				m = ordering.get(i);
				if (xxx <= e.getX() && e.getX() < xxx + m.getSize() * pixelsPerVertex * scale ) {
					int index = (int)Math.floor((e.getX() - xxx) / (pixelsPerVertex * scale) );
					v = m.getVertexMapping()[index];
					isFound = true;
				}
				xxx += (m.getSize() * pixelsPerVertex + moduleSpacing ) * scale;
			}
			
			if (v != null)
				this.setToolTipText("<html>Item: " + v.getName() + "<br>" +
					"module: <b>" + m.getName() + "</b> (" + m.getSize() + ")<br>" +
					"clustering: <b>" + m.getClustering().getName() + "</b><br>" +
					"</html>");
			else
				setToolTipText(null);
		}
		// if has selected items in the module:	
	}
	
	private Module getModule(ArrayList<Module> ordering, int x) {
		Module m;
		int xxx = 0;
		for (int i = 0; i < ordering.size(); i++) {
			m = ordering.get(i);
			
			if (xxx <= x && x < xxx + m.getSize() * pixelsPerVertex * scale ) {
				return m;	
			}
			xxx += (m.getSize() * pixelsPerVertex + moduleSpacing) * scale;
		}
		return null;
	}
	
	private ArrayList<Module> getClusteringBand(int y) {
		if (clustCollection == null) return null;
		boolean found = false;
		Iterator<Clustering> iter = clustCollection.iterator();
		int y_coord = 0, i = 0;
		while (!found && iter.hasNext()) {
			if (y_coord <= y && y < y_coord + cHeight) {
				found = true;
			}
			else {
				y_coord += cHeight + bandSpacing;
				i++;
				iter.next();
			}
		}
		
		if (found) {
			if (orderings!= null)
				return orderings.get(i);
		}
		return null;
	}
	
	private ArrayList<Vertex> getItems(ArrayList<Module> ordering, boolean getModule, int x) {
		int index = 0;
		Module m;
		int xxx = 0;
		for (int i = 0; i < ordering.size(); i++) {
			m = ordering.get(i);
			
			if (xxx <= x && x < xxx + m.getSize() * pixelsPerVertex * scale ) {
				if (getModule) {
					highlightedVertices = m.getVertices();
				}
				else {
					highlightedVertices = new ArrayList<Vertex>();
					index = (int)Math.floor((x - xxx) / (pixelsPerVertex * scale) );
					highlightedVertices.add(m.getVertexMapping()[index]);
				}
				return highlightedVertices;
				
			}
			xxx += (m.getSize() * pixelsPerVertex + moduleSpacing) * scale;
		}
		return null;
	}

	// --------------------------------------------------------------------------
	//
	// Layout, drawing
	//
	// --------------------------------------------------------------------------
	private void drawClusterings() {
		if (clustCollection == null || clustCollection.size() == 0) {
			// TODO
//			image.flush();
			traces = null;
			repaint();
			return;
		}
		
//		long before = System.currentTimeMillis();
		// find largest clustering size
		largestClustSize = 0;
		int pxLength = 10;
		int maxModuleCount = 0;
		int maxVertexCount = 0;
		for (Clustering c : clustCollection) {
			if (maxModuleCount < c.getModuleCount())
				maxModuleCount = c.getModuleCount();
			if (maxVertexCount < c.getVertexCount())
				maxVertexCount = c.getVertexCount();
			pxLength = c.getVertexCount()
					+ (c.getModuleCount() - moduleSpacing);
			if (largestClustSize < pxLength) {
				largestClustSize = pxLength;
			}
		}

		Dimension size = this.getSize();
		int w = (int) size.getWidth();
		int h = (int) size.getHeight();
		if (w <= 0 || h <= 0) {
			return;
		}

		// draw clusterings
		drawClusteringBands(orderings, clustCollection);

		// draw the bands between clusterings
		drawIntersectionBands(orderings, clustCollection);

		drawTraces(orderings, clustCollection);
		
//		long after = System.currentTimeMillis();
//		System.out.println("  Drawing PPP: " + (after - before));
	}

	private void drawTraces(List<ArrayList<Module>> orderings,
							ArrayList<Clustering> clusterings) {
		if (orderings == null || selectedVertices == null)
			return;
		
//		long before = System.currentTimeMillis();
		int count = clusterings.size();
		int w = this.largestClustSize;
		int h = (int)Math.ceil(count * cHeight + (count-1) * bandSpacing);//(int)size.getHeight();
		
//		traces.flush();
		traces = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D imageGraphics = traces.createGraphics();
		RenderingHints rh = new RenderingHints(
	            RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(	RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		imageGraphics.setRenderingHints(rh);
		
		int x1 = 0, x2 = 0, index = -1;
		int y = 0, c;
		int s = clusterings.size();
		ArrayList<Module> ordering1, ordering2;
		
		Stroke stroke = new BasicStroke(2);
		imageGraphics.setStroke(stroke);
		Color color = new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 70);
		imageGraphics.setColor(color);
		
		// for each vertex
		for (Vertex v : selectedVertices) {
			y = cHeight;
			// draw a path through all clusterings
			for (c = 0; c < s - 1; c++) {
				ordering1 = orderings.get(c);
				ordering2 = orderings.get(c + 1);
				x1 = 0;
				for (Module m : ordering1) {
					if (!m.contains(v))
						x1 += (m.getSize() * pixelsPerVertex + moduleSpacing);
					else {
						index = m.getIndex(v);
						x1 += (pixelsPerVertex * index + pixelsPerVertex / 2);
						break;
					}
				}
				if (c == 0) imageGraphics.drawLine(x1, 0, x1, y);
				
				index = -1;
				x2 = 0;
				for (Module m : ordering2) {
					if (!m.contains(v))
						x2 += (m.getSize() * pixelsPerVertex + moduleSpacing);
					else {
						index = m.getIndex(v);
						x2 += (pixelsPerVertex * index + pixelsPerVertex / 2);
						break;
					}
				}
				imageGraphics.drawLine(x1, y, 
						x2, y + Math.round(bandSpacing));
				imageGraphics.drawLine(x2, y + Math.round(bandSpacing), 
						x2, y + cHeight + Math.round(bandSpacing));
				
				y += cHeight + bandSpacing;
			}
		}
//		long after = System.currentTimeMillis();
//		System.out.println("  Drawing traces: " + (after - before));
	}

	private void drawIntersectionBands(List<ArrayList<Module>> moduleOrderings, 
			ArrayList<Clustering> clusteringOrdering) {
		if (moduleOrderings == null)
			return;

//		long beofre = System.currentTimeMillis();
		Graphics2D imageGraphics;
		int bandsCount = clusteringOrdering.size() - 1;
		intersectionBandImage = new BufferedImage[bandsCount];

		int i, y, x;
		ArrayList<Module> ordering1;
		ArrayList<Module> ordering2;
		int s;
		int offset;
		int [] x2; // offset on the other clustering
		
		y = 0;	// offset by cHeight
		for (int j = 0 ; j < bandsCount; j++) {			
			x = 0;
			ordering1 = moduleOrderings.get(j);
			ordering2 = moduleOrderings.get(j+1);
			
			// initialize module x offsets for each module in clustering 2
			x2 = new int[ordering2.size()];
			i = 0;
			offset = 0;
			for (Module m2 : ordering2) {
				x2[i++] = offset;
				offset += getModuleWidth(m2) + moduleSpacing;
			}
			
			Color c = clusteringOrdering.get(j).getColor();
			// turn into a transparent color
			c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 50);
			
			int clustWidth = calculateClusteringWidth(clusteringOrdering.get(j), pixelsPerVertex);
			int clustWidth2 = calculateClusteringWidth(clusteringOrdering.get(j+1), pixelsPerVertex);
			clustWidth = Math.max(clustWidth, clustWidth2);
			
			intersectionBandImage[j] = new BufferedImage(clustWidth, bandSpacing, BufferedImage.TYPE_INT_ARGB);
			imageGraphics = intersectionBandImage[j].createGraphics();
			// TODO: update intersection bands when zoomed in
			// otherwise - ignore aliasing
//			RenderingHints rh = new RenderingHints(
//		            RenderingHints.KEY_ANTIALIASING,
//		            RenderingHints.VALUE_ANTIALIAS_ON);
//			rh.put(	RenderingHints.KEY_INTERPOLATION, 
//					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		    imageGraphics.setRenderingHints(rh);
			imageGraphics.setColor( c );
			
			int [][] indices;
			// go through all module combinations
			
//			long before = System.currentTimeMillis();
			for (Module m1 : ordering1) {
				i = 0;
				for (Module m2 : ordering2) {
					indices = m1.getIntersectionIndices(m2);
					s = indices.length;
					if (s > 0) {
						// draw bands
						for (int k = 0; k < s; k++) {
							int [] xPoints = {
									x + (int)Math.ceil(pixelsPerVertex * indices[k][0]), 
									x + (int)Math.ceil(pixelsPerVertex * (indices[k][0] + 1)), 
									x2[i] + (int)Math.ceil(pixelsPerVertex * (indices[k][1] + 1)), 
									x2[i] + (int)Math.ceil(pixelsPerVertex * indices[k][1])
							};
							int [] yPoints = {
									y, 
									y, 
									y+bandSpacing, 
									y+bandSpacing
							};
							imageGraphics.fillPolygon(xPoints, yPoints, 4);
						}
					}
					i++;
				}
				x += this.getModuleWidth(m1) + moduleSpacing;
			}
//			long afer2 = System.currentTimeMillis();
//			System.out.println(" bands " + j + "," + (j+1) + ": " + (afer2-before));
		}
//		long after = System.currentTimeMillis();
//		System.out.println("  Drawing intersection bands: " + (after - beofre));
	}

	private void drawClusteringBands(List<ArrayList<Module>> moduleOrderings,
			List<Clustering> clusteringOrdering) {
		if (moduleOrderings == null)
			return;

		int count = clusteringOrdering.size();
//		long before = System.currentTimeMillis();
		clusteringBandImage = new BufferedImage[count];

		int x, cWidth, longestClustering = 0, v_y = 0;
		Graphics2D imageGraphics;
		ArrayList<Module> ordering;

		// for each clustering, draw a band
		for (int c = 0; c < count; c++) {
			Clustering clust = clusteringOrdering.get(c);

			cWidth = calculateClusteringWidth(clust, pixelsPerVertex);
			// TODO: precompute
			if (cWidth > longestClustering)
				longestClustering = cWidth;
			clusteringBandImage[c] = new BufferedImage(cWidth, cHeight + 1 + 2,
					BufferedImage.TYPE_INT_ARGB);
			imageGraphics = clusteringBandImage[c].createGraphics();
			ordering = moduleOrderings.get(c);

			assert ordering != null;
			x = 0;

			// draw modules separated by empty space
			for (Module m : ordering) {
				if (m.getName().equals(ModuleFilesParser.GRAB_BAG))
					imageGraphics.setColor(Color.LIGHT_GRAY);
				else
					imageGraphics.setColor(clust.getColor());
				
//				imageGraphics.fillRect(x, 0,
//						(int) (m.getSize() * pixelsPerVertex), cHeight);
				
				for (int v = 0; v < m.getSize(); v++) {
					imageGraphics.fillRect(x + v * pixelsPerVertex, v_y, pixelsPerVertex, cHeight);
					v_y = (v_y == 2 ? 0 : 2);
				}

				x += m.getSize() * pixelsPerVertex + moduleSpacing;
			}

			imageGraphics.setColor(Color.BLACK);

//			if (this.showPartitionLabels) {
//				Rectangle2D bounds = fm.getStringBounds(clust.getName(),
//						imageGraphics);
//				imageGraphics.drawString(clust.getName(), 10,
//						0 + (int) (cHeight + bounds.getHeight()) / 2);
//			}
		}
		
		this.largestClustSize = longestClustering;
//		long after = System.currentTimeMillis();
//		System.out.println("  Drawing clust bands: " + (after - before));
	}

	/**
	 * 
	 * @param clust
	 * @param pixelPerVertex
	 * @return
	 */
	private int calculateClusteringWidth(Clustering clust, float pixelPerVertex) {
		float sum = 0;
		float mWidth;
		for (Module m : clust.getModules()) {
			mWidth = pixelPerVertex * m.getSize();
			if (mWidth < MIN_MOD_WIDTH)
				sum += MIN_MOD_WIDTH;
			else
				sum += mWidth;
		}
		sum += (clust.getModuleCount() - 1) * this.moduleSpacing;
		return (int) sum;
	}
	
	/**
	 * 
	 * @param m
	 * @return
	 */
	private int getModuleWidth(Module m) {
		float w = m.getSize() * this.pixelsPerVertex;
		if (w < MIN_MOD_WIDTH)
			w = MIN_MOD_WIDTH;
		return (int)Math.ceil(w);
	}

	public void updateLabels() {
		if ((this.pixelsPerVertex * scale * 3 > avgLabelWidth) && showItemLabels) {
			Rectangle r = this.getVisibleRect();
			this.drawLabels(r);
		}
	}

	public void setModuleSpacing(int moduleSpacing) {
		this.moduleSpacing = moduleSpacing;
		this.drawClusterings();
		this.repaint();
	}
}
