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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Vertex;

public class SaveCoresDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -80782277629684058L;
	
	private JTextField textField;
	
	private JButton choose;
	
	private JButton okButton;
	
	private JButton cancelButton;
	
	Collection<Clique> cliques;

	public SaveCoresDialog(JFrame frame, Collection<Clique> cliques) {
		super(frame, "Save cores", true);
		this.cliques = cliques;
		JPanel panel = new JPanel(new BorderLayout());
		
		JPanel onTop = new JPanel(new BorderLayout());
		onTop.add(new JLabel("Save to:"), BorderLayout.LINE_START);
		textField = new JTextField(16);
		onTop.add(textField, BorderLayout.CENTER);
		choose = new JButton("Choose...");
		choose.addActionListener(this);
		onTop.add(choose, BorderLayout.LINE_END);
		panel.add(onTop, BorderLayout.PAGE_START);
		
		onTop = new JPanel();
		onTop.setLayout(new BoxLayout(onTop, BoxLayout.X_AXIS));
		onTop.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		onTop.add(Box.createHorizontalGlue());
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		onTop.add(cancelButton);
		okButton = new JButton("Save");
		okButton.addActionListener(this);
		onTop.add(okButton);
		panel.add(onTop, BorderLayout.PAGE_END);
		getContentPane().add(panel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == choose) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int retVal = chooser.showSaveDialog(this.getOwner());
			if (retVal != JFileChooser.APPROVE_OPTION) return;
			File f = chooser.getSelectedFile();
			String fName = f.getName();
			try {
				if (fName.trim() == "") {
					fName = "cores.txt";
					this.textField.setText(f.getCanonicalPath() + File.separator + fName);	
				}
				else
					this.textField.setText(f.getCanonicalPath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		else if (e.getSource() == okButton) {
			String name = textField.getText();
			saveCliquesToFile(cliques, name);
			setVisible(false);
			dispose();
		}
		else if (e.getSource() == cancelButton) {
			setVisible(false);
			dispose();
		}
	}

	private void saveCliquesToFile(Collection<Clique> cliques, String name) {
		try {
			File f = new File(name);
			System.out.println("Saving cores to " + f.getCanonicalPath());
			BufferedWriter out = new BufferedWriter(new FileWriter(name));
			if (cliques == null) {
				out.close();
				return;
			}
			for (Clique cq : cliques) {
				if ( (cq.getVertices().size() > 1) /*&& 
					 (expectedDens < cq.getQualityValue())*/ ) {
//					out.write( String.valueOf(String.valueOf(expectedDens) + "\t") );
					out.write(cq.getQualityValue() + "\t" + (cq.getInEdges() * 1.0f / cq.getOutEdges() ) + "\t");
					for (Vertex v : cq.getVertices()) {
						out.write(v.getName() + " ");
					}
					out.write("\n");
				}
			}
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String argv[]) {
		SaveCoresDialog diag = new SaveCoresDialog(new JFrame(), null);
		diag.pack();
		diag.setVisible(true);
	}
}
