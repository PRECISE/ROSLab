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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;

import roslab.artifacts.ConfigurationSpec;
import roslab.artifacts.PortInstance;


public class AddConnectionDialogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel publisherLabel = null;
	private JLabel subscriberLabel = null;
	private JComboBox<PortInstance> publisherComboBox = null;
	private JComboBox<PortInstance> subscriberComboBox = null;
	
	private ConfigurationSpec currentScenario = null;
	private boolean selectedPubFirst = false;
	private boolean selectedSubFirst = false;
	private boolean selectionValid = false;
	/**
	 * This is the default constructor
	 */
	public AddConnectionDialogPanel() {
		super();
		initialize();
	}
	
	public AddConnectionDialogPanel(ConfigurationSpec currentScenario){
		super();
		this.currentScenario = currentScenario;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		subscriberLabel = new JLabel();
		subscriberLabel.setBounds(new Rectangle(14, 40, 93, 25));
		subscriberLabel.setText("Subscriber:");
		publisherLabel = new JLabel();
		publisherLabel.setBounds(new Rectangle(14, 11, 92, 23));
		publisherLabel.setText("Publisher:");
		this.setSize(471, 82);
		this.setPreferredSize(new Dimension(471, 82));
		this.setLayout(null);
		this.add(publisherLabel, null);
		this.add(subscriberLabel, null);
		this.add(getPublisherComboBox(), null);
		this.add(getSubscriberComboBox(), null);
		populateCombos();
	}
	
	private void populateCombos(){
		if(currentScenario != null){
			List<PortInstance> pubPorts = currentScenario.getPublishInstances();
			List<PortInstance> subPorts = currentScenario.getSubscribeInstances();
			populatePublisher(pubPorts);
			populateSubscriber(subPorts);
			publisherComboBox.setSelectedIndex(0);
		}
	}
	
	private void populatePublisher(List<PortInstance> pubPorts){
		publisherComboBox.removeAllItems();
		for(PortInstance pi : pubPorts){
			publisherComboBox.addItem(pi);
		}
	}
	
	private void populateSubscriber(List<PortInstance> subPorts){
		subscriberComboBox.removeAllItems();
		for(PortInstance pi : subPorts){
			subscriberComboBox.addItem(pi);
		}
	}

	/**
	 * This method initializes publisherComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<PortInstance> getPublisherComboBox() {
		if (publisherComboBox == null) {
			publisherComboBox = new JComboBox<PortInstance>();
			publisherComboBox.setBounds(new Rectangle(107, 11, 352, 24));
			publisherComboBox.addActionListener(new publisherComboBoxAction());
		}
		return publisherComboBox;
	}

	/**
	 * This method initializes subscriberComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<PortInstance> getSubscriberComboBox() {
		if (subscriberComboBox == null) {
			subscriberComboBox = new JComboBox<PortInstance>();
			subscriberComboBox.setBounds(new Rectangle(108, 41, 351, 24));
			subscriberComboBox.addActionListener(new subscriberComboBoxAction());
		}
		return subscriberComboBox;
	}
	
	public PortInstance getSelectedPub(){
		if(selectionValid){
			return (PortInstance)publisherComboBox.getSelectedItem();
		}
		else{
			return null;
		}
	}
	
	public PortInstance getSelectedSub(){
		if(selectionValid){
			return (PortInstance)subscriberComboBox.getSelectedItem();
		}
		else{
			return null;
		}
	}
	
	private class publisherComboBoxAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				if(!selectedSubFirst){
					selectedPubFirst = true;
					PortInstance pi 
						= (PortInstance)publisherComboBox.getSelectedItem();
					List<PortInstance> compatSubInstances 
						= currentScenario.getSubscribeInstancesByType(pi.portType);
					populateSubscriber(compatSubInstances);
					if(subscriberComboBox.getModel().getSize() > 0){
						selectionValid = true;
					}
					else{
						selectionValid = false;
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	private class subscriberComboBoxAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			try{
				if(!selectedPubFirst){
					selectedSubFirst = true;
					PortInstance pi 
						= (PortInstance)publisherComboBox.getSelectedItem();
					List<PortInstance> compatSubInstances 
						= currentScenario.getPublishInstancesByType(pi.portType);
					populatePublisher(compatSubInstances);
					if(publisherComboBox.getModel().getSize() > 0){
						selectionValid = true;
					}
					else{
						selectionValid = false;
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	

}  //  @jve:decl-index=0:visual-constraint="10,10"
