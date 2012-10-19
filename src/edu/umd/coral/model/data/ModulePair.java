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
package edu.umd.coral.model.data;

import java.util.ArrayList;
import java.util.Collection;

import edu.umd.coral.ui.table.ClusteringCompareTableModel;

public class ModulePair {
	
	private Module m1;
	
	private Module m2;

	private ArrayList<Vertex> oneMinusTwo;
	
	private ArrayList<Vertex> twoMinusOne;
	
	private ArrayList<Vertex> onePlusTwo;
	
	private ArrayList<Vertex> intersection;
	
	private float score;
	
	private float module1Ratio;
	
	private float module2Ratio;
	
	private int module1Size;
	
	private int module2Size;

	/**
	 * 
	 * @param m1
	 * @param m2
	 */
	public ModulePair(Module m1, Module m2, float score) {
		this.m1 = m1;
		this.m2 = m2;
		this.score = score;
		
		this.oneMinusTwo = m1.getDifference(m2);
		this.twoMinusOne = m2.getDifference(m1);
		this.onePlusTwo = m1.getUnion(m2);
		
		module1Ratio = m1.getInOutRatio();
		module2Ratio = m2.getInOutRatio();
		
		intersection = m1.getIntersection(m2);
	}
	
	public ArrayList<Vertex> getLeftDifference() {
		return this.oneMinusTwo;
	}
	
	public ArrayList<Vertex> getRightDifference() {
		return this.twoMinusOne;
	}
	
	public ArrayList<Vertex> getUnion() {
		return this.onePlusTwo;
	}
	
	public ArrayList<Vertex> getIntersection() {
		return intersection;
	}
	
	public Module getM1() {
		return m1;
	}

	public Module getM2() {
		return m2;
	}
	
	public Collection<Vertex> getOneMinusTwo() {
		return oneMinusTwo;
	}

	public Collection<Vertex> getTwoMinusOne() {
		return twoMinusOne;
	}

	public Collection<Vertex> getOnePlusTwo() {
		return onePlusTwo;
	}

	public float getScore() {
		return score;
	}

	public float getModule1Ratio() {
		return module1Ratio;
	}

	public float getModule2Ratio() {
		return module2Ratio;
	}

	public int getModule1Size() {
		return module1Size;
	}

	public int getModule2Size() {
		return module2Size;
	}

	public ArrayList<Vertex> getValue(String name) {
		if (name.equals(ClusteringCompareTableModel.INTERSECTION))
			return this.getIntersection();
		if (name.equals(ClusteringCompareTableModel.LEFT_DIFF))
			return this.getLeftDifference();
		if (name.equals(ClusteringCompareTableModel.RIGHT_DIFF))
			return this.getRightDifference();
		if (name.equals(ClusteringCompareTableModel.UNION))
			return this.getUnion();
		if (name.equals(ClusteringCompareTableModel.ITEM_NAME))
			return this.getM1().getVertices();// TODO: m2?
		if (name.equals(ClusteringCompareTableModel.ITEM_PAIR_NAME))
			return this.getM2().getVertices();
		return null;
	}
}
