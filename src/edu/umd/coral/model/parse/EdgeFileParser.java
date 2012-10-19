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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Edge;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;

public class EdgeFileParser extends Parser{
	
	/**
	 *  format: vertex1 <delim> vertex2 - have an edge between them
	 *  assume there is no header and the very first line has an edge
	 * 	@param fileName
	 * 	@param model
	 */
	public void parse(String fileName, DataModel model){
		try {
			long timeBefore = System.currentTimeMillis();

			File f = new File(fileName);
			InputStreamReader fis = new InputStreamReader( new FileInputStream(f));
			BufferedReader br = new BufferedReader(fis);
			
			Set<Edge<Vertex>> edges = new HashSet<Edge<Vertex>>();
			Map<String, Vertex> vertices = model.getVertices();
			String line;
			
			//Might need to check if the file is empty
			while ( (line = br.readLine()) != null) {
				line = line.trim();
				parseLine(line, edges, vertices);
			}
			
//			System.out.println("Parsed edges " + edges.size());
			if (edges.size() == 0)
				System.out.println("No edges in the edge file.");
			else {
				model.setEdges(edges);
				model.setHasEdges(true);
				
			}
			
//			writeNNF(model);
			
			long timeAfter = System.currentTimeMillis();
			System.out.println("parsed network file: " + edges.size()); 
			System.out.println("Loaded in " + (timeAfter - timeBefore) + "ms");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// generate NNF file for Cytoscape for each of the clusterings
	@SuppressWarnings("unused")
	private void writeNNF(DataModel model) throws IOException {
		Map<String, Clustering> map = model.getClusterings();
		
		for (Map.Entry<String, Clustering> entry : map.entrySet()) {
			// open file
			Clustering c = entry.getValue();
			File f = new File(c.getName() + ".nnf");
			OutputStreamWriter fis = new OutputStreamWriter( new FileOutputStream(f));
			BufferedWriter br = new BufferedWriter(fis);
			
			// serialize clustering to file
			
			// clust_name vert1 pp vert2
			Vertex u, v;
			int i, j;
			Collection<Module> modules = c.getModules();
			
			for (Module m : modules) {
				// print(m.getName());
				Collection<Vertex> vertices = m.getVertices();
				ArrayList<Vertex> vertList = new ArrayList<Vertex>(vertices);
				int len = vertList.size();
				for (i = 0; i < len; i++) {
					u = vertList.get(i);
					for (j = i+1; j < len; j++) {
						v = vertList.get(j);
//						print("get neight between " + u.getName() + ", " + v.getName());
//						print(u.getName() + ": " + u.getNeighbors().toString());
//						print(v.getName() + ": " + v.getNeighbors().toString());
						if (u.hasNeighbor(v)) {
							br.write(m.getName() + " " + u.getName() + " pp " + v.getName() + "\n");
						}
						}
				}
			}
			
			// edges between clusters2clusters
			ArrayList<Module> moduleList = new ArrayList<Module>(modules);
			Module m1, m2;
			ArrayList<Vertex> vert1, vert2;
			int modLen = moduleList.size();
			boolean foundMatch = false;
			for (i = 0; i < modLen; i++) {
				m1 = moduleList.get(i);
				vert1 = new ArrayList<Vertex>(m1.getVertices());
				if (m1.getSize() < 2) continue;
				
				for (j = i+1; j < modLen; j++) {
					m2 = moduleList.get(j);
					if (m2.getSize() < 2) continue;
					vert2 = new ArrayList<Vertex>(m2.getVertices());
					foundMatch = false;
					// figure if have an overlap or an edge/edges between them.um
					for (int k = 0; k < vert1.size() && !foundMatch; k++) {
						u = vert1.get(k);
						for (int l = 0; l < vert2.size() && !foundMatch; l++) {
							v = vert2.get(l);
							if (u.equals(v) || u.hasNeighbor(v)) {
								br.write("big_net" + " " + m1.getName() + " im " + m2.getName() + "\n");
								foundMatch = true;
							}
						}
					}
				}
			}
			
			// edges between clusters and free nodes
			
			// close
			br.close();
		}
	}

	// TODO: make sure we're getting only unique edges
	// i.e. (u,v) is the same as (v,u) - should be added just once, no directionality
	private void parseLine(String line, Set<Edge<Vertex>> edges, Map<String, Vertex> vertices)throws IOException {
		line = line.trim();
		String [] parts = line.split("[(\\s+),;]");
		if (parts.length < 2) return;
		
//		for (String s : parts)
//			System.out.print(s + "|");
//		System.out.println();
		
		Vertex u = vertices.get(parts[0]);
		Vertex v = vertices.get(parts[1]);
		
//		System.out.println(u + " " + v);
		
		if (u == null || v == null) return;
		Edge<Vertex> e = new Edge<Vertex>(u, v);
		u.addNeighbor(v);
		v.addNeighbor(u);
		edges.add(e);
	}
	
	public static void print(String s) {
//		System.out.println(s);
	}

}
