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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import edu.umd.coral.managers.MatrixManager;
import edu.umd.coral.model.DataModel;

public class SaveMatrixDialog extends JDialog implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7901140272876185495L;

	final public static String ADJ_LIST = "As adjacency list";
	
	final public static String MATRIX = "As matrix";
	
	final public static String DEFAULT_PATH = ".";
	
	final public static String [] SEPARATORS = {"tab", ",", ";", "space"};
	
	final private static String SAVE = "Save";
	
	final private static String CANCEL = "Cancel";
	
	final private static String BROWSE = "Browse...";

	private boolean saveAsMatrix = true;
	
	private String separator = ";";
	
	private File directory = new File(".");
	
	private DataModel _dataModel;
	
	public SaveMatrixDialog(DataModel model, int x, int y) {
		super(model.getMainWindow(), "Save matrix", true);
		
		setLocation(x, y);
		
		_dataModel = model;
		
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		initUI();
	}
	
	private void initUI() {
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel options = new JPanel();
		options.setAlignmentX(LEFT_ALIGNMENT);
		options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
		
		JPanel p1 = new JPanel();
		p1.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Save as"),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)
				));
		
		p1.setAlignmentY(TOP_ALIGNMENT);
		p1.setLayout(new BoxLayout(p1, BoxLayout.Y_AXIS));
		// set up radio buttons
		ButtonGroup group = new ButtonGroup();
		
		JRadioButton rb = new JRadioButton(ADJ_LIST);
		rb.setAlignmentX(LEFT_ALIGNMENT);
		rb.setSelected(!saveAsMatrix);
		rb.setActionCommand(ADJ_LIST);
		rb.addActionListener(this);
		
		p1.add(rb);
		group.add(rb);
		
		rb = new JRadioButton(MATRIX);
		rb.setAlignmentX(LEFT_ALIGNMENT);
		rb.setSelected(saveAsMatrix);
		rb.setActionCommand(MATRIX);
		rb.addActionListener(this);
		
		p1.add(rb);
		group.add(rb);		
		
		options.add(p1);
		
		// separator
		p1 = new JPanel();
		p1.setBorder(BorderFactory.createTitledBorder("Separator"));
		p1.setAlignmentY(TOP_ALIGNMENT);
		
		JComboBox combo = new JComboBox(SEPARATORS);
		combo.setSelectedItem(separator);
		combo.addActionListener(this);
		p1.add(combo);
		
		options.add(p1);
		
		add(options);
		
		// path to save
		options = new JPanel();
		options.setAlignmentX(LEFT_ALIGNMENT);
		options.setLayout(new BoxLayout(options, BoxLayout.Y_AXIS));
		
		JLabel l = new JLabel("Save to:");
		l.setAlignmentX(LEFT_ALIGNMENT);
		options.add(l);
		
		p1 = new JPanel();
		p1.setAlignmentX(LEFT_ALIGNMENT);
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		
		JTextField tf = new JTextField(20);
		tf.setText(DEFAULT_PATH);
		p1.add(tf);
		
		JButton btn = new JButton(BROWSE);
		btn.addActionListener(this);
		p1.add(btn);
		
		options.add(p1);
		
		add(options);
		
		// ok/cancel buttons
		options = new JPanel();
		options.setAlignmentX(LEFT_ALIGNMENT);
		options.setLayout(new BoxLayout(options, BoxLayout.X_AXIS));
		options.add(Box.createHorizontalGlue());
		btn = new JButton(SAVE);
		btn.addActionListener(this);
		options.add(btn);
		
		btn = new JButton(CANCEL);
		btn.addActionListener(this);
		options.add(btn);
		
		add(options);
	}

	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JRadioButton) {
			JRadioButton button = (JRadioButton) source;
			String text = button.getText();
			if (text.equals(MATRIX)) {
				saveAsMatrix  = true;
			}
			else if (text.equals(ADJ_LIST)) {
				saveAsMatrix = false;
			}
		}
		else if (source instanceof JComboBox) {
			JComboBox combo = (JComboBox) source;
			separator = (String) combo.getSelectedItem();
			
			if (separator.equals("tab"))
				separator = "\t";
			else if (separator.equals("space"))
				separator = " ";
		}
		else if (source instanceof JButton) {
			JButton btn = (JButton) source;
			// browse?
			String text = btn.getText();
			if (text.contains(BROWSE)) {
				// open file browser
				openFileBrowser();
			}
			else if (text.contains(SAVE)){
				// ok/save
				save();
			}
			else {
				// cancel - close
				close();
			}
		}
	}
	
	private void openFileBrowser() {
		File f = new File("temp.tmp");
		String path = null;
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// if path is null, points to user's default dir - home folder
		JFileChooser fc = new JFileChooser(path);
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
            directory = fc.getSelectedFile();
            
            
        }
	}
	
	private void save() {
		MatrixManager m;
		
		if (saveAsMatrix)
			m = new MatrixManager(_dataModel, directory, separator, MatrixManager.SAVE_AS_MATRIX);
		else
			m = new MatrixManager(_dataModel, directory, separator, MatrixManager.SAVE_AS_ADJACENCY_LIST);
        m.execute();
        
        close();
	}
	
	private void close() {
		super.setVisible(false);
		this.dispose();
	}
	
	// FOR TESTING ONLY
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the content pane.
        SaveMatrixDialog dialog = new SaveMatrixDialog(null, 100, 100);

        //Display the window.
        dialog.pack();
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
