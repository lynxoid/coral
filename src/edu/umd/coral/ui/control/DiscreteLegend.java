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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Formatter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.umd.coral.model.GradientColorModel;

public class DiscreteLegend extends JComponent implements MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498972148139318741L;

//	private float [] values;
	
	private final int minValue = 0;
	
	public static final int minBlockSize = 25; // pixels
	
	private int maxValue;
	
	public static final int gradientWidth = 10;
	
	private boolean isVertical = false;
	
	private boolean smooth = false;
	
	public DiscreteLegend(int maxValue) {
		super();
		this.maxValue = maxValue;
		
		this.addMouseMotionListener(this);
	}
//
//	public boolean isVertical() {
//		return isVertical;
//	}
//
//	public void setVertical(boolean isVertical) {
//		this.isVertical = isVertical;
//		repaint();
//	}
	
	public void setMaxValue(int max) {
		this.maxValue = max;
		repaint();
	}
	
	private String getString(float value) {
		Formatter fmt = new Formatter();
		fmt.format("%.0f", value);
	    String sv = fmt.toString();
		return sv;
	}
	
	public Dimension getPreferredSize() {
		Dimension minSize = getMinSize(this.getGraphics());
		
		Dimension d;
		d = new Dimension((int)minSize.getWidth(), (int)minSize.getHeight());
		smooth = false;
		
		if (isVertical && d.getHeight() > 210) {
			d.height = 210;
			smooth = true;
		}
		if (!isVertical && d.getWidth() > 210) {
			d.width = 210;
			smooth = true;
		}
		//System.out.println(d.toString());
		this.setMaximumSize(d);
		return d;
	}
	
	/**
	 * calculates the minimum size the legend needs to draw the color blocks and
	 * the labels
	 * @param g
	 * @return
	 */
	private Dimension getMinSize(Graphics g) {
		if (g == null)
			return null;
		int minW, minH;
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds1 = fm.getStringBounds(getString(minValue), g);
		Rectangle2D bounds2 = fm.getStringBounds(getString(maxValue), g);
		
		if (isVertical) {
			minW = (int) Math.ceil(Math.max(bounds1.getWidth(), bounds2.getWidth())) + gradientWidth;
			minH = (int) Math.ceil(bounds1.getHeight() + bounds2.getHeight());
		}
		else {
			minW = (int) Math.max(bounds1.getWidth() + bounds2.getWidth(), DiscreteLegend.minBlockSize * (maxValue - minValue + 1) );
			minH = (int) Math.ceil(Math.max(bounds1.getHeight(), bounds2.getHeight())) - 3 + gradientWidth;
		}
		
		Dimension d = new Dimension(minW, minH);
		this.setMinimumSize(d);
		return d;
	}

	/**
	 * paint component - draw the gradient and min, max labels
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D)g;
		Dimension d = this.getPreferredSize();
		
		int labelH;
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds1 = fm.getStringBounds(getString(minValue), g);
		Rectangle2D bounds2 = fm.getStringBounds(getString(maxValue), g);
		
//		if (isVertical) {
//			labelW = (int) Math.ceil(Math.max(bounds1.getWidth(), bounds2.getWidth()));
////			minH = (int) Math.ceil(bounds1.getHeight() + bounds2.getHeight()) + 5 /* spacing between labels */;
//			
//			int h = (int) (this.getPreferredSize().getHeight() / maxValue);
//			for (int i = (int)minValue; i <= maxValue; i++) {
//				Color c = MatrixColorModel.getRGBColor(i, maxValue, false);
//				g2d.setColor(c);
//				g2d.fillRect(labelW, h * (i-1), gradientWidth, h);
//			}
//			
//			// draw labels
//			g2d.setColor(Color.BLACK);
//			
//			Rectangle2D bounds = fm.getStringBounds(getString(minValue), g2d);
//			g2d.drawString(getString(minValue), labelW - (int)bounds.getWidth(), (int)Math.ceil(bounds.getHeight()) - 3);
//			bounds = fm.getStringBounds(getString(maxValue), g2d);
//			g2d.drawString(getString(maxValue), 0, d.height - 2);
//		}
//		else
		{
			labelH = (int)Math.max(bounds1.getHeight(), bounds2.getHeight());
			labelH = Math.max(labelH, gradientWidth);
			
			int w = (int)Math.max(
					bounds1.getWidth() + bounds2.getWidth() + 5,// two labels 
					this.getPreferredSize().getWidth());		// from parent
			
			int yMid = (int)Math.floor((this.getPreferredSize().getHeight() - labelH ) / 2);
			// block width -- total width w/o space for dividers broken into maxvalue+1 pieces
			int blockWidth = (int)Math.floor( (w - maxValue) / (maxValue+1.0) );
			
			if (smooth) {
				g2d.setColor(Color.BLACK);
				g2d.fillRect(
						0, yMid, 
						blockWidth, yMid + gradientWidth);
				
				GradientPaint paint = new GradientPaint(
						0, 0, 
						GradientColorModel.getMatrixColor(1, maxValue, false), 
						d.width, 0,
						GradientColorModel.getMatrixColor(maxValue, maxValue, false),
						false);
				g2d.setPaint(paint);
				
				g2d.fillRect(
						blockWidth, yMid,						// x1, y1
						w, yMid + gradientWidth);	// x2, y2
			}
			else {
				
				g2d.setColor(Color.BLACK);
				g2d.fillRect(0, yMid, blockWidth, labelH);
				
				for (int i = 1; i <= maxValue; i++) {
					Color c = GradientColorModel.getMatrixColor(i, maxValue, false);
//					Color c = MatrixColorModel.getRGBColor(i, maxValue, false);
					g2d.setColor(c);
					g2d.fillRect(blockWidth * i + i, yMid, blockWidth, labelH);
				}
				
				g2d.setColor(Color.BLACK);
				// draw dividers
				for (int i = 1; i <= maxValue; i++) {
					g2d.drawLine(blockWidth * i + i-1, yMid, blockWidth * i+i-1, yMid + labelH - 1);
				}
			}
			
			// draw labels
			g2d.getFontMetrics();
			Rectangle2D bounds = fm.getStringBounds(getString(minValue), g2d);
			
			int componentH = (int)this.getPreferredSize().getHeight();
			g2d.setColor(Color.WHITE);
			g2d.drawString(getString(minValue), 
					(int)(blockWidth - bounds.getWidth() ) / 2, 
					componentH - yMid - 3);
			
			g2d.setColor(Color.BLACK);
			g2d.drawString(getString(minValue+1), 
					(int)(blockWidth - bounds.getWidth() ) / 2 + blockWidth, 
					componentH - yMid - 3);

			bounds = fm.getStringBounds(getString(maxValue), g2d);
			g2d.drawString(getString(maxValue), 
					(int) (d.width - blockWidth + (blockWidth - bounds.getWidth() ) / 2), 
					componentH - yMid - 3);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
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
		JFrame f = new JFrame("Swing Gradient Demo");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		ContinuousLegend panel = new ContinuousLegend();
//		Color c1 = MatrixColorModel.getRGBColor(1, 10, false);
//		Color c2 = MatrixColorModel.getRGBColor(10, 10, false);
		DiscreteLegend panel = new DiscreteLegend(10);
		
		f.setPreferredSize(new Dimension(200, 100));
		
		f.getContentPane().add(panel);
        f.pack();
		f.setVisible(true);
	}

	//
	// MouseMotionListener
	//
	
	public void mouseDragged(MouseEvent arg0) {
		// do nothing
	}

	public void mouseMoved(MouseEvent e) {
		Graphics g = this.getGraphics();
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds1 = fm.getStringBounds(getString(minValue), g);
		Rectangle2D bounds2 = fm.getStringBounds(getString(maxValue), g);
		int w = (int)Math.max(
				bounds1.getWidth() + bounds2.getWidth() + 5, // two labels 
				this.getPreferredSize().getWidth());		// from parent
		int blockWidth = (w - maxValue+1) / maxValue;
		int x = e.getX(), value = 0;
		for (int i = minValue; i <= maxValue; i++) {
			if (blockWidth * (i-1) + (i-1) < x && x < blockWidth * i +i-2) {
				value = i;
				break;
			}
		}
		if (value > 0)
			this.setToolTipText(String.valueOf(value));
		else
			this.setToolTipText("");
	}

}
