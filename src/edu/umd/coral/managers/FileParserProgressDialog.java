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
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.umd.coral.managers;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Cooccurrence;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.model.parse.FormatException;
import edu.umd.coral.model.parse.Parser;

public class FileParserProgressDialog extends JDialog implements 
                                        PropertyChangeListener {
 
	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
    private JTextArea taskOutput;
    private Task task;
    private DataModel model;
    private String [] paths;
    
    public FileParserProgressDialog(DataModel model, String [] paths) {
    	super(model.getMainWindow(), "Loading files", true);
        
        this.model = model;
        this.paths = paths;
 
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
 
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(progressBar, BorderLayout.PAGE_START);
        panel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        
        setContentPane(panel);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        Point p = new Point(0, 0);
		Dimension d = model.getMainWindow().getSize();
		p.x = (int) (d.getWidth() - this.getPreferredSize().getWidth())/2;
		p.y = (int) (d.getHeight() - this.getPreferredSize().getHeight())/2;
		setLocation(p);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        task = new Task(model);
        task.addPropertyChangeListener(this);
        task.execute();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    
 
    class Task extends SwingWorker<Matrix, Void> {
    	
    	public Task(DataModel model) {
    	}
    	
    	
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Matrix doInBackground() throws Exception {
            //Initialize progress property.
            setProgress(0);
            taskOutput.append("Resetting the views...\n");
            model.reset();
            setProgress(10);
            taskOutput.append("Parsing input files...\n");

    		ModuleFilesParser parser = new ModuleFilesParser();
    		boolean hasOverlaps = parser.parse(paths, model, this, 10);
    		setProgress(20);
    		
    		if (hasOverlaps == true) {
    			taskOutput.append("Overlapping modules: some comparison metrics may be scewed or inflated due to overlapping modules.\n");
    			// TODO: pop a message?
    		}
    		taskOutput.append("Creating a co-cluster matrix...\n");
    		Matrix matrix = null;
    		try {
    			matrix = createCoclusterMatrix();
    		} catch (Exception e) {
    			e.printStackTrace();
    			JOptionPane.showMessageDialog(null, e.getMessage(),
    					"Data loading error", JOptionPane.ERROR_MESSAGE);
    		}
    		setProgress(80);
    		model.setOriginalMatrix(matrix);
    		taskOutput.append("Updating parallel partitions plot...\n");
    		setProgress(90);
			model.setCurrentMatrix(matrix);
    		setProgress(100);
    		return matrix;
        }
        
        private Matrix createCoclusterMatrix() /*throws Exception*/ {
			// for each clustering, create a co-occurrence matrix
			int size = model.getVertices().size();
			Matrix matrix = new Matrix(size, size);
			
			Map<Vertex, TreeMap<Vertex, Cooccurrence>> hashMap;
			Map<Vertex, TreeMap<Vertex, Cooccurrence>> allCooccurr = null;
			Collection<Clustering> set = model.getClusterings().values();
			int clustCount = set.size(), done = 0;
			
//			long before = System.currentTimeMillis();
			for (Clustering c : set) {
				hashMap = c.createCooccurenceMatrix(model.getInitialVertexOrder());
				done++;
				setProgress( (int) Math.floor(20 + 40.0 * done / clustCount) );
				allCooccurr = mergeInto(allCooccurr, hashMap);
				if (c.getCoocurenceMatrix().getColumnCount() == size) {
					matrix = matrix.addMatrix(c.getCoocurenceMatrix());
				}
				else {
//					throw new Exception("Sizes don't match - data is not formatted correctly");
				}
			}
//			long after = System.currentTimeMillis();
//			System.out.println("added matrices, T=" + (after-before) + "ms");
			taskOutput.append("Added co-cluster matrices\n");
			
			// fix up signatures
			taskOutput.append("Calculating co-cluster signatures...\n");
			Set<Vertex> keys = allCooccurr.keySet();
			Collection<Cooccurrence> entries;
			for (Vertex u : keys) {
				entries = allCooccurr.get(u).values();
				for (Cooccurrence cooccur : entries) {
					cooccur.setSignature(model.getClusteringOrdering());
				}
			}
			taskOutput.append("Updating pairs table...\n");
			model.setVertexPairs(allCooccurr);
			setProgress(70);
			
			return matrix;
		}
        
        /**
		 * 
		 * 
		 * @param allCooccurr
		 * @param hashMap
		 */
		private Map<Vertex, TreeMap<Vertex, Cooccurrence>> mergeInto(
				Map<Vertex, TreeMap<Vertex, Cooccurrence>> allCooccurr,
				Map<Vertex, TreeMap<Vertex, Cooccurrence>> hashMap) {
			
			if (allCooccurr == null)
				return hashMap;
			
			for (Vertex u : hashMap.keySet()) {
				TreeMap<Vertex, Cooccurrence> newEntries = hashMap.get(u);
				TreeMap<Vertex, Cooccurrence> allMap = allCooccurr.get(u);
				
				if (allMap == null) {
					allCooccurr.put(u, newEntries);
				}
				else
					for (Vertex v: newEntries.keySet()) {
						Cooccurrence cooccur = allMap.get(v);
						if (cooccur == null) {
							allMap.put(v, newEntries.get(v));
						}
						else {
							// combine w/ existing co-occurrence
							Set<Clustering> set = newEntries.get(v).getClusterings();
							cooccur.addClusterings(set);
						}
					}
			}
			return allCooccurr;
		}
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
//            startButton.setEnabled(true);
        	boolean gotException = true;
        	Throwable cause = null;
        	try {
				get();
				gotException = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				cause = e.getCause();
//				System.out.println("Cause " + e.getCause());
//				System.out.println("Loc msg " + e.getLocalizedMessage());
//				System.out.println("Msg " + e.getMessage());
//				e.printStackTrace();
			}
            setCursor(null); //turn off the wait cursor
            progressBar.setIndeterminate(false);
            
            if (!gotException) {
            	taskOutput.append("Done!\n");
            	setVisible(false);
                dispose();
            }
            else {
//            	System.out.println("Got exception");
            	String msg = "Could not complete calculation. Parsing will abort.";
            	String addlMsg = "";
            	
            	if (cause instanceof OutOfMemoryError)
            		addlMsg = "\nTry increasing the default Java heap size.";
            	
            	taskOutput.append(msg);
            	JOptionPane.showMessageDialog(null, 
            			msg + "\nCause:  " + 
            			cause.toString() + 
            			addlMsg);
            	model.reset();
            	dispose();
            }   
        }
        
        private class ModuleFilesParser extends Parser {
        	
        	public static final String GRAB_BAG = "__misc__";
        	
        	public final Color [] band_colors = {
        			/*new Color(27, 158, 119),
        			new Color(217, 95, 2),
        			new Color(117, 112, 179),
        			new Color(231, 41, 138),
        			new Color(102, 166, 30),
        			new Color(230, 171, 2),
        			new Color(166, 118, 29),
        			new Color(102, 102, 102)*///,
//        			Color.getHSBColor(h, s, b)
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
//        		new Color(37, 52, 148).brighter(),
//        		new Color(18, 39, 98),
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
        	public Color getColor(int i) {
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
        	 * @param worker TODO
        	 * @return 
        	 * @throws Exception 
        	 */
        	public boolean parse(String [] fileNames, DataModel model, Task worker, int prevProgress) throws Exception {
        		int i;
        		String line;
        		Clustering c;
        		Module module;
        		String clusteringName;
        		
        		long timeBefore = System.currentTimeMillis();
        		ArrayList<Vertex>  initialOrder = new ArrayList<Vertex>();
        		boolean hasOverlapping = false;
        		
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
        				if (c.hasOverlap()) hasOverlapping = true;
        				worker.setProgress(prevProgress + (int) (10.0 * i / fileNames.length) );
//        				worker.firePropertyChange(propertyName, oldValue, newValue);
        				c.setOriginalVertexCount(vertexCount);
//        				System.out.println("loaded " + clusteringName + " clustering: " + vertexCount + " vertices");
        				
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
        							module.addVertex(u);
        						}
        					}
        					if (module.getSize() > 0) // if module is empty, then did not need to create it
        						clustering.addModule(module);
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
        			// check that all clusterings are now of the same length
//        			for (Clustering clu : clusterings.values()) {
//        				System.out.println(clu.getVertexCount() + " " + clu.getOriginalVertexCount()); 
//        			}
        			model.setInitialVertexOrder(initialOrder);
        			model.setVertices(vertices);
        			model.setClusterings(clusterings);
        			
        			// sort alphabetically
        			ArrayList<Clustering> ordering = new ArrayList<Clustering>(clusterings.values());
        			Collections.sort(ordering); // use clustering default comparator to get an initial ordering
        			model.setClusteringOrdering(ordering);
        			model.firePropertyChange(DataModel.NETWORK_LOADED, false, true);
        			
        			// DEBUG
        			if (model.isDebug()) {
        				long timeAfter = System.currentTimeMillis();
        				System.out.println("FILEWORKER: Time to load data: " + (timeAfter - timeBefore ) + "ms");
        			}
        			
        		} catch (FileNotFoundException e) {
        			System.out.println("File not found");
        			e.printStackTrace();
        		} catch (IOException e2) {
        			System.out.println("IOException");
        			e2.printStackTrace();
        		}  catch (NullPointerException e3) {
        			System.out.println("null pointer");
        			e3.printStackTrace();
        		}
        		
        		return hasOverlapping;
        	}

        	/**
        	 * Assumes one line is all vertices in one cluster. cluster name - first 
        	 * word on the line.
        	 * @param c
        	 * @param line
        	 * @throws Exception 
        	 * @throws FormatException 
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
        			System.out.println("Module line too short: " + line);
        			throw new FormatException(c.getName(), "", line);
        		}
        		
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
    }
 
    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }
}
