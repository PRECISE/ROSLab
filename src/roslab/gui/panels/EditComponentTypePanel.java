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

import roslab.artifacts.WorkspaceSpec;
import roslab.types.builtins.CompRoles;


public class EditComponentTypePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel typeNameLabel = null;
	private JLabel roleLabel = null;
	private JTextField nameTextField = null;
	private JComboBox<String> roleComboBox = null;
	
	private List<String> rolesList;
	
	private String compType;
	private String roleName;
	
	public EditComponentTypePanel() {
		super();
	}
	
	public EditComponentTypePanel(String compType, String roleName) {
		super();
		this.compType = compType;
		this.roleName = roleName;
		rolesList = new ArrayList<String>();
        rolesList.add(CompRoles.ROS_NODE);
		initialize();
		
	}
	
	public EditComponentTypePanel(WorkspaceSpec solution){
		super();
	}
	
	public EditComponentTypePanel(WorkspaceSpec solution, JDialog containingParent){
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
		this.setSize(332, 95);
		this.setPreferredSize(new Dimension(332, 95));
		this.setLayout(null);
		this.add(typeNameLabel, null);
		this.add(roleLabel, null);
		this.add(getNameTextField(), null);
		this.add(getRoleComboBox(), null);
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
		nameTextField.setText(compType);
		return nameTextField;
	}

	/**
	 * This method initializes kindComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getRoleComboBox() {
		if (roleComboBox == null) {
			roleComboBox = new JComboBox<String>();
			for(String s : rolesList){
				roleComboBox.addItem(s);
			}
			roleComboBox.setBounds(new Rectangle(101, 56, 214, 24));
		}
		roleComboBox.setSelectedIndex(rolesList.indexOf(roleName));
		return roleComboBox;
	}
	
	
	public String getTypeName(){
		return nameTextField.getText();
	}
	
	public String getRoleName(){
		return (String)roleComboBox.getSelectedItem();
	}

}
