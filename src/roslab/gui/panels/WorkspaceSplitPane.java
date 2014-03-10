/**
 * Copyright 2009-2011, Kansas State University, 
 * PRECISE Center at the University of Pennsylvania, and
 * Andrew King
 *
 *
 * This file is part of the Medical Device Coordination Framework (aka the MDCF)
 *
 *   The MDCF is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   The MDCF  is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the MDCF .  If not, see <http://www.gnu.org/licenses/>.
 */
package roslab.gui.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import roslab.artifacts.ComponentLocation;
import roslab.artifacts.ConfigurationSpec;
import roslab.artifacts.ModuleSpec;
import roslab.artifacts.PortSpec;
import roslab.artifacts.WorkspaceContext;
import roslab.gui.misc.FileConstants;
import roslab.model.transforms.ROSModelCompiler;
import roslab.types.builtins.Channel;
import roslab.types.builtins.CompRoles;
import roslab.types.builtins.ComponentSignature;
import roslab.types.builtins.PortName;
import roslab.types.builtins.PortType;
import roslab.types.builtins.PortTypes;
import roslab.types.builtins.TaskDescriptor;
import roslab.utils.FileIO;
import roslab.utils.Pair;
import roslab.utils.logger.LogMethod;

public class WorkspaceSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	private static String BUILT_IN_NODES = "ROS Nodes";
	private static String NODES = "User Defined Nodes";
	private JScrollPane workspaceTreeScrollPane = null;
	private ConfigurationEditorPanel configurationPanel = null;
	private JTree tree;
	private String currentConfigurationName;

	private JPopupMenu popup;
	private JPopupMenu workspacePopup;
	private JPopupMenu portTypeInstPopup;
	private JPopupMenu componentTypePopup;
	private JPopupMenu deviceComponentTypeInstPopup;
	private JPopupMenu serverComponentTypeInstPopup;
	private JPopupMenu configurationPopup;
	private JPopupMenu configurationInstPopup;
	private JPopupMenu configurationDevCompInstPopup;
	private JPopupMenu configurationServerCompInstPopup;
	private JPopupMenu configurationConnInstPopup;

	private WorkspaceSplitPane me = this;
	private DefaultMutableTreeNode portTypes;
	private DefaultMutableTreeNode componentTypes;
	private DefaultMutableTreeNode builtInTypes;
	private DefaultMutableTreeNode configurationsTree;
	private DefaultTreeModel model;

	Map<String, DefaultMutableTreeNode> treeNodeCompNamesMap = new HashMap<String, DefaultMutableTreeNode>();
	Map<String, DefaultMutableTreeNode> treeNodeServicesNamesMap = new HashMap<String, DefaultMutableTreeNode>();
	Map<String, DefaultMutableTreeNode> portNodeCompNamesMap = new HashMap<String, DefaultMutableTreeNode>();
	Map<String, DefaultMutableTreeNode> configurationCompInstancesMap = new HashMap<String, DefaultMutableTreeNode>();
	Map<String, DefaultMutableTreeNode> configurationConnInstancesMap = new HashMap<String, DefaultMutableTreeNode>();
	Map<String, DefaultMutableTreeNode> configurationExceptionHandlingMap = new HashMap<String, DefaultMutableTreeNode>();
	Map<String, Map<String, DefaultMutableTreeNode>> componentInstances = new HashMap<String, Map<String, DefaultMutableTreeNode>>();
	Map<String, Map<String, DefaultMutableTreeNode>> connectionInstances = new HashMap<String, Map<String, DefaultMutableTreeNode>>();
	Map<String, Map<String, DefaultMutableTreeNode>> exceptionInstances = new HashMap<String, Map<String, DefaultMutableTreeNode>>();

	// IComponentSigGenerator sigGen = new
	// FrameworkComponentSignatureGenerator();
	// IComponentSkelGenerator skelGen = new
	// JavaJmsComponentSkeletonGenerator();
	// IAppConfigGenerator appConfigGen = new
	// FrameworkConfigurationSpecGenerator();

	/**
	 * This is the default constructor
	 */
	public WorkspaceSplitPane() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		this.setLeftComponent(getWorkspaceTreeScrollPane());
		this.setRightComponent(getAppConfigPanel());
		this.setContinuousLayout(true);
		this.popup = getPopup();
		this.workspacePopup = getWorkspacePopup();
		this.portTypeInstPopup = getPortTypeInstPopup();
		this.componentTypePopup = getComponentTypePopup();
		this.deviceComponentTypeInstPopup = getDeviceComponentTypeInstPopup();
		this.serverComponentTypeInstPopup = getServerComponentTypeInstPopup();
		this.configurationPopup = getConfigurationPopup();
		this.configurationInstPopup = getConfigurationInstPopup();
		this.configurationDevCompInstPopup = getConfigurationDevCompInstPopup();
		this.configurationServerCompInstPopup = getConfigurationServerCompInstPopup();
		this.configurationConnInstPopup = getConfigurationConnInstPopup();
		this.addComponentListener(new myComponentListener());
		roslab.gui.misc.PdeConfiguration.wsp = this;
	}

	private JPopupMenu getPopup() {
		if (this.popup == null) {
			popup = new JPopupMenu();
			JMenuItem menuItem = new JMenuItem("A popup menu item");
			popup.add(menuItem);
			menuItem = new JMenuItem("Another popup menu item");
			popup.add(menuItem);

		}
		return this.popup;
	}

	private JPopupMenu getWorkspacePopup() {
		if (this.workspacePopup == null) {
			this.workspacePopup = new JPopupMenu();
			this.workspacePopup.setLightWeightPopupEnabled(false);
			JMenuItem menuItem = new JMenuItem("Save Workspace");
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAction();
				}
			});
			this.workspacePopup.add(menuItem);
		}
		return this.workspacePopup;
	}

	private JPopupMenu getPortTypeInstPopup() {
		if (this.portTypeInstPopup == null) {
			this.portTypeInstPopup = new JPopupMenu();
			this.portTypeInstPopup.setLightWeightPopupEnabled(false);

			JMenuItem editItem = new JMenuItem("Edit");
			editItem.addActionListener(new modifyComponentTypePortMenuListener());

			JMenuItem deleteItem = new JMenuItem("Delete");
			deleteItem
					.addActionListener(new deleteComponentTypePortMenuListener());
			this.portTypeInstPopup.add(editItem);
			this.portTypeInstPopup.add(deleteItem);
		}
		return this.portTypeInstPopup;
	}

	private JPopupMenu getComponentTypePopup() {
		if (this.componentTypePopup == null) {
			this.componentTypePopup = new JPopupMenu();
			this.componentTypePopup.setLightWeightPopupEnabled(false);
			JMenuItem menuItem = new JMenuItem("New component type...");
			menuItem.addActionListener(new addComponentTypeMenuListener());
			JMenuItem importMenuItem = new JMenuItem("Import component type from signature");
			importMenuItem.addActionListener(new importComponentTypeMenuListener());
			// menuItem.addActionListener(this);
			this.componentTypePopup.add(menuItem);
			//this.componentTypePopup.add(importMenuItem);
		}
		return this.componentTypePopup;
	}

	class importComponentTypeMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final JFileChooser fc = new JFileChooser(
					WorkspaceContext.appDevPath);
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File name) {
					return (name.isDirectory() || name.getAbsolutePath()
							.endsWith(FileConstants.COMPONENTSIG));
				}

				@Override
				public String getDescription() {
					return "Component Signature Files *"
							+ FileConstants.COMPONENTSIG;
				}
			});
			int retVal = fc.showOpenDialog(me);
			File f = fc.getSelectedFile();
			if (f != null && (retVal == JFileChooser.APPROVE_OPTION)) {
				importComponentType(f);
			}
		}
	}

	private void importComponentType(File file) {
		ComponentSignature csig = null;
		// = FileIO.loadSignatureFromFile(file);
		if (csig != null) {
			String[] roleOptions = { CompRoles.ROS_BUILTINS,
					CompRoles.ROS_NODE, CompRoles.ROS_SERVICE };
			String role = (String) JOptionPane.showInputDialog(null,
					"Choose role:", "", JOptionPane.INFORMATION_MESSAGE, null,
					roleOptions, roleOptions[0]);
			ModuleSpec mspec = addNewComponentToWorkspace(csig.getType(), role);
			addNewComponentToUI(mspec);

			// now add all the ports
			List<PortName> sendPortNames = csig.getSendPortNames();
			List<PortType> sendPortTypes = csig.getSendPortTypes();
			List<PortName> recvPortNames = csig.getRecvPortNames();
			List<PortType> recvPortTypes = csig.getRecvPortTypes();
			for (int i = 0; i < sendPortNames.size(); i++) {
				PortSpec pspec = new PortSpec(sendPortTypes.get(i).getType(), null);
				addNewPortToComponentTypeSolution(csig.getType(), sendPortNames.get(i).getName(), true, pspec);
				if (i < sendPortNames.size() - 1)
					addPortToComponentUINoReload(csig.getType(), sendPortNames.get(i).getName(), pspec, true);
				else
					addPortToComponentUI(csig.getType(), sendPortNames.get(i).getName(), pspec, true, false);
			}
			for (int i = 0; i < recvPortNames.size(); i++) {
				PortSpec pspec = new PortSpec(recvPortTypes.get(i).getType(), null);
				addNewPortToComponentTypeSolution(csig.getType(), recvPortNames.get(i).getName(), false, pspec);
				if (i < recvPortNames.size() - 1)
					addPortToComponentUINoReload(csig.getType(), recvPortNames.get(i).getName(), pspec, false);
				else
					addPortToComponentUI(csig.getType(), recvPortNames.get(i).getName(), pspec, false, false);
			}
		}

	}

	private JPopupMenu getDeviceComponentTypeInstPopup() {
		if (this.deviceComponentTypeInstPopup == null) {
			this.deviceComponentTypeInstPopup = new JPopupMenu();
			this.deviceComponentTypeInstPopup.setLightWeightPopupEnabled(false);
			JMenuItem newPortMenuItem = new JMenuItem("New port...");
			newPortMenuItem.addActionListener(new addPortToComponentMenuListener());

			JMenuItem editMenuItem = new JMenuItem("Edit component...");
			editComponentTypeMenuListener editListener = new editComponentTypeMenuListener();
			editListener.setTree(tree);
			editMenuItem.addActionListener(editListener);

			JMenuItem delMenuItem = new JMenuItem("Delete");
			delMenuItem.addActionListener(new deleteComponentTypeMenuListener());

			JMenuItem synthMenuItem = new JMenuItem("Generate signature...");
			synthMenuItem.addActionListener(new generateDeviceComponentSignatureMenuListener());

			JMenuItem skelMenuItem = new JMenuItem(
					"Generate Java skeleton");
			skelMenuItem.addActionListener(new generateDeviceComponentSkelMenuListener());

			JMenuItem newTaskMenuItem = new JMenuItem("New task...");
			newTaskMenuItem.addActionListener(new addTaskToComponentMenuListener());

			this.deviceComponentTypeInstPopup.add(newPortMenuItem);
			//this.deviceComponentTypeInstPopup.add(newTaskMenuItem);
			this.deviceComponentTypeInstPopup.add(editMenuItem);
			this.deviceComponentTypeInstPopup.add(delMenuItem);
			//this.deviceComponentTypeInstPopup.add(synthMenuItem);
			//this.deviceComponentTypeInstPopup.add(skelMenuItem);
		}
		return this.deviceComponentTypeInstPopup;
	}

	private JPopupMenu getServerComponentTypeInstPopup() {
		if (this.serverComponentTypeInstPopup == null) {
			this.serverComponentTypeInstPopup = new JPopupMenu();
			this.serverComponentTypeInstPopup.setLightWeightPopupEnabled(false);
			JMenuItem menuItem = new JMenuItem("New port...");
			menuItem.addActionListener(new addPortToComponentMenuListener());

			JMenuItem editMenuItem = new JMenuItem("Edit component...");
			editComponentTypeMenuListener editListener = new editComponentTypeMenuListener();
			editListener.setTree(tree);
			editMenuItem.addActionListener(editListener);

			JMenuItem delMenuItem = new JMenuItem("Delete");
			delMenuItem.addActionListener(new deleteComponentTypeMenuListener());

			JMenuItem newTaskMenuItem = new JMenuItem("New task...");
			newTaskMenuItem.addActionListener(new addTaskToComponentMenuListener());

			//this.serverComponentTypeInstPopup.add(newTaskMenuItem);
			this.serverComponentTypeInstPopup.add(menuItem);
			this.serverComponentTypeInstPopup.add(editMenuItem);
			this.serverComponentTypeInstPopup.add(delMenuItem);
		}
		return this.serverComponentTypeInstPopup;
	}

	private JPopupMenu getConfigurationPopup() {
		if (this.configurationPopup == null) {
			this.configurationPopup = new JPopupMenu();
			JMenuItem menuItem = new JMenuItem("New App Configuration...");
			menuItem.addActionListener(new addAppConfigMenuListener());
			this.configurationPopup.add(menuItem);
		}
		return this.configurationPopup;
	}

	private JPopupMenu getConfigurationInstPopup() {
		if (this.configurationInstPopup == null) {
			this.configurationInstPopup = new JPopupMenu();
			this.configurationInstPopup.setLightWeightPopupEnabled(false);
			JMenuItem delete = new JMenuItem("Delete");
			JMenuItem saveAppConfig = new JMenuItem("Generate ROS Node Bundle...");
			// JMenuItem saveAndGenAllNonDeviceFiles = new
			// JMenuItem("Save App Config., Signatures & Generate Skeletons for non-Devices...");
			// JMenuItem archiveGenItem = new
			// JMenuItem("Create App Archive Folder Structure...");
			// JMenuItem checkConsistencyItem = new
			// JMenuItem("Check App Archive Consistency");

			saveAppConfig.addActionListener(new generateAppConfigMenuListener());
			// saveAndGenAllNonDeviceFiles.addActionListener(new
			// SaveAndGenAllNonDeviceFilesMenuListener());
			// archiveGenItem.addActionListener(new
			// generateAppArchiveStructureMenuListener());
			// checkConsistencyItem.addActionListener(new
			// checkConsistencyMenuListener());
			delete.addActionListener(new deleteAppConfigMenuListener());

			// this.configurationInstPopup.add(archiveGenItem);
			this.configurationInstPopup.add(saveAppConfig);
			// this.configurationInstPopup.add(exportAll);
			// this.configurationInstPopup.add(checkConsistencyItem);
			this.configurationInstPopup.add(delete);
		}
		return this.configurationInstPopup;
	}

	private JPopupMenu getConfigurationDevCompInstPopup() {
		if (this.configurationDevCompInstPopup == null) {
			this.configurationDevCompInstPopup = new JPopupMenu();
			this.configurationDevCompInstPopup.setLightWeightPopupEnabled(false);

			JMenuItem delItem = new JMenuItem("Delete");
			delItem.addActionListener(new deleteAppConfigComponentMenuListener());

			this.configurationDevCompInstPopup.add(delItem);
		}
		return this.configurationDevCompInstPopup;
	}

	private JPopupMenu getConfigurationServerCompInstPopup() {
		if (this.configurationServerCompInstPopup == null) {
			this.configurationServerCompInstPopup = new JPopupMenu();
			this.configurationServerCompInstPopup.setLightWeightPopupEnabled(false);

			JMenuItem synthMenuItem = new JMenuItem("Generate Signature...");
			synthMenuItem.addActionListener(new generateServerComponentSignatureMenuListener());

			JMenuItem skelMenuItem = new JMenuItem("Generate MDCF/Java Skeleton");
			skelMenuItem.addActionListener(new generateServerComponentSkelMenuListener());

			JMenuItem exceptionMenuItem = new JMenuItem("Manage Exception Handler");
			exceptionMenuItem.addActionListener(new manageExceptionHandlingMenuListener());

			JMenuItem delItem = new JMenuItem("Delete");
			delItem.addActionListener(new deleteAppConfigComponentMenuListener());

			//this.configurationServerCompInstPopup.add(synthMenuItem);
			//this.configurationServerCompInstPopup.add(skelMenuItem);
			//this.configurationServerCompInstPopup.add(exceptionMenuItem);
			this.configurationServerCompInstPopup.add(delItem);
		}
		return this.configurationServerCompInstPopup;
	}

	private JPopupMenu getConfigurationConnInstPopup() {
		if (this.configurationConnInstPopup == null) {
			this.configurationConnInstPopup = new JPopupMenu();
			this.configurationConnInstPopup.setLightWeightPopupEnabled(false);
			JMenuItem delItem = new JMenuItem("Delete");
			delItem.addActionListener(new deleteAppConfigConnectionMenuListener());
			this.configurationConnInstPopup.add(delItem);
		}
		return this.configurationConnInstPopup;
	}

	/**
	 * This method initializes solutionTreeScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getWorkspaceTreeScrollPane() {
		if (workspaceTreeScrollPane == null) {
			DefaultMutableTreeNode solRoot = new DefaultMutableTreeNode("Workspace");
			DefaultMutableTreeNode typesRoot = new DefaultMutableTreeNode("Types");

			portTypes = new DefaultMutableTreeNode("Port Types");
			DefaultMutableTreeNode intPortType = new DefaultMutableTreeNode(
					PortTypes.INTEGER_TYPE_NAME);
			DefaultMutableTreeNode floatPortType = new DefaultMutableTreeNode(
					PortTypes.FLOAT_TYPE_NAME);
			DefaultMutableTreeNode doublePortType = new DefaultMutableTreeNode(
					PortTypes.DOUBLE_TYPE_NAME);
			DefaultMutableTreeNode stringPortType = new DefaultMutableTreeNode(
					PortTypes.STRING_TYPE_NAME);
			DefaultMutableTreeNode odomPortType = new DefaultMutableTreeNode(
					PortTypes.ODOM_TYPE_NAME);
			DefaultMutableTreeNode twistPortType = new DefaultMutableTreeNode(
					PortTypes.TWIST_TYPE_NAME);
			DefaultMutableTreeNode imuPortType = new DefaultMutableTreeNode(
					PortTypes.ORIENTATION_TYPE_NAME);
			DefaultMutableTreeNode gpsPortType = new DefaultMutableTreeNode(
					PortTypes.NAVIGATION_SATELLITE_FIX_TYPE_NAME);
			DefaultMutableTreeNode twiststampedPortType = new DefaultMutableTreeNode(
					PortTypes.TWISTSTAMPED_TYPE_NAME);
			DefaultMutableTreeNode uint8PortType = new DefaultMutableTreeNode(
					PortTypes.UInt8_TYPE_NAME);

			
			portTypes.add(intPortType);
			portTypes.add(floatPortType);
			portTypes.add(doublePortType);
			portTypes.add(stringPortType);
			portTypes.add(odomPortType);
			portTypes.add(twistPortType);
			portTypes.add(imuPortType);
			portTypes.add(gpsPortType);
			portTypes.add(twiststampedPortType);
			portTypes.add(uint8PortType);
						
			// componentTypes = new DefaultMutableTreeNode("Component Types");
			componentTypes = new DefaultMutableTreeNode(NODES);
			builtInTypes = new DefaultMutableTreeNode(BUILT_IN_NODES);
			configurationsTree = new DefaultMutableTreeNode("Configurations");

			typesRoot.add(portTypes);
			typesRoot.add(builtInTypes);
			typesRoot.add(componentTypes);

			solRoot.add(typesRoot);
			solRoot.add(configurationsTree);

			tree = new JTree();
			model = new DefaultTreeModel(solRoot);
			tree.setModel(model);
			tree.addMouseListener(new PopupListener());
			tree.addTreeSelectionListener(new treeSelectionListener());

			workspaceTreeScrollPane = new JScrollPane(tree);
			workspaceTreeScrollPane.setSize(this.getSize().width / 3, this.getSize().height);
		}
		return workspaceTreeScrollPane;
	}

	public void resetSolTree() {
		componentTypes.removeAllChildren();
		configurationsTree.removeAllChildren();
	}

	public void resetMapsAndSets() {
		this.treeNodeCompNamesMap.clear();
		this.portNodeCompNamesMap.clear();
		this.configurationCompInstancesMap.clear();
		this.configurationConnInstancesMap.clear();
		this.configurationExceptionHandlingMap.clear();
		this.componentInstances.clear();
		this.connectionInstances.clear();
		this.exceptionInstances.clear();
	}

	private void showAppropriatePopup(MouseEvent e) {
		Object o = tree.getLastSelectedPathComponent();
		if (o != null) {
			// System.out.println(o.toString());
			if (o.toString().equals("PDE Workspace")) {
				// No op
			} else if (o.toString().equals("Types")) {
				// No op
			} else if (o.toString().equals("Port Types")) {
				// Does not support new port type in MDCF2.0
			} else if (o.toString().equals(NODES)) {
				componentTypePopup.show(e.getComponent(), e.getX(), e.getY());
			} else if (o.toString().equals("Configurations")) {
				configurationPopup.show(e.getComponent(), e.getX(), e.getY());
			} else if (isSelectionDeviceComponent()) {
				deviceComponentTypeInstPopup.show(e.getComponent(), e.getX(),
						e.getY());
			} else if (isSelectionServerComponent()) {
				serverComponentTypeInstPopup.show(e.getComponent(), e.getX(),
						e.getY());
			} else if (isSelectionExceptionPort()) {
				// No op for exception port in device type
			} else if (isSelectionComponentPort()) {
				portTypeInstPopup.show(e.getComponent(), e.getX(), e.getY());
			} else if (o instanceof TreeNode) {
				TreeNode tn = (TreeNode) o;
				if (tn.getParent().toString().equals("Configurations")) {
					configurationInstPopup.show(e.getComponent(), e.getX(),
							e.getY());
				} else if (tn.getParent().getParent().getParent().toString()
						.equals("Configurations")) {
					if (tn.getParent().toString().equals("components")) {
						String[] instAndComptype = tn.toString().split(":");
						String componentTypeName = instAndComptype[1];
						ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentTypeName);
						configurationServerCompInstPopup.show(e.getComponent(), e.getX(), e.getY());
					} else {
						configurationConnInstPopup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			} else {
				// System.out.println("No popup configured");
			}
		}
	}

	public ConfigurationEditorPanel getAppConfigPanel() {
		if (configurationPanel == null) {
			configurationPanel = new ConfigurationEditorPanel();
		}
		return configurationPanel;
	}

	public DefaultTreeModel getModel() {
		return model;
	}

	class treeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath tp = e.getNewLeadSelectionPath();
			if (tp != null) {
				TreeNode tn = (TreeNode) tp.getLastPathComponent();
				if (tn.getParent() != null
						&& tn.getParent().toString().equals("Configurations")) {
					currentConfigurationName = tn.toString();
					ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations
                            .get(currentConfigurationName);
					configurationPanel.setConfiguration(sspec);
					configurationPanel.reRender();
				}
			}
		}

	}

	class PopupListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showAppropriatePopup(e);
			}
		}
	}

	class addComponentTypeMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// System.out.println("new component");
			NewComponentTypePanel panel = new NewComponentTypePanel();
			int selection = JOptionPane.showConfirmDialog(me.getParent(),
					panel, "New Component Type", JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.OK_OPTION) {
				ModuleSpec mspec = addNewComponentToWorkspace(
						panel.getTypeName(), panel.getRoleName());
				addNewComponentToUI(mspec);
			}
		}

	}

	class addTaskToComponentMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String[] componentTypeRole = tree.getLastSelectedPathComponent()
					.toString().split(":");
			String componentTypeName = componentTypeRole[0];
			ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes
					.get(componentTypeName);
			AddTaskDialogPanel panel = new AddTaskDialogPanel(mspec);
			int selection = JOptionPane.showConfirmDialog(me.getParent(),
					panel, "New Task Type", JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.OK_OPTION) {
				if (panel.getType().equals("Sporadic"))
					addNewTaskToComponentTypeSolution(mspec.type,
							panel.getName(), panel.getType(),
							panel.getPeriod(), panel.getDeadline(),
							panel.getWCET(), panel.getAssocPortName());
				else if (panel.getType().equals("Periodic"))
					addNewTaskToComponentTypeSolution(mspec.type,
							panel.getName(), panel.getType(),
							panel.getPeriod(), panel.getDeadline(),
							panel.getWCET(), null);
				addTaskToComponentUI(mspec.type, panel.isPeriodic(),
						panel.getName(), panel.getType(), panel.getPeriod(),
						panel.getDeadline(), panel.getWCET());
				configurationPanel.reRender();
			}
		}
	}

	class addPortToComponentMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] componentTypeRole = tree.getLastSelectedPathComponent()
					.toString().split(":");
			String componentTypeName = componentTypeRole[0];

			ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes
					.get(componentTypeName);
			AddPortDialogPanel panel = new AddPortDialogPanel(mspec);
			int selection = JOptionPane.showConfirmDialog(me.getParent(),
					panel, "New Port Type", JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.OK_OPTION) {
				addNewPortToComponentTypeSolution(mspec.type, panel.getName(),
						panel.isPublish(), panel.getPortTypeSpec());
				addPortToComponentUI(mspec.type, panel.getName(),
						panel.getPortTypeSpec(), panel.isPublish(), false);
				configurationPanel.reRender();
			}
		}
	}

	class editComponentTypeMenuListener implements ActionListener {
		JTree tree;

		public void setTree(JTree tree) {

			this.tree = tree;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode o = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			String[] compNameRole = o.toString().split(":");
			String compType = compNameRole[0];

			ModuleSpec spec = WorkspaceContext.currentWorkspace.moduleTypes
					.get(compType);
			if (spec == null)
				return;

			EditComponentTypePanel panel = new EditComponentTypePanel(compType, spec.role);
			int selection = JOptionPane.showConfirmDialog(me.getParent(),
					panel, "Edit Component Type", JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.OK_OPTION) {
				if (!compType.equals(panel.getTypeName())
						|| !spec.role.equals(panel.getRoleName())) {
					ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes.get(compType);
					mspec.type = panel.getTypeName();
					mspec.sig.setType(panel.getTypeName());
					mspec.role = panel.getRoleName();

					WorkspaceContext.currentWorkspace.moduleTypes.remove(compType);
					WorkspaceContext.currentWorkspace.moduleTypes.put(mspec.type, mspec);

					modifyComponentToUI(mspec, o, compType);
				}
			}
		}
	}

	class deleteComponentTypeMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			String[] componentTypeRole = tree.getLastSelectedPathComponent().toString().split(":");
			String componentTypeName = componentTypeRole[0];

			TreePath tp = tree.getSelectionPath();
			List<Pair<String, String>> instancesToDelete = WorkspaceContext.currentWorkspace
					.getCompInstancesPerType(componentTypeName);

			// need to delete the related channel
			// for all the channel that this component has
			ModuleSpec spec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentTypeName);
			List<PortName> rnames = spec.sig.getRecvPortNames();
			for (Iterator<PortName> portIter = rnames.iterator(); portIter.hasNext();) {
				String portName = portIter.next().getName();
				deleteRelatedChannelFromAppConfig(componentTypeName, portName);
			}

			List<PortName> snames = spec.sig.getSendPortNames();
			for (Iterator<PortName> portIter = snames.iterator(); portIter.hasNext();) {
				String portName = portIter.next().getName();
				deleteRelatedChannelFromAppConfig(componentTypeName, portName);
			}

			WorkspaceContext.currentWorkspace.deleteComponentType(componentTypeName);
			for (Pair<String, String> pair : instancesToDelete) {
				delCompInstFromUI(pair.fst, pair.snd);
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
			TreeNode parent = node.getParent();
			node.removeFromParent();
			model.reload(parent);
			model.reload(configurationsTree);
			configurationPanel.reRender();
		}

	}

	class modifyComponentTypePortMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode o = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (o != null) {
				TreeNode parent = o.getParent();

				String[] componentTypeRole = parent.toString().split(":");
				String componentTypeName = componentTypeRole[0];

				String portName = tree.getLastSelectedPathComponent().toString();
				String[] portTok = portName.split(":");
				// Port Format (PortName:Type:[S|P])
				// Task Format (TaskName:[S|P]Period\p:Deadline\d:WCET\e)
				if (portTok.length == 3) {
					// We have a port
					ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentTypeName);
					EditPortDialogPanel panel = new EditPortDialogPanel(mspec, portTok[0], portTok[1]);
					int selection = JOptionPane.showConfirmDialog(
							me.getParent(), panel, "Edit Port Type",
							JOptionPane.OK_CANCEL_OPTION);
					if (selection == JOptionPane.OK_OPTION) {
						deleteRelatedChannelFromAppConfig(mspec.type, portTok[0]);
						editPortToComponentType(mspec, mspec.type, portTok[0],
								panel.getName(), panel.isPublish(),
								panel.getPortTypeSpec(),
								panel.isDirectionChanged());
						editPortToComponentUI(o, mspec.type, panel.getName(),
								panel.getPortTypeSpec(), panel.isPublish());
					}
				} else {
					// We have a task
					ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentTypeName);
					EditTaskDialogPanel panel = new EditTaskDialogPanel(mspec,
							portTok[0], portTok[1], portTok[2], portTok[3]);
					int selection = JOptionPane.showConfirmDialog(
							me.getParent(), panel, "Edit Task Type",
							JOptionPane.OK_CANCEL_OPTION);
					if (selection == JOptionPane.OK_OPTION) {
						deleteRelatedChannelFromAppConfig(mspec.type, portTok[0]);
						mspec.sig.removeTaskByName(portTok[0]);
						removeTaskFromComponentUI(componentTypeName, portName);
						if (panel.getType().equals("Sporadic"))
							addNewTaskToComponentTypeSolution(mspec.type,
									panel.getName(), panel.getType(),
									panel.getPeriod(), panel.getDeadline(),
									panel.getWCET(), panel.getAssocPortName());
						else if (panel.getType().equals("Periodic"))
							addNewTaskToComponentTypeSolution(mspec.type,
									panel.getName(), panel.getType(),
									panel.getPeriod(), panel.getDeadline(),
									panel.getWCET(), null);
						addTaskToComponentUI(mspec.type, panel.isPeriodic(),
								panel.getName(), panel.getType(),
								panel.getPeriod(), panel.getDeadline(),
								panel.getWCET());
						configurationPanel.reRender();
					}
				}
			}
		}
	}

	class deleteComponentTypePortMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String fullPortName = tree.getLastSelectedPathComponent().toString();
			String[] portTok = fullPortName.split(":");

			String portName = portTok[0];
			TreePath tp = tree.getSelectionPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
			TreeNode parent = node.getParent();

			String[] componentTypeRole = parent.toString().split(":");
			String compTypeName = componentTypeRole[0];

			// Port Format (PortName:Type:[S|P])
			// Task Format (TaskName:[S|P]Period\p:Deadline\d:WCET\e)
			if (portTok.length == 3) {
				deleteRelatedChannelFromAppConfig(compTypeName, portName);
				WorkspaceContext.currentWorkspace.deleteComponentTypePubPort(compTypeName, portName);
				WorkspaceContext.currentWorkspace.deleteComponentTypeSubPort(compTypeName, portName);
				node.removeFromParent();
				model.reload(parent);
				model.reload(configurationsTree);
				configurationPanel.reRender();
				// the port doesn't get deleted from the configurations
			} else {
				// We have a task
				ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes.get(compTypeName);
				// Borrowed from http://stackoverflow.com/questions/5235871
				int reply = JOptionPane.showConfirmDialog(null,
						"Are you sure you want to delete the task \""
								+ portTok[0] + "\"?", "Confirm Deletion?",
						JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					deleteRelatedChannelFromAppConfig(mspec.type, portTok[0]);
					mspec.sig.removeTaskByName(portTok[0]);
					removeTaskFromComponentUI(compTypeName, fullPortName);
					configurationPanel.reRender();
				}
			}
		}
	}

	private void deleteRelatedChannelFromAppConfig(String componentType,
			String portName) {
		Set<String> configurationNames = WorkspaceContext.currentWorkspace.configurations.keySet();
		for (String configurationName : configurationNames) {
			ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations.get(configurationName);
			Set<String> localCompNames = sspec.configurationComponents.keySet();
			for (String localName : localCompNames) {
				ModuleSpec mspec = sspec.configurationComponents.get(localName).fst;
				if (mspec.type.equals(componentType)) {
					Map<String, Channel> channels = sspec.channels;
					Set<String> chanNames = channels.keySet();
					List<String> remChan = new ArrayList<String>();
					for (String chanName : chanNames) {
						Channel chan = channels.get(chanName);
						if (chan.getPubName().equals(portName)
								|| chan.getSubName().equals(portName)) {
							DefaultMutableTreeNode node = configurationConnInstancesMap.get(configurationName);
							DefaultMutableTreeNode chanNode = connectionInstances.get(configurationName).get(chan.toString());
							node.remove(chanNode);
							remChan.add(chanName);
							connectionInstances.get(configurationName).remove(chan.toString());
						}
					}
					for (String chanName : remChan) {
						channels.remove(chanName);
					}
				}
			}
		}
	}

	private void deleteRelatedChannelFromGivenAppConfig(String appConfigName,
			String componentType, String portName) {
		ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations.get(appConfigName);
		Set<String> localCompNames = sspec.configurationComponents.keySet();
		for (String localName : localCompNames) {
			ModuleSpec mspec = sspec.configurationComponents.get(localName).fst;
			if (mspec.type.equals(componentType)) {
				Map<String, Channel> channels = sspec.channels;
				Set<String> chanNames = channels.keySet();
				List<String> remChan = new ArrayList<String>();
				for (String chanName : chanNames) {
					Channel chan = channels.get(chanName);
					if (chan.getPubName().equals(portName)
							|| chan.getSubName().equals(portName)) {
						DefaultMutableTreeNode node = configurationConnInstancesMap.get(appConfigName);
						DefaultMutableTreeNode chanNode = connectionInstances.get(appConfigName).get(chan.toString());
						node.remove(chanNode);
						remChan.add(chanName);
						connectionInstances.get(appConfigName).remove(chan.toString());
					}
				}
				for (String chanName : remChan) {
					channels.remove(chanName);
				}
			}
		}
	}

	class generateDeviceComponentSignatureMenuListener implements
			ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] componentTypeRole = tree.getLastSelectedPathComponent().toString().split(":");
			String componentType = componentTypeRole[0];

			ModuleSpec cspec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentType);
			if (cspec == null) {
				LogMethod.log("can't retrieve type: " + componentType);
				return;
			}

			final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);

			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File name) {
					return (name.isDirectory() || name.getAbsolutePath().endsWith(FileConstants.COMPONENTSIG));
				}

				@Override
				public String getDescription() {
					return "Component Signature Files *" + FileConstants.COMPONENTSIG;
				}
			});
			fc.setSelectedFile(new File(componentType + FileConstants.COMPONENTSIG));

			int returnVal = fc.showSaveDialog(me);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String fileName = file.getAbsolutePath();
				try {
					if (fileName.endsWith(FileConstants.COMPONENTSIG)) {
						// sigGen.saveSignature(fileName, cspec, true);
					} else {
						// sigGen.saveSignature(fileName
						// + FileConstants.COMPONENTSIG, cspec, true);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class generateServerComponentSignatureMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			TreeNode tn = (TreeNode) tree.getLastSelectedPathComponent();
			String appName = tn.getParent().getParent().toString();
			String instAndType = tree.getLastSelectedPathComponent().toString();
			generateServerComponentSignature(appName, instAndType, true);
		}
	}

	private boolean generateServerComponentSignature(String appName, String instAndType, boolean useFileChooser) {
		String[] instAndTypeArray = instAndType.split(":");
		String componentType = instAndTypeArray[1];

		ModuleSpec cspec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentType);
		if (cspec == null) {
			LogMethod.log("can't retrieve type: " + componentType);
			return false;
		}

		final JFileChooser fc;
		final String defaultPath;
		if (WorkspaceContext.configToAppArchiveLocations.containsKey(appName)) {
			// if app archive folder is set then bring it there
			defaultPath = WorkspaceContext.configToAppArchiveLocations.get(
					appName).getAbsolutePath()
					+ FileConstants.SKELETONFOLDER
					+ File.separator
					+ appName + FileConstants.APPCOMPFOLDER;
			fc = new JFileChooser(defaultPath);
		} else {
			// if not then just bring to app dev path
			fc = new JFileChooser(WorkspaceContext.appDevPath);
			defaultPath = null;
		}

		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File name) {
				return (name.isDirectory() || name.getAbsolutePath().endsWith(
						FileConstants.COMPONENTSIG));
			}

			@Override
			public String getDescription() {
				return "Component Signature Files *"
						+ FileConstants.COMPONENTSIG;
			}
		});
		fc.setSelectedFile(new File(componentType + FileConstants.COMPONENTSIG));

		int returnVal;
		if (useFileChooser) {
			returnVal = fc.showSaveDialog(me);
		} else {
			returnVal = (defaultPath == null) ? JFileChooser.ERROR_OPTION : JFileChooser.APPROVE_OPTION;
		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = useFileChooser ? fc.getSelectedFile() : new File(defaultPath);
			String fileName = file.getAbsolutePath();
			try {
				if (fileName.endsWith(FileConstants.COMPONENTSIG)) {
					// sigGen.saveSignature(fileName, cspec, false);
				} else {
					// sigGen.saveSignature(fileName
					// + FileConstants.COMPONENTSIG, cspec, false);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
		}
		return true;
	}

	class generateDeviceComponentSkelMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] componentTypeRole = tree.getLastSelectedPathComponent().toString().split(":");
			String componentType = componentTypeRole[0];

			ModuleSpec cspec = WorkspaceContext.currentWorkspace.moduleTypes.get(componentType);
			if (cspec == null) {
				LogMethod.log("can't retrieve type: " + componentType);
				return;
			}

			final JFileChooser fc = new JFileChooser(
					WorkspaceContext.appDevPath);

			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File name) {
					return (name.isDirectory() || name.getAbsolutePath()
							.endsWith(FileConstants.SKELETON));
				}

				@Override
				public String getDescription() {
					return "Skeleton Files *" + FileConstants.SKELETON;
				}
			});
			String pathname = componentType
			// + JavaJmsComponentSkeletonGenerator.typeNameSuffix
					+ FileConstants.SKELETON;
			fc.setSelectedFile(new File(pathname));

			int returnVal = fc.showSaveDialog(me);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String fileName = file.getAbsolutePath();
				try {
					if (fileName.endsWith(FileConstants.SKELETON)) {
						// skelGen.generateComponentSkeleton(null, fileName,
						// cspec, null, null);
					} else {
						// skelGen.generateComponentSkeleton(null, fileName +
						// FileConstants.SKELETON, cspec, null, null);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class generateServerComponentSkelMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			TreeNode tn = (TreeNode) tree.getLastSelectedPathComponent();
			String[] instAndTypeName = tree.getLastSelectedPathComponent()
					.toString().split(":");
			String instanceName = instAndTypeName[0];
			String componentType = instAndTypeName[1];
			String appName = tn.getParent().getParent().toString();

			ModuleSpec cspec = WorkspaceContext.currentWorkspace.moduleTypes
					.get(componentType);
			if (cspec == null) {
				LogMethod.log("can't retrieve type: " + componentType);
				return;
			}

			final JFileChooser fc;
			if (WorkspaceContext.configToAppArchiveLocations
					.containsKey(appName)) {
				// if app archive folder is set then bring it there
				fc = new JFileChooser(
						WorkspaceContext.configToAppArchiveLocations.get(
								appName).getAbsolutePath()
								+ FileConstants.SKELETONFOLDER
								+ File.separator + appName);
			} else {
				// if not then just bring to app dev path
				fc = new JFileChooser(WorkspaceContext.appDevPath);
			}

			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File name) {
					return (name.isDirectory() || name.getAbsolutePath()
							.endsWith(FileConstants.SKELETON));
				}

				@Override
				public String getDescription() {
					return "Skeleton Files *" + FileConstants.SKELETON;
				}
			});
			String pathname = componentType
			// + JavaJmsComponentSkeletonGenerator.typeNameSuffix
					+ FileConstants.SKELETON;
			fc.setSelectedFile(new File(pathname));

			int returnVal = fc.showSaveDialog(me);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				String fileName = file.getAbsolutePath();
				try {
					// need to prepare the exception list and generate the code
					if (fileName.endsWith(FileConstants.SKELETON)) {
						// skelGen.generateComponentSkeleton(appName, fileName,
						// cspec,
						// WorkspaceContext.currentWorkspace.configurations.get(appName),
						// instanceName);
					} else {
						// skelGen.generateComponentSkeleton(appName, fileName +
						// FileConstants.SKELETON, cspec,
						// WorkspaceContext.currentWorkspace.configurations.get(appName),
						// instanceName);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	class manageExceptionHandlingMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			TreeNode tn = (TreeNode) tree.getLastSelectedPathComponent();
			String[] instAndTypeName = tree.getLastSelectedPathComponent()
					.toString().split(":");
			String componentType = instAndTypeName[1];
			String instName = instAndTypeName[0];
			String appName = tn.getParent().getParent().toString();

			ModuleSpec cspec = WorkspaceContext.currentWorkspace.moduleTypes
					.get(componentType);
			if (cspec == null) {
				LogMethod.log("can't retrieve type: " + componentType);
				return;
			}

			// Get the exception list
			// new ExceptionHandlingDialog(appName, instName);

			// Make the change in the connections
			// add or delete exception ports
			ConfigurationSpec configspec = WorkspaceContext.currentWorkspace.configurations
					.get(appName);
			// Map<Pair<String, String>, PdeHandledExceptions> exceps =
			// configspec.getHandledExceptions();
			DefaultMutableTreeNode node = configurationExceptionHandlingMap
					.get(appName);

			// cleanup all the children to add new ones
			node.removeAllChildren();
			exceptionInstances.get(appName).clear();

			// add up all the new children under the "event handler" node
			// null check added for backward compatibility
			// if(exceps != null){
			// //initialize all the server components exception handlers
			// for(PdeHandledExceptions p: exceps.values()){
			// Pair<ModuleSpec, ComponentLocation> componentPair
			// =
			// configspec.configurationComponents.get(p.getExceptionDestName());
			// ModuleSpec compSpec = componentPair.fst;
			// if(compSpec.sig.getExceptionPortName() != null){
			// compSpec.sig.setExceptionPortName(null);
			// }
			// }
			//
			// for(PdeHandledExceptions p: exceps.values()){
			// // if(p.getHandledExceptionNames().isEmpty()){
			// // continue;
			// // } else {
			// // Add PortSpec.ExceptionOutPortName port to component if it
			// is not there (backward compatibility)
			// Pair<ModuleSpec, ComponentLocation> devicePair
			// =
			// configspec.configurationComponents.get(p.getExceptionOrigName());
			// ModuleSpec devSpec = devicePair.fst;
			// if(devSpec.sig.getExceptionPortName() == null){
			// devSpec.sig.setExceptionPortName(new
			// PortName(PortSpec.ExceptionOutPortName));
			// }
			//
			// // Add PortSpec.ExceptionInPortName port to component if it is
			// not there
			// Pair<ModuleSpec, ComponentLocation> componentPair
			// =
			// configspec.configurationComponents.get(p.getExceptionDestName());
			// ModuleSpec compSpec = componentPair.fst;
			// if(compSpec.sig.getExceptionPortName() == null){
			// compSpec.sig.setExceptionPortName(new
			// PortName(PortSpec.ExceptionInPortName));
			// //LinkedList<PredefinedExceptions> handledExceptionTypes = new
			// LinkedList<PredefinedExceptions>();
			// //handledExceptionTypes.addAll(p.getHandledExceptionNames());
			// //compSpec.sig.setHandledExceptionTypes(handledExceptionTypes);
			// // }
			// }
			//
			// String exceptionHandling = p.toString();
			// if
			// (!exceptionInstances.get(appName).containsKey(exceptionHandling))
			// {
			// DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
			// exceptionHandling);
			//
			// node.add(newNode);
			// exceptionInstances.get(appName).put(exceptionHandling, newNode);
			// }
			// }
			// }
			// reload the model
			model.reload(node);

			// rerender the configuration
			configurationPanel.reRender();
		}

	}

	class generateAppConfigMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			generateAndSaveAppConfig(getLastSelectedConfigName());
		}
	}

	class SaveAndGenAllNonDeviceFilesMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String appConfigName = generateAppArchiveStructure();
			if (appConfigName == null) {
				JOptionPane.showMessageDialog(me,
						"Could not create app directory structure.");
				return;
			}
			boolean status = generateAndSaveAppConfig(appConfigName);
			if (!status) {
				return;
			}
			System.out.println("AppConfig generated for " + appConfigName);
			// Proceed to export all other files.
			// WorkspaceContext.configToAppArchiveLocations.put(appConfName,
			// file);
			ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations
					.get(appConfigName);
			Map<String, Pair<ModuleSpec, ComponentLocation>> compInfo = sspec.configurationComponents;
			if (compInfo == null)
				return;
			Set<String> compSet = compInfo.keySet();
			for (String comp : compSet) {
				status = generateServerComponentSignature(appConfigName, comp,
						false);
				if (!status)
					break;
				System.out.println("Component signature generated for " + comp);
				// FIXME: finish implementation.
			}
		}
	}

	private String getLastSelectedConfigName() {
		String appConfigName = tree.getLastSelectedPathComponent().toString();
		return appConfigName;
	}

	private boolean generateAndSaveAppConfig(String appConfigName) {
		ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations.get(appConfigName);
		if (sspec == null) {
			LogMethod.log("can't retrieve app spec: " + appConfigName);
			return false;
		}

		final JFileChooser fc;
		if (WorkspaceContext.configToAppArchiveLocations
				.containsKey(appConfigName)) {
			// if app archive folder is set then bring it there
			fc = new JFileChooser(WorkspaceContext.configToAppArchiveLocations
					.get(appConfigName).getAbsolutePath()
					+ FileConstants.SKELETONFOLDER
					+ File.separator
					+ appConfigName + FileConstants.APPCONFIGFOLDER);
		} else {
			// if not then just bring to app dev path
			fc = new JFileChooser(WorkspaceContext.appDevPath);
		}

		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File name) {
				return (name.isDirectory() || name.getAbsolutePath().endsWith(
						FileConstants.APPCONFIGURATION));
			}

			@Override
			public String getDescription() {
				return "App Configuration Files *"
						+ FileConstants.APPCONFIGURATION;
			}
		});
		fc.setSelectedFile(new File(appConfigName + FileConstants.APPCONFIGURATION));

		int returnVal = fc.showSaveDialog(me);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			File dir = fc.getCurrentDirectory();
			System.out.println("DIR: " + dir);
			String abs = dir.getAbsolutePath();
			System.out.println(file);
			String fileName = file.getAbsolutePath();
			ROSModelCompiler builder = new ROSModelCompiler();
			Map<String, String> codez = builder.generateNodeFiles(appConfigName);
			for (String inst : codez.keySet()) {
				String package_dir = abs + File.separator + inst;
				String src_dir = package_dir + File.separator + "src";
				File dirp = new File(package_dir);
                dirp.mkdirs();
				dirp = new File(src_dir);
                dirp.mkdirs();

				// Generating Source Code
                File outFileName;

                if (inst.contains("GUI") || inst.contains("gui")) {
                    outFileName = new File(src_dir + File.separator + inst + ".py");
                    System.out.println("outfilename: " + outFileName);
                    try {
                        FileIO.saveText(outFileName, codez.get(inst));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                outFileName = new File(src_dir + File.separator + inst + ".cpp");
				System.out.println("outfilename: " + outFileName);
				try {
					FileIO.saveText(outFileName, codez.get(inst));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// Generating Manifest.xml
				outFileName = new File(package_dir + File.separator + "manifest.xml");
				String manifest = 
						"<package>\n" + 
 						"<description brief=\"" + inst + "\">\n" +
  			    		inst + "\n" + 
  			    		"</description>\n" + 
						"<author>PRECISE</author>\n" +
						"<license>BSD</license>\n" +
						"<review status=\"unreviewed\" notes=\"\"/>\n" +
						"<url>http://ros.org/wiki/" + inst + "</url>\n" +
						"<depend package=\"roscpp\"/>\n" +
						"<depend package=\"quadrotor_msgs\"/>\n" +
						"<depend package=\"sensor_msgs\"/>\n"+
						"<depend package=\"geometry_msgs\"/>\n"+
						//"<depend package=\"nav_msgs\"/>\n"+
						"</package>\n";
						
				try {
					FileIO.saveText(outFileName, manifest);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// Generating CMakeList.txt
				outFileName = new File(package_dir + File.separator + "CMakeLists.txt");
				String cmakelist = 
						"cmake_minimum_required(VERSION 2.4.6)\n" + 
						"include($ENV{ROS_ROOT}/core/rosbuild/rosbuild.cmake)\n" +
						"rosbuild_init()\n" +
						"set(EXECUTABLE_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/bin)\n" +
						"set(LIBRARY_OUTPUT_PATH ${PROJECT_SOURCE_DIR}/lib)\n" +
						"rosbuild_add_executable(" + inst + " src/" + inst + ".cpp)\n";
				try {
					FileIO.saveText(outFileName, cmakelist);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// Generating Makefile
				outFileName = new File(package_dir + File.separator + "Makefile");
				String makefile = "include $(shell rospack find mk)/cmake.mk\n";
				try {
					FileIO.saveText(outFileName, makefile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
									
			
			System.out.println(codez);
			try {
				if (fileName.endsWith(FileConstants.APPCONFIGURATION)) {
					// appConfigGen.saveAppConfig(fileName, sspec);
				} else {
					// appConfigGen.saveAppConfig(fileName +
					// FileConstants.APPCONFIGURATION, sspec);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return true;
	}

	/** Returns appConfigName if successful, null otherwise. */
	private String generateAppArchiveStructure() {
		String appConfName = getLastSelectedConfigName();

		final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File name) {
				return name.isDirectory();
			}

			@Override
			public String getDescription() {
				return "App Archive Workspace Folder";
			}
		});

		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			File appCompPackageStructure = new File(file.getAbsolutePath()
					+ FileConstants.SKELETONFOLDER + File.separator
					+ appConfName);
			appCompPackageStructure.mkdirs();

			File appCfgStructure = new File(file.getAbsolutePath()
					+ FileConstants.SKELETONFOLDER + File.separator
					+ appConfName + FileConstants.APPCONFIGFOLDER);
			appCfgStructure.mkdirs();

			File appCompStructure = new File(file.getAbsolutePath()
					+ FileConstants.SKELETONFOLDER + File.separator
					+ appConfName + FileConstants.APPCOMPFOLDER);
			appCompStructure.mkdirs();

			/*
			 * Removing app meta data File appDescStructure = new
			 * File(file.getAbsolutePath() + FileConstants.DESCFOLDER);
			 * appDescStructure.mkdirs();
			 */

			JOptionPane
					.showMessageDialog(
							me,
							"Created App Structure for "
									+ appConfName
									+ ". "
									+ "Use the \'Refresh\' option in Eclipse to expose the generated files in your Pakcage Explorer.");

			if (WorkspaceContext.configToAppArchiveLocations == null) {
				WorkspaceContext.configToAppArchiveLocations = new HashMap<String, File>();
			}

			WorkspaceContext.configToAppArchiveLocations.put(appConfName, file);
		}
		return appConfName;
	}

	class generateAppArchiveStructureMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			generateAppArchiveStructure();
		}
	}

	class checkConsistencyMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String appConfName = tree.getLastSelectedPathComponent().toString();

			StringBuffer filenameBuffer = new StringBuffer();
			ArrayList<String> serverComponentTypeNames = new ArrayList<String>();
			ArrayList<String> deviceComponentTypeNames = new ArrayList<String>();

			// Read App Configuration
			// * Get the list of server components
			File folder = WorkspaceContext.configToAppArchiveLocations
					.get(appConfName);

			if (folder == null) {
				JOptionPane.showMessageDialog(me, "Set App Archive Folder for "
						+ appConfName + " before the consistency check.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (!readAppConfig(appConfName, folder, serverComponentTypeNames,
					deviceComponentTypeNames, filenameBuffer)) {
				return;
			}

			// Check the Existence of Components Signatures
			if (!checkConsistency(appConfName, folder,
					serverComponentTypeNames, filenameBuffer)) {
				return;
			}

			JOptionPane.showMessageDialog(me,
					"Consistency Check Successful for " + appConfName + ".",
					"Information", JOptionPane.INFORMATION_MESSAGE);

		}

	}

	private boolean readAppConfig(String appConfName, File folder,
			ArrayList<String> serverComponentTypeNames,
			ArrayList<String> deviceComponentTypeNames,
			StringBuffer filenameBuffer) {
		File appconfigFolder = new File(folder.getAbsolutePath()
				+ FileConstants.SKELETONFOLDER + File.separator
				+ appConfName + FileConstants.APPCONFIGFOLDER);

		java.io.FileFilter ff = new java.io.FileFilter() {
			@Override
			public boolean accept(File name) {
				return (name.getName()
						.endsWith(FileConstants.APPCONFIGURATION));
			}
		};

		File[] files = appconfigFolder.listFiles(ff);
		if (files.length == 0) {
			// error: only one app configuration should be here
			JOptionPane.showMessageDialog(this,
					"At least one app configuration is needed in "
							+ FileConstants.SKELETONFOLDER + File.separator
							+ appConfName + FileConstants.APPCONFIGFOLDER,
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} else if (files.length > 1) {
			// error: only one app configuration should be here
			JOptionPane.showMessageDialog(this,
					"Only one app configuration is allowed in "
							+ FileConstants.SKELETONFOLDER + File.separator
							+ appConfName + FileConstants.APPCONFIGFOLDER,
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}

		// AppSpec appSpec = FileIO.loadAppConfigurationFromFile(files[0]);
		// for( Iterator<VirtualComponent> compsIter =
		// appSpec.getComponents().iterator(); compsIter.hasNext(); ){
		// VirtualComponent comp = compsIter.next();
		// if(comp.getRole().equals(CompRoles.DEVICE_NAME)){
		// deviceComponentTypeNames.add(comp.getType());
		// } else {
		// serverComponentTypeNames.add(comp.getType());
		// }
		// }

		// Collect the absolute path of the archive files
		filenameBuffer.append("."
				+ files[0].getAbsolutePath().substring(
						folder.getAbsolutePath().length()) + " ");

		return true;
	}

	private boolean checkConsistency(String appConfName, File folder,
			ArrayList<String> serverComponentTypeNames,
			StringBuffer filenameBuffer) {
		File compSigFolder = new File(folder.getAbsolutePath()
				+ FileConstants.SKELETONFOLDER + File.separator
				+ appConfName + FileConstants.APPCOMPFOLDER);

		java.io.FileFilter ff = new java.io.FileFilter() {
			@Override
			public boolean accept(File name) {
				return (name.getName().endsWith(FileConstants.COMPONENTSIG));
			}
		};

		File[] files = compSigFolder.listFiles(ff);

		ArrayList<String> compsigList = new ArrayList<String>();
		for (int index = 0; index < files.length; index++) {
			// ComponentSignature compsig =
			// FileIO.loadSignatureFromFile(files[index]);

			// if(serverComponentTypeNames.contains(compsig.getType())){
			// compsigList.add(compsig.getType());
			// //Collect the absolute path of the archive files
			// filenameBuffer.append("." +
			// files[index].getAbsolutePath().substring(folder.getAbsolutePath().length())
			// + " ");
			// }
		}

		if (!compsigList.containsAll(serverComponentTypeNames)) {
			// error: folder should have all the necessary component signature,
			// something is missing here.
			// this is the list of missing components:
			// serverComponentTypeNames.removeAll(compsigList);
			serverComponentTypeNames.removeAll(compsigList);
			JOptionPane.showMessageDialog(this, serverComponentTypeNames
					+ ":Component Signature Missing", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

	class myComponentListener implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentResized(ComponentEvent e) {
			configurationPanel.setSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

	}

	private void saveAction() {
		if (WorkspaceContext.currentWorkspace != null
				&& WorkspaceContext.currentWorkspaceFileName != null) {
			try {
				FileIO.saveObject(new File(
						WorkspaceContext.currentWorkspaceFileName),
						WorkspaceContext.currentWorkspace);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error saving workspace");
				e.printStackTrace();
			}
		} else if (WorkspaceContext.currentWorkspace != null) {
			final JFileChooser fc = new JFileChooser(
					WorkspaceContext.appDevPath);
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File name) {
					return (name.isDirectory() || name.getAbsolutePath()
							.endsWith(FileConstants.WORKSPACE));
				}

				@Override
				public String getDescription() {
					return "Workspace Files *" + FileConstants.WORKSPACE;
				}
			});

			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					if (file.getName().endsWith(FileConstants.WORKSPACE)) {
						FileIO.saveObject(file.getName(),
								WorkspaceContext.currentWorkspace);
					} else {
						FileIO.saveObject(file.getName()
								+ FileConstants.WORKSPACE,
								WorkspaceContext.currentWorkspace);
					}
					WorkspaceContext.currentWorkspaceFileName = file
							.getAbsolutePath();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this,
							"Error saving workspace");
					e.printStackTrace();
				}
			}
		}
	}

	private void addNewTaskToComponentTypeSolution(String compType,
			String name, String type, int period, int deadline, int wcet,
			String trigPortName) {
		ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes
				.get(compType);
		ComponentSignature csig = mspec.sig;
		csig.addTaskDescriptor(new TaskDescriptor(name, type, period,
				deadline, wcet, trigPortName));
	}

	private void addNewPortToComponentTypeSolution(String compType,
			String portName, boolean publish, PortSpec pspec) {
		ModuleSpec mspec = WorkspaceContext.currentWorkspace.moduleTypes
				.get(compType);
		ComponentSignature csig = mspec.sig;
		if (publish) {
			List<PortName> portNames = csig.getSendPortNames();
			List<PortType> portTypes = csig.getSendPortTypes();
			portNames.add(new PortName(portName));
			portTypes.add(new PortType(pspec.typeName));
		} else {
			List<PortName> portNames = csig.getRecvPortNames();
			List<PortType> portTypes = csig.getRecvPortTypes();
			portNames.add(new PortName(portName));
			portTypes.add(new PortType(pspec.typeName));
		}
	}

	public void addPortToComponentUI(String typeName, String portName,
			PortSpec pspec, boolean publish, boolean rosService) {
		if (treeNodeCompNamesMap.containsKey(typeName) & !rosService) {
			DefaultMutableTreeNode root = treeNodeCompNamesMap.get(typeName);
			String direction = publish ? "P" : "S";

			System.out.println(portName);

			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					portName + ":" + pspec.typeName + ":" + direction);
			root.add(newNode);
			model.reload(root);
		} else if (treeNodeServicesNamesMap.containsKey(typeName) & rosService) {
			DefaultMutableTreeNode root = treeNodeServicesNamesMap
					.get(typeName);
			String direction = publish ? "P" : "S";

			System.out.println(portName);

			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					portName + ":" + pspec.typeName + ":" + direction);
			root.add(newNode);
			model.reload(root);
		}
	}

	public void addTaskToComponentUI(String typeName, boolean periodic,
			String name, String type, int period, int deadline, int wcet) {
		if (treeNodeCompNamesMap.containsKey(typeName)) {
			DefaultMutableTreeNode root = treeNodeCompNamesMap.get(typeName);
			String direction = periodic ? "P" : "S";

			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name
					+ ":" + direction + period + "p:" + deadline + "d:" + wcet
					+ "e");
			root.add(newNode);
			model.reload(root);
		}
	}

	public void removeTaskFromComponentUI(String typeName, String nodeLabel) {
		if (treeNodeCompNamesMap.containsKey(typeName)) {
			DefaultMutableTreeNode root = treeNodeCompNamesMap.get(typeName);
			DefaultMutableTreeNode currentChild = (DefaultMutableTreeNode) root
					.getFirstChild();
			do {
				if (currentChild.getUserObject().equals(nodeLabel)) {
					root.remove(currentChild);
					currentChild = null;
				} else
					currentChild = (DefaultMutableTreeNode) root
							.getChildAfter(currentChild);
			} while (currentChild != null);
			model.reload(root);
		}
	}

	public void addExceptionPortToComponentUI(String typeName) {
		if (treeNodeCompNamesMap.containsKey(typeName)) {
			DefaultMutableTreeNode root = treeNodeCompNamesMap.get(typeName);
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
					PortSpec.ExceptionOutPortName + ":E");
			root.add(newNode);
			model.reload(root);
		}
	}

	private void editPortToComponentType(ModuleSpec spec, String compType,
			String originalPortName, String portName, boolean publish,
			PortSpec pspec, boolean directionChange) {
		ModuleSpec mspec = spec;
		ComponentSignature csig = mspec.sig;

		if (publish) {
			if (directionChange) {
				// find the port information from subscriber
				List<PortName> portNames = csig.getRecvPortNames();
				List<PortType> portTypes = csig.getRecvPortTypes();
				int portIndex = portNames.indexOf(new PortName(
						originalPortName));
				PortName port = portNames.get(portIndex);
				PortType type = portTypes.get(portIndex);

				// apply the change
				port.setName(portName);
				type.setType(pspec.typeName);

				// move to the publisher
				csig.getSendPortNames().add(port);
				csig.getSendPortTypes().add(type);

				// remove from the subscriber
				csig.getRecvPortNames().remove(port);
				csig.getRecvPortTypes().remove(type);
			} else {
				// find the port information from publisher
				List<PortName> portNames = csig.getSendPortNames();
				List<PortType> portTypes = csig.getSendPortTypes();
				int portIndex = portNames.indexOf(new PortName(originalPortName));
				PortName port = portNames.get(portIndex);
				PortType type = portTypes.get(portIndex);

				// apply the change
				port.setName(portName);
				type.setType(pspec.typeName);
			}
		} else {
			if (directionChange) {
				// find the port information from publisher
				List<PortName> portNames = csig.getSendPortNames();
				List<PortType> portTypes = csig.getSendPortTypes();
				int portIndex = portNames.indexOf(new PortName(originalPortName));
				PortName port = portNames.get(portIndex);
				PortType type = portTypes.get(portIndex);

				// apply the change
				port.setName(portName);
				type.setType(pspec.typeName);

				// move to the subscriber
				csig.getRecvPortNames().add(port);
				csig.getRecvPortTypes().add(type);

				// remove from the subscriber
				csig.getSendPortNames().remove(port);
				csig.getSendPortTypes().remove(type);
			} else {
				// find the port information from subscriber
				List<PortName> portNames = csig.getRecvPortNames();
				List<PortType> portTypes = csig.getRecvPortTypes();
				int portIndex = portNames.indexOf(new PortName(originalPortName));
				PortName port = portNames.get(portIndex);
				PortType type = portTypes.get(portIndex);

				// apply the change
				port.setName(portName);
				type.setType(pspec.typeName);
			}
		}
	}

	public void editPortToComponentUI(DefaultMutableTreeNode node,
			String typeName, String portName, PortSpec pspec, boolean publish) {
		if (treeNodeCompNamesMap.containsKey(typeName)) {
			String direction = publish ? "P" : "S";

			node.setUserObject(portName + ":" + pspec.typeName + ":" + direction);
			model.reload(componentTypes);
			model.reload(configurationsTree);
			// update the configuration part
			configurationPanel.reRender();
		}
	}

	public void addPortToComponentUINoReload(String typeName, String portName,
			PortSpec pspec, boolean publish) {
		if (treeNodeCompNamesMap.containsKey(typeName)) {
			DefaultMutableTreeNode root = treeNodeCompNamesMap.get(typeName);
			String direction = publish ? "P" : "S";

			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(portName + ":" + pspec.typeName + ":" + direction);
			root.add(newNode);
			// model.reload(root);
		}
	}

	class addAppConfigMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			AddConfigurationDialogPanel panel = new AddConfigurationDialogPanel();
			int selection = JOptionPane.showConfirmDialog(me.getParent(),
					panel, "New Configuration", JOptionPane.OK_CANCEL_OPTION);
			if (selection == JOptionPane.OK_OPTION) {
				String appConfigName = panel.getName();
				ConfigurationSpec sspec = new ConfigurationSpec(appConfigName);
				addConfigurationToWorkspaceContext(appConfigName, sspec);
				addConfigurationToUI(appConfigName, sspec);
			}
		}
	}

	class deleteAppConfigMenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String appConfName = tree.getLastSelectedPathComponent().toString();

			// Removing the app archive location for this app
			WorkspaceContext.configToAppArchiveLocations.remove(appConfName);

			// remove actual app configuration data
			removeConfigurationFromWorkspaceContext(appConfName);

			// Remove UI portion of the app
			deleteConfigurationfromUI(appConfName);
		}
	}

	class deleteAppConfigComponentMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) selected.getParent().getParent();

			// splitting component instance name
			String[] componentInstType = selected.toString().split(":");
			String componentInstName = componentInstType[0];
			String componentTypeName = componentInstType[1];

			// need to delete the related channel
			// for all the channel that this component has
			ModuleSpec spec = WorkspaceContext.currentWorkspace.moduleTypes
					.get(componentTypeName);
			List<PortName> rnames = spec.sig.getRecvPortNames();
			for (Iterator<PortName> portIter = rnames.iterator(); portIter.hasNext();) {
				String portName = portIter.next().getName();
				deleteRelatedChannelFromGivenAppConfig(tn.toString(), componentTypeName, portName);
			}

			List<PortName> snames = spec.sig.getSendPortNames();
			for (Iterator<PortName> portIter = snames.iterator(); portIter.hasNext();) {
				String portName = portIter.next().getName();
				deleteRelatedChannelFromGivenAppConfig(tn.toString(), componentTypeName, portName);
			}

			componentInstances.get(tn.toString()).remove(componentInstName);
			ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations.get(tn.toString());
			sspec.removeComponent(componentInstName);
			// delCompInstFromUI(tn.toString(), componentInstName);
			configurationPanel.reRender();
			DefaultMutableTreeNode comp = (DefaultMutableTreeNode) selected
					.getParent();
			comp.remove(selected);
			model.reload(comp.getParent());
		}

	}

	class deleteAppConfigConnectionMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) selected
					.getParent().getParent();
			String channelInstName = selected.toString();
			connectionInstances.get(tn.toString()).remove(channelInstName);
			ConfigurationSpec sspec = WorkspaceContext.currentWorkspace.configurations
					.get(tn.toString());
			sspec.removeChannel(channelInstName);
			configurationPanel.reRender();
			DefaultMutableTreeNode comp = (DefaultMutableTreeNode) selected
					.getParent();
			comp.remove(selected);
			model.reload(comp);
		}

	}

	public void addConfigurationToUI(String scenarioName,
			ConfigurationSpec sspec) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
				scenarioName);
		DefaultMutableTreeNode components = new DefaultMutableTreeNode(
				"components");
		DefaultMutableTreeNode connections = new DefaultMutableTreeNode(
				"connections");
		DefaultMutableTreeNode eventHandlings = new DefaultMutableTreeNode(
				"event handlers");
		newNode.add(components);
		newNode.add(connections);
		newNode.add(eventHandlings);
		configurationCompInstancesMap.put(scenarioName, components);
		configurationConnInstancesMap.put(scenarioName, connections);
		configurationExceptionHandlingMap.put(scenarioName, eventHandlings);
		componentInstances.put(scenarioName,
				new HashMap<String, DefaultMutableTreeNode>());
		connectionInstances.put(scenarioName,
				new HashMap<String, DefaultMutableTreeNode>());
		exceptionInstances.put(scenarioName,
				new HashMap<String, DefaultMutableTreeNode>());

		configurationsTree.add(newNode);
		model.reload(configurationsTree);

		configurationPanel.setConfiguration(sspec);
		configurationPanel.reRender();
	}

	public void deleteConfigurationfromUI(String appConfigName) {

		DefaultMutableTreeNode components = configurationCompInstancesMap
				.get(appConfigName);

		// delete the configuration
		configurationsTree.remove((MutableTreeNode) components.getParent());
		configurationCompInstancesMap.remove(appConfigName);
		configurationConnInstancesMap.remove(appConfigName);
		configurationExceptionHandlingMap.remove(appConfigName);
		componentInstances.remove(appConfigName);
		connectionInstances.remove(appConfigName);
		exceptionInstances.remove(appConfigName);
		model.reload(configurationsTree);

		// set current configuration to null
		configurationPanel.setConfiguration(null);
		configurationPanel.reRender();

	}

	private void addConfigurationToWorkspaceContext(String appConfigName,
			ConfigurationSpec sspec) {
		WorkspaceContext.currentWorkspace.configurations.put(appConfigName,
				sspec);
	}

	private void removeConfigurationFromWorkspaceContext(String appConfigName) {
		WorkspaceContext.currentWorkspace.configurations.remove(appConfigName);
	}

	private boolean isSelectionDeviceComponent() {
		DefaultMutableTreeNode o = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		if (o != null) {
			TreeNode parent = o.getParent();
			// FIXME
			if (parent.toString().equals(NODES)) {
				String[] typeNameAndRole = o.toString().split(":");
				String role = typeNameAndRole[1];
				if (role.equals(CompRoles.ROS_NODE)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	private boolean isSelectionServerComponent() {
		DefaultMutableTreeNode o = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (o != null) {
			TreeNode parent = o.getParent();
			if (parent.toString().equals("Component Types")) {
                return false;
            }
		}
		return false;
	}

	private boolean isSelectionExceptionPort() {
		DefaultMutableTreeNode o = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (o != null) {
			String[] nameAndPortType = o.toString().split(":");
			if (nameAndPortType.length != 2) {
				return false;
			}
			String portType = nameAndPortType[1];
			if (portType.equals("E")) {
				TreeNode parent = o.getParent();
				if (parent != null) {
					String[] typeNameAndRole = parent.toString().split(":");
					String role = typeNameAndRole[1];
					if (role.equals(CompRoles.ROS_NODE)) {
						parent = parent.getParent();
						if (parent != null && parent.toString().equals("Component Types")) {
							return true;
						}
					}
				}
			}
		}
		return false;

	}

	private boolean isSelectionComponentPort() {
		DefaultMutableTreeNode o = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		if (o != null) {
			TreeNode parent = o.getParent();
			if (parent != null) {
				parent = parent.getParent();
				if (parent != null && parent.toString().equals("Component Types")) {
					return true;
				}
			}
		}
		return false;
	}

	private ModuleSpec addNewComponentToWorkspace(String typeName,
			String roleName) {
		ArrayList<PortName> sendPortNames = new ArrayList<PortName>();
		ArrayList<PortName> recvPortNames = new ArrayList<PortName>();
		ArrayList<PortType> sendPortTypes = new ArrayList<PortType>();
		ArrayList<PortType> recvPortTypes = new ArrayList<PortType>();
		ArrayList<TaskDescriptor> taskDescriptors = new ArrayList<TaskDescriptor>();
		ComponentSignature csig = new ComponentSignature(typeName,
				sendPortNames, recvPortNames, sendPortTypes, recvPortTypes,
				taskDescriptors);
		ModuleSpec mspec = new ModuleSpec(typeName, roleName, csig);

		if (WorkspaceContext.currentWorkspace != null) {
			WorkspaceContext.currentWorkspace.moduleTypes.put(typeName, mspec);
		}
		return mspec;
	}

	public void addNewComponentToUI(ModuleSpec mspec) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(mspec.type
				+ ":" + mspec.role);
		if (mspec.role.equals("ROS Node")) {
			componentTypes.add(newNode);
			treeNodeCompNamesMap.put(mspec.type, newNode);
		} else if (mspec.role.equals("ROS Service")) {
			treeNodeServicesNamesMap.put(mspec.type, newNode);
			builtInTypes.add(newNode);
		}
		model.reload(componentTypes);
		model.reload(builtInTypes);
	}

	public void modifyComponentToUI(ModuleSpec mspec,
			DefaultMutableTreeNode old, String oldTypeName) {
		old.setUserObject(mspec.type + ":" + mspec.role);
		treeNodeCompNamesMap.remove(mspec.type);
		treeNodeCompNamesMap.put(mspec.type, old);

		// for all the app configurations
		Set<String> configNames = componentInstances.keySet();
		for (Iterator<String> configIter = configNames.iterator(); configIter
				.hasNext();) {
			String configName = configIter.next();

			// for all the instance name group
			// check whether the type part has the name of the changing type
			Set<String> instNames = componentInstances.get(configName).keySet();
			for (Iterator<String> instNameIter = instNames.iterator(); instNameIter
					.hasNext();) {
				String instName = instNameIter.next();
				DefaultMutableTreeNode compNode = componentInstances.get(
						configName).get(instName);
				// if it is change the contents
				if (compNode.toString().split(":")[1].equals(oldTypeName)) {
					compNode.setUserObject(instName + ":" + mspec.type);
				}
			}
		}
		// if the all the loop is done, update the configuration
		model.reload(configurationsTree);

		model.reload(componentTypes);

		// update app configuration UI
		configurationPanel.reRender();
	}

	public void addCompInstToUI(String scenarioName, String instName,
			Pair<ModuleSpec, ComponentLocation> comp) {
		DefaultMutableTreeNode node = configurationCompInstancesMap
				.get(scenarioName);
		if (node != null) {
			if (!componentInstances.get(scenarioName).containsKey(instName)) {
				// adding component instance with its type
				DefaultMutableTreeNode compNode = new DefaultMutableTreeNode(
						instName + ":" + comp.fst.type);
				node.add(compNode);
				model.reload(node.getParent());
				componentInstances.get(scenarioName).put(instName, compNode);
			}
		}
	}

	public void delCompInstFromUI(String appConfigName, String instName) {

		DefaultMutableTreeNode node = configurationCompInstancesMap
				.get(appConfigName);
		if (node != null) {
			if (componentInstances.get(appConfigName).containsKey(instName)) {
				DefaultMutableTreeNode compNode = componentInstances.get(
						appConfigName).get(instName);
				TreeNode parent = compNode.getParent();
				componentInstances.get(appConfigName).remove(instName);

				compNode.removeFromParent();
				model.reload(parent);
			}
		}

	}

	public void addConnInstToUI(String appConfigName, Channel channel) {
		DefaultMutableTreeNode node = configurationConnInstancesMap
				.get(appConfigName);
		String channelS = channel.toString();
		if (node != null) {
			if (!connectionInstances.get(appConfigName).containsKey(channelS)) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
						channelS);
				node.add(newNode);
				connectionInstances.get(appConfigName).put(channelS, newNode);
				model.reload(node);
			}
		}
	}

	public void addExceptionHandlerInstToUI(String appConfigName, String label) {
		DefaultMutableTreeNode node = configurationExceptionHandlingMap
				.get(appConfigName);
		String channelS = label; // channel.toString();
		if (node != null) {
			if (!exceptionInstances.get(appConfigName).containsKey(channelS)) {
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(
						channelS);
				node.add(newNode);
				exceptionInstances.get(appConfigName).put(channelS, newNode);
				model.reload(node);
			}
		}
	}
}
