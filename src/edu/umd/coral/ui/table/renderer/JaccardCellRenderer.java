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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

import edu.umd.coral.model.GradientColorModel;

public class JaccardCellRenderer extends JLabel implements TableCellRenderer {

	private Border unselectedBorder = null;
	
    private Border selectedBorder = null;
    
    private boolean isBordered = true;
    
    private int borderThickness = 1;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9007553277735237032L;
	
	public JaccardCellRenderer(boolean isBordered) {
        this.isBordered = isBordered;
        setOpaque(true); //MUST do this for background to show up.
    }


	/**
	 * 
	 * Color the cell (red-green) based on the Jaccard coeff value
	 * 
	 */
	
	public Component getTableCellRendererComponent(JTable table,
			Object object,
            boolean isSelected, 
            boolean hasFocus,
            int row, int column) {		
//		
		Float value = (Float)object;
		Color c = GradientColorModel.getLadderColor(value, 1, false);
//		Color c = GradientColorModel.getGradientColor(Color.RED, Color.GREEN, value, isSelected);
        setBackground(c);
        
        this.setText(Float.toString(value));
        
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
        return this;
	}

}
