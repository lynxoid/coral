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
package edu.umd.coral.ui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowListener;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.util.DockingUtil;
import no.uib.cipr.matrix.MatrixEntry;
import edu.umd.coral.managers.ClusteringsManager;
import edu.umd.coral.managers.FileParserProgressDialog;
import edu.umd.coral.managers.LooseCliqueFinder;
import edu.umd.coral.managers.MatrixCollapseManager;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.BaseMatrix;
import edu.umd.coral.model.data.BitSymmetricMatrix;
import edu.umd.coral.model.data.Clique;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Matrix;
import edu.umd.coral.model.data.MyMTJMatrix;
import edu.umd.coral.model.parse.EdgeFileParser;
import edu.umd.coral.ui.dialog.AboutDialog;
import edu.umd.coral.ui.dialog.ReorderMatrixDialog;
import edu.umd.coral.ui.dialog.SaveCoresDialog;
import edu.umd.coral.ui.dialog.SaveMatrixDialog;
import edu.umd.coral.ui.dialog.SelectClusteringOrderDialog;
import edu.umd.coral.ui.dialog.SelectColumnsDialog;
import edu.umd.coral.ui.table.ClusteringCompareTableModel;
import edu.umd.coral.ui.table.Vertex2VertexTableModel;

/**
 * CoralMenu sets up the menu bar at the top of the CORAL application window.
 * 
 * @author darya
 * 
 */
