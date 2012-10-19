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
package edu.umd.coral.model.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;

public class VertexFileParser extends Parser {
	private String [] clusteringNames;
	
	public void parse(String fileName, DataModel model) throws Exception {
		if (fileName == null)
			// TODO: throw exception
			return;
		
		clusterings = new HashMap<String, Clustering>();
		vertices = new HashMap<String, Vertex>();
		ArrayList<Vertex>  rowOrdering = new ArrayList<Vertex>();
		
		vertexIndex = 0;
		try {
			long timeBefore = System.currentTimeMillis();
			//File f = new File("blah.txt");
			//System.out.println(f.getAbsolutePath());
			
			String nextLine;
			String [] parts;
			Boolean has_header = true;
			
			File f = new File(fileName);
			InputStreamReader fis = new InputStreamReader( new FileInputStream(f));
			BufferedReader br = new BufferedReader(fis);
			
			while ( (nextLine = br.readLine()) != null) {
				parts = nextLine.split("[\\s,;]");
				if (has_header) {
					parseHeader(parts);
					has_header = false;
				}
				else {
					parseVertexLine(parts, rowOrdering);
				}
			}
			
			for (Clustering c : clusterings.values())
				c.setOriginalVertexCount(c.getVertexCount());
			
			model.setInitialVertexOrder(rowOrdering);
			model.setVertices(vertices);
			model.setClusterings(clusterings);
			// sort alphabetically
			ArrayList<Clustering> ordering = new ArrayList<Clustering>(clusterings.values());
			Collections.sort(ordering); // use clustering default comparator to get an initial ordering
			model.setClusteringOrdering(ordering);
			model.firePropertyChange(DataModel.NETWORK_LOADED, false, true);
			
			// DEBUG
			long timeAfter = System.currentTimeMillis();
			System.out.println("Loaded in " + (timeAfter - timeBefore) + "ms");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	private void parseVertexLine(String[] nextLine, ArrayList<Vertex> rowOrdering) throws Exception {
		String vertexName = nextLine[0].trim();
		Vertex v = new Vertex(vertexName, vertexIndex);
		vertices.put(nextLine[0], v);
		rowOrdering.add(v);
		
		String clusteringName;
		String moduleName;
		Clustering clustering;
		Module m;
		int i;
		for (i = 1; i < nextLine.length; i++) {
			if (nextLine.length > clusteringNames.length + 1) {
				System.out.println("Invalid format. Skipping line: " + nextLine[0]);
			}
			else {
				clusteringName = clusteringNames[i-1];
				clustering = clusterings.get(clusteringName);
				moduleName = nextLine[i].trim();
				if (clustering.hasModule(moduleName) ) {
					m = clustering.getModule(moduleName);
					clustering.addToModule(moduleName, v);
				}	
				else {
					// create a module
					m = new Module(moduleName, clustering);
					m.addVertex(v);
					clustering.addModule(m);
				}
//				v.addClusteringModule(clustering, m);
			}
		}
		
		vertexIndex++;
	}

	// vertex - ignore, clusteringName1, clusteringName2, ...
	private void parseHeader(String[] nextLine) {
		clusteringNames = new String[nextLine.length - 1];
		
		String name;
		Clustering clustering;
		for (int i = 1; i < nextLine.length; i++) {
			name = nextLine[i].trim();
			clustering = new Clustering(name);
			clustering.setColor(ModuleFilesParser.getColor(i-1));
			clusterings.put(name, clustering);
			clusteringNames[i-1] = name;
		}
	}
}
