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

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.PatternSyntaxException;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Cooccurrence;
import edu.umd.coral.model.data.Vertex;
import edu.umd.coral.ui.JPanelExt;
import edu.umd.coral.ui.table.Vertex2VertexTableModel;
import edu.umd.coral.ui.table.Vertex2VertexTableSorter;
import edu.umd.coral.ui.table.renderer.CooccurCellRenderer;
import edu.umd.coral.ui.table.renderer.HistogramCellRenderer;

public class Vertex2VertexPanel extends JPanelExt 
	implements PropertyChangeListener, ListSelectionListener, ActionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6715557762733072669L;
	
	private JTextField sigFilter;
	
	private JLabel countLabel;
	
	private Vertex2VertexTableModel tableModel;
	
	private Vertex2VertexTableSorter sorter;
	
	private JTable table;
	
	public Vertex2VertexPanel(DataModel model) {
		super(model);
		
//		model.addPropertyChangeListener(DataModel.ORIGINAL_MATRIX_CHANGED, this);
		model.addPropertyChangeListener(DataModel.VERTEX_PAIRS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.VERTEX_TABLE_COLUMNS_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		model.addPropertyChangeListener(DataModel.SEARCH_ITEM_CHANGED, this);
		
		initUI();
	}
	
	// initializes UI
	private void initUI() {
		this.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("Filter:", SwingConstants.TRAILING);
		sigFilter = new JTextField();
		sigFilter.addActionListener(this);
		label.setLabelFor(sigFilter);
		countLabel = new JLabel();
		
		tableModel = new Vertex2VertexTableModel();
		sorter = new Vertex2VertexTableSorter(tableModel);
		
		table = new JTable(){
			private static final long serialVersionUID = 7050895341970662607L;

			public TableCellRenderer getCellRenderer(int row, int column) {
				TableModel model = this.getModel();
				if (model instanceof Vertex2VertexTableModel) {
					Vertex2VertexTableModel v2vm = (Vertex2VertexTableModel)model;
					int index = v2vm.getColumnIndex(Vertex2VertexTableModel.SIGNATURE);
					if (index == column)
					    return new HistogramCellRenderer(true);
					index = v2vm.getColumnIndex(Vertex2VertexTableModel.CO_OCCUR_COUNT);
					Map<String, Clustering> map = _dataModel.getClusterings();
					if (map == null) return null;
					int maxValue = map.size();
					if (index == column)
						return new CooccurCellRenderer(true, maxValue);
				}
				
				return super.getCellRenderer(row, column);
			  }
			
			public String getToolTipText(MouseEvent e) {
				Point p = e.getPoint();
				int row = this.rowAtPoint(p);
				Cooccurrence c = tableModel.getRow(row);
				return c.getNiceHTMLString();
			}
		};
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);
		table.setModel(tableModel);
		table.setRowSorter(sorter);
		table.addMouseListener(this);
		
		
		_dataModel.setVertexTableColumns(tableModel.getSelectedColmnNames());
		
		JScrollPane scroll = new JScrollPane(table);
		
		GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(scroll, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sigFilter, GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(countLabel)))
                )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(sigFilter, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(countLabel))
                .addComponent(scroll, GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                )
        );
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Event handling
	//
	////////////////////////////////////////////////////////////////////////////

	//--------------------------------------------------------------------------
	//
	// PropertyChangeListener interface
	//
	//--------------------------------------------------------------------------
	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		
		if (name.equals(DataModel.VERTEX_TABLE_COLUMNS_CHANGED)) {
			tableModel.setColumns(_dataModel.getVertexTableColumns());
		}
		else if (name.equals(DataModel.VERTEX_PAIRS_CHANGED)) {
			tableModel.setData(_dataModel.getVertexPairs());
			countLabel.setText(String.valueOf(sorter.getViewRowCount()));
		}
		else if (name.equals(DataModel.SELECTED_VERTICES_CHANGED)) {
			Map<Vertex, TreeMap<Vertex, Cooccurrence>> pairs = _dataModel.getVertexPairs();
			if (pairs == null)
				return;
			ListSelectionModel lsm = table.getSelectionModel();
			lsm.clearSelection();
			ArrayList<Vertex> selectedVert = _dataModel.getSelectedVertices();
			if (selectedVert == null) return;
			lsm.removeListSelectionListener(this);
			if (selectedVert.size() == 2) {
				// figure out index and select one row
				Vertex u = selectedVert.get(0);
				Vertex v = selectedVert.get(1);
				Cooccurrence c = getCooccurrence(pairs, u, v);
				if (c == null) return; // could not find  matching cooccurrence
				int index = addToSelection(c);
				// scroll table s that the selection is visible
				Rectangle blah = table.getCellRect(index, 0, true);
				table.scrollRectToVisible(blah);
			}
			else if (selectedVert.size() > 2) {
				Vertex u, v;
				Cooccurrence c = null;
				int size = selectedVert.size();
				for (int i = 0; i < size-1; i++) {
					u = selectedVert.get(i);
					for (int j = i+1; j < size; j++) {
						v = selectedVert.get(j);
						// get u, v cooccurrence
						c = getCooccurrence(pairs, u, v);
						if (c != null)
							addToSelection(c);
					}
				}
				// scroll table s that the selection is visible
//				if (index >= 0)
//					table.scrollRectToVisible(table.getCellRect(index, 0, true));
			}
			table.getSelectionModel().addListSelectionListener(this);
		}
		else if (name.equals(DataModel.SEARCH_ITEM_CHANGED)) {
			Vertex v = _dataModel.getSearchItem();
			if (v == null)
				// deselect all
				table.getSelectionModel().clearSelection();
			else {
				// get the number of rows at the top related to the selected vert
				int rowCount = tableModel.sortMatchesToTop(v);
				// fire an update to let the table know it needs to redraw
				tableModel.fireTableDataChanged();
				// highlight the top rows
				ListSelectionModel selectionModel = table.getSelectionModel();
				selectionModel.removeListSelectionListener(this);
				selectionModel.setSelectionInterval(0, rowCount-1);
				selectionModel.addListSelectionListener(this);
				// awesome!!!!
			}
		}
	}

	private int addToSelection(Cooccurrence c) {
		// find the row that matches the cooccurrence
		int index = tableModel.getIndex(c);
		if (index < 0) return -1; // no row matches this
		index = table.convertRowIndexToView(index);
		if (index < 0) return -1;
		table.getSelectionModel().addSelectionInterval(index, index);
//		table.getSelectionModel().setSelectionInterval(index, index);
		return index;
	}
	
	public static void println(String s) {
//		System.out.println(s);
	}

	private Cooccurrence getCooccurrence(
			Map<Vertex, TreeMap<Vertex, Cooccurrence>> pairs, Vertex u, Vertex v) {
		Cooccurrence c = null;
		TreeMap<Vertex, Cooccurrence> cooccs;
		cooccs = pairs.get(u);
		if (cooccs != null) {
			c = cooccs.get(v);
		}
		cooccs = pairs.get(v);
		if (c == null && cooccs != null) {
			c = cooccs.get(u);
		}
		return c;
	}

	//--------------------------------------------------------------------------
	//
	// ListSelectionListener interface
	//
	//--------------------------------------------------------------------------
	// fired when users selects rows in the table
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		int index = table.getSelectedRow();
		
		if (index < 0)
			return;
		
		index = table.convertRowIndexToModel(index);
		Cooccurrence pair = tableModel.getRow(index);

		ArrayList<Vertex> col = new ArrayList<Vertex>();
		col.add(pair.u);
		col.add(pair.v);
		_dataModel.removePropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
		_dataModel.setSelectedVertices(col);
		_dataModel.addPropertyChangeListener(DataModel.SELECTED_VERTICES_CHANGED, this);
	}

	//--------------------------------------------------------------------------
	//
	// ActionListener interface
	//
	//--------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
