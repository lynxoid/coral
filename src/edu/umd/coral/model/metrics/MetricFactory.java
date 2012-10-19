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
package edu.umd.coral.model.metrics;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MetricFactory {
	public static final String VI = "Variation of information";
	public static final String MI = "Mutual information";
	public static final String MIRKIN = "Mirkin";
	public static final String RAND = "Rand";
	public static final String JACCARD = "Jaccard";
	public static final String PURITY = "Purity";
	public static final String INV_PURITY = "Inverse purity";
	public static final String FMEASURE = "F-measure";
	public static final String FOLKES = "Folkes-Mallows";
	
	private static final Metric JACCARD_METRIC = new JaccardMetric();
	
	// throws NotImplementedException when requestedMetric is not defined in
	// MetricInfoMessage list of metrics.
	public static Metric getMetricInstance(String requestedMetric) throws NotImplementedException{
		if (MetricFactory.VI.equals(requestedMetric)) {
			return new VariationOfInformation();
		}
		else if (MetricFactory.MI.equals(requestedMetric)) {
			return new MutualInformation();
		}
		else if (MetricFactory.RAND.equals(requestedMetric)) {
			return new RandIndex();
		}
		else if (MetricFactory.JACCARD.equals(requestedMetric)) {
			return JACCARD_METRIC;
		}
		else if (MetricFactory.MIRKIN.equals(requestedMetric)) {
			return new MirkinMetric();
		}
		else if (MetricFactory.PURITY.equals(requestedMetric)) {
			return new PurityMetric();
		}
		else if (MetricFactory.INV_PURITY.equals(requestedMetric)) {
			return new InversePurity();
		}
		else if (MetricFactory.FMEASURE.equals(requestedMetric)) {
			return new FMeasureMetric();
		}
		else if (MetricFactory.FOLKES.equals(requestedMetric)) {
			return new FolkesMetric();
		}
		
		throw new NotImplementedException();
	}
}
