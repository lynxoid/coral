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
import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Vertex;

public class TestClique extends TestCase {
	
	/**
	 * a -- b -- d -- e
	 * |  /
	 * c
	 * 
	 * 
	 */
	public void testInOutEdges1() {
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		Vertex e = new Vertex("e");
		
		a.addNeighbor(b);
		a.addNeighbor(c);
		b.addNeighbor(c);
		b.addNeighbor(d);
		d.addNeighbor(e);
		
		Clique c1 = new Clique();
		c1.addVertex(a);
		c1.addVertex(b);
		c1.addVertex(c);
		
		Clique c2 = new Clique();
		c2.addVertex(d);
		c2.addVertex(e);
		
		assertTrue(c1.getInEdges() == 3);
		assertTrue(c1.getOutEdges() == 1);
		
		assertTrue(c2.getInEdges() == 1);
		assertTrue(c2.getOutEdges() == 1);
	}
	
	/**
	 * a -- b -- d -- e
	 * |  / |  /
	 * c -- f
	 * 
	 * 
	 */
	public void testInOutEdges2() {
		Vertex a = new Vertex("a");
		Vertex b = new Vertex("b");
		Vertex c = new Vertex("c");
		Vertex d = new Vertex("d");
		Vertex e = new Vertex("e");
		Vertex f = new Vertex("f");
		
		a.addNeighbor(b);
		a.addNeighbor(c);
		b.addNeighbor(c);
		b.addNeighbor(d);
		d.addNeighbor(e);
		b.addNeighbor(f);
		c.addNeighbor(f);
		d.addNeighbor(f);
		
		Clique c1 = new Clique();
		c1.addVertex(a);
		c1.addVertex(b);
		c1.addVertex(c);
		
		Clique c2 = new Clique();
		c2.addVertex(d);
		c2.addVertex(e);
		
		assertTrue(c1.getInEdges() == 3);
		assertTrue(c1.getOutEdges() == 3);
		
		assertTrue(c2.getInEdges() == 1);
		assertTrue(c2.getOutEdges() == 2);
	}
	
}
