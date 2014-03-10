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
package roslab;

import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import roslab.artifacts.ConfigurationSpec;
import roslab.artifacts.ModuleSpec;
import roslab.artifacts.PortSpec;
import roslab.artifacts.WorkspaceContext;
import roslab.artifacts.WorkspaceSpec;
import roslab.gui.dialogs.AppDevPathDialog;
import roslab.gui.misc.FileConstants;
import roslab.gui.panels.WorkspaceSplitPane;
import roslab.types.builtins.ComponentSignature;
import roslab.types.builtins.PortName;
import roslab.types.builtins.PortType;
import roslab.types.builtins.PortTypes;
import roslab.types.builtins.TaskDescriptor;
import roslab.utils.FileIO;


public class ROSLab extends JFrame implements WindowListener {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JMenuBar mainMenuBar = null;
	private JMenu fileMenu = null;
	private JMenuItem newMenuItem = null;
	private JMenuItem openMenuItem = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem saveAsMenuItem = null;
	private JMenuItem setAppDevMenuItem = null;
	private JMenuItem quitMenuItem = null;
	
	//private JMenu appArchiveMenu = null;
	//private JMenuItem buildAppArchiveMenuItem = null;
	//private JMenuItem setClassPathMenuItem = null;
	
	private WorkspaceSplitPane wsp;
	

	/**
	 * This method initializes mainMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getMainMenuBar() {
		if (mainMenuBar == null) {
			mainMenuBar = new JMenuBar();
			mainMenuBar.add(getFileMenu());
			//remove from the feature mainMenuBar.add(getAppArchiveMenu());
		}
		return mainMenuBar;
	}

	/**
	 * This method initializes fileMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getNewMenuItem());
			fileMenu.add(getOpenMenuItem());
			fileMenu.add(getSaveMenuItem());
			fileMenu.add(getSaveAsMenuItem());
			//fileMenu.add(setAppDevPathMenuItem());
			fileMenu.add(getQuitMenuItem());
		}
		return fileMenu;
	}
	
	/* Removing app archive menu
	private JMenu getAppArchiveMenu(){
		if (appArchiveMenu == null) {
			appArchiveMenu = new JMenu();
			appArchiveMenu.setText("App Archive");
			appArchiveMenu.add(getBuildAppArchiveMenuItem());
			appArchiveMenu.add(getClassPathMenuItem());
		}
		return appArchiveMenu;
	}
	*/

	/* Removing app archive menu
	private JMenuItem getBuildAppArchiveMenuItem(){
		if (buildAppArchiveMenuItem == null ) {
			buildAppArchiveMenuItem = new JMenuItem();
			buildAppArchiveMenuItem.setText("Build App Archive...");
			buildAppArchiveMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					buildAppArchiveAction();
				}
			});
		}
		return buildAppArchiveMenuItem;
	}
	
	private JMenuItem getClassPathMenuItem(){
		if (setClassPathMenuItem == null ) {
			setClassPathMenuItem = new JMenuItem();
			setClassPathMenuItem.setText("Set Classpath...");
			setClassPathMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setClassPathAction();
				}
			});
		}
		return setClassPathMenuItem;
	}
	*/	

