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
import java.util.List;

import javax.swing.JTextField;
import javax.swing.JComboBox;

import roslab.artifacts.ModuleSpec;
import roslab.artifacts.WorkspaceContext;
import roslab.types.builtins.CompRoles;


public class AddComponentToConfigurationDialog extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel typeLabel = null;
	private JLabel nameLabel = null;
	private JTextField nameTextField = null;
	private JComboBox<ModuleSpec> componentTypeCombo = null;
	
	private String roleName = CompRoles.ROS_NODE;

	/**
	 * This is the default constructor
	 */
	public AddComponentToConfigurationDialog() {
		super();
		initialize();
	}
	
	public AddComponentToConfigurationDialog(String roleName) {
		super();
		this.roleName = roleName;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		nameLabel = new JLabel();
		nameLabel.setBounds(new Rectangle(14, 14, 129, 21));
		nameLabel.setText("Instance Name:");
		typeLabel = new JLabel();
		typeLabel.setBounds(new Rectangle(13, 46, 131, 18));
		typeLabel.setText("Component Type:");
		this.setSize(364, 83);
		this.setPreferredSize(new Dimension(364, 83));
		this.setLayout(null);
		this.add(typeLabel, null);
		this.add(nameLabel, null);
		this.add(getNameTextField(), null);
		this.add(getComponentTypeCombo(), null);
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBounds(new Rectangle(152, 15, 192, 20));
		}
		return nameTextField;
	}

	/**
	 * This method initializes componentTypeCombo	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<ModuleSpec> getComponentTypeCombo() {
		if (componentTypeCombo == null) {
			componentTypeCombo = new JComboBox<ModuleSpec>();
			componentTypeCombo.setBounds(new Rectangle(151, 43, 194, 22));
			List<ModuleSpec> specList 
				= WorkspaceContext.currentWorkspace.getCompTypesByRole(roleName);
			for(ModuleSpec mspec : specList){
				componentTypeCombo.addItem(mspec);
			}
		}
		return componentTypeCombo;
	}

	public String getCompType(){
		return componentTypeCombo.getSelectedItem().toString();
	}
	
	@Override
	public String getName(){
		return nameTextField.getText();
	}
}