//		String pattern = sigFilter.getText();
		// filter table
		RowFilter<Vertex2VertexTableModel, Object> rf = null;
		try {
			rf = new RowFilter<Vertex2VertexTableModel, Object>() {

				public boolean include(
						RowFilter.Entry<? extends Vertex2VertexTableModel, ? extends Object> entry) {
					Vertex2VertexTableModel model = entry.getModel();
					Object o = entry.getIdentifier();
					if (o instanceof Integer) {
						int id = (Integer)o;
						Cooccurrence co = model.getRow(id);
						String signature = co.getSignature();
						return signature.matches("^" + sigFilter.getText() + "(.)*");
						// match signature and filter string
					}
					
					return false;
				}
			};
		}
		catch(PatternSyntaxException ex) {
			
		}
		sorter.setRowFilter(rf);
		countLabel.setText(String.valueOf(sorter.getViewRowCount()));
	}
	
	/**
	 * 
	 * @param value
	 */
	private void openURL(String value) {
		if (!java.awt.Desktop.isDesktopSupported()) {
			System.out.println("Desktop is not supported (fatal)");
			return;
		}

		java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

		if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
			System.out.println("Desktop doesn't support the browse action");
			return;
		}
		java.net.URI uri;
		try {
			uri = new java.net.URI(
				"http://www.arabidopsis.org/servlets/TairObject?type=locus" +
				"&name=" + value);
			desktop.browse(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	//
	// MouseListener interface
	//
	//--------------------------------------------------------------------------
	public void mouseClicked(MouseEvent e) {
		if (e.isControlDown()) {
			int c = table.getSelectedColumn();
			int r = table.getSelectedRow();
			// TODO: make columns immovable - exception otherwise
			// TODO: use model.getRealColumnIndex(c)
			if (r > 0  && c > 0 && r < 2) {
				String value = (String)this.tableModel.getValueAt(r, c);
//				System.out.println(value + " " + this.tableModel.getValueAt(r, c) + " " + r + " " + c);
				openURL(value);
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}
}
