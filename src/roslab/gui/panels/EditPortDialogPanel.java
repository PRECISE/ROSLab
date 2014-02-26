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
import java.util.Map;
import java.util.Set;

import javax.swing.JTextField;
import javax.swing.JComboBox;

import roslab.artifacts.ModuleSpec;
import roslab.artifacts.PortSpec;
import roslab.artifacts.WorkspaceContext;
import roslab.types.builtins.PortName;
import roslab.types.builtins.PortTypes;
import roslab.types.constraints.ConstructionConstraints;


public class EditPortDialogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel nameLabel = null;
	private JLabel typeLabel = null;
	private JLabel directionLabel = null;
	private JTextField nameTextField = null;
	private JComboBox<PortSpec> typeComboBox = null;
	private JComboBox<String> directionComboBox = null;
	
	private ModuleSpec mspec;
	private String portName;
	private String portType;
	
	private String originalDirection;
	
	/**
	 * This is the default constructor
	 */
	public EditPortDialogPanel() {
		super();
		initialize();
	}
	
	public EditPortDialogPanel(ModuleSpec mspec, String portName, String portType){
		super();
		this.mspec = mspec;
		this.portName = portName;
		this.portType = portType;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		directionLabel = new JLabel();
		directionLabel.setBounds(new Rectangle(19, 71, 68, 16));
		directionLabel.setText("Direction:");
		typeLabel = new JLabel();
		typeLabel.setBounds(new Rectangle(19, 47, 49, 16));
		typeLabel.setText("Type:");
		nameLabel = new JLabel();
		nameLabel.setBounds(new Rectangle(18, 16, 51, 23));
		nameLabel.setText("Name:");
		this.setSize(249, 106);
		this.setPreferredSize(new Dimension(249, 106));
		this.setLayout(null);
		this.add(nameLabel, null);
		this.add(typeLabel, null);
		this.add(directionLabel, null);
		this.add(getNameTextField(), null);
		this.add(getTypeComboBox(), null);
		this.add(getDirectionComboBox(), null);
		setupOptions();
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBounds(new Rectangle(73, 16, 157, 23));
		}
		nameTextField.setText(this.portName);
		return nameTextField;
	}

	/**
	 * This method initializes typeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<PortSpec> getTypeComboBox() {
		if (typeComboBox == null) {
			typeComboBox = new JComboBox<PortSpec>();
			typeComboBox.setBounds(new Rectangle(75, 43, 153, 25));
		}
		return typeComboBox;
	}

	/**
	 * This method initializes directionComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getDirectionComboBox() {
		if (directionComboBox == null) {
			directionComboBox = new JComboBox<String>();
			directionComboBox.setBounds(new Rectangle(90, 68, 138, 22));
		}
		return directionComboBox;
	}
	
	private void setupOptions(){
		Map<String, PortSpec> builtInPortTypes = 
			PortTypes.getBuiltInPortMap();
		Set<String> portTypes = builtInPortTypes.keySet();
		for(String type : portTypes){
			PortSpec pspec = builtInPortTypes.get(type);
			typeComboBox.addItem(pspec);
			if(pspec.typeName.equals(this.portType)){
				typeComboBox.setSelectedItem(pspec);	
			}
		}
		if((mspec != null) && WorkspaceContext.currentWorkspace != null){
			if(this.mspec != null){
				List<PortName> rports = mspec.sig.getRecvPortNames();
								
				if(ConstructionConstraints.canAddNewPublishPort(mspec)){
					directionComboBox.addItem("Publish");
				}
				if(ConstructionConstraints.canAddNewSubscribePort(mspec)){
					directionComboBox.addItem("Subscribe");
				}
				
				if(rports.contains(new PortName(this.portName))){
					directionComboBox.setSelectedItem("Subscribe");
					originalDirection = "Subscribe";
				} else {
					directionComboBox.setSelectedItem("Publish");
					originalDirection = "Publish";
				}
			}		
		}
	}
	
	public String getDirection(){
		return directionComboBox.getSelectedItem().toString();
	}
	
	@Override
	public String getName(){
		return nameTextField.getText();
	}
	
	public String getPortType(){
		return typeComboBox.getSelectedItem().toString();
	}
	
	public PortSpec getPortTypeSpec(){
		return (PortSpec)(typeComboBox.getSelectedItem());
	}
	
	public boolean isPublish(){
		return directionComboBox.getSelectedItem().toString().equals("Publish");
	}
	
	public boolean isDirectionChanged(){
		if(originalDirection.equals("Publish") && directionComboBox.getSelectedItem().toString().equals("Publish")){
			return false;
		} else if(originalDirection.equals("Subscribe") && directionComboBox.getSelectedItem().toString().equals("Subscribe")){
			return false;
		} else {
			return true;
		}
	}

}
