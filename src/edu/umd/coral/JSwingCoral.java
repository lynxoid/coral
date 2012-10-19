/*
 * Copyright (c) 2004 NNL Technology AB
 * All rights reserved.
 *
 * "Work" shall mean the contents of this file.
 *
 * Redistribution, copying and use of the Work, with or without
 * modification, is permitted without restrictions.
 *
 * Visit www.infonode.net for information about InfoNode(R)
 * products and how to contact NNL Technology AB.
 *
 * THE WORK IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THE WORK, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

// modified: Darya Filippova, March 2012

// $Id: DockingWindowsExample.java,v 1.28 2007/01/28 21:25:10 jesper Exp $
package edu.umd.coral;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;
import net.infonode.docking.ViewSerializer;
import net.infonode.docking.WindowBar;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.properties.WindowTabButtonProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.MixedViewHandler;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.util.Direction;
import edu.umd.coral.model.DataModel;
import edu.umd.coral.ui.CoralMenu;
import edu.umd.coral.ui.dialog.SelectClusteringOrderDialog;
import edu.umd.coral.ui.dialog.action.SaveFigureAction;
import edu.umd.coral.ui.panel.LadderPanel;
import edu.umd.coral.ui.panel.MatrixPanel;
import edu.umd.coral.ui.panel.Vertex2VertexPanel;
import edu.umd.coral.ui.tabs.Module2ModulePanel;
import edu.umd.coral.ui.tabs.OverviewTab;
import edu.umd.coral.ui.tabs.ParallelSetsPanel;

/**
 * A small example on how to use InfoNode Docking Windows. This example shows
 * how to handle both static and dynamic views in the same root window.
 * 
 * @author $Author: jesper $
 * @version $Revision: 1.28 $
 */
public class JSwingCoral {
	
	// consts

	public static final String APP_NAME = "Coral";

	public static final String APP_ICON = "edu/umd/coral/resources/coralicon.png";

//	public static final String APP_CONFIG_FILE = "window_config.xml";

	public static final String overviewID = "Statistics";
	public static final String parSetsID = "Parallel partitions";
	public static final String matrixID = "Co-cluster matrix";
	public static final String vertexID = "Item pairs";
	public static final String compareClustID = "Clustering comparison";
	public static final String clustDetailsID = "Module pairs";
//	private static final String networkDetailsID = "Network details";
	
	/**
	 * Custom view button icon.
	 */
	private Icon BUTTON_ICON;	

	/**
	 * The one and only root window
	 */
	private RootWindow rootWindow;

	/**
	 * An array of the static views
	 */
	private View[] views = new View[6];

	/**
	 * Contains all the static views
	 */
	private ViewMap viewMap = new ViewMap();

	/**
	 * The currently applied docking windows theme
	 */
	private DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();

	/**
	 * A dynamically created view containing an id.
	 */
	private static class DynamicView extends View {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1440916922477653648L;
		private int id;

		/**
		 * Constructor.
		 * 
		 * @param title
		 *            the view title
		 * @param icon
		 *            the view icon
		 * @param component
		 *            the view component
		 * @param id
		 *            the view id
		 */
		DynamicView(String title, Icon icon, Component component, int id) {
			super(title, icon, component);
			this.id = id;
		}

		/**
		 * Returns the view id.
		 * 
		 * @return the view id
		 */
		public int getId() {
			return id;
		}
	}

	/**
	 * In this properties object the modified property values for close buttons
	 * etc. are stored. This object is cleared when the theme is changed.
	 */
	private RootWindowProperties properties = new RootWindowProperties();

	/**
	 * The application frame
	 */
	private JFrame frame = new JFrame(APP_NAME);

	private CoralMenu menu;

