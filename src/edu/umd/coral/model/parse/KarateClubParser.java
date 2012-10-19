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
 * Parses custom clustering files into a series of files w/
 * a single clustering per file
 * 
 */

package edu.umd.coral.model.parse;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class KarateClubParser {
	
	public KarateClubParser() {
		
	}
	
	public void parseKarate(String path, String fileName) {
		try {
			BufferedReader input =  new BufferedReader(new FileReader(path + "/" + fileName));
			
			String line;
			BufferedWriter out = null;
			
			while ( (line = input.readLine()) != null ) {
				// parseLine
				if (line.contains("== ")) {
					if (out != null)
						out.close();
					out = parseKarateLine(line, path + "/clusterings");
				}
				else
					parseClusterLine(line, out);
				
			}
			
			if (out != null)
				out.close();
			input.close();
		}
		catch (FileNotFoundException e) {
			
		}
		catch (IOException e) {
			System.out.println("Could not read file");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param line
	 * @throws IOException 
	 */
	private void parseClusterLine(String line, BufferedWriter out) throws IOException {
		if (out == null)
			return; 	// throw an exception?
		
		out.write(line + "\n");
	}

	/**
	 * creates an output file and returns a handle to it
	 * @param line
	 * @return
	 * @throws IOException 
	 */
	private BufferedWriter parseKarateLine(String line, String path) throws IOException {
		String clusteringName;
		String modularity;
		String distance;
		
		String [] parts = line.trim().split(" ");
		
		clusteringName = parts[1];
		modularity = parts[3];
		distance = parts[6];
		
		String fileName = clusteringName + "_" + modularity + "_" + distance; 
		BufferedWriter out =  new BufferedWriter(new FileWriter(path + "/" + fileName));
		
		return out;
	}

	/**
	 * Main to run the parsing
	 * @param argv
	 */
	public static void main(String [] argv) {
		KarateClubParser parser = new KarateClubParser();
		parser.parseKarate("../data/karate_club", "karate_gml_kfast.clust");
	}
}
