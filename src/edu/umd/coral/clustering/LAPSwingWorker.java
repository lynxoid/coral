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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javaasp.LinearAssignmentSolverMTJ;

import javax.swing.SwingWorker;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.VectorEntry;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.SparseVector;
import edu.umd.coral.model.data.BaseMatrix;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.MyMTJMatrix;

public class LAPSwingWorker extends SwingWorker<Matrix, Void> {

	private final String INDENT = "   ";

	private Matrix matrix2Reorder;

	private ReorderingProgressDialog parent;
	
	private int greedyIterCount = 1;
	
	private int totalIterCount = 1;

	private Matrix originalMatrix;

	public LAPSwingWorker(Matrix matrix, ReorderingProgressDialog parent, int greedyIter, int optIter) {
		this.originalMatrix = matrix;
		if (matrix instanceof BaseMatrix) { // reorder by the base
			BaseMatrix base = (BaseMatrix)matrix;
			matrix2Reorder = base.getBaseMatrix();
			
			/*Matrix order = reordering.reorder( base.getBaseMatrix() );
			int s = order.getColumnCount();
			double [][] data = new double[s][s];
			for (int i = 0; i < s; i++) for (int j = i; j < s; j++) data[i][j] = data[j][i] = order.getElement(i, j);
			MyMTJMatrix flexOrder = new MyMTJMatrix(data, order.rowNames, order.columnNames, order.getMax());
			reorderedMatrix = new BaseMatrix(MyMTJMatrix.reorderSubMatrix(base.getDataMatrix(), order), flexOrder);*/
		}
		else this.matrix2Reorder = matrix;
		
		this.parent = parent;
		this.greedyIterCount = greedyIter;
		this.totalIterCount = greedyIter + optIter;
	}

