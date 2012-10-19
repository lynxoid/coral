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
package edu.umd.coral.ui.tabs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.umd.coral.managers.ModuleOrderManager;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.Module;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.panel.HasSaveableImage;
import edu.umd.coral.ui.panel.ZoomableParallelSets;

public class ParallelSetsPanel extends JPanelExt implements ComponentListener, 
		PropertyChangeListener, HasSaveableImage, AdjustmentListener  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7175354079615188081L;
	
	private ZoomableParallelSets vis;
	
	private PPDataModel ppdm;
	
	/**
	 * 
	 * @param model
	 */
	public ParallelSetsPanel(DataModel model) {
		super(model);
		setLayout(new BorderLayout());
//		setBackground(Color.RED);
		
		ppdm = new PPDataModel(this);
		ppdm.addPropertyChangeListener(PPDataModel.SELECTED_VERTICES_CHANGED, this);
//		vis = new ParallelSets(ppdm);
		vis = new ZoomableParallelSets(ppdm);
		vis.setShowPartitionItems(model.getShowPItems());
		vis.setShowPartitionLabels(model.getShowPLabels());
		JScrollPane pane = new JScrollPane(vis);
		pane.getHorizontalScrollBar().addAdjustmentListener(this);
		pane.getVerticalScrollBar().addAdjustmentListener(this);
		pane.addComponentListener(this);
		add(pane);
		
//		model.addPropertyChangeListener(DataModel.ORIGINAL_MATRIX_CHANGED, this);
//		model.addPropertyChangeListener(DataModel.CLUSTERING_ORDERING_CHANGED, this);
		model.addPropertyChangeListener(DataModel.MODULE_SPACING_CHANGED, this);
		model.addPropertyChangeListener(DataModel.MATRIX_CHANGED, this);
		model.addPropertyChangeListener(DataModel.CLUSTERINGS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SEARCH_ITEM_CHANGED, this);
		model.addPropertyChangeListener(DataModel.METRIC_SCORES_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SHOW_P_ITEMS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SHOW_P_LABELS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.CLUSTERING_PP_ORDERING_CHANGED, this);
	}
	
	
	
	
	//==========================================================================
	//
	// ComponentChangeListener implementation
	//
	//==========================================================================

	
	public void componentHidden(ComponentEvent e) {		
	}

	
	public void componentMoved(ComponentEvent e) {
	}

	
	public void componentResized(ComponentEvent e) {
		vis.revalidate();
		vis.repaint();
	}

	
	public void componentShown(ComponentEvent e) {
	}

	//==========================================================================
	//
	// PropertyChangeListener implementation
	//
	//==========================================================================
	
	
	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		