public class CoralMenu extends JMenuBar implements ActionListener,
		PropertyChangeListener, DockingWindowListener {

	public static final String CORAL_URL = "http://cbcb.umd.edu/kingsford-group/coral/";

	private static final String ABOUT = "About Coral";

	private static final String GO_TO_WEBPAGE = "Go to Coral webpage";

	private static final String CHOOSE_BASE = "Base";
	
	private static final String REORDER = "Reorder...";

	private static final String OVERLAY_EDGE_COUNTS = "Overlay edge counts";

	private static final String HIGHLIGHT_CORES = "Highlight cores";

	private static final String SEARCH = "Search...";

	private static final String SHOW_ITEM_LABELS = "Show item labels";

	private static final String SHOW_PARTITION_LABELS = "Show partition labels";

	private static final String ORDER = "Order...";

	private static final String SELECT_MODULE_TABLE_COLUMNS = "Select columns for modules";

	private static final String SELECT_VERTEX_TABLE_COLUMNS = "Select columns for pairs";

	private static final String RESET_WINDOWS = "Reset windows";

	private static final String SAVE_FIGURE = "Save figure...";

	private static final String SAVE_CORES = "Save cores...";

	private static final String SAVE_MATRIX = "Save matrix...";

	private DataModel _dataModel;

	private RootWindow rootWindow;

	private JMenuItem[] viewItems;

	/**
	 * generated
	 */
	private static final long serialVersionUID = 3003740848858645782L;

	/**
	 * 
	 * Constants
	 * 
	 */
	public static final String RESET_WINDOWS_EVENT = null;

	private JCheckBoxMenuItem highlightItem;

	private JCheckBoxMenuItem overlayItem;

	private JMenu baseMenu;

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public CoralMenu(DataModel model, RootWindow rootW) {
		super();
		_dataModel = model;
		rootWindow = rootW;
		model.addPropertyChangeListener(DataModel.CLUSTERINGS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.EDGES_CHANGED, this);
		_dataModel.addPropertyChangeListener(
				DataModel.HIGHLIGHT_CLIQUES_CHANGED, this);
		_dataModel.addPropertyChangeListener(DataModel.OVERLAY_CLIQUES_CHANGED,
				this);
		setUpMenu();
	}

	private void setUpMenu() {
		createFileMenu();
		createMatrixMenu();
		createViewMenu();
		createHelpMenu();
		if (_dataModel.isDebug())
			createTestMenu();
	}

	private void createTestMenu() {
		JMenu menu = new JMenu("Test");
		menu.setMnemonic(KeyEvent.VK_T);
		menu.getAccessibleContext().setAccessibleDescription("Test");
		add(menu);

		// a group of JMenuItems
		menu.add(getMenuItem("clusterings 6", KeyEvent.VK_L,
				ActionEvent.CTRL_MASK, "Open a saved session"));
		menu.add(getMenuItem("karate", KeyEvent.VK_K,
				ActionEvent.CTRL_MASK, "Load karate data"));
		
		menu.add(getMenuItem("arabidopsis", KeyEvent.VK_A,
				ActionEvent.CTRL_MASK, "Load arabidopsis data"));
		// fileMenu.add(getMenuItem("small 6", KeyEvent.VK_E,
		// ActionEvent.CTRL_MASK,
		// "Open file containing information on clusterings"));
		menu.add(getMenuItem("dave", KeyEvent.VK_D, ActionEvent.CTRL_MASK,
				"Open a file containing network information"));
		menu.add(getMenuItem("spawn (3s)", KeyEvent.VK_B,
				ActionEvent.CTRL_MASK, "Spawn a test thread"));
	}

	private void createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("File menu");
		add(fileMenu);

		fileMenu.add(getMenuItem("Open clusterings...", KeyEvent.VK_O,
				ActionEvent.CTRL_MASK,
				"Open file containing information on clusterings"));
		fileMenu.add(getMenuItem("Open network...", KeyEvent.VK_N,
				ActionEvent.CTRL_MASK,
				"Open a file containing network information"));
		fileMenu.addSeparator();

		JMenu export = new JMenu("Export");
		export.add(getMenuItem(SAVE_MATRIX, "Save matrix to a file"));
		export.add(getMenuItem(SAVE_CORES, "Save cores to a file"));
//		export.add(getMenuItem(SAVE_FIGURE, "Save visualizations as figures"));

		fileMenu.add(export);
		fileMenu.addSeparator();
		fileMenu.add(getMenuItem("Quit", KeyEvent.VK_Q, ActionEvent.CTRL_MASK,
				"Close the application"));
	}

	private void createViewMenu() {
		JMenu menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menu.getAccessibleContext().setAccessibleDescription("View options");
		// menu.add(getMenuItem("Reset windows", KeyEvent.VK_R,
		// ActionEvent.ALT_MASK, "Reset windows positions"));
		JMenu show = new JMenu("Show");

		View[] views = this._dataModel.getViews();
		viewItems = new JMenuItem[views.length];
		for (int i = 0; i < views.length; i++) {
			final View v = views[i];
			viewItems[i] = new JMenuItem(v.getTitle());
			viewItems[i].setEnabled(v.getRootWindow() == null);
			viewItems[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (v.getRootWindow() != null)
						v.restoreFocus();
					else {
						DockingUtil.addWindow(v, rootWindow);
					}
				}
			});
			show.add(viewItems[i]);
		}

		show.addSeparator();
		show.add(getMenuItem(RESET_WINDOWS, RESET_WINDOWS));
		menu.add(show);

		JMenu items = new JMenu("Items table");
		items.add(getMenuItem(SELECT_VERTEX_TABLE_COLUMNS,
				SELECT_VERTEX_TABLE_COLUMNS));
		menu.add(items);

		JMenu modules = new JMenu("Module table");
		modules.add(getMenuItem(SELECT_MODULE_TABLE_COLUMNS,
				SELECT_MODULE_TABLE_COLUMNS));
		menu.add(modules);

		JMenu ppp = new JMenu("Parallel partitions");
		ppp.add(getMenuItem(ORDER, "Parallel partitions order"));

		JCheckBoxMenuItem l = new JCheckBoxMenuItem(SHOW_PARTITION_LABELS);
		l.setSelected(true);
		// l.setMnemonic(KeyEvent.VK_P);
		l.getAccessibleContext().setAccessibleDescription(
				"Show partition labels in the Parallel Partitions plot");
		l.addActionListener(this);
		ppp.add(l);

		l = new JCheckBoxMenuItem(SHOW_ITEM_LABELS);
		l.setSelected(true);
		// l.setMnemonic(KeyEvent.VK_I);
		l.getAccessibleContext().setAccessibleDescription(
				"Show item labels in the Parallel Partitions plot");
		l.addActionListener(this);
		ppp.add(l);
		menu.add(ppp);

		add(menu);
	}

	private void createMatrixMenu() {
		JMenu fileMenu = new JMenu("Matrix");
		fileMenu.setMnemonic(KeyEvent.VK_M);
		fileMenu.getAccessibleContext().setAccessibleDescription(
				"Matrix operations");
		add(fileMenu);

		fileMenu.add(getMenuItem(
				"Zoom in", 
				KeyEvent.VK_PLUS,
				ActionEvent.SHIFT_MASK, 
				"Zoom in on the matrix"));
		fileMenu.add(getMenuItem("Zoom out", KeyEvent.VK_MINUS,
				ActionEvent.SHIFT_MASK, "Zoom in on the matrix"));

		fileMenu.addSeparator();

		// checkbox to show/hide cliques
		highlightItem = new JCheckBoxMenuItem(HIGHLIGHT_CORES);
		highlightItem.setSelected(false);
		highlightItem.setMnemonic(KeyEvent.VK_H);
		// highlightItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
		// ActionEvent.CTRL_MASK));
		highlightItem.getAccessibleContext().setAccessibleDescription(
				HIGHLIGHT_CORES);
		highlightItem.addActionListener(this);
		fileMenu.add(highlightItem);

		overlayItem = new JCheckBoxMenuItem(OVERLAY_EDGE_COUNTS);
		overlayItem.setEnabled(false);
		overlayItem.setSelected(false);
		// overlayItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_,
		// ActionEvent.CTRL_MASK));
		overlayItem.getAccessibleContext().setAccessibleDescription(
				OVERLAY_EDGE_COUNTS);
		overlayItem.addActionListener(this);
		fileMenu.add(overlayItem);

		fileMenu.addSeparator();

		baseMenu = new JMenu(CHOOSE_BASE);
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem rb = new JRadioButtonMenuItem("None");
		rb.setSelected(true);
		rb.addActionListener(this);
		group.add(rb);
		baseMenu.add(rb);
		baseMenu.addSeparator();
		
		if (_dataModel != null && _dataModel.getClusterings() != null)
			for (Clustering c : _dataModel.getClusterings().values()) {
				rb = new JRadioButtonMenuItem(c.getName());
				rb.setSelected(true);
				group.add(rb);
				baseMenu.add(rb);
			}
		fileMenu.add(baseMenu);
		
		
		
		JMenuItem item = new JMenuItem(REORDER, KeyEvent.VK_R);
		item.addActionListener(this);
		fileMenu.add(item);
	}

	private void createHelpMenu() {
		JMenu fileMenu = new JMenu("Help");
		fileMenu.setMnemonic(KeyEvent.VK_H);
		fileMenu.getAccessibleContext().setAccessibleDescription("Help menu");
		add(fileMenu);

		// a group of JMenuItems
		fileMenu.add(getMenuItem(GO_TO_WEBPAGE, "Open a saved session"));
		// fileMenu.add(getMenuItem("Manual", KeyEvent.VK_M,
		// ActionEvent.CTRL_MASK,
		// "Open file containing information on clusterings"));
		fileMenu.add(getMenuItem(ABOUT, "Version"));
	}

	private JMenuItem getMenuItem(String label, int keyStroke, int mask,
			String desc) {
		JMenuItem menuItem = new JMenuItem(label, keyStroke);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(keyStroke, mask));
		menuItem.getAccessibleContext().setAccessibleDescription(desc);
		menuItem.addActionListener(this);
		return menuItem;
	}

	private JMenuItem getMenuItem(String label, String desc) {
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.getAccessibleContext().setAccessibleDescription(desc);
		menuItem.addActionListener(this);
		return menuItem;
	}

	public void actionPerformed(ActionEvent e) {
		JMenuItem menuItem = (JMenuItem) e.getSource();

		int mnemonic = menuItem.getMnemonic();
		String label = menuItem.getText();

		switch (mnemonic) {
			case KeyEvent.VK_O: // load clustering files
				openClusteringsDialog();
				break;
	
			case KeyEvent.VK_N:
				openNetworkDialog();
				break;
	
			case KeyEvent.VK_PLUS:
				zooomIn();
				break;
	
			case KeyEvent.VK_MINUS:
				zoomOut();
				break;
				
			case KeyEvent.VK_A: // load test clusterings
				loadArabidopsisNetwork();
				break;
				
			case KeyEvent.VK_K:
				loadKarateNetwork();
				break;
	
			case KeyEvent.VK_D:
				loadDavesNetwork();
				break;
	
			case KeyEvent.VK_Q:
				// save props
				Properties props = new Properties();
				if (_dataModel.getLastPath() != null)
					props.put("DEFAULT_PATH", _dataModel.getLastPath());
				FileOutputStream out;
				try {
					out = new FileOutputStream("defaultProperties");
					props.store(out, "---No Comment---");
					out.close();
				} catch (FileNotFoundException e1) {
					System.out.println("Failed to write properties file");
				} catch (IOException e2) {
					System.out.println("Failed to write properties file");
				}
	
				System.exit(1);
				break;
		}

		// file menu
		if (e.getSource() instanceof JRadioButtonMenuItem) {
			JRadioButtonMenuItem btn = (JRadioButtonMenuItem)e.getSource();
			handleBaseClusteringSelection(btn);
		}
		
		if (label.equals(CoralMenu.ABOUT)) {
			showAboutDialog();
		}
		else if (label.equals(CoralMenu.GO_TO_WEBPAGE)) {
			goToCoralPage();
		}
		else if (label.equals(CoralMenu.SAVE_MATRIX)) {
			int x = menuItem.getX();
			int y = menuItem.getY();
			openSaveMatrixDialog(x, y);
		} else if (label.equals(CoralMenu.SAVE_CORES)) {
			saveCores();
		} else if (label.equals(CoralMenu.SAVE_FIGURE)) {
			// TODO: save figures
		}
		// matrix menu
		else if (label.equals(CoralMenu.SEARCH)) {
//			showSearchDialog();
		} else if (label.equals(CoralMenu.HIGHLIGHT_CORES)) {
			highlightCliques();
		} else if (label.equals(CoralMenu.OVERLAY_EDGE_COUNTS)) {
			overlayNetData();
		} else if (label.equals(CoralMenu.REORDER)) {
			showReorderingDialog();
		}
		// view menu
		else if (label.equals(CoralMenu.RESET_WINDOWS)) {
			resetWindows();
		} else if (label.equals(CoralMenu.SELECT_VERTEX_TABLE_COLUMNS)) {
			openSelectTableColumnsDialog(Vertex2VertexTableModel.V2V_TABLE);
		} else if (label.equals(CoralMenu.SELECT_MODULE_TABLE_COLUMNS)) {
			openSelectTableColumnsDialog(ClusteringCompareTableModel.C2C_TABLE);
		} else if (label.equals(CoralMenu.SHOW_ITEM_LABELS)) {
			_dataModel.setShowPItems(((JCheckBoxMenuItem) menuItem)
					.isSelected());
		} else if (label.equals(CoralMenu.SHOW_PARTITION_LABELS)) {
			_dataModel.setShowPLabels(((JCheckBoxMenuItem) menuItem)
					.isSelected());
		} else if (label.equals(CoralMenu.ORDER)) {
			openParParOrderDialog();
		}
	}

	private void saveCores() {
		JFrame frame = _dataModel.getMainWindow();
		Collection<Clique> cliques = _dataModel.getCliques();
		SaveCoresDialog dialog = new SaveCoresDialog(frame, cliques);
		dialog.pack();
		
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}

	private void goToCoralPage() {
		// using this in real life, you'd probably want to check that the desktop
		// methods are supported using isDesktopSupported()...
		String htmlAddr = CORAL_URL; // path to your new file
		try {
			URL url = new URL(htmlAddr);
//			File htmlFile = new File(url.getFile());
			
			if (!Desktop.isDesktopSupported()) {
				System.out.println("Desktop operations (browsing) are not supported.");
				return;
			}
			Desktop.getDesktop().browse(url.toURI());
			// if a web browser is the default HTML handler, this might work too
//			Desktop.getDesktop().open(htmlFile);
			
		} catch (MalformedURLException e1) {
			System.out.println("here1");
			e1.printStackTrace();
		} catch (IOException e) {
			System.out.println("here2");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.out.println("here3");
			e.printStackTrace();
		}	
	}

	private void showAboutDialog() {
		JFrame frame = _dataModel.getMainWindow();
		AboutDialog dialog = new AboutDialog(frame);
		dialog.pack();
		
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}

	private void handleBaseClusteringSelection(JRadioButtonMenuItem btn) {
		String txt = btn.getText();
		if (txt.equals("None")) {
			// selected to show all clusterings - add co-cluster matrices up and
			// preserved the order of the base clustering
			_dataModel.setBaseClustering(null);
			// compute sum of co-cluster matrices
			Matrix cm = _dataModel.getCurrentMatrix();
			Matrix reordered = null;
			if (cm instanceof BaseMatrix)
				reordered = ((BaseMatrix)cm).getDataMatrix();
			else {
				Clustering c = _dataModel.getClusterings().values().iterator().next();
				BitSymmetricMatrix base = c.getCoocurenceMatrix();
				MyMTJMatrix sum = new MyMTJMatrix(base.columnNames, base.columnNames);
				
				for (Clustering clust : _dataModel.getClusterings().values()) {
					sum.addMatrix(clust.getCoocurenceMatrix());
				}
				reordered = Matrix.reorderSubMatrix(sum, cm);
			}
			
			_dataModel.setCurrentMatrix(reordered);
		}
		else {
			Map<String, Clustering> clusterings = _dataModel.getClusterings();
			Clustering c = clusterings.get(txt);
			/* select this clustering as base and only show co-cluster events 
			 * falling inside its clusters */
			_dataModel.setBaseClustering(c);
			BaseMatrix m = computeBaseClustering(c, clusterings, _dataModel.getCurrentMatrix());
			_dataModel.setCurrentMatrix(m);
		}	
	}
	
	private BaseMatrix computeBaseClustering(Clustering c,
			Map<String, Clustering> clusterings, Matrix refM) {
		System.out.println("Computing base clsutering for " + c.getName());
		int s = refM.getColumnCount();
		double [][] data = new double[s][s];
		for (int i = 0; i < s; i++)
			for (int j = i; j < s; j++)
				data[i][j] = data[j][i] = refM.getElement(i, j); 

		MyMTJMatrix ref = new MyMTJMatrix(data, refM.rowNames, refM.columnNames, refM.getMax());

		BitSymmetricMatrix base = c.getCoocurenceMatrix();
		BitSymmetricMatrix filteredMatrix, bsm;
		MyMTJMatrix sum = new MyMTJMatrix(base.columnNames, base.columnNames);
		
		for (Clustering clust : clusterings.values()) {
			bsm = clust.getCoocurenceMatrix();
			// is bms's order different from base's order?
			for (int i = 0; i < base.getColumnCount(); i++) {
				if (bsm.columnNames[i] != base.columnNames[i])
					System.out.println("not equals");
			}
			
			filteredMatrix = BitSymmetricMatrix.filter(base, bsm);
			sum.addMatrix(filteredMatrix);
		}
		MyMTJMatrix baseCont = MyMTJMatrix.reorderSubMatrix(sum, refM);
		
		// calculate stats on how many counts of each type there are in the base
		// clustering
		getStats(c, baseCont);
		
		
		// validate: zeros in refM should stay zeros in reordered
		/*
		int blah = 0;
		for (int i = 0; i < reordered.getColumnCount(); i++)
			for (int j = 0; j < reordered.getColumnCount(); j++)
				if (refM.getElement(i, j) == 0 && reordered.getElement(i, j) != 0)
					blah++;
		if (blah > 0)
			System.out.println("errors: " + blah);
		*/
		
		BaseMatrix baseMatrix = null;
		try {
			baseMatrix = new BaseMatrix(ref, baseCont);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return baseMatrix;
	}

	private void getStats(Clustering clust, MyMTJMatrix baseCont) {
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for (int i = 0; i < this._dataModel.getClusterings().size()+1; i++)
			counts.add(0);
		
		Iterator<MatrixEntry> iter = baseCont.getMatrix().iterator();
		MatrixEntry me;
		int c;
		
		while (iter.hasNext()) {
			me = iter.next();
			c = (int)me.get();
			if (counts.get(c) == null)
				counts.set(c, 1);
			else
				counts.set(c, counts.get(c) + 1);
		}
		
//		System.out.println("Co-cluster for " + clust.getName());
//		// print stats
//		for (int i = 0; i < counts.size(); i++) {
//			System.out.println("Co-clustered " + i + " times: " + counts.get(i) + " pairs");
//		}
	}

	private void loadArabidopsisNetwork() {
		_dataModel.reset();
		
		String baseName = "..\\..\\..\\..\\write\\coral\\arabidopsis\\analysis\\ai_main\\filt_size_pd\\";
		String [] fNames = {"A.Thal_original", 
				"BLONDEL.filt", 
				"CFINDER.filt", "CLAUSET.filt", "MCL.filt", "MCODE.F.filt", 
				"MCODE.filt", "MINE.filt", "SPICi.filt"};

		// replace the slashes w/ system-defined name separator
		baseName = baseName.replace('\\', File.separatorChar);
		int N = fNames.length;
		for (int i = 0; i < N; i++) {
			fNames[i] = baseName + fNames[i];
		}
		
		FileParserProgressDialog dialog = new FileParserProgressDialog(_dataModel, fNames);
		dialog.pack();
		JFrame frame = _dataModel.getMainWindow();
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		
		dialog.setVisible(true);
	}

//	private void showSearchDialog() {
//		System.out.println("Show search dialog");
//		JDialog dialog = new SearchDialog(_dataModel.getMainWindow(), false,
//				_dataModel);
//		dialog.pack();
//		dialog.setVisible(true);
//		dialog.setLocation(100, 100);
//	}

	private void showReorderingDialog() {
		JFrame frame = _dataModel.getMainWindow();
		JDialog dialog = new ReorderMatrixDialog(frame, _dataModel);
		dialog.pack();
		
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		
		dialog.setVisible(true);
	}

	private void zoomOut() {
		float zoom = _dataModel.getZoomValue();
		_dataModel.setZoomValue(zoom - 0.2f);
	}

	private void zooomIn() {
		float zoom = _dataModel.getZoomValue();
		_dataModel.setZoomValue(zoom + 0.2f);
	}

	private void openNetworkDialog() {
//		System.out.println("Open netw dialog");
		JFileChooser fc = new JFileChooser(_dataModel.getLastPath());
		fc.setMultiSelectionEnabled(false);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			EdgeFileParser m = new EdgeFileParser();
			m.parse(f.getAbsolutePath(), _dataModel);
		}
	}

	private void resetWindows() {
		System.out.println("Resetting windows");
		// show all windows in the default positions
		View[] views = this._dataModel.getViews();
		for (View v : views) {
			System.out.println("restoring " + v.getTitle());
			if (v.getRootWindow() != null)
				v.restore();
			else
				DockingUtil.addWindow(v, rootWindow);
		}
	}

	private void openSelectTableColumnsDialog(String tableName) {
		SelectColumnsDialog dialog = null;
		if (tableName.equals(Vertex2VertexTableModel.V2V_TABLE))
			dialog = new SelectColumnsDialog(_dataModel,
					Vertex2VertexTableModel.getColumnNames(), tableName);
		else if (tableName.equals(ClusteringCompareTableModel.C2C_TABLE)) {
			System.out.println("c2c");
			dialog = new SelectColumnsDialog(_dataModel,
					ClusteringCompareTableModel.getColumnNames(), tableName);
		}
		// dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
		JFrame frame = _dataModel.getMainWindow();
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		
		dialog.setVisible(true);
	}

	private void openParParOrderDialog() {
		SelectClusteringOrderDialog dialog = null;

		dialog = new SelectClusteringOrderDialog(_dataModel);
		// dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
		JFrame frame = _dataModel.getMainWindow();
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}

	private void openSaveMatrixDialog(int x, int y) {
		final SaveMatrixDialog dialog = new SaveMatrixDialog(_dataModel, x, y);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.pack();
		
		JFrame frame = _dataModel.getMainWindow();
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x_diag = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y_diag = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x_diag, y_diag);
		dialog.setVisible(true);
	}

