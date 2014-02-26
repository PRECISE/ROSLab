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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import roslab.model.Module;
import roslab.model.Port;

public class ModuleVisualizationPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private mxGraph visGraph = null;
	private static int PORT_GAP = 20;
	/**
	 * This is the default constructor
	 */
	public ModuleVisualizationPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		
		this.setLayout(new BorderLayout());
		this.add(new mxGraphComponent(getGraph()));
	}
	
	private mxGraph getGraph(){
		if(this.visGraph != null){
			return this.visGraph;
		}
		else{
			mxGraphModel model = new mxGraphModel();
			mxStylesheet style = new mxStylesheet();
			//view.setAutoSizeOnValueChange(true);
			this.visGraph = new mxGraph(model, style);
			this.visGraph.setMaximumGraphBounds(new mxRectangle(0,0,Integer.MAX_VALUE, Integer.MAX_VALUE));
			//this.visGraph.setSize(this.getWidth(), this.getHeight());
			//this.visGraph.setCloneable(true);
			//this.visGraph.setPortsVisible(true);
			// Enable edit without final RETURN keystroke
			//this.visGraph.setInvokesStopCellEditing(true);

			// When over a cell, jump to its default port (we only have one, anyway)
			//this.visGraph.setJumpToDefaultPort(true);
			return null;
		}
	}
	
	public void updateView(final Module m){
		if(visGraph != null){
			visGraph.removeCells();
			/*
			final List<Port> inPorts = m.getInPorts();
			final List<Port> outPorts = m.getOutPorts();
			final String name = m.getName();

			final double visualWidth = name.length() * 15.0d;
			final double inH = inPorts.size() * PORT_GAP + 15;
			final double inW = outPorts.size() * PORT_GAP + 15;
			*/
		}
	}
	
	
	
/*
	@SuppressWarnings("unused")
	private  DefaultGraphCell createVertex(String name, double x,
			double y, double w, double h, Color bg, boolean raised, List<Port> inPorts, List<Port> outPorts) {
		final int portMax = Math.max(inPorts.size(), outPorts.size());
		final double portHeight = portMax * PORT_GAP + 15;
		final double height = Math.max(h, portHeight);
		// Create vertex with the given name
		DefaultGraphCell cell = new DefaultGraphCell(name);
		// Set bounds
		
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(
				x, y, w, height));
		// Set fill color
		if (bg != null) {
			GraphConstants.setBackground(cell.getAttributes(), bg);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}
		// Set raised border
		if (raised)
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 2));
		else
			// Set black border
			GraphConstants.setBorderColor(cell.getAttributes(), Color.BLACK);
		// Add a Floating Port
		//cell.addPort();
		//add input ports
		for(int i = 0; i < inPorts.size(); i++){
			Point p =  new Point(GraphConstants.PERMILLE, (i * GraphConstants.PERMILLE / inPorts.size()));
			GraphConstants.setOffset(cell.getAttributes(),p);
			cell.addPort(p);
		}
		//add output ports
		for(int i = 0; i < outPorts.size(); i++){
			Point p = new Point(0, (i * PORT_GAP * 15) + 15);
			GraphConstants.setOffset(cell.getAttributes(), p);
			cell.addPort(p);
		}
		return cell;
	}
*/

}
