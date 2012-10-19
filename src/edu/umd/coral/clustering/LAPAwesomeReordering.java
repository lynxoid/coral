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
package edu.umd.coral.clustering;

import java.util.ArrayList;

import javaasp.LinearAssignmentSolver;
import edu.umd.coral.model.data.Matrix;

public class LAPAwesomeReordering implements MatrixReordering {
	
	private final String INDENT = "   ";
	
	private int greedyIterCount = 1;
	
	private int totalIterCount = 1;
	
	public LAPAwesomeReordering(int greedyIter, int optIter) {
		this.greedyIterCount = greedyIter;
		this.totalIterCount = greedyIter + optIter;
	}

	public Matrix reorder(Matrix originalMatrix) {
		System.out.println("reordering a matrix");
		long startMillis = System.currentTimeMillis();
		
		// get connected components
		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));
		Matrix [] components = Matrix.getConnectedComponents2(originalMatrix);
//		for (Matrix m : components)
//			System.out.println(m.toString());
				
		System.out.println("found connected components " + components.length);
		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));
		
		int size = components.length;
		Matrix [] reorderedComponents = new Matrix[size];
		
		int i = 0;
		for (Matrix m : components) {
			reorderedComponents[i] = this.reorderConnectedMatrix(m);
			i++;
		}
		System.out.println("reordered connected components ");
		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));
		
		/// merge component matrices into a single matrix
		// row, column names - check that correct
		
		Matrix matrix = Matrix.mergeComponents(reorderedComponents, originalMatrix.getMax());
		System.out.println("merged components ");
		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));
		
		long endMillies = System.currentTimeMillis();
		System.out.println("total time to reorder: " + (endMillies - startMillis));
		
		return matrix;
	}

	/**
	 * Reorder a single connected component
	 * @param m
	 * @return
	 */
	private Matrix reorderConnectedMatrix(Matrix m) {
		System.out.println(INDENT + "Reordering component of size " + m.getColumnCount());
		
		int size = m.getColumnCount();
		double [][] weightM = createWeightMatrix(size);
		boolean has_improvements = true;
		ArrayList<String []> prevOrders = new ArrayList<String []>();
		prevOrders.add(new String[size]);
		for (int i = 0; i < size; i++) prevOrders.get(0)[i]= String.valueOf(i);//m.getRowName(i);
		
		String [] newRowOrder = new String[size];
		int [] newIntRowOrder = new int[size], newIntColOrder = new int[size];
		int iter = 0;
		double [][] M = m.getData().clone();
		double [][] MT;// = m.getData().clone();
		double [][] cost;
		LinearAssignmentSolver las = new LinearAssignmentSolver();
		GreedyLAP greedy = new GreedyLAP();
		
		System.out.println("Starting reordering");
		long b4, after;
		
		while ( (iter < totalIterCount) && has_improvements) {			
			has_improvements = false;
			b4 = System.currentTimeMillis();
			MT = M.clone();
			after = System.currentTimeMillis();
			print("Cloned: " + (after-b4));
			
			long before = System.currentTimeMillis();
			cost = dot(MT, weightM);
			if (iter < greedyIterCount) {
				// solve with greedy solver
				print(INDENT + "Doing stuff at " + iter + " iteration (greedy)");
				greedy.solve(size, cost, newIntColOrder, newIntRowOrder);
			}
			else {
				// solve optimally
				print(INDENT + "Doing stuff at " + iter + " iteration (opt)");
				las.solve(size, cost, newIntColOrder, newIntRowOrder);
			}
			
			newRowOrder = translate(newIntRowOrder, prevOrders.get(prevOrders.size() - 1));
			if (different(newRowOrder, prevOrders) ) {
				prevOrders.add(newRowOrder);
				has_improvements = true;
				M = robSwap(M, newIntRowOrder);
			}
			after = System.currentTimeMillis();
			print(INDENT + "Iteration took " + (after - before));
			iter++;
		}
		
		String [] col_names = new String[size];
		String [] lastOrder = prevOrders.get(prevOrders.size() - 1);
		for (int i = 0; i < size; i++) {
			col_names[i] = m.getColumnName(Integer.parseInt(lastOrder[i]));
		}
		
		Matrix reordered = new Matrix(M, col_names, col_names.clone());
		
		return reordered;
	}

	private String[] translate(int[] newIntRowOrder, String[] strings) {
		int N = strings.length;
		String [] newRowOrder = new String[N];
		
		for (int i = 0; i < N; i++)
			newRowOrder[i] = strings[newIntRowOrder[i]];
		return newRowOrder;
	}

	private boolean different(String [] newRowOrder, ArrayList<String []> prevOrders) {
		// check that does not repeat
		int N = newRowOrder.length, K = prevOrders.size();
		int diff = 0;
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < N; j++)
				if (!newRowOrder[j].equals(prevOrders.get(i)[j]) ) {
					diff++;
					break;
				}	
		}
		if (diff == K) return true;
		return false;
	}

	private double[][] robSwap(double[][] m, int [] ranks) {
		long b4 = System.currentTimeMillis();
		
		int size = ranks.length, i, j;
		double [][] data = new double[size][size];
		int oldRowIndex, oldColumnIndex;
		
		for (i = 0; i < size; i++) {
			oldRowIndex = ranks[i];
			for (j = 0; j < size; j++) {
				oldColumnIndex = ranks[j];
//				data[i][j] = data[j][i] = originalMatrix.getElement(oldRowIndex, oldColumnIndex);
				//data[oldRowIndex][oldColumnIndex] = m[i][j];
				data[i][j] = m[oldRowIndex][oldColumnIndex];
			}
		}
		long after = System.currentTimeMillis();
		
		System.out.println("Rob swap:" + (after-b4));
		return data;
	}

	/**
	 * Matrix product
	 * @param A
	 * @param B
	 * @return
	 */
	private double[][] dot(double[][] A, double[][] B) {
		long b4 = System.currentTimeMillis();
		int N = A.length;
		double [][] result = new double[N][N];
		int i, j, k;
		double sum;
		for (i = 0; i < N; i++) // row in A 
			for (j = 0; j < N; j++) { // column in B
				sum = 0;
				for (k = 0; k < N; k++) // sum over row and column
					sum += A[i][k] * B[k][j];
				
				result[i][j] = sum;
			}
		long after = System.currentTimeMillis();
		System.out.println("Dot product: " + (after - b4));
		return result;
	}

	private double[][] createWeightMatrix(int N) {
		System.out.println(INDENT + "create weight matrix");
		long begin = System.currentTimeMillis();
		double [][] W = new double[N][N];
		
		for (int i = 0; i < N; i++) {
			for (int j = i; j < N; j++) {
				W[i][j] = W[j][i] = Math.abs(i-j);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("Time to create matrix: " + (end-begin));
		return W;
	}

	private static void print(String s) {
		System.out.println(s);
	}
}
