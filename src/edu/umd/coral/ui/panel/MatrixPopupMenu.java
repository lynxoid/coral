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
package edu.umd.coral.ui.panel;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;

import edu.umd.coral.clustering.LAPSwingWorker;
import edu.umd.coral.clustering.ReorderingProgressDialog;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Matrix;

public class MatrixPopupMenu extends JPopupMenu implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7173525782297146939L;
	
	private DataModel model;
	
	private final String REORDER = "Reorder current matrix";

	public MatrixPopupMenu(DataModel model) {
		this.model = model;
		JMenuItem menuItem = new JMenuItem(REORDER);
	    menuItem.addActionListener(this);
	    add(menuItem);
	}

	public void actionPerformed(ActionEvent ev) {
		JMenuItem source = (JMenuItem)ev.getSource();
		
		// reorder matrix
		String text = source.getText();
		if (text.equals(REORDER)) {
			int greedy = model.getMatrixGreedyIterations();
			int opt = model.getMatrixOptIterations();
			
			// asynchronous reordering
			// get Matrix window
			JFrame frame = model.getMainWindow();
			final ReorderingProgressDialog dialog = new ReorderingProgressDialog(frame);
			
			// get the matrix
			Matrix localMatrix = model.getCurrentMatrix();
			final LAPSwingWorker lapWorker = 
				new LAPSwingWorker(localMatrix, dialog, greedy, opt);
			
			dialog.cancelBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Attempting to cancel reordering");
					lapWorker.cancel(true);
					dialog.setVisible(false);
					dialog.dispose();
				}
			});
			
			// dialog needs to listen to the thread to print updates
			lapWorker.addPropertyChangeListener(dialog);
			// also want to listen to the state changes here - and when the 
			// state is DONE, update the matrix value
			lapWorker.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals("state") && 
							e.getNewValue().equals(SwingWorker.StateValue.DONE)) {
						LAPSwingWorker lapWorker = (LAPSwingWorker)e.getSource();
						try {
							Matrix m = lapWorker.get();
							dialog.setVisible(false);
							dialog.dispose();
							model.setCurrentMatrix(m);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						} catch (ExecutionException e1) {
							e1.printStackTrace();
						}
						catch (CancellationException e1) {
							System.out.println("Got a cancellation exception");
							lapWorker.done();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						MatrixPopupMenu.this.setVisible(false);
					}
				}
			});
			// remove tis dialog
			setVisible(false);
			
			// show progress dialog
			dialog.pack();
			// put in the center
			
			Point p = frame.getLocationOnScreen();
			Rectangle r = frame.getBounds();
			Dimension d = dialog.getPreferredSize();
			int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
			int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
			dialog.setLocation(x, y);
			lapWorker.execute();
			dialog.setVisible(true);
			
		/*
			MatrixReordering reordering = model.getReordering();
			if (reordering instanceof LapFasterReordering) {
				((LapFasterReordering)reordering).setIterations(
						model.getMatrixGreedyIterations(), 
						model.getMatrixOptIterations());
			}
			Matrix localMatrix = model.getCurrentMatrix();
			try {
				Matrix reorderedMatrix = null;
				if (localMatrix instanceof BaseMatrix) { // reorder by the base
					BaseMatrix base = (BaseMatrix)localMatrix;
					Matrix order = reordering.reorder( base.getBaseMatrix() );
					int s = order.getColumnCount();
					double [][] data = new double[s][s];
					for (int i = 0; i < s; i++) for (int j = i; j < s; j++) data[i][j] = data[j][i] = order.getElement(i, j);
					MyMTJMatrix flexOrder = new MyMTJMatrix(data, order.rowNames, order.columnNames, order.getMax());
					reorderedMatrix = new BaseMatrix(MyMTJMatrix.reorderSubMatrix(base.getDataMatrix(), order), flexOrder);
				}
				else
					reorderedMatrix = reordering.reorder(localMatrix);
				model.setCurrentMatrix(reorderedMatrix);
				//System.out.println("reodered current matrix");
			} catch (IndexOutOfBoundsException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}*/
		}
//		else if (text.equals("Save as *.png")) {
//			Calendar c = Calendar.getInstance();
//			String name = 	c.get(Calendar.MONTH) + "_" + 
//							c.get(Calendar.DATE) + "_" + 
//							c.get(Calendar.YEAR) + " " +
//							c.get(Calendar.HOUR) + ":" +
//							c.get(Calendar.MINUTE) + ":" +
//							c.get(Calendar.SECOND) + 
//						"_matrix.png";
//			
//			try {
//			    File outputfile = new File(name);
//			    ImageIO.write(image, "png", outputfile);
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
	}

}