	/**
	 * This method initializes openMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getNewMenuItem() {
		if (newMenuItem == null) {
			newMenuItem = new JMenuItem();
			newMenuItem.setText("New Workspace");
			newMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newAction();
				}
			});
		}
		return newMenuItem;
	}
	
	/**
	 * This method initializes openMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem();
			openMenuItem.setText("Open Workspace...");
			openMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					openAction();
				}
			});
		}
		return openMenuItem;
	}

	/**
	 * This method initializes saveMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem();
			saveMenuItem.setText("Save Workspace");
			saveMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAction();
				}
			});
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes saveAsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveAsMenuItem() {
		if (saveAsMenuItem == null) {
			saveAsMenuItem = new JMenuItem();
			saveAsMenuItem.setText("Save Workspace As...");
			saveAsMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAsAction();
				}
			});
		}
		return saveAsMenuItem;
	}

	/**
	 * This method initializes setAppDevMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem setAppDevPathMenuItem() {
		if (setAppDevMenuItem == null) {
			setAppDevMenuItem = new JMenuItem();
			setAppDevMenuItem.setText("Set App Development Path...");
			setAppDevMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setAppDevPath();
				}
			});
		}
		return setAppDevMenuItem;
	}

	
	
	/**
	 * This method initializes quitMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getQuitMenuItem() {
		if (quitMenuItem == null) {
			quitMenuItem = new JMenuItem();
			quitMenuItem.setText("Quit");
			quitMenuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FileIO.saveUserSettings();
					System.exit(0);
				}
			});
		}
		return quitMenuItem;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		 try {
			UIManager.setLookAndFeel(
			            UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ROSLab thisClass = new ROSLab();
				thisClass.setVisible(true);
			}
		});
	}

	/**
	 * This is the default constructor
	 */
	public ROSLab() {
		super();
		initialize();
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(this);
		FileIO.loadUserSettings();
		if(WorkspaceContext.currentWorkspaceFileName != null){
			File file = new File(WorkspaceContext.currentWorkspaceFileName);
			try {
				WorkspaceSpec sol = FileIO.loadWorkspace(file);
				//WorkspaceSpec sol = new WorkspaceSpec("Untitled");
				wsp.resetSolTree();
				wsp.resetMapsAndSets();
				openSetupContextAndUI(sol, file.getAbsolutePath());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error loading workspace");
			}
			
		}
		else{
			WorkspaceSpec sol = new WorkspaceSpec("Untitled");
			wsp.resetSolTree();
			wsp.resetMapsAndSets();
			openSetupContextAndUI(sol, null);
		}
		
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(1024, 768);
		this.setJMenuBar(getMainMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("ROSLab Development Environment Prototype");
		this.wsp = new WorkspaceSplitPane();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(wsp);
		this.validate();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
		}
		return jContentPane;
	}
	
