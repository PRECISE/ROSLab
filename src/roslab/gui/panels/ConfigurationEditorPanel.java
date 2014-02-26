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
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import roslab.artifacts.ComponentLocation;
import roslab.artifacts.ConfigurationSpec;
import roslab.artifacts.ModuleSpec;
import roslab.artifacts.PortInstance;
import roslab.artifacts.WorkspaceContext;
import roslab.gui.misc.PdeConfiguration;
import roslab.types.builtins.Channel;
import roslab.types.builtins.CompRoles;
import roslab.types.builtins.ComponentSignature;
import roslab.types.builtins.PortName;
import roslab.utils.Pair;
import roslab.utils.logger.LogMethod;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel.mxGeometryChange;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxEdgeStyle;
import com.mxgraph.view.mxGraph;

public class ConfigurationEditorPanel extends Panel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6681945293022722439L;
	final int PORT_DIAMETER = 7;
	private static int PORT_GAP = 22;
	final int PORT_RADIUS = PORT_DIAMETER / 2;
	private mxGraph graph = null;
	private mxGraphComponent  graphComponent = null;
	private JScrollPane jsp = null;
	private JPopupMenu popup;
	
	private int lastClickedX;
	private int lastClickedY;
	private ConfigurationSpec currentConfiguration;
	
	private Map<String, mxCell> portMap = new HashMap<String, mxCell>();
	private List<mxCell> graphCells = new ArrayList<mxCell>();
	private List<mxCell> graphEdges = new ArrayList<mxCell>();
	
	public ConfigurationEditorPanel(){
		this.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		this.setPreferredSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		this.setLayout(new BorderLayout());
		this.graphComponent = getScenarioGraphComponent();
		this.graphComponent.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		graph.getModel().addListener(mxEvent.CHANGE, new changeListener());
		this.jsp = new JScrollPane(graphComponent);
		this.jsp.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		this.add(jsp);
		getPopup();
		graphComponent.getGraphControl().addMouseListener(new popupListener());
	}
	
	private JPopupMenu getPopup(){
		if(this.popup == null){
			popup = new JPopupMenu();
			JMenu submenu = new JMenu("Add component");
			if(!PdeConfiguration.lightweightMenusOverJgraph){
				popup.setLightWeightPopupEnabled(false);
				submenu.getPopupMenu().setLightWeightPopupEnabled(false);
			}
			JMenuItem deviceMenuItem = new JMenuItem("ROS Service");
			deviceMenuItem.addActionListener(new addDeviceComponentMenuListener());
			//JMenuItem appPanelMenuItem = new JMenuItem("App Panel");
			//appPanelMenuItem.addActionListener(new addAppPanelComponentMenuListener());
			JMenuItem logicMenuItem = new JMenuItem("ROS Node");
			logicMenuItem.addActionListener(new addLogicComponentMenuListener());
			submenu.add(deviceMenuItem);
		//	submenu.add(appPanelMenuItem);
			submenu.add(logicMenuItem);
		    popup.add(submenu);
		    JMenuItem menuItem = new JMenuItem("Add connection");
		    menuItem.addActionListener(new addConnectionMenuListener());
		    popup.add(menuItem);  
		}
		return this.popup;
	}
	
	private final mxGraphComponent getScenarioGraphComponent(){
		graph = new mxGraph() {
			
			// Ports are not used as terminals for edges, they are
			// only used to compute the graphical connection point
			@Override
			public boolean isPort(Object cell)
			{
				mxGeometry geo = getCellGeometry(cell);
				
				return (geo != null) ? geo.isRelative() : false;
			}
			
			// Implements a tooltip that shows the actual
			// source and target of an edge
			@Override
			public String getToolTipForCell(Object cell)
			{
				if (model.isEdge(cell))
				{
					return convertValueToString(model.getTerminal(cell, true)) + " -> " +
						convertValueToString(model.getTerminal(cell, false));
				}
				
				return super.getToolTipForCell(cell);
			}
			
			// Removes the folding icon and disables any folding
			@Override
			public boolean isCellFoldable(Object cell, boolean collapse)
			{
				return false;
			}
		};
		
		// Sets the default edge style
		Map<String, Object> style = graph.getStylesheet().getDefaultEdgeStyle();
		style.put(mxConstants.STYLE_EDGE, mxEdgeStyle.ElbowConnector);
		
		//Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		try
		{
			/*mxCell v1 = (mxCell) graph.insertVertex(parent, null, "Hello", 20,
					20, 100, 100, "");
			v1.setConnectable(false);
			mxGeometry geo = graph.getModel().getGeometry(v1);
			// The size of the rectangle when the minus sign is clicked
			geo.setAlternateBounds(new mxRectangle(20, 20, 100, 50));

			mxGeometry geo1 = new mxGeometry(0, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			// Because the origin is at upper left corner, need to translate to
			// position the center of port correctly
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);

			mxCell port1 = new mxCell(null, geo1,
					"shape=ellipse;perimter=ellipsePerimeter");
			port1.setVertex(true);

			mxGeometry geo2 = new mxGeometry(1.0, 0.5, PORT_DIAMETER,
					PORT_DIAMETER);
			geo2.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo2.setRelative(true);

			mxCell port2 = new mxCell(null, geo2,
					"shape=ellipse;perimter=ellipsePerimeter");
			port2.setVertex(true);

			graph.addCell(port1, v1);
			graph.addCell(port2, v1);

			Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30);
			
			graph.insertEdge(parent, null, "Edge", port2, v2);*/
		}
		finally
		{
			graph.getModel().endUpdate();
		}

		mxGraphComponent graphComponent = new mxGraphComponent(graph);
		graphComponent.setToolTips(true);
		graph.setCellsResizable(false);
		graph.setEdgeLabelsMovable(false);
		return graphComponent;
	}
	
	public void renderConfiguration(ConfigurationSpec spec){
			graph.getModel().beginUpdate();
			Set<String> keys = spec.configurationComponents.keySet();
			for(String k : keys){
				renderComponent(spec, k);
			}
			Map<String, Channel> channels = spec.channels;
			Set<String> channNames = channels.keySet();
			for(String channame : channNames){
				Channel chan = channels.get(channame);
				PdeConfiguration.wsp.addConnInstToUI(spec.configurationName, chan);
				mxCell pubPort = portMap.get(chan.getPubIdent());
				mxCell subPort = portMap.get(chan.getSubIdent());
				mxCell edge = (mxCell)graph.insertEdge(graph.getDefaultParent(),
						null , "", pubPort, subPort, "edgeStyle=entityRelationEdgeStyle");
				graphEdges.add(edge);
				
			}
			
			//checking null is added for backward compatibility
//			if(spec.getHandledExceptions() != null){
//				for(Pair<String, String> p : spec.getHandledExceptions().keySet()){
//					//if(spec.getHandledException(p).getHandledExceptionNames().isEmpty()) continue;
//					
//					String pubPortS = p.fst + "." + PortSpec.ExceptionOutPortName;
//					String subPortS = p.snd + "." + PortSpec.ExceptionInPortName;
//					String pub2sub = pubPortS + "->" + subPortS;
//					PdeConfiguration.wsp.addExceptionHandlerInstToUI(spec.configurationName, pub2sub);
//					mxCell pubPort = portMap.get(pubPortS);
//					mxCell subPort = portMap.get(subPortS);
//					mxCell edge = (mxCell)graph.insertEdge(graph.getDefaultParent(),
//							null , "", pubPort, subPort, "edgeStyle=entityRelationEdgeStyle");
//					graphEdges.add(edge);
//				}
//			}
			
			graph.getModel().endUpdate();		
	}
	
	public void renderComponent(ConfigurationSpec spec, String localName){
		Pair<ModuleSpec, ComponentLocation> p = spec.configurationComponents.get(localName);
		PdeConfiguration.wsp.addCompInstToUI(spec.configurationName, localName, p);
		Object parent = graph.getDefaultParent(); //fix
		ModuleSpec mspec = p.fst;
		ComponentLocation loc = p.snd;
		ComponentSignature csig = mspec.sig;
		List<PortName> pubPorts = csig.getSendPortNames();
		List<PortName> subPorts = csig.getRecvPortNames();

		int textWidth = localName.length() * 8;
		int subTextMax = 0;
		int pubTextMax = 0;
		for(PortName s : subPorts){
			subTextMax = Math.max(subTextMax, s.getName().length());
		}
		for(PortName s : pubPorts){
			pubTextMax = Math.max(pubTextMax, s.getName().length());
		}
		
		String renderLabel = localName; // + "\n - \n" + mspec.type;
		String color = "";
		final int portMax;
		int maxPorts;
		
		PortName pname = csig.getExceptionPortName();
		
		if(mspec.role.equals(CompRoles.ROS_NODE)){
			color = "fillColor=#CC9966";
			//Leaving some space for exception port
			portMax = Math.max(subPorts.size() + 1, pubPorts.size());
			maxPorts = Math.max(pubPorts.size(), subPorts.size());
			if(pname!=null){
				subTextMax = Math.max(subTextMax, pname.getName().length());
				maxPorts = Math.max(subPorts.size() + 1, maxPorts);
			}
		}
		else{
            if(mspec.type.equals("GUI")) {
                color = "fillColor=#fada5e";
            }
            else {
			color = "fillColor=#6699CC";
            }
			//Making some space for exception port
			portMax = Math.max(subPorts.size(), pubPorts.size() + 1);
			maxPorts = Math.max(pubPorts.size(), subPorts.size());
			if(pname!=null){
				pubTextMax = Math.max(pubTextMax, pname.getName().length());
				maxPorts = Math.max(pubPorts.size() + 1, maxPorts);
			}
		}
		
		subTextMax = subTextMax * 10;
		pubTextMax = pubTextMax * 10;
		int portTextMax = Math.max(subTextMax, pubTextMax);
		textWidth = Math.max(textWidth, portTextMax);
		int width = Math.max(80, textWidth );
		
		final int portHeight = portMax * PORT_GAP + 15;
		final int height = Math.max(85, portHeight + 30);
		
		mxCell comp = (mxCell) graph.insertVertex(parent, null, renderLabel, loc.x,
				loc.y, width, height, color + ";verticalAlign=top;fontColor=black;rounded=1");
		
		//graphComponent.get
		graphCells.add(comp);
		comp.setConnectable(false);
		//make publish ports
		int i = 0;
		for(PortName name : pubPorts){
			double offset = 0.25 + (i * (0.65 / maxPorts));
			mxGeometry geo1 = new mxGeometry(1, offset
					, PORT_DIAMETER,
					PORT_DIAMETER);
			// Because the origin is at upper left corner, need to translate to
			// position the center of port correctly
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);
			mxCell port1 = new mxCell(name + "  ", geo1,
					"shape=ellipse;perimter=ellipsePerimeter;align=right;fillColor=red");
			port1.setVertex(true);
			port1.setConnectable(false);
			graph.addCell(port1, comp);
			String ident = localName + "." + name;
			portMap.put(ident, port1);
			graphCells.add(port1);
			i++;
		}
		
		//make exception generator port
