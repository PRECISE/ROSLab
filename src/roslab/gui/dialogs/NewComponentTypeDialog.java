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

import java.awt.Frame;
import java.awt.BorderLayout;

import javax.swing.JDialog;

import roslab.gui.panels.NewComponentTypePanel;


public class NewComponentTypeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;

	/**
	 * @param owner
	 */
	public NewComponentTypeDialog(Frame owner) {
		super(owner, "New Component Type", true);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(377, 193);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new NewComponentTypePanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}

}  //  @jve:decl-index=0:visual-constraint="27,0"