	public JSwingCoral() {
		File f = new File("temp.txt");
		String canonical = "";
		try {
			canonical = f.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		String parent = canonical.substring(0, canonical.indexOf("temp.txt"));
//		String iconPath = /*parent + */"edu.umd.coral.resources." + File.separator + "chart_bar.png";
		String iconPath = /*parent + */"/edu/umd/coral/resources/chart_bar.png";
//		System.out.println(iconPath);
//		System.out.println();
		BUTTON_ICON = new ImageIcon(getClass().getResource(iconPath), "Save figure");

		
		try {
    	    // Set System L&F
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
           // handle exception
        }
        catch (ClassNotFoundException e) {
           // handle exception
        }
        catch (InstantiationException e) {
           // handle exception
        }
        catch (IllegalAccessException e) {
           // handle exception
        }
        
        
        
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
        
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1100, 700);
		frame.setPreferredSize(new Dimension(1100, 700));
		ImageIcon icon = new ImageIcon(APP_ICON);
		frame.setIconImage(icon.getImage());
		
		DataModel dataModel = new DataModel(frame);		
		createRootWindow(dataModel);		
		setDefaultLayout(dataModel);
		showFrame(dataModel);
	}

	/**
	 * Creates the root window and the views.
	 * @param dataModel 
	 */
	private void createRootWindow(final DataModel dataModel) {
		// Create the views
		MatrixPanel matrixPanel = new MatrixPanel(dataModel);
		views[0] = new View(matrixID, null, matrixPanel);
		viewMap.addView(0, views[0]);
		
		Vertex2VertexPanel v2vPanel = new Vertex2VertexPanel(dataModel);
		views[1] = new View(vertexID, null, v2vPanel);
		viewMap.addView(1, views[1]);
//		Vertex2VertexPanel v2v = new Vertex2VertexPanel(_dataModel);
		
		ParallelSetsPanel ppp = new ParallelSetsPanel(dataModel);
		views[2] = new View(parSetsID, null, ppp);
		viewMap.addView(2, views[2]);
		
		OverviewTab tab = new OverviewTab(dataModel);
		views[3] = new View(overviewID, null, tab);
		viewMap.addView(3, views[3]);
		
		LadderPanel ladderPanel = new LadderPanel(dataModel);
		views[4] = new View(compareClustID, null, ladderPanel);
		viewMap.addView(4, views[4]);
		views[5] = new View(clustDetailsID, null, new Module2ModulePanel(dataModel));
		viewMap.addView(5, views[5]);
		
		dataModel.setViews(views);
		
		// show button to save image as PNG on matrix
		WindowTabButtonProperties prop = views[0].getViewProperties()
				.getViewTitleBarProperties().getNormalProperties()
				.getUndockButtonProperties();		
		prop.setVisible(true);
		prop.setIcon(BUTTON_ICON);		
		prop.setToolTipText("Save figure");
		prop.setAction(new SaveFigureAction(frame, dataModel, matrixPanel));
		
		prop = views[0].getViewProperties()
			.getViewTitleBarProperties().getFocusedProperties()
			.getUndockButtonProperties();
		prop.setVisible(true);
		prop.setIcon(BUTTON_ICON);
		prop.setToolTipText("Save figure");
		prop.setAction(new SaveFigureAction(frame, dataModel, matrixPanel));
		
		prop = views[1].getViewProperties().getViewTitleBarProperties()
			.getNormalProperties().getUndockButtonProperties();
		prop.setVisible(false);
//		prop.setAction(new SaveFigureAction(frame, dataModel, ppp));
		prop = views[1].getViewProperties().getViewTitleBarProperties()
			.getFocusedProperties().getUndockButtonProperties();
		prop.setVisible(false);

		// show undock icon on ppp 
		prop = views[2].getViewProperties()
			.getViewTitleBarProperties().getNormalProperties()
			.getUndockButtonProperties();		
		prop.setVisible(true);
		prop.setIcon(BUTTON_ICON);
		prop.setToolTipText("Save figure");
		prop.setAction(new SaveFigureAction(frame, dataModel, ppp));

		// show undock icon on bar charts
		prop = views[3].getViewProperties()
			.getViewTitleBarProperties().getNormalProperties()
			.getUndockButtonProperties();		
		prop.setVisible(true);
		prop.setIcon(BUTTON_ICON);
		prop.setToolTipText("Save figure");
		prop.setAction(new SaveFigureAction(frame, dataModel, tab));
		
		// show undock button on ladder
		prop = views[4].getViewProperties().getViewTitleBarProperties()
			.getNormalProperties().getUndockButtonProperties();
		prop.setIcon(BUTTON_ICON);
		prop.setToolTipText("Save figure");
		prop.setVisible(true);
		prop.setAction(new SaveFigureAction(frame, dataModel, ladderPanel));
		prop = views[4].getViewProperties().getViewTitleBarProperties()
			.getFocusedProperties().getUndockButtonProperties();
		prop.setVisible(true);
		prop.setIcon(BUTTON_ICON);
		prop.setToolTipText("Save figure");
		prop.setAction(new SaveFigureAction(frame, dataModel, ladderPanel));
		
		// do not show undock icon on modules table
		prop = views[5].getViewProperties().getViewTitleBarProperties()
			.getNormalProperties().getUndockButtonProperties();
		prop.setVisible(false);
		prop = views[5].getViewProperties().getViewTitleBarProperties()
			.getFocusedProperties().getUndockButtonProperties();
		prop.setVisible(false);

		
		// The mixed view map makes it easy to mix static and dynamic views
		// inside the same root window
		MixedViewHandler handler = new MixedViewHandler(viewMap,
				new ViewSerializer() {
					public void writeView(View view, ObjectOutputStream out)
							throws IOException {
						out.writeInt(((DynamicView) view).getId());
					}

					public View readView(ObjectInputStream in)
							throws IOException {
						return null;//getDynamicView(in.readInt());
					}
				});

		rootWindow = DockingUtil.createRootWindow(viewMap, handler, true);

		// Set gradient theme. The theme properties object is the super object
		// of our properties object, which
		// means our property value settings will override the theme values
		properties.addSuperObject(currentTheme.getRootWindowProperties());		

		// Our properties object is the super object of the root window
		// properties object, so all property values of the
		// theme and in our property object will be used by the root window
		rootWindow.getRootWindowProperties().addSuperObject(properties);

		// Enable the bottom window bar
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);

//		properties.getDockingWindowProperties().setUndockEnabled(true);
//		properties.getViewProperties().getViewTitleBarProperties().
//			getNormalProperties().getUndockButtonProperties().setIcon(BUTTON_ICON);
		
		
		
