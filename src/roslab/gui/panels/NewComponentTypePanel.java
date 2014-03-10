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

import javax.swing.JPanel;

import java.awt.Dimension;

import javax.swing.JLabel;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;

import roslab.artifacts.WorkspaceSpec;
import roslab.types.builtins.CompRoles;

public class NewComponentTypePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel typeNameLabel = null;
	private JLabel roleLabel = null;
	private JTextField nameTextField = null;
	private JComboBox roleComboBox = null;
	private int numTasks = 0;
	
	private List<String> rolesList;
	private JTextField taskNameTextField;
	private JTextField periodField;
	private JTextField deadlineField;
	private JTextField wcetField;

	/**
	 * This is the default constructor
	 */
	{
		rolesList = new ArrayList<String>();
		rolesList.add(CompRoles.ROS_NODE);
		initialize();
	}
	
	public NewComponentTypePanel() {
		super();
	}
	
	public NewComponentTypePanel(WorkspaceSpec solution){
		super();
	}
	
	public NewComponentTypePanel(WorkspaceSpec solution, JDialog containingParent){
		super();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		roleLabel = new JLabel();
		roleLabel.setBounds(new Rectangle(28, 60, 67, 17));
		roleLabel.setText("Role:");
		typeNameLabel = new JLabel();
		typeNameLabel.setBounds(new Rectangle(28, 29, 65, 16));
		typeNameLabel.setText("Name:");
		this.setSize(332, 195);
		this.setPreferredSize(new Dimension(333, 98));
		this.setLayout(null);
		this.add(typeNameLabel, null);
		this.add(roleLabel, null);
		this.add(getNameTextField(), null);
		this.add(getRoleComboBox(), null);
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(28, 163, 279, 218);
		//add(tabbedPane);
		tabbedPane.setEnabled(false);
		
		final JPanel panel = new JPanel();
		tabbedPane.addTab("Task 1", null, panel, null);
		panel.setEnabled(false);
		panel.setLayout(null);
		
		final JLabel lblTaskName = new JLabel("Task Name:");
		lblTaskName.setBounds(6, 10, 97, 16);
		lblTaskName.setEnabled(false);
		panel.add(lblTaskName);
		
		final JComboBox portTypeCombo = new JComboBox();
		portTypeCombo.setBounds(108, 38, 144, 27);
		portTypeCombo.setModel(new DefaultComboBoxModel(new String[] {"Sporadic", "Periodic"}));
		portTypeCombo.setEnabled(false);
		panel.add(portTypeCombo);
		
		final JLabel dispatchTypeLbl = new JLabel("Dispatch Type:");
		dispatchTypeLbl.setEnabled(false);
		dispatchTypeLbl.setBounds(6, 44, 97, 16);
		panel.add(dispatchTypeLbl);
		
		taskNameTextField = new JTextField();
		taskNameTextField.setEnabled(false);
		taskNameTextField.setBounds(108, 4, 144, 28);
		panel.add(taskNameTextField);
		taskNameTextField.setColumns(10);
		
		final JLabel periodLbl = new JLabel("Period:");
		periodLbl.setEnabled(false);
		periodLbl.setBounds(6, 78, 97, 16);
		panel.add(periodLbl);
		
		periodField = new JTextField();
		periodField.setEnabled(false);
		periodField.setColumns(10);
		periodField.setBounds(108, 72, 117, 28);
		panel.add(periodField);
		
		final JLabel periodMsLbl = new JLabel("ms");
		periodMsLbl.setEnabled(false);
		periodMsLbl.setBounds(233, 78, 19, 16);
		panel.add(periodMsLbl);
		
		final JLabel deadlineMsLbl = new JLabel("ms");
		deadlineMsLbl.setEnabled(false);
		deadlineMsLbl.setBounds(233, 112, 19, 16);
		panel.add(deadlineMsLbl);
		
		deadlineField = new JTextField();
		deadlineField.setEnabled(false);
		deadlineField.setColumns(10);
		deadlineField.setBounds(108, 106, 117, 28);
		panel.add(deadlineField);
		
		final JLabel deadlineLbl = new JLabel("Deadline:");
		deadlineLbl.setEnabled(false);
		deadlineLbl.setBounds(6, 112, 97, 16);
		panel.add(deadlineLbl);
		
		final JLabel wcetLbl = new JLabel("WCET:");
		wcetLbl.setEnabled(false);
		wcetLbl.setBounds(6, 145, 97, 16);
		panel.add(wcetLbl);
		
		wcetField = new JTextField();
		wcetField.setEnabled(false);
		wcetField.setColumns(10);
		wcetField.setBounds(108, 139, 117, 28);
		panel.add(wcetField);
		
		final JLabel wcetMsLbl = new JLabel("ms");
		wcetMsLbl.setEnabled(false);
		wcetMsLbl.setBounds(233, 145, 19, 16);
		panel.add(wcetMsLbl);
		
		JButton btnAddTask = new JButton("Add Task");
		btnAddTask.setEnabled(false);
		btnAddTask.addActionListener(new ComponentTypeListener(periodMsLbl, wcetMsLbl, deadlineMsLbl,
				dispatchTypeLbl, periodLbl, lblTaskName, wcetLbl,
				portTypeCombo, deadlineLbl, panel, tabbedPane));
		btnAddTask.setBounds(99, 110, 117, 29);
		//add(btnAddTask);
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBounds(new Rectangle(99, 29, 218, 24));
		}
		return nameTextField;
	}

	/**
	 * This method initializes kindComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getRoleComboBox() {
		if (roleComboBox == null) {
			roleComboBox = new JComboBox();
			for(String s : rolesList){
				roleComboBox.addItem(s);
			}
			roleComboBox.setBounds(new Rectangle(101, 56, 214, 24));
		}
		return roleComboBox;
	}
	
	
	public String getTypeName(){
		return nameTextField.getText();
	}
	
	public String getRoleName(){
		return (String)roleComboBox.getSelectedItem();
	}
	
	private final class ComponentTypeListener implements ActionListener {
		private final JLabel periodMsLbl;
		private final JLabel wcetMsLbl;
		private final JLabel deadlineMsLbl;
		private final JLabel dispatchTypeLbl;
		private final JLabel periodLbl;
		private final JLabel lblTaskName;
		private final JLabel wcetLbl;
		private final JComboBox portTypeCombo;
		private final JLabel deadlineLbl;
		private final JPanel panel;
		private final JTabbedPane tabbedPane;

		private ComponentTypeListener(JLabel periodMsLbl, JLabel wcetMsLbl,
				JLabel deadlineMsLbl, JLabel dispatchTypeLbl, JLabel periodLbl,
				JLabel lblTaskName, JLabel wcetLbl,
				JComboBox portTypeCombo, JLabel deadlineLbl,
				JPanel panel, JTabbedPane tabbedPane) {
			this.periodMsLbl = periodMsLbl;
			this.wcetMsLbl = wcetMsLbl;
			this.deadlineMsLbl = deadlineMsLbl;
			this.dispatchTypeLbl = dispatchTypeLbl;
			this.periodLbl = periodLbl;
			this.lblTaskName = lblTaskName;
			this.wcetLbl = wcetLbl;
			this.portTypeCombo = portTypeCombo;
			this.deadlineLbl = deadlineLbl;
			this.panel = panel;
			this.tabbedPane = tabbedPane;
		}

		public void actionPerformed(ActionEvent e){
			numTasks++;
			if(numTasks == 1) {
				panel.setEnabled(true);
				lblTaskName.setEnabled(true);
				tabbedPane.setEnabled(true);
				tabbedPane.setEnabled(true);
				lblTaskName.setEnabled(true);
				portTypeCombo.setEnabled(true);
				dispatchTypeLbl.setEnabled(true);
				taskNameTextField.setEnabled(true);
				periodLbl.setEnabled(true);
				periodField.setEnabled(true);
				periodMsLbl.setEnabled(true);
				deadlineMsLbl.setEnabled(true);
				deadlineField.setEnabled(true);
				deadlineLbl.setEnabled(true);
				wcetLbl.setEnabled(true);
				wcetField.setEnabled(true);
				wcetMsLbl.setEnabled(true);
			} else {
				JPanel panel = new JPanel();
				panel.setLayout(null);
				
				tabbedPane.addTab("Task " + numTasks, null, panel, null);
				
				final JLabel lblTaskName = new JLabel("Task Name:");
				lblTaskName.setBounds(6, 10, 97, 16);
				panel.add(lblTaskName);
				
				final JComboBox portTypeCombo = new JComboBox();
				portTypeCombo.setBounds(108, 38, 144, 27);
				portTypeCombo.setModel(new DefaultComboBoxModel(new String[] {"Sporadic", "Periodic"}));
				panel.add(portTypeCombo);
				
				final JLabel dispatchTypeLbl = new JLabel("Dispatch Type:");
				dispatchTypeLbl.setBounds(6, 44, 97, 16);
				panel.add(dispatchTypeLbl);
				
				taskNameTextField = new JTextField();
				taskNameTextField.setBounds(108, 4, 144, 28);
				panel.add(taskNameTextField);
				taskNameTextField.setColumns(10);
				
				final JLabel periodLbl = new JLabel("Period:");
				periodLbl.setBounds(6, 78, 97, 16);
				panel.add(periodLbl);
				
				periodField = new JTextField();
				periodField.setColumns(10);
				periodField.setBounds(108, 72, 117, 28);
				panel.add(periodField);
				
				final JLabel periodMsLbl = new JLabel("ms");
				periodMsLbl.setBounds(233, 78, 19, 16);
				panel.add(periodMsLbl);
				
				final JLabel deadlineMsLbl = new JLabel("ms");
				deadlineMsLbl.setBounds(233, 112, 19, 16);
				panel.add(deadlineMsLbl);
				
				deadlineField = new JTextField();
				deadlineField.setColumns(10);
				deadlineField.setBounds(108, 106, 117, 28);
				panel.add(deadlineField);
				
				final JLabel deadlineLbl = new JLabel("Deadline:");
				deadlineLbl.setBounds(6, 112, 97, 16);
				panel.add(deadlineLbl);
				
				final JLabel wcetLbl = new JLabel("WCET:");
				wcetLbl.setBounds(6, 145, 97, 16);
				panel.add(wcetLbl);
				
				wcetField = new JTextField();
				wcetField.setColumns(10);
				wcetField.setBounds(108, 139, 117, 28);
				panel.add(wcetField);
				
				final JLabel wcetMsLbl = new JLabel("ms");
				wcetMsLbl.setBounds(233, 145, 19, 16);
				panel.add(wcetMsLbl);
			}
		}
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