	private void saveAsAction(){
		if(WorkspaceContext.currentWorkspace != null){
			final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File name){
					return (name.isDirectory() || name.getAbsolutePath().endsWith(FileConstants.WORKSPACE));
				}
				
				@Override
				public String getDescription(){
					return "Workspace Files *" + FileConstants.WORKSPACE;
				}
			});
			
			
			int returnVal = fc.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				try {
					if(file.getName().endsWith(FileConstants.WORKSPACE)){
						FileIO.saveObject(file, WorkspaceContext.currentWorkspace);
					} else {
						FileIO.saveObject(file + FileConstants.WORKSPACE, WorkspaceContext.currentWorkspace);
					}
					WorkspaceContext.currentWorkspaceFileName = file.getAbsolutePath();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Error saving workspace");
					e.printStackTrace();
				}
			}
		}
	}
	
	private void saveAction(){
		if(WorkspaceContext.currentWorkspace != null && 
			WorkspaceContext.currentWorkspaceFileName != null){
			try {
				FileIO.saveObject(new File(WorkspaceContext.currentWorkspaceFileName), 
						WorkspaceContext.currentWorkspace);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error saving workspace");
				e.printStackTrace();
			}
		}else if(WorkspaceContext.currentWorkspace != null){
			final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);
			fc.setAcceptAllFileFilterUsed(false);
			fc.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File name){
					return (name.isDirectory() || name.getAbsolutePath().endsWith(FileConstants.WORKSPACE));
				}
				
				@Override
				public String getDescription(){
					return "Workspace Files *" + FileConstants.WORKSPACE;
				}
			});
			
			int returnVal = fc.showSaveDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				try {
					if(file.getName().endsWith(FileConstants.WORKSPACE)){
						FileIO.saveObject(file, WorkspaceContext.currentWorkspace);
					} else {
						FileIO.saveObject(file + FileConstants.WORKSPACE, WorkspaceContext.currentWorkspace);
					}
					WorkspaceContext.currentWorkspaceFileName = file.getAbsolutePath();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Error saving workspace");
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void newAction(){

		wsp.resetSolTree();
		wsp.resetMapsAndSets();
		wsp.getAppConfigPanel().setConfiguration(null);
		wsp.getModel().reload();
		wsp.getAppConfigPanel().reRender();
		
		//initialize workspace
		WorkspaceContext.currentWorkspaceFileName = null;		
		WorkspaceContext.currentWorkspace = new WorkspaceSpec("Untitled");
		
		
		/*
		Map<String, ModuleSpec> moduleTypes = solution.moduleTypes;
		Set<String> compTypeNames = moduleTypes.keySet();
		
		for(String name : compTypeNames){
			ModuleSpec mspec = moduleTypes.get(name);
			wsp.addNewComponentToUI(mspec);
			ComponentSignature csig = mspec.sig;
			List<PortName> subPorts = csig.getRecvPortNames();
			List<PortType> subPortTypes = csig.getRecvPortTypes();
			List<PortName> pubPorts = csig.getSendPortNames();
			List<PortType> pubPortTypes = csig.getSendPortTypes();
			Map<String, PortSpec> portTypes =  PortTypes.getBuiltInPortMap();
			for(int i = 0; i < subPorts.size(); i++){
				String portName = subPorts.get(i).getName();
				String portType = subPortTypes.get(i).getType();
				PortSpec pspec = portTypes.get(portType);
				wsp.addPortToComponentUI(name, portName, pspec, false);
			}
			for(int i = 0; i < pubPorts.size(); i++){
				String portName = pubPorts.get(i).getName();
				String portType = pubPortTypes.get(i).getType();
				PortSpec pspec = portTypes.get(portType);
				wsp.addPortToComponentUI(name, portName, pspec, true);
			}
		}
		Map<String, ConfigurationSpec> configurations = solution.configurations;
		Set<String> solutionNames = configurations.keySet();
		for(String sname : solutionNames){
			wsp.addConfigurationToUI(sname, configurations.get(sname));
		}
		*/
	}
	
	private void openAction(){
		final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File name){
				return (name.isDirectory() || name.getAbsolutePath().endsWith(FileConstants.WORKSPACE));
			}
			
			@Override
			public String getDescription(){
				return "Workspace Files *" + FileConstants.WORKSPACE;
			}
		});
		
		int returnVal = fc.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			File file = fc.getSelectedFile();
			try {
				WorkspaceSpec sol = FileIO.loadWorkspace(file);
				wsp.resetSolTree();
				wsp.resetMapsAndSets();
				openSetupContextAndUI(sol, file.getAbsolutePath());
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Error loading workspace");
			}
		}
	}
	
	private void openSetupContextAndUI(WorkspaceSpec solution, String filename){
		WorkspaceContext.currentWorkspaceFileName = filename;
		WorkspaceContext.currentWorkspace = solution;
		Map<String, ModuleSpec> moduleTypes = solution.moduleTypes;
		Set<String> compTypeNames = moduleTypes.keySet();
		
		for(String name : compTypeNames){
			ModuleSpec mspec = moduleTypes.get(name);
			wsp.addNewComponentToUI(mspec);
			boolean isService = mspec.role.equals("ROS Service");
			ComponentSignature csig = mspec.sig;
			List<PortName> subPorts = csig.getRecvPortNames();
			List<PortType> subPortTypes = csig.getRecvPortTypes();
			List<PortName> pubPorts = csig.getSendPortNames();
			List<PortType> pubPortTypes = csig.getSendPortTypes();
			List<TaskDescriptor> tasks = csig.getTaskDescriptors();
			Map<String, PortSpec> portTypes =  PortTypes.getBuiltInPortMap();
			for(int i = 0; i < subPorts.size(); i++){
				String portName = subPorts.get(i).getName();
				String portType = subPortTypes.get(i).getType();
				PortSpec pspec = portTypes.get(portType);
				wsp.addPortToComponentUI(name, portName, pspec, false, isService);
			}
			for(int i = 0; i < pubPorts.size(); i++){
				String portName = pubPorts.get(i).getName();
				String portType = pubPortTypes.get(i).getType();
				PortSpec pspec = portTypes.get(portType);
				wsp.addPortToComponentUI(name, portName, pspec, true, isService);
			}
			if(tasks == null)
				continue; // I considered initializing tasks to an empty list, but then I'd have to bring an implementation
						  // (e.g. LinkedList or ArrayList) into this file, which has so far been generic - Sam 9/23/12
			for(int i = 0; i < tasks.size(); i++){
				String typeName = tasks.get(i).getType();
				boolean isPeriodic = tasks.get(i).isPeriodic();
				String taskName = tasks.get(i).getName();
				int period = tasks.get(i).getPeriod();
				int deadline = tasks.get(i).getDeadline();
				int wcet = tasks.get(i).getWcet();				
				wsp.addTaskToComponentUI(name, isPeriodic, taskName, typeName, period, deadline, wcet);
			}
		}
		Map<String, ConfigurationSpec> configurations = solution.configurations;
		Set<String> solutionNames = configurations.keySet();
		for(String sname : solutionNames){
			wsp.addConfigurationToUI(sname, configurations.get(sname));
		}
	}
	
	/* Removing app archive builder
	private void buildAppArchiveAction(){
		new AppArchiveBuilderDialog(WorkspaceContext.configToAppArchiveLocations);
	}
	
	private void setClassPathAction(){
		new ClassPathDialog();
	}
	*/

	private void setAppDevPath(){
		new AppDevPathDialog();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		FileIO.saveUserSettings();
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
	
	
}
