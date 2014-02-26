package roslab.gui.dialogs;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import roslab.artifacts.WorkspaceContext;


public class AppDevPathDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5204359096641409189L;

	private JPanel jContentPane = null;
	private JTextField appDevPathTextField = null;	
	private JButton findAppDevPathFolderButton = null;
	private String appDevPath = null;
	private AppDevPathDialog thisInstance = this;
	
	public AppDevPathDialog() {
		super();		

		initialize();
	}

	
	private void initialize(){
		this.setSize(400, 150);
		this.setContentPane(getJContentPane());
		this.setTitle("App Development Path");
		this.setResizable(false);
		
		this.setEnabled(true);
		this.setVisible(true);
		this.pack();
	}
	
	private JPanel getJContentPane(){
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.add(getAppDevPathTextField(), null);
			jContentPane.add(getFinAppDevPathFolderButton(), null);
		}
		return jContentPane;
	}

	private JButton getFinAppDevPathFolderButton() {
		if (findAppDevPathFolderButton == null) {
			findAppDevPathFolderButton = new JButton();
			findAppDevPathFolderButton.setBounds(new Rectangle(100, 70, 100, 30));
			findAppDevPathFolderButton.setText("Find");
			findAppDevPathFolderButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					final JFileChooser fc = new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setFileFilter(new FileFilter(){
						@Override
						public boolean accept(File name){
							return name.isDirectory();
						}
						
						@Override
						public String getDescription(){
							return "App Development Folder";
						}
					});
					
					int returnVal = fc.showOpenDialog(thisInstance);
					if(returnVal == JFileChooser.APPROVE_OPTION){
						File file = fc.getSelectedFile();
						
						WorkspaceContext.appDevPath = file;
						appDevPath = file.getAbsolutePath();
						appDevPathTextField.setText(appDevPath);
						
						/* removing skeleton compiling feature
						//put class paths of the core project and channel service project
						File corePath = new File(file.getAbsolutePath(), FileConstants.CORERELATIVECPATHFROMAPPDEV);
						if(!corePath.exists()){
							JOptionPane.showMessageDialog(thisInstance,
									"mdcf2-core project is not reachable from " + file.getAbsolutePath() + ". "
									+ "For compilation, please set the location of mdcf2-core in the class path setting.", 
									"Warning", JOptionPane.WARNING_MESSAGE);
							thisInstance.dispose();
							return;
						}
						
						File chanServPath = new File(file.getAbsolutePath(), FileConstants.CHANNELSERVICERELATIVECPATHFROMAPPDEV);
						if(!chanServPath.exists()){
							JOptionPane.showMessageDialog(thisInstance,  
									"mdcf2-channelservice project is not reachable from " + file.getAbsolutePath() + ". "
									+ "For compilation, please set the location of mdcf2-channelservice in the class path setting.", 
									"Warning", JOptionPane.WARNING_MESSAGE);
							thisInstance.dispose();
							return;
						}

						
						WorkspaceContext.buildClasspath.add(corePath);
						WorkspaceContext.buildClasspath.add(chanServPath);
						*/
						
						/*
						File appArchivePath = new File(file.getAbsolutePath(), FileConstants.APPARCHIVERELATVIEPATHFROMAPPDEV);
						if(appArchivePath.exists()){
							WorkspaceContext.appArchiveStoragePath = appArchivePath.getAbsolutePath();
						} else {
							JOptionPane.showMessageDialog(thisInstance,  
									"mdcf2-apps project is not reachable from " + file.getAbsolutePath() + ". "
									+ "When building archive, the archive won't be copied to mdcf2-apps automatically.", 
									"Warning", JOptionPane.WARNING_MESSAGE);
							thisInstance.dispose();
							return;
						}
						*/
						thisInstance.dispose();
						
					}
				}
			});
		}
		return findAppDevPathFolderButton;
	}
	
	private JTextField getAppDevPathTextField() {
		if (appDevPathTextField == null) {
			
			appDevPathTextField = new JTextField(WorkspaceContext.appDevPath!=null ? WorkspaceContext.appDevPath.getAbsolutePath() : null);
			appDevPathTextField.setEditable(false);
			appDevPathTextField.setColumns(20);
		}
		return appDevPathTextField;
	}
	
}
