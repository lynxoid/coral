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
package edu.umd.coral.ui.dialog;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import net.infonode.docking.View;

import edu.umd.coral.clustering.LAPSwingWorker;
import edu.umd.coral.clustering.ReorderingProgressDialog;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Matrix;

public class ReorderMatrixDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8643721408657143633L;
	
    private JButton reorderButton;
    private JButton cancelButton;
    private JSpinner greedyIter;
    private JSpinner optIter;
    private DataModel model;

	/** Creates new form ReorderMatrixDialog */
    public ReorderMatrixDialog(JFrame parent, DataModel model) {
        super(parent, true);
        this.model = model;
        initComponents();
    }

    private void initComponents() {
        SpinnerModel model = new SpinnerNumberModel(
            		this.model.getMatrixGreedyIterations(), //initial value
            		0, //min
            		10, //max
            		1);                //step
        greedyIter = new JSpinner(model);
        
        model = new SpinnerNumberModel(
        		this.model.getMatrixOptIterations(), //initial value
        		0, //min
        		15, //max
        		1);                //step
        optIter = new JSpinner(model);
        
        
        JLabel jLabel1 = new JLabel("greedy iterations");
        JLabel jLabel2 = new JLabel("optimal iterations");
        
        reorderButton = new JButton("Reorder");
        cancelButton = new JButton("Cancel");
        JLabel jLabel3 = new JLabel("Run the algorithm for:");

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reorder matrix");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reorder matrix");
        getContentPane().setLayout(new GridBagLayout());
        
        GridBagConstraints gridBagConstraints;
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(6, 30, 0, 0);
        getContentPane().add(greedyIter, gridBagConstraints);

        jLabel1.setText("greedy iterations");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(18, 10, 0, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText("optimal iterations");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(22, 10, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(10, 30, 0, 0);
        getContentPane().add(optIter, gridBagConstraints);

        reorderButton.setText("Reorder");
        reorderButton.addActionListener(this);
        
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(18, 0, 17, 17);
        getContentPane().add(reorderButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(this);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(18, 75, 17, 0);
        getContentPane().add(cancelButton, gridBagConstraints);

        jLabel3.setText("Run the algorithm for:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(20, 20, 0, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        pack();
    }// </editor-fold>

    private void jButton2ActionPerformed(ActionEvent evt) {
    	this.setVisible(false);
    	this.dispose();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ReorderMatrixDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReorderMatrixDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReorderMatrixDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReorderMatrixDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                ReorderMatrixDialog dialog = new ReorderMatrixDialog(new JFrame(), null);
                dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.reorderButton) {
			if (model != null) {
				int greedy = (Integer)this.greedyIter.getValue();
				int opt = (Integer)this.optIter.getValue();
				model.setMatrixGreedyIterations(greedy);
				model.setMatrixOptIterations(opt);
				model.setHighlightCliques(false);
				model.setOverlayCliques(false);
				// synchronous way of getting the reordered matrix
//				LapFasterReordering lap = new LapFasterReordering(greedy, opt);
//				Matrix m = lap.reorder(model.getCurrentMatrix());
				
				// asynchronous reordering
				// get Matrix window
				View [] views = model.getViews();
				if (views == null || views.length < 1) return;
				JFrame frame = model.getMainWindow();
				final ReorderingProgressDialog dialog = new ReorderingProgressDialog(frame);
				final LAPSwingWorker lapWorker = 
					new LAPSwingWorker(model.getCurrentMatrix(), dialog, greedy, opt);
				
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
							Matrix m;
							try {
								m = lapWorker.get();
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
							}
							ReorderMatrixDialog.this.setVisible(false);
							ReorderMatrixDialog.this.dispose();
						}
					}
				});
				// remove reorder dialog
				setVisible(false);
				dispose();
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
			}
			this.setVisible(false);
			this.dispose();
		}
		else if (e.getSource() == this.cancelButton) {
			this.setVisible(false);
			this.dispose();
		}
	}
}