//		if (name.equals(DataModel.METRIC_SCORES_CHANGED) ) {
//			if (_dataModel.getSelectedMetric() != 
//				MetricFactory.getMetricInstance(MetricFactory.JACCARD)) return;
//			
//			System.out.println("updating clust order " + _dataModel.getSelectedMetric());
//			ArrayList<Clustering> axisOrdering = _dataModel.getClusteringOrdering();
////			System.
//			
//			if (axisOrdering == null || _dataModel.getCurrentMatrix() == null) {
//				vis.setOrderings(null);
//			}
//			else {
//				ModuleOrderManager mom = new ModuleOrderManager();
//				Matrix m = _dataModel.getCurrentMatrix();
//				List<ArrayList<Module>> moduleOrdering = new ArrayList<ArrayList<Module>>();
//				for (Clustering c : axisOrdering) {
//					moduleOrdering.add(mom.getOrdering(c, m.columnNames, 2));
//				}
//				vis.setOrderings(moduleOrdering);
//			}
//			
//			// copy axis ordering from the global dataModel to local
//			_dataModel.setPPClusteringOrdering(axisOrdering);
//			vis.setCollection(axisOrdering);
//			
////			vis.invalidateData();// TODO: do not redraw clusterings
//		}
//		else 
		if (name.equals(DataModel.MODULE_SPACING_CHANGED)) {
			vis.setModuleSpacing(_dataModel.getModuleSpacing());
		}
		else if (name.equals(DataModel.CLUSTERINGS_CHANGED) ||
			name.equals(DataModel.MATRIX_CHANGED)) {
			
			ArrayList<Clustering> axisOrdering = _dataModel.getClusteringOrdering();
			if (axisOrdering == null || _dataModel.getCurrentMatrix() == null) {
				vis.setOrderings(null);
			}
			else {
				ModuleOrderManager mom = new ModuleOrderManager();
				Matrix m = _dataModel.getCurrentMatrix();
				List<ArrayList<Module>> moduleOrdering = new ArrayList<ArrayList<Module>>();
				for (Clustering c : axisOrdering) {
					moduleOrdering.add(mom.getOrdering(c, m.columnNames, 2));
				}
				vis.setOrderings(moduleOrdering);
			}
			
			// copy axis ordering from the global dataModel to local
			_dataModel.removePropertyChangeListener(DataModel.CLUSTERING_PP_ORDERING_CHANGED, this);
			_dataModel.setPPClusteringOrdering(axisOrdering);
			_dataModel.addPropertyChangeListener(DataModel.CLUSTERING_PP_ORDERING_CHANGED, this);
			
			vis.setCollection(axisOrdering);
			vis.invalidateData();
		}
		else if (name.equals(DataModel.SEARCH_ITEM_CHANGED)) {
			Vertex u = _dataModel.getSearchItem();
			ArrayList<Vertex> a = new ArrayList<Vertex>();
			a.add(u);
			vis.setSelectedVertices(a);
		}
		else if (name.equals(DataModel.SELECTED_VERTICES_CHANGED)) {
			Collection<Vertex> collection = _dataModel.getSelectedVertices();
			vis.setSelectedVertices(collection);
		}
		else if (name.equals(DataModel.SHOW_P_LABELS_CHANGED)) {
			vis.setShowPartitionLabels(_dataModel.getShowPLabels());
		}
		else if (name.equals(DataModel.SHOW_P_ITEMS_CHANGED)) {
			vis.setShowPartitionItems(_dataModel.getShowPItems());
		}
		else if (name.equals(PPDataModel.SELECTED_VERTICES_CHANGED)) {
//			System.out.println("Updating sel vert from PP, size " + ppdm.getSelectedVertices().size());
			_dataModel.removePropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
			_dataModel.setSelectedVertices(ppdm.getSelectedVertices());
			_dataModel.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		}
		else if (name.equals(DataModel.CLUSTERING_PP_ORDERING_CHANGED)) {
			
			ArrayList<Clustering> axisOrdering = _dataModel.getPPClusteringOrdering();
			if (axisOrdering == null || _dataModel.getCurrentMatrix() == null) {
				vis.setOrderings(null);
			}
			else {
				ModuleOrderManager mom = new ModuleOrderManager();
				Matrix m = _dataModel.getCurrentMatrix();
				List<ArrayList<Module>> moduleOrdering = new ArrayList<ArrayList<Module>>();
				for (Clustering c : axisOrdering) {
					moduleOrdering.add(mom.getOrdering(c, m.columnNames, 2));
				}
				vis.setOrderings(moduleOrdering);
			}
			vis.setCollection(_dataModel.getPPClusteringOrdering());
			vis.invalidateData();
		}
		