//	private void collapseCliques() {
//		System.out.println("Collapsing the universe!");
//		_dataModel.setHighlightCliques(false); // find and collapse
//		_dataModel.setCliques(null);
//		MatrixCollapseManager m = new MatrixCollapseManager(_dataModel);
//		m.execute();
//	}

	private void highlightCliques() {
		_dataModel.setHighlightCliques(!_dataModel.getHighlightCliques());
		if (!_dataModel.getHighlightCliques()) {
			this.overlayItem.setEnabled(false);
			_dataModel.setOverlayCliques(false);
		}

		if (_dataModel.getHighlightCliques()) {
			MatrixCollapseManager m = new MatrixCollapseManager(_dataModel);
			m.execute();
		}
	}

	private void overlayNetData() {
		_dataModel.setOverlayCliques(!_dataModel.getOverlayCliques());
	}

	/**
	 * Opens a dialog to select clusterings
	 */
	private void openClusteringsDialog() {
//		System.out.println("Open Clust Dialog");
		String path = _dataModel.getLastPath();
		if (path == null || path.equals(""))
			path = ".";
		JFileChooser fc = new JFileChooser(path);
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File[] files = fc.getSelectedFiles();
			_dataModel.setLastPath(files[0].getParent());
			ClusteringsManager m = new ClusteringsManager(_dataModel, files);
			m.execute();
		}
	}

	/**
	 * Loads a test network
	 */
