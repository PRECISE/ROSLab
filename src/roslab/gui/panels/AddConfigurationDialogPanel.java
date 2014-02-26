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
import javax.swing.JTextField;

public class AddConfigurationDialogPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel nameLabel = null;
	private JTextField nameTextField = null;
	/**
	 * This is the default constructor
	 */
	public AddConfigurationDialogPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		nameLabel = new JLabel();
		nameLabel.setBounds(new Rectangle(16, 17, 48, 20));
		nameLabel.setText("Name:");
		this.setSize(286, 67);
		this.setPreferredSize(new Dimension(286, 67));
		this.setLayout(null);
		this.add(nameLabel, null);
		this.add(getNameTextField(), null);
	}

	/**
	 * This method initializes nameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getNameTextField() {
		if (nameTextField == null) {
			nameTextField = new JTextField();
			nameTextField.setBounds(new Rectangle(70, 15, 189, 23));
		}
		return nameTextField;
	}
	
	@Override
	public String getName(){
		return nameTextField.getText();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
