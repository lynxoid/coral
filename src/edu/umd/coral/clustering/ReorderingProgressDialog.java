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
package edu.umd.coral.clustering;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * task.addPropertyChangeListener(dialog);
 * 
 * 
 * @author lynxoid
 *
 */
public class ReorderingProgressDialog extends JDialog implements PropertyChangeListener {
	/**
	 * Popup JDialog that reports the progress of the reordering
	 * 
	 * @author lynxoid
	 *
	 */
		private static final long serialVersionUID = 1L;
		private JProgressBar progressBar;
		public JButton cancelBtn;
		protected JTextArea taskOutput;
		
		public ReorderingProgressDialog(Frame mainWindow) {
			super((Frame)null, "Reordering progress", true);
		
		    progressBar = new JProgressBar(0, 100);
		    progressBar.setValue(0);
		    progressBar.setStringPainted(true);
		
		    taskOutput = new JTextArea(5, 20);
//		    taskOutput.setMargin(new Insets(5,5,5,5));
		    taskOutput.setEditable(false);
		    
		    cancelBtn = new JButton("Cancel");
		    
		    JPanel topPanel = new JPanel(new BorderLayout());
		    topPanel.add(progressBar, BorderLayout.CENTER);
		    topPanel.add(cancelBtn, BorderLayout.EAST);
		
		    JPanel panel = new JPanel(new BorderLayout());
		    panel.add(topPanel, BorderLayout.NORTH);
		    panel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		    
		    setContentPane(panel);
		    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		    
		    Point p = new Point(0, 0);
			Dimension d = mainWindow.getSize();
			p.x = (int) (d.getWidth() - this.getPreferredSize().getWidth())/2;
			p.y = (int) (d.getHeight() - this.getPreferredSize().getHeight())/2;
			setLocation(p);
		    
		    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//		    task = new Task(matrix, this);
//		    task.addPropertyChangeListener(this);
//		    task.execute();
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