//	private void loadTestNetwork() {
//		_dataModel.reset();
//
//		String fileName = "..\\data\\test\\vertex\\cluster_6_clust.csv";
//		fileName = fileName.replace('\\', File.separatorChar);
//
//		VertexFileParser parser = new VertexFileParser();
//		parser.parse(fileName, _dataModel);
//
//		try {
//			fileName = "..\\data\\test\\vertex\\cluster_6_network.txt";
//			fileName = fileName.replace('\\', File.separatorChar);
//
//			EdgeFileParser netParser = new EdgeFileParser();
//			netParser.parse(fileName, _dataModel);
//
//			_dataModel
//					.firePropertyChange(DataModel.NETWORK_LOADED, false, true);
//
//			System.out.println("Loaded test network");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, e.getMessage(),
//					"Data loading error", JOptionPane.ERROR_MESSAGE);
//		}
//	}
	
	private void loadKarateNetwork() {
		_dataModel.reset();
		int N = 10;
		String baseName = "..\\data\\karate_club\\clusterings\\";
		String [] fNames = new String[N];

		// replace the slashes w/ system-defined name separator
		baseName = baseName.replace('\\', File.separatorChar);
		for (int i = 0; i < N; i++) {
			fNames[i] = baseName + ((Integer)i).toString();
		}
		
		FileParserProgressDialog dialog = new FileParserProgressDialog(_dataModel, fNames);
		dialog.pack();
		JFrame frame = _dataModel.getMainWindow();
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		dialog.setVisible(true);
	}

	/**
	 * Loads Dave's dataset
	 */
	private void loadDavesNetwork() {
		_dataModel.reset();

		String[] fileNames = new String[] { "..\\data\\dave\\arthur.mods",
				"..\\data\\dave\\pgs.mods", "..\\data\\dave\\bandyo.mods",
				"..\\data\\dave\\us.mods", };

		// replace the slashes w/ system-defined name separator
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = fileNames[i].replace('\\', File.separatorChar);
		}
		
		FileParserProgressDialog dialog = new FileParserProgressDialog(_dataModel, fileNames);
		dialog.pack();
		JFrame frame = _dataModel.getMainWindow();
		Point p = frame.getLocationOnScreen();
		Rectangle r = frame.getBounds();
		Dimension d = dialog.getPreferredSize();
		int x = p.x + (int)Math.floor((r.width - d.getWidth())/2);
		int y = p.y + (int)Math.floor((r.height - d.getHeight())/2);
		dialog.setLocation(x, y);
		dialog.setVisible(true);

		try {
			boolean load_network = false;
			if (load_network) {
				String fileName = "..\\data\\dave\\chromosome-emap.edg";
				fileName = fileName.replace('\\', File.separatorChar);
				EdgeFileParser netParser = new EdgeFileParser();
				netParser.parse(fileName, _dataModel);
				_dataModel.firePropertyChange(DataModel.NETWORK_LOADED, false,
						true);
			}

			boolean generateCliques = false;
			if (generateCliques) {
				// TODO: disable cliques
				// MaximalCliquesFinder cliqueF = new
				// MaximalCliquesFinder(_dataModel);
				// cliqueF.execute();

				LooseCliqueFinder cliqueLoose = new LooseCliqueFinder(
						_dataModel);
				cliqueLoose.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"Data loading error", JOptionPane.ERROR_MESSAGE);
		}

	}

	// ==========================================================================
	//
	// PropertyChangeListener interface
	//
	// ==========================================================================

	public void propertyChange(PropertyChangeEvent e) {
		String eventName = e.getPropertyName();
		if (eventName.equals(DataModel.HIGHLIGHT_CLIQUES_CHANGED)) {
			highlightItem.setSelected(_dataModel.getHighlightCliques());
			if (_dataModel.hasEdges())
				overlayItem.setEnabled(true);
		} else if (eventName.equals(DataModel.OVERLAY_CLIQUES_CHANGED)) {
			overlayItem.setSelected(_dataModel.getOverlayCliques());
		}
		else if (eventName.equals(DataModel.EDGES_CHANGED)) {
			if (_dataModel.hasEdges() && _dataModel.getHighlightCliques())
				this.overlayItem.setEnabled(true);
			else
				this.overlayItem.setEnabled(false);
		}
		else if (eventName.equals(DataModel.CLUSTERINGS_CHANGED)) {
			baseMenu.removeAll();
			ButtonGroup group = new ButtonGroup();
			JRadioButtonMenuItem rb = new JRadioButtonMenuItem("None");
			rb.setSelected(true);
			rb.addActionListener(this);
			group.add(rb);
			baseMenu.add(rb);
			baseMenu.addSeparator();
			
			if (_dataModel.getClusterings() != null) {
				Map<String, Clustering> map = _dataModel.getClusterings();
				Collection<Clustering> clusterings = map.values();
				List<Clustering> list = new ArrayList<Clustering>(clusterings);
				Collections.sort(list);
				for (Clustering c : list) {
					rb = new JRadioButtonMenuItem(c.getName());
					rb.addActionListener(this);
					rb.setSelected(true);
					group.add(rb);
					baseMenu.add(rb);
				}
			}
//			fileMenu.add(baseMenu);
		}
	}

	// ==========================================================================
	//
	// DockingWindowListener interface
	//
	// ==========================================================================

	public void windowAdded(DockingWindow addedToWindow,
			DockingWindow addedWindow) {
		updateViews(addedWindow, true);
	}

	public void windowRemoved(DockingWindow removedFromWindow,
			DockingWindow removedWindow) {
		updateViews(removedWindow, false);
	}

	public void windowClosing(DockingWindow window)
			throws OperationAbortedException {
		// Confirm close operation
		// if (JOptionPane.showConfirmDialog(frame,
		// "Really close window '" + window + "'?") != JOptionPane.YES_OPTION)
		// throw new OperationAbortedException(
		// "Window close was aborted!");
	}

	public void windowDocking(DockingWindow window)
			throws OperationAbortedException {
	}

	public void windowUndocking(DockingWindow window)
			throws OperationAbortedException {
	}

	/**
	 * Update view menu items and dynamic view map.
	 * 
	 * @param window
	 *            the window in which to search for views
	 * @param added
	 *            if true the window was added
	 */
	private void updateViews(DockingWindow window, boolean added) {
		if (window instanceof View) {
			View[] views = this._dataModel.getViews();
			for (int i = 0; i < views.length; i++)
				if (views[i] == window && viewItems[i] != null)
					viewItems[i].setEnabled(!added);
		} else {
			for (int i = 0; i < window.getChildWindowCount(); i++)
				updateViews(window.getChildWindow(i), added);
		}
	}

	public void viewFocusChanged(View arg0, View arg1) {
	}

	public void windowClosed(DockingWindow arg0) {
	}

	public void windowDocked(DockingWindow arg0) {
	}

	public void windowHidden(DockingWindow arg0) {
	}

	public void windowMaximized(DockingWindow arg0) {
	}

	public void windowMaximizing(DockingWindow arg0)
			throws OperationAbortedException {
	}

	public void windowMinimized(DockingWindow arg0) {
	}

	public void windowMinimizing(DockingWindow arg0)
			throws OperationAbortedException {
	}

	public void windowRestored(DockingWindow arg0) {
	}

	public void windowRestoring(DockingWindow arg0)
			throws OperationAbortedException {
	}

	public void windowShown(DockingWindow arg0) {
	}

	public void windowUndocked(DockingWindow arg0) {
	}
}
