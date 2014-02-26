/**
 * Copyright 2009-2011, Kansas State University, 
 * PRECISE Center at the University of Pennsylvania, and
 * Sam Procter
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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import roslab.artifacts.ModuleSpec;
import roslab.types.builtins.PortName;


public class EditTaskDialogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel nameLabel = null;
	private JLabel typeLabel = null;
	private JLabel periodLabel;
	private JTextField nameTextField = null;
	private JLabel assocPortLabel = null;
	private JComboBox typeComboBox = null;
	private JComboBox assocPortComboBox = null;
	
	private ModuleSpec mspec;
	private JTextField periodField;
	private JTextField deadlineField;
	private JTextField wcetField;
	private String portType;
	/**
	 * This is the default constructor
	 */
	public EditTaskDialogPanel() {
		super();
		initialize("Default Task Name", -1, 1, 1, "Default Assoc Port Name");
	}
	
	public EditTaskDialogPanel(ModuleSpec mspec, String taskName, String period, String deadline, String wcet){
		super();
		this.mspec = mspec;
		//Task Format (TaskName:[S|P]Period\p:Deadline\d:WCET\e)
		int iPeriod = cleanInput(period);
		int iDeadline = cleanInput(deadline);
		int iWcet = cleanInput(wcet);
		String assocPortName = mspec.sig.getTaskDescriptorByName(taskName).getTrigPortName();
		initialize(taskName, iPeriod, iDeadline, iWcet, assocPortName);
	}

	private int cleanInput(String dirty) {
		if(dirty.charAt(0) == 'S'){
			dirty = dirty.substring(1);
			portType = "Sporadic";
		} else if (dirty.charAt(0) == 'P'){
			dirty = dirty.substring(1);
			portType = "Periodic";
		}
		if(dirty.charAt(dirty.length() - 1) == 'p' || dirty.charAt(dirty.length() - 1) == 'd' || dirty.charAt(dirty.length() - 1) == 'e')
			dirty = dirty.substring(0, dirty.length()-1);
		return Integer.valueOf(dirty);
	}

	/**
	 * This method initializes this
	 * @param assocPortName 
	 * 
	 * @return void
	 */
	private void initialize(String taskName, int period, int deadline, int wcet, Object assocPortName) {
		periodLabel = new JLabel();
		periodLabel.setBounds(new Rectangle(18, 79, 68, 16));
		periodLabel.setText("Period:");
		periodLabel.setEnabled(false);
		typeLabel = new JLabel();
		typeLabel.setBounds(new Rectangle(18, 47, 49, 16));
		typeLabel.setText("Type:");
		nameLabel = new JLabel();
		nameLabel.setBounds(new Rectangle(18, 16, 51, 23));
		nameLabel.setText("Name:");
		this.setSize(249, 106);
		this.setPreferredSize(new Dimension(308, 200));
		this.setLayout(null);
		this.add(nameLabel, null);
		this.add(typeLabel, null);
		this.add(periodLabel, null);
		this.add(getNameTextField(), null);
		nameTextField.setText(taskName);
		this.add(getTypeComboBox(), null);
		
		periodField = new JTextField();
		periodField.setBounds(new Rectangle(73, 16, 157, 23));
		periodField.setBounds(140, 76, 134, 23);
		periodField.setEnabled(false);
		if(period >= 0)
			periodField.setText(String.valueOf(period));
		add(periodField);
		
		JLabel periodMsLabel = new JLabel();
		periodMsLabel.setText("ms");
		periodMsLabel.setBounds(new Rectangle(19, 71, 68, 16));
		periodMsLabel.setBounds(276, 79, 22, 16);
		add(periodMsLabel);
		
		JLabel deadlineLabel = new JLabel();
		deadlineLabel.setText("Deadline:");
		deadlineLabel.setBounds(new Rectangle(19, 71, 68, 16));
		deadlineLabel.setBounds(18, 109, 68, 16);
		add(deadlineLabel);
		
		deadlineField = new JTextField();
		deadlineField.setBounds(new Rectangle(73, 16, 157, 23));
		deadlineField.setBounds(140, 106, 134, 23);
		deadlineField.setText(String.valueOf(deadline));
		add(deadlineField);
		
		JLabel deadlineMsLabel = new JLabel();
		deadlineMsLabel.setText("ms");
		deadlineMsLabel.setBounds(new Rectangle(19, 71, 68, 16));
		deadlineMsLabel.setBounds(276, 109, 22, 16);
		add(deadlineMsLabel);
		
		JLabel wcetLabel = new JLabel();
		wcetLabel.setText("WCET:");
		wcetLabel.setBounds(new Rectangle(19, 71, 68, 16));
		wcetLabel.setBounds(18, 138, 68, 16);
		add(wcetLabel);
		
		wcetField = new JTextField();
		wcetField.setBounds(new Rectangle(73, 16, 157, 23));
		wcetField.setBounds(140, 135, 134, 23);
		wcetField.setText(String.valueOf(wcet));
		add(wcetField);
		
		JLabel wcetMs = new JLabel();
		wcetMs.setText("ms");
		wcetMs.setBounds(new Rectangle(19, 71, 68, 16));
		wcetMs.setBounds(276, 138, 22, 16);
		add(wcetMs);
		
		assocPortLabel = new JLabel();
		assocPortLabel.setText("Associated Port:");
		assocPortLabel.setBounds(new Rectangle(19, 71, 68, 16));
		assocPortLabel.setBounds(18, 165, 111, 16);
		add(assocPortLabel);
		
		assocPortComboBox = new JComboBox();
		assocPortComboBox.setBounds(new Rectangle(84, 44, 116, 25));
		assocPortComboBox.setBounds(140, 164, 134, 25);
		assocPortComboBox.setModel(new DefaultComboBoxModel(initPortModel()));
		assocPortComboBox.setSelectedItem(assocPortName);
		add(assocPortComboBox);
	}
	
	private String[] initPortModel() {
		String[] portNames = new String[this.mspec.sig.getRecvPortNames().size()];
		int i = 0;
		for(PortName portName : this.mspec.sig.getRecvPortNames()){
			portNames[i++] = portName.getName();
		}
		return portNames;
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBounds(new Rectangle(140, 17, 134, 23));
		}
		return nameTextField;
	}

	/**
	 * This method initializes typeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getTypeComboBox() {
		if (typeComboBox == null) {
			typeComboBox = new JComboBox();
			typeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Sporadic", "Periodic"}));
			typeComboBox.setBounds(new Rectangle(140, 45, 134, 25));
			if(portType.equals("Sporadic"))
				typeComboBox.setSelectedIndex(0);
			else if(portType.equals("Periodic"))
				typeComboBox.setSelectedIndex(1);
			typeComboBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if(event.getActionCommand().equals("comboBoxChanged")){
						if(typeComboBox.getSelectedItem().toString().equals("Sporadic")){
							assocPortComboBox.setEnabled(true);
							assocPortLabel.setEnabled(true);
							periodField.setEnabled(false);
							periodLabel.setEnabled(false);
						} else if(typeComboBox.getSelectedItem().toString().equals("Periodic")){
							assocPortComboBox.setEnabled(false);
							assocPortLabel.setEnabled(false);
							periodField.setEnabled(true);
							periodLabel.setEnabled(true);
						}
					}
				}
			});
		}
		return typeComboBox;
	}
	
	public String getAssocPortName(){
		return assocPortComboBox.getSelectedItem().toString();
	}
	
	public String getType(){
		return typeComboBox.getSelectedItem().toString();
	}
	
	@Override
	public String getName(){
		return nameTextField.getText();
	}

	public int getPeriod(){
		int ret = -1;
		try{
			Integer.valueOf(periodField.getText()); 
		} catch (NumberFormatException e){
			// leave ret at -1
		}
		return ret;
	}
	
	public int getDeadline(){
		return Integer.valueOf(deadlineField.getText());
	}
	
	public int getWCET(){
		return Integer.valueOf(wcetField.getText());
	}
	
	public boolean isPeriodic(){
		if(typeComboBox.getSelectedItem().toString().equals("Periodic"))
			return true;
		else
			return false;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
