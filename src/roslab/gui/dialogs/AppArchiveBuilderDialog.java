package roslab.gui.dialogs;
/**
 * Copyright 2009-2011, Kansas State University, 
 * PRECISE Center at the University of Pennsylvania,
 * Andrew King, and Yu Jin Kim
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

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import roslab.artifacts.WorkspaceContext;
import roslab.gui.misc.FileConstants;

//import mdcf.core.app.AppSpec;
//import mdcf.core.app.VirtualComponent;


/**
 * 
 * Class for building a AppArchive file
 *
 */
public class AppArchiveBuilderDialog extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3476485354829709545L;
	
	
	private String appConfigName = null;
	private JList<String> folderList = null;
	private DefaultListModel<String> folderListModel = null;
	private JPanel jContentPane = null;
	private JScrollPane appArchiveScrollPane = null;	
	private JButton buildArchiveButton = null;
	
	private HashMap<String, File> configToFolderMap = null;
	
	
	public AppArchiveBuilderDialog(Map<String, File> configToAppArchiveLocations) {
		super();		
		
		this.configToFolderMap = (HashMap<String, File>) configToAppArchiveLocations;
		
		initialize();
	}
	
	private void initialize(){
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("App Archive Builder");
		this.setResizable(false);
		
		listTheFolder();
		
		this.setEnabled(true);
		this.setVisible(true);
	}
	
	
	private void listTheFolder(){				
		Runnable doWork = new Runnable() {
			@Override
			public void run() {
				folderList.removeAll();
				if (folderListModel != null) {
					folderListModel.removeAllElements();
				}
				else {
					folderListModel = new DefaultListModel<String>();
				}
				for(String configName : configToFolderMap.keySet()){
						folderListModel.addElement(configName);
				}
				folderList.setModel(folderListModel);
			}
		};
		SwingUtilities.invokeLater(doWork);
	}
	
	private void buildAppArchive(File folder){
		StringBuffer filenameBuffer = new StringBuffer();
		ArrayList<String> serverComponentTypeNames = new ArrayList<String>();
		ArrayList<String> deviceComponentTypeNames = new ArrayList<String>();
		
		//Read App Configuration
		//* Get the list of server components
		if(!readAppConfig(folder, serverComponentTypeNames, deviceComponentTypeNames, filenameBuffer)){
			this.dispose();
			return;
		}
			
		
		//Check the Existence of Components Signatures
		if(!checkConsistency(folder, serverComponentTypeNames, filenameBuffer)){
			this.dispose();
			return;
		}
		
		//Compile Java files
		//* Check Skeleton files and compile
		if(!compileSkeleton(folder, serverComponentTypeNames, filenameBuffer)){
			this.dispose();
			return;
		}
		
		//Generate the Metadata.
		generateMetaData(folder, deviceComponentTypeNames, filenameBuffer);
		
		//Archive the contents.
		if(archiveContents(folder, filenameBuffer)){
			JOptionPane.showMessageDialog(this,  appConfigName + ".jar is created at " + FileConstants.APPARCHIVERELATVIEPATHFROMAPPDEV);
			this.dispose();
		} else {
			this.dispose();
			return;
		}
		
	}

	private boolean compileSkeleton(File folder,
			ArrayList<String> serverComponentTypeNames,
			StringBuffer filenameBuffer) {
		File skeltonFolder = new File(folder.getAbsolutePath() + FileConstants.SKELETONFOLDER);
		FileFilter ff = new FileFilter(){
			@Override
			public boolean accept(File name){
				return (name.getName().endsWith(FileConstants.SKELETON));
			}
		};
		
		File[] files = skeltonFolder.listFiles(ff);
	
		ArrayList<String> skeletonList = new ArrayList<String>();
		for(int index = 0; index < files.length; index++) {
			skeletonList.add(files[index].getName().substring(0, (files[index].getName().length() - FileConstants.SKELETON.length())));
		}
		if(!skeletonList.containsAll(serverComponentTypeNames)){
			//error: at least there should be all the skeleton file for the server component.
			//this is the list of missing components: serverComponentTypeNames.removeAll(skeletonList);
			serverComponentTypeNames.removeAll(skeletonList);
			JOptionPane.showMessageDialog(this,  serverComponentTypeNames + ":skeleton missing", 
											"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		//compile the source code
		String command = null;
		String classpath = null;
		String fileNames = getFileNames(files);
		if(!WorkspaceContext.buildClasspath.isEmpty()){
			classpath = "-classpath " + formatClassPath() + " ";
			command = "javac " + classpath + fileNames; 
		} else {
			command = "javac " + fileNames;
		}
		System.out.println(command);
	    	    
		try {
			Process child = Runtime.getRuntime().exec(command, null, skeltonFolder);
			InputStream stderr = child.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
			int exitVal = child.waitFor();
			
			//Compile Error
			if(exitVal != 0){
				JOptionPane.showMessageDialog(this,  "Compile Error: Check the Console", 
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		FileFilter sourceAndClass = new FileFilter(){
			@Override
			public boolean accept(File name){
				return (name.getName().endsWith(FileConstants.SKELETON) || name.getName().endsWith(".class"));
			}
		};
		
		File[] compileArtifacts = skeltonFolder.listFiles(sourceAndClass);
		for(int index = 0; index < compileArtifacts.length; index++) {
			//Collect the absolute path of the archive files
			filenameBuffer.append("." + compileArtifacts[index].getAbsolutePath().substring(folder.getAbsolutePath().length()) + " ");
		}
		
		return true;
	}

	private String getFileNames(File[] files) {
		StringBuffer sb = new StringBuffer();
		for(int index = 0; index < files.length; index++) {
			sb.append(files[index].getName() + " ");
		}
		return sb.toString();
		
	}

	private String formatClassPath() {
		StringBuffer sb = new StringBuffer();
		for(Iterator<File> classPathIter = WorkspaceContext.buildClasspath.iterator(); classPathIter.hasNext(); ){
			sb.append(classPathIter.next().getAbsolutePath());
			if(classPathIter.hasNext()){
				sb.append(File.pathSeparator);
			}
		}
		
		return sb.toString();
	}

	private boolean archiveContents(File folder, StringBuffer filenameBuffer) {
		Process child = null;
		BufferedReader br = null;
		String line = null;
		int exitVal = 0;
		
		try {
			
			String jarCommand = "jar cvf " + appConfigName + ".jar " + filenameBuffer.toString();
			System.out.println(jarCommand);
			
			child = Runtime.getRuntime().exec(jarCommand, null, folder);			
			br = new BufferedReader(new InputStreamReader(child.getErrorStream()));
			line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
			
			
			exitVal = child.waitFor();
			
			if(exitVal != 0){
				JOptionPane.showMessageDialog(this,  "Problem occurred while Archiving: Check the Console", 
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			String mvCommand = null;
			if(WorkspaceContext.appDevPath != null){
				mvCommand =  "mv " + folder.getAbsolutePath() + File.separator + appConfigName + ".jar " + FileConstants.APPARCHIVERELATVIEPATHFROMAPPDEV;
				System.out.println(mvCommand);
				child = Runtime.getRuntime().exec(mvCommand, null, WorkspaceContext.appDevPath);
			} else {
				 mvCommand = "mv " + folder.getAbsolutePath() + File.separator + appConfigName + ".jar " + "." + File.separator;
				child = Runtime.getRuntime().exec(mvCommand, null, folder);
			}
			br = new BufferedReader(new InputStreamReader(child.getErrorStream()));
			line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
			}
			
			exitVal = child.waitFor();
			
			if(exitVal != 0){
				JOptionPane.showMessageDialog(this,
						"Problem occurred while moving the App Archive: Check the Console. The app archive file is at " + folder + ". ", 
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return true;
	}

	private void generateMetaData(File folder, ArrayList<String> deviceComponentTypeNames, StringBuffer filenameBuffer) {
		Properties properties = new Properties();
		
		properties.setProperty("name", appConfigName);
		properties.setProperty("ver", FileConstants.VERSION);
		
		String listString = deviceComponentTypeNames.toString();
		listString = listString.substring(1, listString.length() - 1);
		
		properties.setProperty("dep", listString);
		
		try {
			File metafile = new File(folder.getAbsolutePath() + FileConstants.DESCFOLDER + File.separator + FileConstants.DESCRIPTORNAME);
			//Collect the absolute path of the archive files
			filenameBuffer.append("." + metafile.getAbsolutePath().substring(folder.getAbsolutePath().length()) + " ");
			
			properties.store(
					new FileOutputStream(metafile),
					null
					);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private boolean checkConsistency(File folder, ArrayList<String> serverComponentTypeNames, StringBuffer filenameBuffer) {
		File compSigFolder = new File(folder.getAbsolutePath() + FileConstants.APPCOMPFOLDER);
		
		FileFilter ff = new FileFilter(){
			@Override
			public boolean accept(File name){
				return (name.getName().endsWith(FileConstants.COMPONENTSIG));
			}
		};
		
		File[] files = compSigFolder.listFiles(ff);
				
		ArrayList<String> compsigList = new ArrayList<String>();
		for(int index = 0; index < files.length; index++) {
		//	ComponentSignature compsig = FileIO.loadSignatureFromFile(files[index]);
			
//			if(serverComponentTypeNames.contains(compsig.getType())){
//				compsigList.add(compsig.getType());
//				//Collect the absolute path of the archive files
//				filenameBuffer.append("." + files[index].getAbsolutePath().substring(folder.getAbsolutePath().length()) + " ");
//			}
		}
		
		if(!compsigList.containsAll(serverComponentTypeNames)){
			//error: folder should have all the necessary component signature, something is missing here.
			//this is the list of missing components: serverComponentTypeNames.removeAll(compsigList);
			serverComponentTypeNames.removeAll(compsigList);
			JOptionPane.showMessageDialog(this,  serverComponentTypeNames + ":Component Signature Missing", 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}

	private boolean readAppConfig(File folder, ArrayList<String> serverComponentTypeNames, ArrayList<String> deviceComponentTypeNames, StringBuffer filenameBuffer) {
		File appconfigFolder = new File(folder.getAbsolutePath() + FileConstants.APPCONFIGFOLDER);
		
		FileFilter ff = new FileFilter(){
			@Override
			public boolean accept(File name){
				return (name.getName().endsWith(FileConstants.APPCONFIGURATION));
			}
		};
			
		File[] files = appconfigFolder.listFiles(ff);
		if(files.length != 1){
			//error: only one app configuration should be here
			JOptionPane.showMessageDialog(this,  "Only one app configuration is allowed in " + FileConstants.APPCONFIGFOLDER, 
					"Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		System.out.println("AKING: Unsupported MDCF link");
		//AppSpec appSpec = FileIO.loadAppConfigurationFromFile(files[0]);
		//for( Iterator<VirtualComponent> compsIter = appSpec.getComponents().iterator(); compsIter.hasNext(); ){
		//	VirtualComponent comp = compsIter.next();
		//	if(comp.getRole().equals(CompRoles.DEVICE_NAME)){
		//		deviceComponentTypeNames.add(comp.getType());
		//	} else {
		//		serverComponentTypeNames.add(comp.getType());
		//	}
	//	}
		
		//Collect the absolute path of the archive files
		filenameBuffer.append("." + files[0].getAbsolutePath().substring(folder.getAbsolutePath().length()) + " ");
		
		return true;
	}


	
	private JPanel getJContentPane(){
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.add(getBuildAppArchiveButton(), null);
			jContentPane.add(getAppListScrollPane(), null);
		}
		return jContentPane;
	}
	
	private JScrollPane getAppListScrollPane() {
		if (appArchiveScrollPane == null) {
			appArchiveScrollPane = new JScrollPane();
			appArchiveScrollPane.setBounds(new Rectangle(18, 14, 215, 100));
			appArchiveScrollPane.setViewportView(getAppConfigList());
		}
		return appArchiveScrollPane;
	}
	
	private JList<String> getAppConfigList() {
		if (folderList == null) {
			folderList = new JList<String>();
			folderList.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							appConfigListSelected();
						}
					});
		}
		return folderList;
	}
	
	private void appConfigListSelected(){

		if(folderList.getSelectedValue() != null){
			appConfigName = folderList.getSelectedValue().toString();
		}
		else{
			
		}
	}
	
	private JButton getBuildAppArchiveButton() {
		if (buildArchiveButton == null) {
			buildArchiveButton = new JButton();
			buildArchiveButton.setBounds(new Rectangle(260, 312, 181, 29));
			buildArchiveButton.setText("Build Archive");
			buildArchiveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(appConfigName == null){
						return;
					} else {
						File location = WorkspaceContext.configToAppArchiveLocations.get(appConfigName);
						buildAppArchive(location);
					}
				}
			});
		}
		return buildArchiveButton;
	}
	
	

	
}
