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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;

public class ModuleFilesParser extends Parser {
	
	public static final String GRAB_BAG = "__misc__";
	
	public final static Color [] band_colors = {
			/*new Color(27, 158, 119),
			new Color(217, 95, 2),
			new Color(117, 112, 179),
			new Color(231, 41, 138),
			new Color(102, 166, 30),
			new Color(230, 171, 2),
			new Color(166, 118, 29),
			new Color(102, 102, 102)*///,
//			Color.getHSBColor(h, s, b)
		new Color(141, 211, 199),
		new Color(254, 224, 144),
		new Color(190, 186, 218),
		new Color(128, 177, 211),
		new Color(253, 180, 98),
		new Color(179, 222, 105),
		new Color(252, 205, 229),
		new Color(217, 217, 217),
		new Color(191, 129, 45),
		new Color(128, 205, 193),
		new Color(53, 151, 143),
		new Color(254, 178, 76),
//		new Color(37, 52, 148).brighter(),
//		new Color(18, 39, 98),
		new Color(128, 115, 172),
		new Color(178, 171, 210),
		new Color(224, 130, 20),
		new Color(90, 174, 97),
		new Color(27, 120, 55),
		new Color(153, 112, 171)
	};
	
	/**
	 * Get color for a band from an array of predefined colors 
	 * 
	 * @param i
	 * @return
	 */
	public static Color getColor(int i) {
		return band_colors[i % band_colors.length]; 
	}

	/**
	 * Module file format:
	 * 
	 * module_name1 v1 v2 v3 v4 ...
	 * module_name2 v5 v6 ...
	 * module_name3 v7 ...
	 * ...
	 * module_name99 ... 
	 * 
	 * @param fileNames
	 * @param model
	 * @throws Exception 
	 */
	public void parse(String [] fileNames, DataModel model) throws Exception {
		int i;
		String line;
		Clustering c;
		Module module;
		String clusteringName;
		
		long timeBefore = System.currentTimeMillis();
		ArrayList<Vertex>  initialOrder = new ArrayList<Vertex>();
		
		vertexIndex = 0;
		try {
			// for each file
			for (i = 0; i < fileNames.length; i++) {
				vertexCount = 0;
				
				File f = new File(fileNames[i]);
				InputStreamReader fis = new InputStreamReader( new FileInputStream(f));
				BufferedReader br = new BufferedReader(fis);
				
				if (f.getName().endsWith(".txt"))
					clusteringName = f.getName().replace(".txt", "");
				else if (f.getName().endsWith(".clust"))
					clusteringName = f.getName().replace(".clust", "");
				else
					clusteringName = f.getName();
				
				c = new Clustering(clusteringName);
				c.setColor(getColor(i));
				
				clusterings.put(clusteringName, c);
				
				// get all modules, vertices
				while ( (line = br.readLine()) != null) {
					line = line.trim();
					
					parseModule(c, line, initialOrder);
				}
//				worker.firePropertyChange(propertyName, oldValue, newValue);
				c.setOriginalVertexCount(vertexCount);
//				System.out.println("loaded " + clusteringName + " clustering: " + vertexCount + " vertices");
				
				br.close();
				fis.close();
			}
			
			// put all unclustered vertices into one "misc" cluster
			if (model.getAggregateSingletons()) {
				for (Clustering clustering : clusterings.values()) {
					clustering.recordOriginalModuleCount();
					clusteringName = clustering.getName();
					module = new Module(GRAB_BAG, clustering);
					for (Vertex u : vertices.values()) {
						// if vertex is not present in this clustering
						if (!clustering.hasVertex(u)) {
							// add a "self-module"
							System.out.println("added a vertex to grab bag");
							module.addVertex(u);
						}
					}
					if (module.getSize() > 0) { // if no vert were added to grab bag - did not need it
						System.out.println("added grab bag to " + clustering.getName() + " size " + module.getSize());
						clustering.addModule(module);
					}
				}
			}
			else {
				String vertexName;
				// pad clusterings w/ singleton vertices that are not in the clustering originally
				for (Clustering clustering : clusterings.values()) {
					clustering.recordOriginalModuleCount();
					clusteringName = clustering.getName();
					for (Vertex u : vertices.values()) {
						// if vertex is not present in this clustering
						if (!clustering.hasVertex(u)) {
							// add a "self-module"
							vertexName = u.getName();
							module = new Module(vertexName + "_singl", true, clustering);
							module.addVertex(u);
							// add to this clustering
							clustering.addModule(module);
						}
					}
				}
			}
			
			// check that all clusterings ar enow of hte same length
			for (Clustering clu : clusterings.values()) {
				System.out.println(clu.getVertexCount() + " " + clu.getOriginalVertexCount()); 
			}
			
			model.setInitialVertexOrder(initialOrder);
			model.setVertices(vertices);
			model.setClusterings(clusterings);
			
			
			// sort alphabetically
			ArrayList<Clustering> ordering = new ArrayList<Clustering>(clusterings.values());
			Collections.sort(ordering); // use clustering default comparator to get an initial ordering
			model.setClusteringOrdering(ordering);
			model.firePropertyChange(DataModel.NETWORK_LOADED, false, true);
			
			// DEBUG
			long timeAfter = System.currentTimeMillis();
			System.out.println("Time to load data: " + (timeAfter - timeBefore ) + "ms");
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		} catch (IOException e2) {
			System.out.println("IOException");
			e2.printStackTrace();
		}  catch (NullPointerException e3) {
			System.out.println("null pointer");
			e3.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assumes one line is all vertices in one cluster. cluster name - first 
	 * word on the line.
	 * @param c
	 * @param line
	 * @throws Exception 
	 */
	private void parseModule(Clustering c, String line,
			ArrayList<Vertex> initialOrder) throws Exception {
		String[] columns;
		int j;
		String moduleName; 
		Module module;
		String vertexName;
		Vertex v;
		
		columns = line.split("[ \t\b\n\f\r]");
		
		if (columns.length < 2) {
			System.out.println("bad module");
			throw new FormatException(c.getName(), "", line);
		}
		
		System.out.println("-----");
		for (String part : columns) System.out.print(part + "**");
		System.out.println("-----");
		
		moduleName = columns[0].trim();
		module = new Module(moduleName, c);
				
		// skip first item - it was a module name
		for (j = 1; j < columns.length; j++) {
			vertexName = columns[j].trim();
			if (vertices.containsKey(vertexName)) {
				// already parsed this vertex in other clusterings
				v = vertices.get(vertexName);
				if (!c.containsVertex(v))
					vertexCount++;
			}
			else {
				// new vertex
				v = new Vertex(vertexName, vertexIndex);
				vertices.put(vertexName, v);
				vertexIndex++;
				vertexCount++;
				initialOrder.add(v);
			}
			module.addVertex(v);
		}
		c.addModule(module);
		
	}		
}
