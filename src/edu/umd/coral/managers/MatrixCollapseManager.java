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
package edu.umd.coral.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.MyMTJMatrix;
import edu.umd.coral.model.data.Vertex;

// TODO: is this used?
public class MatrixCollapseManager extends Manager {

	public MatrixCollapseManager(DataModel m) {
		super(m);
	}

	/**
	 * find cliques
	 */
	
	public void execute() {
		Matrix matrix = getModel().getCurrentMatrix();
		Map<String, Clustering> map = getModel().getClusterings();
		if (map == null)
			return;
		int max = map.size();
		Map<String, Vertex> vertices = getModel().getVertices();
		
		// find all cliques
		CliqueManager manager = getModel().getCliqueManager();
		String name;
		Vertex v;
		
		
		long before = System.currentTimeMillis();
		ArrayList<Clique> cliques = manager.getCliques(matrix, false);
		// translate cliques from indices to vertices
		for (Clique c: cliques) {
			for (int i : c.getIndices()) {
				name = matrix.getColumnName(i);
				v = vertices.get(name);
				c.addVertex(v);
			}
		}
		long after = System.currentTimeMillis();
		
		System.out.println("Time to compute cliques: " + (after - before));
		
		// compute new collapsed matrix and substitute the current matrix w/ new matrix
		if (getModel().getHighlightCliques()) {
			//Collection<Clique> cliqueMatrix = cliques;//buildCliqueMatrix(cliques, matrix, max);
			getModel().setCliques(cliques);
		}
		else {
			MyMTJMatrix collapsedMatrix = generateCollapsedMatrix(cliques, matrix, max);
			getModel().setCurrentMatrix(collapsedMatrix);
		}
		// TODO: keep matrix history - ArrayList<Matrix> - or save into a binary file
	}

	private MyMTJMatrix generateCollapsedMatrix(ArrayList<Clique> cliques, Matrix matrix, int max) {
		Clique columnClq, rowClq;
		int i, j;
		int newSize = cliques.size();
		double[][] data = new double[newSize][newSize];
		float weight;
		
		String [] names = new String[newSize];
		
		for (i = 0; i < newSize; i++) {
			data[i][i] = max;
			columnClq = cliques.get(i);
			names[i] = columnClq.getAggregateName();
			
			// fill the column above this clique
			for (j = 0; j < i; j++) {
				rowClq = cliques.get(j);
				weight = computeEdgeWeight(rowClq, columnClq, matrix);
				data[j][i] = weight;
				// symmetric matrix
				data[i][j] = weight;
			}
		}
		
		return new MyMTJMatrix(data, names, names, max);
	}

	/*
	private Matrix buildCliqueMatrix(ArrayList<Clique> cliques, Matrix matrix, int max) {
		Collection<Vertex> vertices;
		int size = matrix.getColumnCount();
		float [][] data = new float[size][size];
		
		int indexU, indexV;
		
		for (Clique q : cliques) {
			vertices = q.getVertices();
			for (Vertex u : vertices) {
				indexU = matrix.getColumnIndex(u.getName());
				for (Vertex v : vertices) {
					indexV = matrix.getColumnIndex(v.getName());
					data[indexU][indexV] = data[indexV][indexU] = max;
				}
			}
		}
		return new Matrix(data, matrix.rowNames, matrix.rowNames);
	}
	*/
	
	private float computeEdgeWeight(Clique rowClique, Clique columnClique, Matrix m) {
		float weight = 0;
		Collection<Vertex> rowVert = rowClique.getVertices();
		Collection<Vertex> colVert = columnClique.getVertices();
		
		int row, column;
		for (Vertex u : rowVert) {
			row = u.getIndex();
			for (Vertex v : colVert) {
				column = v.getIndex();
				weight += m.getElement(row, column);
			}
		}
		weight = weight / rowVert.size() / colVert.size();
		
		return weight;
	}

	/*
	private float getBinomial(int n, int m) {
		if (n < m)
			return 1;
		int [] b = new int[n+1];
		b[0] = 1;
		for (int i = 1; i < n+1; i++) {
			b[i] = 1;
			for (int j = i - 1; j > 0; --j)
				b[j] += b[j-1];
		}
		return b[m];
	}
	*/
}
