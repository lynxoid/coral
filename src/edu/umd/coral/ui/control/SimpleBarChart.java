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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.umd.coral.model.data.ColoredPair;
import edu.umd.coral.model.data.Pair;
import edu.umd.coral.ui.panel.BarChartPanel;

/**
 * Visualizes bar chart givent he data.
 * Data format: Map<String, Float> - a collection of label-value pairs.
 * 
 * @author lynxoid
 *
 */
public class SimpleBarChart extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3188537316247087159L;
	
//	private Map<String, Float> dataProvider;
	
	private ArrayList<ColoredPair<String, Float>> dataProvider;
	
	private Color barColor = Color.GREEN;
	
	private String dtype = BarChartPanel.FLOAT;
	
	private final int LABEL_PADDING = 2;
	
	private final int BOTTOM_PADDING = 17;
	
	private final int AXIS_WIDTH = 1;
	
	private final int TICK_LENGTH = 3;

	private boolean showMsg = false;

	private ArrayList<Boolean> overlap;
	
	BufferedImage stripesImg;
	int img_width = 20;
	TexturePaint tpaint;
	
	// Constructor
	public SimpleBarChart() {
		super();
		
		stripesImg = new BufferedImage(img_width, img_width, BufferedImage.TYPE_INT_ARGB);
		Graphics2D imgG = stripesImg.createGraphics();
		imgG.setColor(Color.blue);
		imgG.setStroke(new BasicStroke(6));
		// draw three stripes?
		imgG.drawLine(0, img_width/2, img_width/2, 0);
//		imgG.drawLine(0, img_width, img_width, 0);
		imgG.drawLine(img_width/2, img_width, img_width, img_width/2);
		tpaint = new TexturePaint(stripesImg, new Rectangle2D.Float(0f, 0f, (float)img_width, (float)img_width));
	}
	
	public SimpleBarChart(String dtype) {
		super();
		this.dtype = dtype;
	}
	
	public Color getBarColor() {
		return barColor;
	}

	/**
	 * Sets the color used for filling the bars and updates the screen
	 * @param barColor
	 */
	public void setBarColor(Color barColor) {
		this.barColor = barColor;
		repaint();
	}	
	
	public ArrayList<ColoredPair<String, Float>> getDataProvider() {
		return dataProvider;
	}

	/**
	 * Sets data to DP and updates the screen
	 * @param dataProvider
	 */
	public void setDataProvider(ArrayList<ColoredPair<String, Float>> dataProvider) {
		this.dataProvider = dataProvider;

		repaint();
	}

	/**
	 * Paint component
	 * TODO: paint into image
	 */
	public void paintComponent(Graphics g_) {
		super.paintComponent(g_);
		
		Graphics2D g = (Graphics2D)g_;
		
		int w = getWidth(), h = getHeight();
		
		g.clearRect(0, 0, w, h);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);
		
		if (dataProvider == null || dataProvider.size() == 0) {
			// draw axis w/ max value 10
			drawAxisLabels(g, h, 10f);
			// draw axes
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D bounds1 = fm.getStringBounds("9.00", g);
			
			// how wide and tall a "0.00" string would be
			int littlW = (int)Math.ceil(bounds1.getWidth());
			
			
			g.setColor(Color.BLACK);
			g.drawLine(littlW + LABEL_PADDING, 0, littlW + LABEL_PADDING, h - BOTTOM_PADDING);		// left vertical axis
			g.drawLine(littlW + LABEL_PADDING, h - BOTTOM_PADDING, w, h - BOTTOM_PADDING);		// bottom horizontal axis
			
			Dimension d = getSize();
			Rectangle2D size = fm.getStringBounds("Load network data to see density", g);
			if (showMsg) {
				int x = (int)(d.getWidth() - size.getWidth())/2;
				int y = (int)(d.getHeight() - size.getHeight())/2;
				g.drawString("Load network data to see density", 
						x,
						y);
			}
			
			return;
		}
		
		
		// find max value
		float maxValue = 0;
		for (Pair<String, Float> v : this.dataProvider) {
			if (v.getValue() > maxValue)
				maxValue = v.getValue();
		}
		
		// draw axis labels - 0, increments of 1, 2, 5, or 10 and max
		// measure how much space need on the left for axis labels
		int estWidth = drawAxisLabels(g, h, maxValue);
		int maxLabelSize = estWidth + LABEL_PADDING;	// including padding from axis
		
		// draw axes
		g.setColor(Color.BLACK);
		g.drawLine(maxLabelSize + LABEL_PADDING, 0, maxLabelSize + LABEL_PADDING, h - BOTTOM_PADDING);		// left vertical axis
		g.drawLine(maxLabelSize + LABEL_PADDING, h - BOTTOM_PADDING, w, h - BOTTOM_PADDING);		// bottom horizontal axis
		
		// draw bars
		drawBars(g, w, h, maxValue, overlap, maxLabelSize);
	}

	/**
	 * Draw bars for the chart, labels for each bar and its values
	 * 
	 * @param g
	 * @param w
	 * @param h
	 * @param maxValue
	 * @param maxLabelSize
	 */
	private void drawBars(Graphics2D g, int w, int h, float maxValue, ArrayList<Boolean> overlap,
			int maxLabelSize) {
		
		float pxPerCount = (h - BOTTOM_PADDING) / maxValue;
		int spacing = 10;
		int count = this.dataProvider.size();
		int barWidth = (w - maxLabelSize - LABEL_PADDING - AXIS_WIDTH) / count - 2 * spacing;
		int barHeight;
		int xOffset = maxLabelSize + LABEL_PADDING + AXIS_WIDTH + spacing;
		int yBottom = h - BOTTOM_PADDING;
		
		if (barWidth <= 0)
			barWidth = 1;
		
		Formatter fmt = new Formatter();
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds1;
		int fontWidth, fontHeight;
		
		// create texture for the stripes
		
		
		for (ColoredPair<String, Float> pair : this.dataProvider) {
			float v = pair.getValue();
			barHeight = (int) Math.floor(pxPerCount * v);
			
			// draw hashed if pair.hasOverlap() == true
			if (pair.getOverlap()) {
				g.setPaint(StripedImage.getTexturePaint(pair.getColor()));
			}
			else 
				g.setColor(pair.getColor());
			g.fillRect(xOffset, yBottom - barHeight, barWidth, barHeight);
			g.setColor(Color.BLACK);
			g.drawRect(xOffset, yBottom - barHeight, barWidth, barHeight);
			
			fmt = new Formatter();
			if (dtype == BarChartPanel.FLOAT)
				fmt.format("%.2f", pair.getValue());
			else if (dtype == BarChartPanel.INT)
				fmt.format("%d", (int)Math.round(pair.getValue()) );
		    String sv = fmt.toString();
			
			bounds1 = fm.getStringBounds(sv, g);
			
			// how wide and tall a string would be
			fontWidth = (int)Math.ceil(bounds1.getWidth());
			fontHeight = (int) Math.ceil(bounds1.getHeight());
			// draw bar value
			g.drawString(sv, xOffset + barWidth/2 - fontWidth/2, 
					Math.min(yBottom - barHeight + fontHeight, yBottom));
			
			// draw bar label, truncate if necessary
			String s = pair.getKey();
			int letterCount = barWidth / 10; /* calculate width */

			if (letterCount < s.length()) {
				// cut
				s = pair.getKey().substring(0, letterCount);
			}
			
			bounds1 = fm.getStringBounds(s, g);
			fontWidth = (int) Math.ceil(bounds1.getWidth());
			fontHeight = (int) Math.ceil(bounds1.getHeight());
			g.drawString(s, xOffset + barWidth/2 - fontWidth/2, h - 2);
			
			xOffset += 2 * spacing + barWidth;
		}
	}

	/**
	 * Estimates how many and which axis labels to draw and draws them.
	 * Estimate space for this label w/ two digits precision	
	 * @private
	 * @param g
	 * @param h
	 * @param maxLabelSize
	 * @param maxValue
	 */
	private int drawAxisLabels(Graphics g, int h, float maxValue) {
		FontMetrics fm = g.getFontMetrics();
		String f = suggestFormat(maxValue);
		// System.out.println(f);
		Formatter form = new Formatter();
		if (dtype == BarChartPanel.FLOAT)
			form.format(f, 0.0f);
		else
			form.format(f, 0);
		String v = form.toString();
		
		// how wide and tall a "0.00" string would be
		Rectangle2D bounds1 = fm.getStringBounds(v, g);
		int zeroWidth = (int)Math.ceil(bounds1.getWidth());
		int fontHeight = (int) Math.ceil(bounds1.getHeight());
		
		// estimate maxLabelSize (how big a label can be)
		form = new Formatter();
		if (dtype == BarChartPanel.FLOAT)
			form.format(f, maxValue);
		else
			form.format(f, (int)maxValue);
		
		v = form.toString();
		bounds1 = fm.getStringBounds(v, g);
		int maxLabelWidth = (int) Math.ceil(bounds1.getWidth());
		
		int labelY = h - BOTTOM_PADDING, labelX = maxLabelWidth - zeroWidth;
		float delta = estimateAxisStep(fontHeight, maxValue, h - BOTTOM_PADDING - fontHeight);
		//float pxStep = maxValue / delta;
		float pxStep = delta / maxValue * h;
		float value = 0.0f;
		int valueW;
		
		
		g.setColor(Color.BLACK);
		
		while (labelY > 0) {
			form = new Formatter();
			if (dtype == BarChartPanel.FLOAT)
				form.format(f, value);
			else
				form.format(f, (int)value);
			v = form.toString();
			bounds1 = fm.getStringBounds(v, g);
			valueW = (int)Math.ceil(bounds1.getWidth());
			labelX = maxLabelWidth - valueW;
			g.drawString(v, labelX, labelY);
			// draw a tick mark
			g.drawLine(maxLabelWidth + LABEL_PADDING, labelY, maxLabelWidth + LABEL_PADDING + TICK_LENGTH, labelY);
			value += delta;
//			System.out.println(value);
			labelY -= pxStep; // lower left x,y
		}
		
		return maxLabelWidth;
	}

	private String suggestFormat(float maxValue) {
		if (dtype == BarChartPanel.INT) {
			//NumberFormat nf = NumberFormat.getIntegerInstance();
			return "%d";
		}
		
		NumberFormat formatter = new DecimalFormat("0.##E0");
	    String value = formatter.format(maxValue); // 2,147484E9
	    int index = value.indexOf("E");
	    // float digits = Float.parseFloat(value.substring(0, index));
	    float exponent = Float.parseFloat(value.substring(index + 1));
	    
	    if (exponent > 1) {
	    	return "%" + (int)exponent + ".0f";
	    }
	    else if (maxValue <= 10 && maxValue > 1)
	    	return "%.1f";
	    
	    // less than 1
	    exponent = -exponent + 1;
	    String f = "%." + (int)exponent + "f";
	    //System.out.println(f);
		return f;
	}

	// TODO: improve:)
	/**
	 * try to fit as many labels as possible w/o overcrowding
	 */
	private float estimateAxisStep(int fontHeight, float maxValue, int h) {
		float [] increments = {10, 5, 4, 2, 1, 0.5f, 0.25f, 0.2f, 0.1f};
		
		NumberFormat formatter = new DecimalFormat();
		formatter = new DecimalFormat("0.##E0");
	    String value = formatter.format(maxValue); // 2,147484E9
	    int index = value.indexOf("E");
	    // float digits = Float.parseFloat(value.substring(0, index));
	    float exponent = Float.parseFloat(value.substring(index + 1));
	    float step = 0;
	    float inc;
	    int i;
	    
	    step = (float)Math.pow(10, exponent);
    	inc = step;
    	i = 0;
    	for (i = 0; i < increments.length; i++) {
    		inc = step / increments[i];
    		if (maxValue / inc < h / (2 * fontHeight) )
    			break;
    	}
    	
	    
	    return inc;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame f = new JFrame("Swing Paint Demo");
		//f.setSize(300, 400);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		SimpleBarChart chart = new SimpleBarChart("float");
		chart.setPreferredSize(new Dimension(300,400));
		
		ArrayList<ColoredPair<String, Float>> data = new ArrayList<ColoredPair<String, Float>>();
		
//		data.add(new Pair<String, Float>("A", 103.0f));
//		data.add(new Pair<String, Float>("B", 3.5f));
//		data.add(new Pair<String, Float>("C", 15.2f));
//		data.add(new Pair<String, Float>("D", 5.1f));
		
//		data.add(new Pair<String, Float>("A", 1.2f));
//		data.add(new Pair<String, Float>("B", 0.3f));
//		data.add(new Pair<String, Float>("C", 0.2f));
//		data.add(new Pair<String, Float>("D", 0.4f));
		
		data.add(new ColoredPair<String, Float>("A", 6.0f));
		data.add(new ColoredPair<String, Float>("B", 5f));
		data.add(new ColoredPair<String, Float>("C", 4f));
		data.add(new ColoredPair<String, Float>("D", 4f));
		
		
		//Object [] array = {{}, {}, {}};
		chart.setDataProvider(data);
		
		f.add(chart);
        f.pack();
		f.setVisible(true);
		
		f.setPreferredSize(new Dimension(300, 400));
	}

	public void showMessage() {
		showMsg  = true;
	}
}
