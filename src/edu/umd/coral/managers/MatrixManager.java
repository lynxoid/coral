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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Matrix;

public class MatrixManager extends Manager {
	
	private static final String MATRIX_NAME = "co_occurr_matrix";
	
	private static final String NODE_FILE_NAME = "node_list";
	
	public static final int SAVE_AS_MATRIX = 1;
	
	public static final int SAVE_AS_ADJACENCY_LIST = 2;	
	
	private File directory;
	
	private String delimiter;
	
	private int mode;

	public MatrixManager(DataModel dataModel, File directory, String d, int mode) {
		super(dataModel);
		
		this.directory = directory;
		this.delimiter = d;
		this.mode = mode;
	}

	public void execute() {
		if (getModel().getCurrentMatrix() == null) {
			System.out.println("Save matrix: matrix is null");
			return;
		}
		
		if ( (mode & SAVE_AS_MATRIX) > 0) {
			saveAsMatrix();
		}
		if ( (mode & SAVE_AS_ADJACENCY_LIST)>>1 > 0) {
			saveAsAdjacencyList();
		}
	}

	private void saveAsAdjacencyList() {
		try {
			String path = directory.getCanonicalPath();
			
			Matrix currentMatrix = getModel().getCurrentMatrix();
		    
		    saveNodesNames(currentMatrix.columnNames, delimiter, path);
			
		    Calendar time = Calendar.getInstance();
		    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		    String label = format.format(time.getTime());
		    
		    BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator + MATRIX_NAME + "_" + label + ".csv"));
		    currentMatrix.saveToFileAsAdjacencyList(out, delimiter);
		    
		    System.out.println("Done: coccurrence matrix save to " + path + File.separator + MATRIX_NAME + "_" + label + ".csv");
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	private void saveNodesNames(String [] columnNames, String d, String path) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator + NODE_FILE_NAME + ".txt"));
			int i = 0;
			for (String s : columnNames) {
				out.write(String.valueOf(i));
				out.write(d);
				out.write(s);
				out.write("\n");
				i++;
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveAsMatrix() {
		try {
			String path = directory.getCanonicalPath();
		    BufferedWriter out = new BufferedWriter(new FileWriter(path + File.separator + MATRIX_NAME + ".txt"));
		    Matrix currentMatrix = getModel().getCurrentMatrix();
		    currentMatrix.saveToFileAsCSVMatrix(out, delimiter);
		    System.out.println("Done: coccurrence matrix save to " + path + File.separator + MATRIX_NAME + ".txt");
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
}
