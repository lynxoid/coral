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
 *
 * @author dfilippo
 */

package edu.umd.coral.ui.panel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.umd.coral.managers.MetricScoreManager;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.GradientColorModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.ComparisonScores;
import edu.umd.coral.model.data.Score;
import edu.umd.coral.model.metrics.Metric;
import edu.umd.coral.model.metrics.MetricFactory;
import edu.umd.coral.ui.ISelectable;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.control.ContinuousLegend;
import edu.umd.coral.ui.ladder.All2AllTable;

public class LadderPanel extends JPanelExt implements ActionListener, PropertyChangeListener, HasSaveableImage {

	/**
	 * A collection of metric used for comparing clustering pairs
	 */
	public static final Metric [] metrics = {
		MetricFactory.getMetricInstance(MetricFactory.JACCARD), 
		MetricFactory.getMetricInstance(MetricFactory.MIRKIN), 
		MetricFactory.getMetricInstance(MetricFactory.RAND),
		MetricFactory.getMetricInstance(MetricFactory.MI),
		MetricFactory.getMetricInstance(MetricFactory.VI),
		MetricFactory.getMetricInstance(MetricFactory.PURITY),
		MetricFactory.getMetricInstance(MetricFactory.INV_PURITY),
		MetricFactory.getMetricInstance(MetricFactory.FOLKES),
		MetricFactory.getMetricInstance(MetricFactory.FMEASURE)
	};
	
	/**
	 * generated ID
	 */
	private static final long serialVersionUID = 2503266504179112103L;
	
	private JComboBox metricsCombo;
	
	private All2AllTable ladder;
	
	private ContinuousLegend legend;

	////////////////////////////////////////////////////////////////////////////
	//
	// Constructor(s)
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Default constructor - initializes UI
	 * 
	 * @param model
	 */
	public LadderPanel(DataModel model) {
		super(model);
		
		// listen for changes in the model
		_dataModel.addPropertyChangeListener(DataModel.ORIGINAL_MATRIX_CHANGED, this);

		initUI(model);
	}

	/**
	 * @private
	 * set up combobox for metrics and a ladder 
	 * @param model
	 */
	private void initUI(DataModel model) {
		setLayout(new BorderLayout());

		// panel to hold label and a combo
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		JLabel l = new JLabel("Metric:");
		panel.add(l, BorderLayout.LINE_START);

		// metrics combo
		metricsCombo = new JComboBox(metrics);
		metricsCombo.setToolTipText(metrics[0].getAnnotation());
		metricsCombo.setSelectedIndex(0);
		metricsCombo.addActionListener(this);
		panel.add(metricsCombo, BorderLayout.CENTER);

		add(panel, BorderLayout.PAGE_START);
		
		// comparison ladder
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		ladder = new All2AllTable();
		ladder.addPropertyChangeListener(ISelectable.CELL_SELECTED_CHANGED, this);
		ladder.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panel.add(ladder);
		
		legend = new ContinuousLegend();
		legend.setAlignmentY(Component.BOTTOM_ALIGNMENT);
	    panel.add(legend);
	    add(panel, BorderLayout.CENTER);
	}
	
	
	
	private void updateLegend(float maxValue) {
		// configure legend
		legend.setMinColor(GradientColorModel.getLadderColor(0, 1, false));
		legend.setMaxColor(GradientColorModel.getLadderColor(0.99f, 1, false));
		legend.setMinValue(0);
		legend.setMaxValue(maxValue);
		legend.revalidate();
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Event handling
	//
	////////////////////////////////////////////////////////////////////////////
	
	public void actionPerformed(ActionEvent e) {
		// ComboBox argument changed
		Metric selectedMetric = (Metric) metricsCombo.getSelectedItem();
		metricsCombo.setToolTipText(selectedMetric.getAnnotation());
		System.out.println("switched to " + selectedMetric.toString() + " metric");
		updateScores(selectedMetric);
		_dataModel.setSelectedMetric(selectedMetric);
	}
	
	private void updateScores(Metric m) {
		MetricScoreManager manager = new MetricScoreManager(_dataModel.getIncludeGrabBag());
		ComparisonScores<Clustering> scores;
		scores = manager.computeScores(m, _dataModel.getClusteringOrdering());
		_dataModel.addScores(m, scores);
		ladder.setComparisonItems(scores);
		ladder.repaint();
		
		if (scores != null)
			updateLegend(scores.getMax());
	}

	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		
		if (name.equals(ISelectable.CELL_SELECTED_CHANGED)) {
			@SuppressWarnings("unchecked")
			Score<Clustering> value = (Score<Clustering>) e.getNewValue();
			_dataModel.setSelectedClusteringPair(value);
		}
		else if (name.equals(DataModel.ORIGINAL_MATRIX_CHANGED)) {
			updateScores(_dataModel.getSelectedMetric());
			repaint();
			
//			Map<String, Clustering> map = _dataModel.getClusterings();
//			
//			if (map == null)
//				return;
//			
//			Set<String> keys = map.keySet();
//			String [] array = keys.toArray(new String[0]);
//			Arrays.sort(array, new Comparator<String>() {
//				public int compare(String s1, String s2) {
//					try {
//						int i1 = Integer.valueOf(s1);
//						int i2 = Integer.valueOf(s2);
//						
//						if (i1 < i2) return -1;
//						if (i1 > i2) return 1;
//						return 0;
//					}
//					catch (Exception e) {
//						return s1.compareTo(s2);
//					}
//				}
//			});
//			
		}
	}

	public RenderedImage getImage() {
		BufferedImage image = new BufferedImage(
				this.ladder.getWidth(),
				this.ladder.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		ladder.paint(image.getGraphics());
		return image;
	}

}
