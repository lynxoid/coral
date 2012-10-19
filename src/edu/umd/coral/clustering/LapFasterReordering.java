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
import java.util.Iterator;

import javaasp.LinearAssignmentSolverMTJ;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import edu.umd.coral.model.data.Matrix;

public class LapFasterReordering implements MatrixReordering {
	
	private final String INDENT = "   ";
	
	private int greedyIterCount = 1;
	
	private int totalIterCount = 1;
	
	public LapFasterReordering(int greedyIter, int optIter) {
		this.greedyIterCount = greedyIter;
		this.totalIterCount = greedyIter + optIter;
	}

	public Matrix reorder(Matrix originalMatrix) {
		System.out.println("reordering a matrix");
		
		// get connected components
		Matrix [] components = Matrix.getConnectedComponents2(originalMatrix);
		System.out.println("found connected components " + components.length);		
		int size = components.length;
		Matrix [] reorderedComponents = new Matrix[size];
		
		int i = 0;
		for (Matrix m : components) {
			reorderedComponents[i] = this.reorderConnectedMatrix(m);
			i++;
		}
		Matrix matrix = Matrix.mergeComponents(reorderedComponents, originalMatrix.getMax());
		return matrix;
	}

	/**
	 * Reorder a single connected component
	 * @param m
	 * @return
	 */
	private Matrix reorderConnectedMatrix(Matrix m) {
		print("*******************");
		System.out.println(INDENT + "Reordering component of size " + m.getColumnCount());
		
		int size = m.getColumnCount();
		DenseMatrix weightM = createWeightMatrix(size);
		boolean has_improvements = true;
		ArrayList<String []> prevOrders = new ArrayList<String []>();
		prevOrders.add(new String[size]);
		for (int i = 0; i < size; i++) prevOrders.get(0)[i]= String.valueOf(i);//m.getRowName(i);
		
		String [] newRowOrder = new String[size];
		int [] newIntRowOrder = new int[size], newIntColOrder = new int[size];
		int iter = 0;
		
		FlexCompRowMatrix M = getSparseMatrix(m);
		M.compact();
		FlexCompRowMatrix MT; 
		FlexCompRowMatrix cost = new FlexCompRowMatrix(size, size); 
		
		LinearAssignmentSolverMTJ las = new LinearAssignmentSolverMTJ();
		GreedyLAP greedy = new GreedyLAP();
		
		long b4, after;
		
		while ( (iter < totalIterCount) && has_improvements) {			
			has_improvements = false;
			b4 = System.currentTimeMillis();
			MT = (FlexCompRowMatrix)M.copy(); // M.clone();
			
			mult(MT, weightM, cost);
			
			if (iter < greedyIterCount) {
				// solve with greedy solver
				print(INDENT + "Doing stuff at GREEDY " + iter + " iteration");
				greedy.solve(size, cost, newIntColOrder, newIntRowOrder);
			}
			else {
				// solve optimally
				print(INDENT + "Doing stuff at OPT " + iter + " iteration");
				las.solve(size, cost, newIntColOrder, newIntRowOrder);
			}
			
			newRowOrder = translate(newIntRowOrder, prevOrders.get(prevOrders.size() - 1));
			if (different(newRowOrder, prevOrders) ) {
				prevOrders.add(newRowOrder);
				has_improvements = true;
				M = robSwap(M, newIntRowOrder);
			}
			after = System.currentTimeMillis();
			print(INDENT + "Iteration took " + (after - b4) +"ms");
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


	/**
	 * Multiple two sparse matrices
	 * 
	 * @param mT
	 * @param weightM
	 * @param cost
	 */
	private void mult(FlexCompRowMatrix mT, DenseMatrix weightM,
			FlexCompRowMatrix cost) {
		long before = System.currentTimeMillis();
//		Iterator<MatrixEntry> iter = mT.iterator();
		Iterator<VectorEntry> iter;
		VectorEntry ve;
		SparseVector row;
		int i, j, size = mT.numRows();
		double sum = 0;
		
		for (i = 0; i < size; i++) {
			row = mT.getRow(i);
			for (j = 0; j < size; j++) {
				sum = 0;
				// iterate over the matrix -- it may be sparse
				iter = row.iterator();
				while (iter.hasNext()) {
					ve = iter.next();
					sum += ve.get() * weightM.get(j, ve.index());
				}
				cost.set(i, j, sum);  
			}
		}
		long after = System.currentTimeMillis();
		print(INDENT + "Matrix mult: " + (after - before) +"ms");
	}
	

	/**
	 * 
	 * @param m
	 * @return
	 */
	private FlexCompRowMatrix getSparseMatrix(Matrix m) {
		int size = m.getRowCount();
		FlexCompRowMatrix M = new FlexCompRowMatrix(size, size);
		
		for (int i = 0; i < size; i++)
			M.setRow(i,  new SparseVector(new DenseVector((double[])m.getRow(i))));
		M.compact();
		return M;
	}

	/**
	 * 
	 * @param newIntRowOrder
	 * @param strings
	 * @return
	 */
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

	private FlexCompRowMatrix robSwap(FlexCompRowMatrix m, int [] ranks) {
//		long b4 = System.currentTimeMillis();
		
		int size = ranks.length, i, j;
//		double [][] data = new double[size][size];
		int oldRowIndex, oldColumnIndex;
		DenseVector dense;
		FlexCompRowMatrix temp = new FlexCompRowMatrix(size, size);
		
		for (i = 0; i < size; i++) {
			oldRowIndex = ranks[i];
			dense = new DenseVector(size);
			for (j = 0; j < size; j++) {
				oldColumnIndex = ranks[j];
//				data[i][j] = data[j][i] = originalMatrix.getElement(oldRowIndex, oldColumnIndex);
				//data[oldRowIndex][oldColumnIndex] = m[i][j];
				dense.set(j, m.get(oldRowIndex, oldColumnIndex));
//				data[i][j] = m.get(oldRowIndex, oldColumnIndex);
			}
			temp.setRow(i, new SparseVector(dense));
		}
//		long after = System.currentTimeMillis();
		
//		System.out.println(INDENT + "Rob swap:" + (after-b4));
		return temp;
	}

	private DenseMatrix createWeightMatrix(int N) {
//		System.out.println(INDENT + "create weight matrix");
//		long begin = System.currentTimeMillis();
		double [][] W = new double[N][N];
		
		for (int i = 0; i < N; i++) {
			for (int j = i; j < N; j++) {
				W[i][j] = W[j][i] = Math.abs(i-j);
			}
		}
//		long end = System.currentTimeMillis();
//		System.out.println(INDENT + "Time to create matrix: " + (end-begin));
		return new DenseMatrix(W);
	}

	private static void print(String s) {
		System.out.println(s);
	}

	public void setIterations(int greedy,
			int opt) {
		this.greedyIterCount = greedy;
		this.totalIterCount = greedy + opt;
	}
}