	@Override
	public Matrix doInBackground() throws Exception {
		final int GET_CC = 2;
		final int MERGE_CC = 2;
		
		parent.taskOutput.append("Computing connected components...\n");
		long startMillis = System.currentTimeMillis();
		// get connected components
		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));

		setProgress(0);
		Matrix[] components = Matrix.getConnectedComponents2(matrix2Reorder);
		setProgress(GET_CC);
		parent.taskOutput.append("Found " + components.length + " components\n");

		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));

		int size = components.length;
		Matrix[] reorderedComponents = new Matrix[size];
		
		// estimate the time to reorder all components
		long sizeCubed = 0;
		int n = 0;
		for (Matrix m : components) {
			n = m.getColumnCount();
//			if (n > 3)
			print ("Sizes " + n);
			sizeCubed += Math.pow(n, 3);
		}
		print("All sizes ^3 : " + sizeCubed);

		int i = 0, accumProgress = 0;
		double ratio;
		for (Matrix m : components) {
			n = m.getColumnCount();
			if (n > 3) {
				parent.taskOutput.append("Reordering component, size " + 
						n + "...\n");
				
				print ( (100 - GET_CC - MERGE_CC) + " " + Math.pow(n, 3) + " " + sizeCubed);
				ratio = (100 - GET_CC - MERGE_CC) * Math.pow(n, 3) / sizeCubed;
				reorderedComponents[i] = reorderConnectedMatrix(m, GET_CC  + accumProgress, ratio);
				print( Double.toString(96.0 * Math.pow(n, 3) / sizeCubed) );
				accumProgress += Math.floor((100 - GET_CC - MERGE_CC) * Math.pow(n, 3) / sizeCubed);
				setProgress(GET_CC + accumProgress );
			} else {
				parent.taskOutput.append("Skipping component\n");
				reorderedComponents[i] = m;
			}
				
			i++;
		}
		System.out.println("reordered connected components ");
		System.out.println("total time to reorder conn comp: "
				+ (System.currentTimeMillis() - startMillis));

		// / merge component matrices into a single matrix
		// row, column names - check that correct

		// return null;
		parent.taskOutput.append("Merging reordered components...\n");
		Matrix reorderedMatrix = Matrix.mergeComponents(reorderedComponents, matrix2Reorder.getMax());
		setProgress(100);
		parent.taskOutput.append("Merged\n");
		System.out.println("merged components ");
		System.out.println("current time: " + (System.currentTimeMillis() - startMillis));

		long endMillies = System.currentTimeMillis();
		System.out.println("total time to reorder: " + (endMillies - startMillis));
		
		writeColOrderToFile(reorderedMatrix);
		
		// if if was a base matrix, then need to append the grayed out parts
		if (originalMatrix instanceof BaseMatrix) {
			BaseMatrix originalBase = (BaseMatrix)originalMatrix;
			int N = reorderedMatrix.getColumnCount();
			// create a copy of the base matrix
			double [][] data = new double[N][N];
			for (i = 0; i < N; i++) 
				for (int j = i; j < N; j++) 
					data[i][j] = data[j][i] = reorderedMatrix.getElement(i, j);
			MyMTJMatrix copy = new MyMTJMatrix(data, reorderedMatrix.rowNames, reorderedMatrix.columnNames, reorderedMatrix.getMax());
			reorderedMatrix = new BaseMatrix(MyMTJMatrix.reorderSubMatrix(originalBase.getDataMatrix(), reorderedMatrix), copy);
		}
		
		return reorderedMatrix;
	}
	
	private void writeColOrderToFile(Matrix matrix) {
		try {
			FileWriter writer = new FileWriter("column_order");
			BufferedWriter out = new BufferedWriter(writer);
			String names = "";
			for (String name : matrix.columnNames)
				names += " " + name;
			out.write(names);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void done() {
		parent.taskOutput.append("Done.");
		parent.setCursor(null); //turn off the wait cursor
	}

	/**
	 * 
	 * @param m
	 * @param prevProgress
	 * @param ratio
	 * @return
	 */
	private Matrix reorderConnectedMatrix(Matrix m, int prevProgress, double ratio) {
		print("*******************");
		System.out.println(INDENT + "Reordering component of size "
				+ m.getColumnCount());

		int size = m.getColumnCount();
		parent.taskOutput.append(INDENT + "Computing weight matrix...\n");
		DenseMatrix weightM = createWeightMatrix(size);
		boolean has_improvements = true;
		ArrayList<String[]> prevOrders = new ArrayList<String[]>();
		prevOrders.add(new String[size]);
		for (int i = 0; i < size; i++)
			prevOrders.get(0)[i] = String.valueOf(i);// m.getRowName(i);

		String[] newRowOrder = new String[size];
		int[] newIntRowOrder = new int[size], newIntColOrder = new int[size];
		int iter = 0;

//		parent.taskOutput.append(INDENT + "Get sparse matrix representation...\n");
		FlexCompRowMatrix M = getSparseMatrix(m);
		M.compact();
		FlexCompRowMatrix MT;
		FlexCompRowMatrix cost = new FlexCompRowMatrix(size, size);

		LinearAssignmentSolverMTJ las = new LinearAssignmentSolverMTJ();
		GreedyLAP greedy = new GreedyLAP();
		
		/**
		 * Through empirical testing, we determined the following run times:
		 * 
		 * matrix mult 		  - 34% of time
		 * solving greedy 	  - 4%
		 * solving opt 		  - 15%
		 * rearranging matrix - 12% 
		 */
		double perIteration = ratio / this.totalIterCount;
		print("Ratio " + ratio + " Per iter: " + perIteration + " totalIter " + this.totalIterCount);
		final float GR_MULT = 0.67f;
		final float GR_LAP = 0.08f;
		final float GR_REARR = 0.24f;
		// optimal
		final float OPT_MULT = 0.57f;
		final float OPT_LAP = 0.24f;
		final float OPT_REARR = 0.18f;

		long b4, after;
		boolean did_greedy = false;
		
		if (this.isCancelled()) return null;

		while ((iter < totalIterCount) && has_improvements) {
			has_improvements = false;
			b4 = System.currentTimeMillis();
			MT = (FlexCompRowMatrix) M.copy(); // M.clone();

			parent.taskOutput.append(INDENT + "Multipling matrices...\n");
			mult(MT, weightM, cost, prevProgress, GR_MULT * perIteration);
			if (this.isCancelled()) return null;

			if (iter < greedyIterCount) {
				prevProgress += Math.floor(perIteration * GR_MULT);
				
				setProgress(prevProgress);
				parent.taskOutput.append(INDENT + "Solving LAP using greedy heuristic, iteration " + iter + "...\n");
				print(INDENT + "Doing stuff at GREEDY " + iter + " iteration");
				greedy.solve(size, cost, newIntColOrder, newIntRowOrder);
				prevProgress += Math.floor(perIteration * GR_LAP);
				
				setProgress(prevProgress);
				did_greedy = true;
			} else {
				parent.taskOutput.append(INDENT + "Solving LAP using Hungarian, iteration " + iter + "...\n");
				print(INDENT + "Doing stuff at OPT " + iter + " iteration");
				prevProgress += Math.floor(perIteration * OPT_MULT);
				
				setProgress(prevProgress);
				long beforeOpt = System.currentTimeMillis();
				las.solve(size, cost, newIntColOrder, newIntRowOrder);
				prevProgress += Math.floor(perIteration * OPT_LAP);
				
				setProgress(prevProgress);
				long afterOpt = System.currentTimeMillis();
				System.out.println(INDENT + "   opt iteration: " + (afterOpt - beforeOpt));
				did_greedy = false;
			}
			if (this.isCancelled()) return null;
			parent.taskOutput.append(INDENT + "Rearranging rows and columns...\n");
			newRowOrder = translate(newIntRowOrder,
					prevOrders.get(prevOrders.size() - 1));
			
			if (different(newRowOrder, prevOrders)) {
				prevOrders.add(newRowOrder);
				has_improvements = true;
				M = robSwap(M, newIntRowOrder);
				if (did_greedy)
					prevProgress += Math.floor(perIteration * GR_REARR);
				else
					prevProgress += Math.floor(perIteration * OPT_REARR);
				setProgress(prevProgress);
			}
			if (this.isCancelled()) return null;
			after = System.currentTimeMillis();
			print(INDENT + "Iteration took " + (after - b4) + "ms");
			iter++;
		}

		String[] col_names = new String[size];
		String[] lastOrder = prevOrders.get(prevOrders.size() - 1);
		for (int i = 0; i < size; i++) {
			col_names[i] = m.getColumnName(Integer.parseInt(lastOrder[i]));
		}

		parent.taskOutput.append(INDENT + "Finalizing reordering\n");
		Matrix reordered = new Matrix(M, col_names, col_names.clone());

		return reordered;
	}

	/**
	 * Multiple two sparse matrices
	 * 
	 * @param mT
	 * @param weightM
	 * @param cost
	 * @param ratio 
	 * @param prevProgress 
	 */
	private void mult(FlexCompRowMatrix mT, DenseMatrix weightM,
			FlexCompRowMatrix cost, double prevProgress, double ratio) {
		long before = System.currentTimeMillis();
		// Iterator<MatrixEntry> iter = mT.iterator();
		Iterator<VectorEntry> iter;
		VectorEntry ve;
		SparseVector row;
		int i, j, size = mT.numRows();
		double sum = 0;
		
		double perIter = ratio / (size * size);
		print("Per iter mult " + perIter);

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
				prevProgress += perIter;
				
//				print("mult progress " + prevProgress	);
				setProgress((int)Math.floor(prevProgress));
				if (this.isCancelled()) return;
			}
		}
		long after = System.currentTimeMillis();
		print(INDENT + "Matrix mult: " + (after - before) + "ms");
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
			M.setRow(i, new SparseVector(
					new DenseVector((double[]) m.getRow(i))));
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
		String[] newRowOrder = new String[N];

		for (int i = 0; i < N; i++)
			newRowOrder[i] = strings[newIntRowOrder[i]];
		return newRowOrder;
	}

	private boolean different(String[] newRowOrder,
			ArrayList<String[]> prevOrders) {
		// check that does not repeat
		int N = newRowOrder.length, K = prevOrders.size();
		int diff = 0;
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < N; j++)
				if (!newRowOrder[j].equals(prevOrders.get(i)[j])) {
					diff++;
					break;
				}
		}
		if (diff == K)
			return true;
		return false;
	}

	private FlexCompRowMatrix robSwap(FlexCompRowMatrix m, int[] ranks) {
		// long b4 = System.currentTimeMillis();

		int size = ranks.length, i, j;
		// double [][] data = new double[size][size];
		int oldRowIndex, oldColumnIndex;
		DenseVector dense;
		FlexCompRowMatrix temp = new FlexCompRowMatrix(size, size);

		for (i = 0; i < size; i++) {
			oldRowIndex = ranks[i];
			dense = new DenseVector(size);
			for (j = 0; j < size; j++) {
				oldColumnIndex = ranks[j];
				// data[i][j] = data[j][i] =
				// originalMatrix.getElement(oldRowIndex, oldColumnIndex);
				// data[oldRowIndex][oldColumnIndex] = m[i][j];
				dense.set(j, m.get(oldRowIndex, oldColumnIndex));
				// data[i][j] = m.get(oldRowIndex, oldColumnIndex);
			}
			temp.setRow(i, new SparseVector(dense));
		}
		// long after = System.currentTimeMillis();

		// System.out.println(INDENT + "Rob swap:" + (after-b4));
		return temp;
	}

	private DenseMatrix createWeightMatrix(int N) {
		 System.out.println(INDENT + "create weight matrix");
		 long begin = System.currentTimeMillis();
		double[][] W = new double[N][N];

		for (int i = 0; i < N; i++) {
			for (int j = i; j < N; j++) {
				W[i][j] = W[j][i] = Math.abs(i - j);
			}
		}
		 long end = System.currentTimeMillis();
		 System.out.println(INDENT + "Time to create matrix: " + (end-begin));
		return new DenseMatrix(W);
	}

	private static void print(String s) {
		System.out.println(s);
	}

	public void setIterations(int greedy, int opt) {
		this.greedyIterCount = greedy;
		this.totalIterCount = greedy + opt;
	}

}