/*		if(mspec.role.equals(CompRoles.DEVICE_NAME)){
			if(pname != null){
				double offset = 0.25 + (i * (0.65 / maxPorts));
				mxGeometry geo1 = new mxGeometry(1, offset
						, PORT_DIAMETER,
						PORT_DIAMETER);
				// Because the origin is at upper left corner, need to translate to
				// position the center of port correctly
				geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
				geo1.setRelative(true);
				mxCell port1 = new mxCell(pname + "  ", geo1,
						"shape=ellipse;perimter=ellipsePerimeter;align=right;fillColor=yellow");
				port1.setVertex(true);
				port1.setConnectable(false);
				graph.addCell(port1, comp);
				String ident = localName + "." + pname;
				portMap.put(ident, port1);
				graphCells.add(port1);
			}
		}*/
		
		//make subscribe ports
		i = 0;
		for(PortName name : subPorts){
			double offset = 0.33 + (i * (0.65 / maxPorts));
			mxGeometry geo1 = new mxGeometry(0, offset
					, PORT_DIAMETER,
					PORT_DIAMETER);
			// Because the origin is at upper left corner, need to translate to
			// position the center of port correctly
			geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
			geo1.setRelative(true);
			mxCell port1 = new mxCell("  " + name, geo1,
					"shape=ellipse;perimter=ellipsePerimeter;align=left;fillColor=blue");
			port1.setVertex(true);
			port1.setConnectable(false);
			graph.addCell(port1, comp);
			String ident = localName + "." + name;
			portMap.put(ident, port1);
			graphCells.add(port1);
			i++;
		}
		
		//make exception subscriber port