//		vis.invalidateData();
		vis.revalidate();
		vis.repaint();
	}
	
	
	
	//==========================================================================
	//
	// Testing only
	//
	//==========================================================================
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
	 * @throws Exception 
     */
    private static void createAndShowGUI() throws Exception {
        //Create and set up the window.
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(300, 300);
        
        DataModel dm = new DataModel(frame);
        dm.setShowPItems(true);
//        dm.setShowPLabels(true);
        ParallelSetsPanel psp = new ParallelSetsPanel(dm);        
        
        Map<String, Clustering> clusterings = makeClusterings();
        ArrayList<Clustering> list = new ArrayList<Clustering>();
        for (Clustering c : clusterings.values()) {
        	list.add(c);
        }
        
        double [][] data = {{},{},{},{},{},{},{},{}};
        String [] names = {"c", "a", "b", "x", "d", "f", "g", "e"};
        Matrix m = new Matrix(data, names, names);
        dm.setOriginalMatrix(m);
        dm.setCurrentMatrix(m);
        dm.setClusteringOrdering(list);
        
        psp.vis.setCollection(list);
        List<ArrayList<Module>> moduleOrdering = new ArrayList<ArrayList<Module>>();
        ModuleOrderManager mom = new ModuleOrderManager();
        for (Clustering c : list)
        	moduleOrdering.add(mom.getOrdering(c, names, 2));
//         psp.getModuleOrderings(list);
        		
		psp.vis.setOrderings(moduleOrdering);
		
		ArrayList<Vertex> coll = new ArrayList<Vertex>();
        coll.add(list.get(0).getVertices()[0]);
        coll.add(list.get(0).getVertices()[1]);
        dm.setSelectedVertices(coll);
        
        frame.getContentPane().setPreferredSize(new Dimension(300,300));
        frame.getContentPane().add(psp);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
//        psp.vis.invalidateData();
    }

    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					createAndShowGUI();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }
    
    /**
     * make fake clusterings
     * 
     * @return
     * @throws Exception 
     */
    private static Map<String, Clustering> makeClusterings() throws Exception {
    	 Map<String, Clustering> clusterings = new HashMap<String, Clustering>();
         
         Clustering c1 = new Clustering("clust2");
         
         c1.setColor(new Color(27, 158, 119));
         Module m = new Module("m1", c1);
         
         Vertex v = new Vertex("a");
         m.addVertex(v);
         v = new Vertex("b");
         m.addVertex(v);
         v = new Vertex("x");
         m.addVertex(v);
         v = new Vertex("c");
         m.addVertex(v);
         v = new Vertex("d");
         m.addVertex(v);
         c1.addModule(m);
         
         m = new Module("m2", c1);
         
         v = new Vertex("e");
         m.addVertex(v);
         v = new Vertex("f");
         m.addVertex(v);
         v = new Vertex("g");
         m.addVertex(v);
         c1.addModule(m);
         c1.setOriginalVertexCount(8);
         
         clusterings.put("clust2", c1);
         
         
         c1 = new Clustering("clust1");
         c1.setColor(new Color(217, 95, 2));
         m = new Module("m1", c1);
         
         v = new Vertex("a");
         m.addVertex(v);
         v = new Vertex("b");
         m.addVertex(v);
         v = new Vertex("d");
         m.addVertex(v);
         c1.addModule(m);
         
         m = new Module("m2", c1);
         v = new Vertex("c");
         m.addVertex(v);
         v = new Vertex("e");
         m.addVertex(v);
         c1.addModule(m);
         
         m = new Module("m3", c1);
         v = new Vertex("f");
         m.addVertex(v);
         v = new Vertex("g");
         m.addVertex(v);
         c1.addModule(m);
         c1.setOriginalVertexCount(7);
         
         clusterings.put("clust1", c1);
         
//         c1 = new Clustering("clust3");
//         m = new Module("m1");
//         
//         v = new Vertex("a");
//         m.addVertex(v);
//         v = new Vertex("b");
//         m.addVertex(v);
//         v = new Vertex("e");
//         m.addVertex(v);
//         v = new Vertex("c");
//         m.addVertex(v);
//         
//         
//         c1.addModule(m);
//         
//         m = new Module("m2");
//         v = new Vertex("d");
//         m.addVertex(v);
//         v = new Vertex("f");
//         m.addVertex(v);
//         
//         c1.addModule(m);
//         c1.setOriginalVertexCount(6);
//         
//         clusterings.put("clust3", c1);
         
         return clusterings;
    }




	public RenderedImage getImage() {
		return this.vis.getImage();
	}

	//--------------------------------------------------------------------------
	//
	// Adjustment listener ofr scroll bars
	//
	//--------------------------------------------------------------------------
	public void adjustmentValueChanged(AdjustmentEvent ev) {
		// TODO Auto-generated method stub
		vis.updateLabels();
	}
	
}
