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
 * Load clusterings
 * 
 * @author dfilippo
 * 
 */

package edu.umd.coral.managers;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.parse.VertexFileParser;

public class ClusteringsManager implements IManager {
	
	private DataModel _dataModel;
	private File [] moduleFiles;
	private File vertexFile;

	public ClusteringsManager(DataModel model, File [] files) {
		_dataModel = model;
		
		// check what type of file they are
		boolean areClusterFiles = checkType(files);
		if (files.length == 1 && !areClusterFiles)
			this.vertexFile = files[0];
		else
			this.moduleFiles = files;

	}
	
	/**
	 * Returns true if all <code>files</code> contain a C as their first line
	 * (are all cluster files, not vertex files)
	 * 
	 * @param files
	 * @return
	 */
	private boolean checkType(File[] files) {
		boolean areClusterFiles = true;
		
		String header;
		for (File f : files) {
			InputStreamReader fis;
			try {
				fis = new InputStreamReader( new FileInputStream(f));
				BufferedReader br = new BufferedReader(fis);
				
				header = br.readLine();
				
				if (header != null) {
					header = header.trim().toLowerCase();
					areClusterFiles = areClusterFiles && header.toLowerCase().startsWith("c");
				}
				
			} catch (FileNotFoundException e) {
				System.out.println("Could not open an input file " + f.getName());
				e.printStackTrace();
				
			} catch (IOException e) {
				System.out.println("Could not read from an input file " + f.getName());
				e.printStackTrace();
			}
			
		}
		
		return areClusterFiles;
	}

	public void execute() {
		if (vertexFile != null) {
			loadVertexFile();
		}
		else if (moduleFiles != null) {
			loadClusteringFiles();
		}
	}

	private void loadClusteringFiles() {
		System.out.println("Parsing clustering files");
		
		try {
			// store file names in an array
			String [] fileNames = new String [moduleFiles.length];
			for (int i = 0; i < fileNames.length; i++){
				fileNames[i] = moduleFiles[i].getCanonicalPath();
			}
			
			FileParserProgressDialog dialog = new FileParserProgressDialog(_dataModel, fileNames);
			dialog.pack();
			JFrame frame = _dataModel.getMainWindow();
			Point p = frame.getLocationOnScreen();
			Rectangle r = frame.getBounds();
			Dimension d = dialog.getPreferredSize();
			int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
			int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
			dialog.setLocation(x, y);
			dialog.setVisible(true);
						
			System.out.println("Loaded " + moduleFiles.length + " clustering files");
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
					e.getMessage(), 
					"Data loading error", 
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
					e.getMessage(), 
					"Data loading error", 
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadVertexFile() {
		try {
			String fileName = vertexFile.getCanonicalPath();
			VertexFileParser parser = new VertexFileParser();
			
			_dataModel.reset();
			parser.parse(fileName, _dataModel);
			// TODO
//			_dataModel.createCooccurenceMatrix();
			System.out.println("Loaded vertex file");
			
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
					e.getMessage(), 
					"Data loading error", 
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, 
					e.getMessage(), 
					"Data loading error", 
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
