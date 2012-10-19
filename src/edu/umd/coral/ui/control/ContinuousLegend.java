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
import java.awt.geom.Rectangle2D;
import java.util.Formatter;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.umd.coral.model.GradientColorModel;
import edu.umd.coral.model.MatrixColorModel;

public class ContinuousLegend extends JComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498972148139318741L;

	private Color minColor = Color.WHITE;

	private Color maxColor = GradientColorModel.getLadderColor(1.0f, 1, false);

	private float minValue = 0.0f;

	private float maxValue = 1.0f;
	
	private int gradientWidth = 10;
	
	private boolean isVertical = true;
	
	public ContinuousLegend() {
		super();
		
//		this.setBorder(BorderFactory.createLineBorder(Color.red));
		
		if (isVertical) {
			setMinimumSize(new Dimension(30, 50));
//			setPreferredSize(new Dimension(30, 50));
		}
		else {
			setMinimumSize(new Dimension(50, 30));
//			setPreferredSize(new Dimension(50, 30));
		}
	}
	
	public ContinuousLegend(Color minColor, Color maxColor) {
		this.minColor = minColor;
		this.maxColor = maxColor;
	}

	public boolean isVertical() {
		return isVertical;
	}

	public void setVertical(boolean isVertical) {
		this.isVertical = isVertical;
		repaint();
	}

	public void setMinColor(Color c) {
		minColor = c;
		repaint();
	}

	public void setMaxColor(Color c) {
		maxColor = c;
		repaint();
	}

	public void setMinValue(float value) {
		minValue = value;
		repaint();
	}

	public void setMaxValue(float value) {
		maxValue = value;
		repaint();
	}
	
	private String getString(float value) {
		Formatter fmt = new Formatter();
		fmt.format("%.2f", value);
	    String sv = fmt.toString();
		return sv;
	}
	
	public Dimension getPreferredSize() {
		Dimension minSize = getMinSize(this.getGraphics());
		Dimension parentSize = this.getParent().getSize();
		
		Dimension d;
		if (isVertical) 
			d = new Dimension((int)minSize.getWidth(), (int)parentSize.getHeight());
		else 
			d = new Dimension((int)parentSize.getWidth(), (int)minSize.getHeight());
		//System.out.println(d.toString());
		this.setMaximumSize(d);
		return d;
	}
	
	private Dimension getMinSize(Graphics g) {
		
		int minW, minH;
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds1 = fm.getStringBounds(getString(minValue), g);
		Rectangle2D bounds2 = fm.getStringBounds(getString(maxValue), g);
		
		if (isVertical) {
			minW = (int) Math.ceil(Math.max(bounds1.getWidth(), bounds2.getWidth())) + gradientWidth;
			minH = (int) Math.ceil(bounds1.getHeight() + bounds2.getHeight());
		}
		else {
			minW = (int) Math.ceil(bounds1.getWidth() + bounds2.getWidth()) + 5;
			minH = (int) Math.ceil(Math.max(bounds1.getHeight(), bounds2.getHeight())) - 3 + gradientWidth;
		}
		//System.out.println("min: " + minW + " " + minH);
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
		Dimension d = this.getSize();
		GradientPaint paint;
		
		int labelW;
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds1 = fm.getStringBounds(getString(minValue), g);
		Rectangle2D bounds2 = fm.getStringBounds(getString(maxValue), g);
		
		if (isVertical) {
			labelW = (int) Math.ceil(Math.max(bounds1.getWidth(), bounds2.getWidth()));
//			minH = (int) Math.ceil(bounds1.getHeight() + bounds2.getHeight()) + 5 /* spacing between labels */;
			
			paint = new GradientPaint(	0, 0, 
										minColor, 
										0, d.height,
										maxColor, 
										false);
			g2d.setPaint(paint);
			g2d.fillRect(	labelW, 0,					// x1, y1
							gradientWidth, d.height);	// x2, y2
			// draw labels
			g2d.setColor(Color.BLACK);
			Rectangle2D bounds = fm.getStringBounds(getString(minValue), g2d);
			g2d.drawString(getString(minValue), 0, (int)Math.ceil(bounds.getHeight()) - 3);
			bounds = fm.getStringBounds(getString(maxValue), g2d);
			g2d.drawString(getString(maxValue), 0, d.height - 2);
		}
		else {
			labelW = (int) Math.ceil(bounds1.getWidth() + bounds2.getWidth()) + 5 /* spacing between labels */;
//			minH = (int) Math.ceil(Math.max(bounds1.getHeight(), bounds2.getHeight())) - 3 + gradientWidth;
			paint = new GradientPaint(
					0, 0, 
					minColor, 
					d.width, 0,
					maxColor, false);
			g2d.setPaint(paint);
			g2d.fillRect(
					0, 0,						// x1, y1
					d.width, gradientWidth);	// x2, y2
			// draw labels
			g2d.setColor(Color.BLACK);
			g2d.getFontMetrics();
			Rectangle2D bounds = fm.getStringBounds(getString(minValue), g2d);
			g2d.drawString(getString(minValue), 
					0, 
					gradientWidth + (int)Math.ceil(bounds.getHeight()) - 3);
			bounds = fm.getStringBounds(getString(minValue), g2d);
			g2d.drawString(getString(maxValue), 
					d.width - (int)Math.ceil(bounds.getWidth()), 
					gradientWidth + (int)Math.ceil(bounds.getHeight()) - 3);
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
		Color c1 = MatrixColorModel.getRGBColor(1, 10, false);
		Color c2 = MatrixColorModel.getRGBColor(10, 10, false);
		ContinuousLegend panel = new ContinuousLegend(c1, c2);
		
		f.setPreferredSize(new Dimension(200, 200));
		
		f.getContentPane().add(panel);
        f.pack();
		f.setVisible(true);
	}
}