		properties.getDockingWindowProperties().setMaximizeEnabled(true);
		
		final RootWindowProperties titleBarStyleProperties = PropertiesUtil
				.createTitleBarStyleRootWindowProperties();
		properties.addSuperObject(titleBarStyleProperties);
	}

	/**
	 * Sets the default window layout.
	 * @param dataModel 
	 */
	private void setDefaultLayout(DataModel dataModel) {
		SplitWindow splitWindow = new SplitWindow(true, 0.3f, views[4], views[5]);

		rootWindow.setWindow(
			new SplitWindow(false, 0.7f, 
					new SplitWindow(true, 0.65f,
						new SplitWindow(true, 0.65f, views[0], views[1]), 
						views[2]), 
				new SplitWindow(true, 0.4f, views[3], splitWindow)));

		WindowBar windowBar = rootWindow.getWindowBar(Direction.DOWN);

		while (windowBar.getChildWindowCount() > 0)
			windowBar.getChildWindow(0).close();
	}

	/**
	 * Initializes the frame and shows it.
	 */
	private void showFrame(DataModel dataModel) {
//		frame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
		frame.getContentPane().add(rootWindow, BorderLayout.CENTER);
		frame.setJMenuBar(createMenuBar(dataModel));
		frame.setSize(900, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Creates the frame menu bar.
	 * 
	 * @return the menu bar
	 */
	private JMenuBar createMenuBar(DataModel dataModel) {
		menu = new CoralMenu(dataModel, rootWindow);
		
		// menu.add(createViewMenu());
		frame.setJMenuBar(menu);
		rootWindow.addListener(menu);
		
		return menu;
	}

	public static void main(String[] args) throws Exception {
		// Set InfoNode Look and Feel
		UIManager.setLookAndFeel(new InfoNodeLookAndFeel());

		// Docking windwos should be run in the Swing thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new JSwingCoral();
			}
		});
	}
}