/*		if(!mspec.role.equals(CompRoles.DEVICE_NAME)){
			if(pname != null){
				double offset = 0.33 + (i * (0.65 / maxPorts));
				mxGeometry geo1 = new mxGeometry(0, offset
						, PORT_DIAMETER,
						PORT_DIAMETER);
				// Because the origin is at upper left corner, need to translate to
				// position the center of port correctly
				geo1.setOffset(new mxPoint(-PORT_RADIUS, -PORT_RADIUS));
				geo1.setRelative(true);
				mxCell port1 = new mxCell("  " + pname, geo1,
						"shape=ellipse;perimter=ellipsePerimeter;align=left;fillColor=black");
				port1.setVertex(true);
				port1.setConnectable(false);
				graph.addCell(port1, comp);
				String ident = localName + "." + pname;
				portMap.put(ident, port1);
				graphCells.add(port1);
			}
		}*/
		
	}
	
	class popupListener extends MouseAdapter{
		 @Override
		public void mousePressed(MouseEvent e) {
			 	LogMethod.log("MP");
		    	maybeShowPopup(e); 
		    }

		    @Override
			public void mouseReleased(MouseEvent e) {
		    	LogMethod.log("MR");
		        maybeShowPopup(e);
		    }
		    
		    @Override
			public void mouseClicked(MouseEvent e){

		    }
		    
		    @Override
			public void mouseDragged(MouseEvent e){
		    	LogMethod.log("Dragged");
		    }

		    private void maybeShowPopup(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		        	showAppropriatePopup(e);
		        }
		    }
	}
	
	class cellListener extends MouseAdapter{
		
		mxCell owner;
	
		public cellListener(mxCell owner) {
			super();
			this.owner = owner;
		}

		@Override
		public void mouseReleased(MouseEvent e){
			
		}
	}
	
	/**
	 * @author aking
	 * This is a kludge.  I need to change this to get the selected cell
	 * directly and then use some form of direct indexing to find the correct
	 * component instance in the current solution
	 */
	class changeListener implements mxIEventListener{

		@Override
		public void invoke(Object arg0, mxEventObject arg1) {
			Map<String, Object> changeMap =  arg1.getProperties();
			try{
				@SuppressWarnings("unchecked")
				List<mxGeometryChange> list = (List<mxGeometryChange>)changeMap.get("changes");
				for(int i = 0; i < list.size(); i++){
					if(list.get(i) instanceof mxGeometryChange){
						mxGeometryChange change = list.get(i);
						mxCell instance = (mxCell)change.getCell();
						//make it so edges can't be moved directly
						if(instance.isEdge()){
							
						}
						double x = instance.getGeometry().getX();
						double y = instance.getGeometry().getY();
						if(currentConfiguration != null){
							@SuppressWarnings("unused")
							ComponentLocation loc 
								= new ComponentLocation((int)x,(int)y);
							String[] labelTokens = ((String)instance.getValue()).split(" ");
							Pair<ModuleSpec, ComponentLocation> orig =
							currentConfiguration.configurationComponents.get(labelTokens[0].trim());
							if(orig != null){
								orig.snd.x = (int)x;
								orig.snd.y = (int)y;
								LogMethod.log(instance.getValue() + "updated to" + orig.snd);
							}
						}
						LogMethod.log("" + instance.getValue());
					}
					else{
						
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}	
		}	
	}
	
	public void setConfiguration(ConfigurationSpec scenarioSpec){
		this.currentConfiguration = scenarioSpec;
	}
	
	public void reRender(){
		clearConfiguration();
		if(WorkspaceContext.currentWorkspace != null && currentConfiguration != null){
			renderConfiguration(currentConfiguration);
		}
	}
	
	public void clearConfiguration(){
		portMap.clear();
		graph.getModel().beginUpdate();
		for(mxCell cell : graphCells){
			graph.getModel().remove(cell);
		}
		for(mxCell edge : graphEdges){
			graph.getModel().remove(edge);
		}
		graphCells.clear();
		graph.getModel().endUpdate();
		graph.refresh();
	}
	
	private void showAppropriatePopup(MouseEvent e){
		Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
				this);
		LogMethod.log("POPUP");
		lastClickedX = pt.x;
		lastClickedY = pt.y;
		popup.setFocusable(true);
		popup.show(this, pt.x, pt.y);
		//e.consume();
	}
	
	private void addComponentToConfiguration(String typeName, String name){
		ModuleSpec spec =
			WorkspaceContext.currentWorkspace.moduleTypes.get(typeName);
		if(spec != null){
			ComponentLocation cloc = new ComponentLocation(lastClickedX, lastClickedY);
			Pair<ModuleSpec, ComponentLocation> comp = new Pair<ModuleSpec, ComponentLocation>(spec, cloc);
			currentConfiguration.configurationComponents.put(name, comp);
			this.reRender();
		}
		else{
			LogMethod.log("Unknown component type: " + typeName);
		}
	}
	
	class addDeviceComponentMenuListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			AddComponentToConfigurationDialog panel = 
				new AddComponentToConfigurationDialog(CompRoles.ROS_SERVICE);
			int selection = JOptionPane.showConfirmDialog(graphComponent,panel,
					"New Device Component Instance" ,JOptionPane.OK_CANCEL_OPTION);
			if(selection == JOptionPane.OK_OPTION){
				addComponentToConfiguration(panel.getCompType(), panel.getName());
			}
		}	
	}
	
	class addLogicComponentMenuListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			AddComponentToConfigurationDialog panel = 
				new AddComponentToConfigurationDialog(CompRoles.ROS_NODE);
			int selection = JOptionPane.showConfirmDialog(graphComponent,panel,
					"New Logic Component Instance" ,JOptionPane.OK_CANCEL_OPTION);
			if(selection == JOptionPane.OK_OPTION){
				addComponentToConfiguration(panel.getCompType(), panel.getName());
			}
		}	
	}
	
	class addConnectionMenuListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			AddConnectionDialogPanel panel 
				= new AddConnectionDialogPanel(currentConfiguration);
			int selection = JOptionPane.showConfirmDialog(graphComponent, panel,
					"New Connection" ,JOptionPane.OK_CANCEL_OPTION);
			if(selection == JOptionPane.OK_OPTION){
				PortInstance publisher = panel.getSelectedPub();
				PortInstance subscriber = panel.getSelectedSub();
				if(publisher != null && subscriber != null){
					Channel channel = new Channel();
					Pair<String, ModuleSpec> pcomp = new Pair<String, ModuleSpec>(publisher.moduleInstanceName, publisher.moduleSpec);
					channel.setPubComp(pcomp);
					int pubindex = publisher.moduleSpec.sig.getSendPortNames().indexOf(new PortName(publisher.portName));
					channel.setPubName(publisher.moduleSpec.sig.getSendPortNames().get(pubindex));
					
					Pair<String, ModuleSpec> scomp = new Pair<String, ModuleSpec>(subscriber.moduleInstanceName, subscriber.moduleSpec);
					channel.setSubComp(scomp);
					int subindex = subscriber.moduleSpec.sig.getRecvPortNames().indexOf(new PortName(subscriber.portName));
					channel.setSubName(subscriber.moduleSpec.sig.getRecvPortNames().get(subindex));
					
					channel.setChanName(System.currentTimeMillis() + "chan"); //FIXME: find a better ID
					currentConfiguration.addChannel(channel);
					reRender();
				}
			}
		}	
	}
	
	

}
