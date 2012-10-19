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
package edu.umd.coral.ui.dialog.action;

import javax.swing.JDialog;
import javax.swing.JFrame;

import edu.umd.coral.model.DataModel;
import edu.umd.coral.ui.dialog.SaveFigureDialog;
import edu.umd.coral.ui.panel.HasSaveableImage;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.action.DockingWindowAction;

public class SaveFigureAction extends DockingWindowAction {
	
	private JFrame frame;
	private DataModel model;
	private HasSaveableImage panel;
	private static final long serialVersionUID = 2969170780095333275L;

	public SaveFigureAction (JFrame frame, DataModel model, HasSaveableImage panel) {
		this.frame = frame;
		this.model = model;
		this.panel = panel;
	}

	@Override
	public String getName() {
		return "saveMatrixAsFig";
	}

	@Override
	public boolean isPerformable(DockingWindow arg0) {
		return true;
	}

	@Override
	public void perform(DockingWindow window) {
		JDialog dialog = new SaveFigureDialog(frame, true, model, panel.getImage());
//		dialog.setLocation(new );
		dialog.pack();
		dialog.setVisible(true);
	}

}
