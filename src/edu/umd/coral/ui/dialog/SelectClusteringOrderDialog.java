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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectClusteringOrder.java
 *
 * Created on Nov 8, 2011, 7:40:42 PM
 */
package edu.umd.coral.ui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.ui.control.ReorderableJList;

/**
 *
 * @author lynxoid
 */
public class SelectClusteringOrderDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -6611940567034627127L;
	
	private DataModel model;
	
	/** Creates new form SelectClusteringOrder */
    public SelectClusteringOrderDialog(DataModel model) {
    	this.model = model;
    	
        initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jLabel1 = new JLabel();
//        jScrollPane1 = new JScrollPane();
        
        jList1 = new ReorderableJList();
		DefaultListModel defModel = new DefaultListModel();
		jList1.setModel(defModel);
		
//		String[] listItems = { "Chris", "Joshua", "Daniel", "Michael", "Don",
//				"Kimi", "Kelly", "Keagan" };
		ArrayList<Clustering> clusts = model.getPPClusteringOrdering();
		if (clusts != null) {
			Iterator<Clustering> iter = clusts.iterator();
			while (iter.hasNext())
				defModel.addElement(iter.next());
		}

		// show list
		jScrollPane1 = new JScrollPane(jList1,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		JFrame frame = new JFrame("Checkbox JList");
//		frame.getContentPane().add(scroller);
//		frame.pack();
//		frame.setVisible(true);
		
        
        okButton = new JButton();
        cancelButton = new JButton();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Drag the labels to change their order:");

//        jList1.setModel(new AbstractListModel() {
//            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
//            public int getSize() { return strings.length; }
//            public Object getElementAt(int i) { return strings[i]; }
//        });
        jScrollPane1.setViewportView(jList1);

        okButton.setText("Ok");
        okButton.addActionListener(this);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(this);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jLabel1, GroupLayout.Alignment.LEADING))
                        .addContainerGap(82, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addGap(18, 18, 18)
                        .addComponent(okButton)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    
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
            java.util.logging.Logger.getLogger(SelectClusteringOrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SelectClusteringOrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SelectClusteringOrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SelectClusteringOrderDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                SelectClusteringOrderDialog dialog = new SelectClusteringOrderDialog(null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify
    private JButton okButton;
    private JButton cancelButton;
    private JLabel jLabel1;
    private JList jList1;
    private JScrollPane jScrollPane1;
    // End of variables declaration

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton jButts = (JButton)e.getSource();
			if (jButts.getText().equals("Ok")) {
				
				ArrayList<Clustering> blah = new ArrayList<Clustering>();
				for (int i = 0; i < model.getPPClusteringOrdering().size(); i++) {
					blah.add((Clustering)jList1.getModel().getElementAt(i));
				}
				System.out.println("save clust ordering: " + blah);
				model.setPPClusteringOrdering(blah);
			}
			else {
				System.out.println("Ignore ordering");
			}
			close();
		}
	}
	
	private void close() {
		super.setVisible(false);
		this.dispose();
	}
}
