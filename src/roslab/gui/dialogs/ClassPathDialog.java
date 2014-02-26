package roslab.gui.dialogs;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import roslab.artifacts.WorkspaceContext;


public class ClassPathDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5204359096641409189L;

	private JPanel jContentPane = null;
	private JScrollPane classPathScrollPane = null;	
	private JButton addClasspathFolderButton = null;
	private JButton addClasspathFileButton = null;
	private JButton removeClasspathButton = null;
	private DefaultListModel<String> folderListModel = null;
	private JList<String> folderList = null;
	private String selectedClassPath = null;
	private ClassPathDialog thisInstance = this;
	
	public ClassPathDialog() {
		super();		

		initialize();
	}

	
	private void initialize(){
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Set Class Path");
		this.setResizable(false);

		listTheClassPath();
		
		this.setEnabled(true);
		this.setVisible(true);
	}
	
	private JPanel getJContentPane(){
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.add(getAddClassPathFolderButton(), null);
			jContentPane.add(getAddClassPathFileButton(), null);
			jContentPane.add(getRemoveClassPathButton(), null);
			jContentPane.add(getClassPathScrollPane(), null);
		}
		return jContentPane;
	}
	
	private JButton getAddClassPathFileButton() {
		if (addClasspathFileButton == null) {
			addClasspathFileButton = new JButton();
			addClasspathFileButton.setBounds(new Rectangle(100, 100, 200, 30));
			addClasspathFileButton.setText("Add File");
			addClasspathFileButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
						
						final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);
						fc.setAcceptAllFileFilterUsed(false);
						fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
							@Override
							public boolean accept(File name) {
								return (name.isDirectory() || name.getAbsolutePath().endsWith(".jar") || name.getAbsolutePath().endsWith(".zip"));
							}

							@Override
							public String getDescription() {
								return "Class path: *.zip or *.jar";
							}
						});

						int returnVal = fc.showOpenDialog(thisInstance);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							WorkspaceContext.buildClasspath.add(file);
							listTheClassPath();
						}
				}
			});
		}
		return addClasspathFileButton;
	}


	private JButton getAddClassPathFolderButton() {
		if (addClasspathFolderButton == null) {
			addClasspathFolderButton = new JButton();
			addClasspathFolderButton.setBounds(new Rectangle(300, 100, 200, 30));
			addClasspathFolderButton.setText("Add Folder");
			addClasspathFolderButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
						
						final JFileChooser fc = new JFileChooser(WorkspaceContext.appDevPath);
						fc.setAcceptAllFileFilterUsed(false);
						fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
							@Override
							public boolean accept(File name) {
								return (name.isDirectory());
							}

							@Override
							public String getDescription() {
								return "Class Path: folder";
							}
						});

						int returnVal = fc.showOpenDialog(thisInstance);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File file = fc.getSelectedFile();
							WorkspaceContext.buildClasspath.add(file);
							listTheClassPath();
						}
				}
			});
		}
		return addClasspathFolderButton;
	}
	
	private JButton getRemoveClassPathButton() {
		if (removeClasspathButton == null) {
			removeClasspathButton = new JButton();
			removeClasspathButton.setBounds(new Rectangle(500, 100, 200, 30));
			removeClasspathButton.setText("Remove");
			removeClasspathButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(selectedClassPath == null){
						return;
					} else {
						for(Iterator<File> classPathIter = WorkspaceContext.buildClasspath.iterator(); classPathIter.hasNext(); ){
							File classpath = classPathIter.next();
							if(selectedClassPath.equals(classpath.getAbsolutePath())){
								WorkspaceContext.buildClasspath.remove(classpath);
								listTheClassPath();
								return;
							}
						}
					}
				}
			});
		}
		return removeClasspathButton;
	}
	
	private JScrollPane getClassPathScrollPane() {
		if (classPathScrollPane == null) {
			classPathScrollPane = new JScrollPane();
			classPathScrollPane.setBounds(new Rectangle(20, 150, 300, 250));
			classPathScrollPane.setViewportView(getClassPathList());
		}
		return classPathScrollPane;
	}
	
	private JList<String> getClassPathList() {
		if (folderList == null) {
			folderList = new JList<String>();
			folderList.addListSelectionListener(new ListSelectionListener() {
						@Override
						public void valueChanged(ListSelectionEvent e) {
							classPathSelected();
						}
					});
		}
		return folderList;
	}
	
	private void classPathSelected(){

		if(folderList.getSelectedValue() != null){
			selectedClassPath = folderList.getSelectedValue().toString();
		}
		else{
			
		}
	}
	
	private void listTheClassPath(){				
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
				for(Iterator<File> classPathIter = WorkspaceContext.buildClasspath.iterator(); classPathIter.hasNext(); ){
					File classpath = classPathIter.next();
					folderListModel.addElement(classpath.getAbsolutePath());
				}
				folderList.setModel(folderListModel);
			}
		};
		SwingUtilities.invokeLater(doWork);
	}
}
