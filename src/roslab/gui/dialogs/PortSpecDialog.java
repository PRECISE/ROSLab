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
package roslab.gui.dialogs;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JComboBox;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JSlider;

/**
 * @author aking
 *
 */
public class PortSpecDialog extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JTextField portNameTextField = null;
	private JLabel portNameLabel = null;
	private JLabel directionLabel = null;
	private JComboBox<String> directionComboBox = null;
	
	private String[] directionStrings = {"publish", "subscribe"};
	private JLabel typeLabel = null;
	private JComboBox<?> typeComboBox = null;
	private JButton setTypeParamButton = null;
	private JLabel consLabel = null;
	private JLabel timingULabel = null;
	private JLabel timingLLabel = null;
	private JSlider timingUSlider = null;
	private JSlider timingLSlider = null;
	private JLabel valueConsLabel = null;
	/**
	 * @param owner
	 */
	public PortSpecDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(new Dimension(424, 298));
		this.setContentPane(getJContentPane());
		this.setTitle("Port Specification");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			valueConsLabel = new JLabel();
			valueConsLabel.setBounds(new Rectangle(16, 216, 129, 16));
			valueConsLabel.setText("Value Constraints: ");
			timingLLabel = new JLabel();
			timingLLabel.setBounds(new Rectangle(16, 171, 113, 16));
			timingLLabel.setText("Timing Lower:");
			timingULabel = new JLabel();
			timingULabel.setBounds(new Rectangle(12, 123, 108, 16));
			timingULabel.setText("Timing Upper:");
			consLabel = new JLabel();
			consLabel.setBounds(new Rectangle(13, 105, 104, 16));
			consLabel.setText("Constraints:");
			typeLabel = new JLabel();
			typeLabel.setBounds(new Rectangle(14, 60, 37, 16));
			typeLabel.setText("Type:");
			directionLabel = new JLabel();
			directionLabel.setBounds(new Rectangle(14, 38, 76, 16));
			directionLabel.setText("Direction:");
			portNameLabel = new JLabel();
			portNameLabel.setBounds(new Rectangle(16, 15, 90, 16));
			portNameLabel.setText("Name:");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setSize(new Dimension(424, 298));
			jContentPane.add(getPortNameTextField(), null);
			jContentPane.add(portNameLabel, null);
			jContentPane.add(directionLabel, null);
			jContentPane.add(getDirectionComboBox(), null);
			jContentPane.add(typeLabel, null);
			jContentPane.add(getTypeComboBox(), null);
			jContentPane.add(getSetTypeParamButton(), null);
			jContentPane.add(consLabel, null);
			jContentPane.add(timingULabel, null);
			jContentPane.add(timingLLabel, null);
			jContentPane.add(getTimingUSlider(), null);
			jContentPane.add(getTimingLSlider(), null);
			jContentPane.add(valueConsLabel, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes portNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getPortNameTextField() {
		if (portNameTextField == null) {
			portNameTextField = new JTextField();
			portNameTextField.setBounds(new Rectangle(109, 6, 131, 28));
			portNameTextField.setText("port name");
		}
		return portNameTextField;
	}

	/**
	 * This method initializes directionComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<String> getDirectionComboBox() {
		if (directionComboBox == null) {
			directionComboBox = new JComboBox<String>();
			directionComboBox.setBounds(new Rectangle(111, 33, 126, 27));
			directionComboBox.addItem(directionStrings[0]);
			directionComboBox.addItem(directionStrings[1]);
		}
		return directionComboBox;
	}

	/**
	 * This method initializes typeComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox<?> getTypeComboBox() {
		if (typeComboBox == null) {
			typeComboBox = new JComboBox<Object>();
			typeComboBox.setBounds(new Rectangle(110, 60, 52, 27));
		}
		return typeComboBox;
	}

	/**
	 * This method initializes setTypeParamButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSetTypeParamButton() {
		if (setTypeParamButton == null) {
			setTypeParamButton = new JButton();
			setTypeParamButton.setBounds(new Rectangle(239, 57, 110, 29));
			setTypeParamButton.setText("Set Params...");
		}
		return setTypeParamButton;
	}

	/**
	 * This method initializes timingUSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getTimingUSlider() {
		if (timingUSlider == null) {
			timingUSlider = new JSlider();
			timingUSlider.setBounds(new Rectangle(15, 141, 344, 21));
		}
		return timingUSlider;
	}

	/**
	 * This method initializes timingLSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getTimingLSlider() {
		if (timingLSlider == null) {
			timingLSlider = new JSlider();
			timingLSlider.setBounds(new Rectangle(10, 195, 347, 17));
		}
		return timingLSlider;
	}

}
