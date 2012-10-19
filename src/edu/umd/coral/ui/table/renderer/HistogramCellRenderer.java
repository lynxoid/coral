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
package edu.umd.coral.ui.table.renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import edu.umd.coral.model.data.Clustering;
import edu.umd.coral.model.data.Cooccurrence;

public class HistogramCellRenderer extends JComponent implements
		TableCellRenderer {

	private Border unselectedBorder = null;
	
    private Border selectedBorder = null;
    
    private boolean isBordered = true;
    
    private int borderThickness = 1;
    
    private Cooccurrence cooccur;
    
    /**
     * spacing on either side of a histogram column
     */
    private int spacing = 1;
    
    /**
     * the height of a histogram column representing 0 value
     * 
     */
    private int nullHeight = 3;
    
	/**
	 * 
	 */
	private static final long serialVersionUID = -3120175049864772172L;

	public HistogramCellRenderer(boolean isBordered) {
		this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
	}

	
	public Component getTableCellRendererComponent(JTable table, 
			Object signature,
			boolean isSelected, 
			boolean hasFocus, 
			int row, 
			int column) {
		
		this.cooccur = (Cooccurrence) signature;
		
		if (isBordered) {
            if (isSelected) {
                if (selectedBorder == null) {
                    selectedBorder = BorderFactory.createMatteBorder(
                    			borderThickness,
                    			borderThickness,
                    			borderThickness,
                    			borderThickness,
                                table.getSelectionBackground());
                }
                setBorder(selectedBorder);
            } else {
                if (unselectedBorder == null) {
                    unselectedBorder = BorderFactory.createMatteBorder(1,1,1,1,
                                              table.getBackground());
                }
                setBorder(unselectedBorder);
            }
        }
		
//		Vertex2VertexTableModel model = (Vertex2VertexTableModel) table.getModel();
//		int index = table.convertRowIndexToModel(row);
//		if (index >= 0) {
//			Cooccurrence cooccur = model.getRow(index);
//			setToolTipText(cooccur.getNiceHTMLString());
//		}
		
		setBackground(Color.WHITE);
		return this;
	}
	
	// custom painting
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (cooccur == null)
			return;
		
		String signature = cooccur.getSignature(), s;
		
		if (signature == null)
			return;
		
		int x = spacing;
		int oneHeight = getHeight();
		int length = signature.length();
		int columnWidth = (int) Math.floor( (getWidth() * 1.0 - 2 * spacing * length )/ length);
		
		if (columnWidth <= 0)
			columnWidth = 1;
		
		Clustering c;
		
		for (int i = 0; i < length; i++) {
			s = signature.substring(i, i+1);
			c = cooccur.getClusteringAt(i);
			if (c != null)
				g.setColor(c.getColor());
			if (s.equals("0")) {
				g.fillRect(x, oneHeight - nullHeight, columnWidth, nullHeight);
			}
			else if (s.equals("1")) {
				g.fillRect(x, 0, columnWidth, oneHeight);
			}
			x += columnWidth + 2 * spacing;
		}
	}
}
