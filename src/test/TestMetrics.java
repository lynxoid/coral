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
package test;

import junit.framework.TestCase;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.metrics.FMeasureMetric;
import edu.umd.coral.model.metrics.InversePurity;
import edu.umd.coral.model.metrics.JaccardMetric;
import edu.umd.coral.model.metrics.PurityMetric;

public class TestMetrics extends TestCase {
	
	Clustering A, B;
	
	float precision = 0.00001f;
	
//	@Before
    public void setUp() {
		A = new Clustering("A");
		B = new Clustering("B");
    }
    
    private void getEqualClust() throws Exception {
    	Module m = new Module("m1", A);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		m.addVertex(new Vertex("d"));
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		
		A.addModule(m);
		B.addModule(m);
    }
    
    private void getHalfAndWholeClust() throws Exception {
    	A.clear();
		B.clear();
		
		Module m = new Module("m1", A);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		A.addModule(m);
		
		m = new Module("m2", A);
		m.addVertex(new Vertex("d"));
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		A.addModule(m);
		
		m = new Module("m3", B);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		m.addVertex(new Vertex("d"));
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		B.addModule(m);
    }
    
    private void getUnequalAndWholeClust() throws Exception {
    	A.clear();
		B.clear();
		
		Module m = new Module("m1", A);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		m.addVertex(new Vertex("d"));
		A.addModule(m);
		
		m = new Module("m2", A);
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		A.addModule(m);
		
		m = new Module("m3", B);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		m.addVertex(new Vertex("d"));
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		B.addModule(m);
    }
    
    private void getUnequalClust() throws Exception {
    	A.clear();
		B.clear();
		
		Module m = new Module("m1", A);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		m.addVertex(new Vertex("d"));
		A.addModule(m);
		
		m = new Module("m2", A);
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		A.addModule(m);
		
		m = new Module("m3", B);
		m.addVertex(new Vertex("a"));
		m.addVertex(new Vertex("b"));
		m.addVertex(new Vertex("c"));
		B.addModule(m);
		m = new Module("m4", B);
		m.addVertex(new Vertex("d"));
		m.addVertex(new Vertex("e"));
		m.addVertex(new Vertex("f"));
		B.addModule(m);
    }
	
	public void testPurity() throws Exception {
		PurityMetric p = new PurityMetric();
		getEqualClust();
		float score = p.getScore(A, B, false);
		assertEquals(1.0f, score, precision);
		
		getHalfAndWholeClust();
		score = p.getScore(A, B, true);
		assertEquals(1.0, score, precision);
		
		getUnequalClust();
		score = p.getScore(A, B, true);
		assertEquals(5.0/6, score, precision);
	}
	
	public void testInvPurity() throws Exception {
		InversePurity p = new InversePurity();
		getEqualClust();
		float score = p.getScore(A, B, false);
		assertEquals(1.0f, score, precision);
		
		getHalfAndWholeClust();
		score = p.getScore(A, B, true);
		assertEquals(1.0, score, precision);
		
		getUnequalAndWholeClust();
		score = p.getScore(A, B, true);
		System.out.println("InvPurity " + score);
		assertEquals(4.0/6, score, precision);
		
		getUnequalClust();
		score = p.getScore(A, B, true);
		System.out.println("InvPurity " + score);
		assertEquals(5.0/6, score, precision);
	}
	
	public void testFolkes() {
		// TODO
	}
	
	public void testJaccard() throws Exception {
		JaccardMetric metric = new JaccardMetric();
		getEqualClust();
		A.createCooccurenceMatrix();
		B.createCooccurenceMatrix();
		// Jaccard should be 1
		float j = metric.getScore(A, B, false);
		assertEquals(1, j, precision);
		
		A.clear();
		B.clear();
		
		getHalfAndWholeClust();
		A.createCooccurenceMatrix();
		B.createCooccurenceMatrix();
		
		j = metric.getScore(A, B, false);
		assertEquals(0.4, j, precision);
	}
	
	public void testJaccardGrabBag() {
//		boolean includeGB = false;
//		Module m = new Module("__misc__", A);
		
		A.createCooccurenceMatrix();
		B.createCooccurenceMatrix();
	}
	
	public void testRand() {
		// TODO
	}
	
	public void testMirkin() {
		// TODO
	}
	
	public void testVI() {
		// TODO
	}
	
	/**
	 * Assume B is a true labeling, A is obtained through some other means and
	 * we want to measure how similar A is to B (truth)
	 * @throws Exception 
	 */
	public void testFmeasure() throws Exception {
		FMeasureMetric metric = new FMeasureMetric();
		getEqualClust();
		float score = metric.getScore(B, A, true);
		assertEquals(1.0, score, precision);
		A.clear();
		B.clear();
		
		getHalfAndWholeClust();
		score = metric.getScore(B, A, true);
		assertEquals(6.0/9.0, score, precision);
		
		getUnequalAndWholeClust();
		score = metric.getScore(B, A, true);
		assertEquals(0.8, score, precision);
		
		getUnequalClust();
		score = metric.getScore(B, A, true);
		assertEquals(87.0f/105.0f, score, precision);
	}
}
