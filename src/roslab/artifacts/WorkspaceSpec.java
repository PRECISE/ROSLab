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
package roslab.artifacts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roslab.types.builtins.Channel;
import roslab.types.builtins.ComponentSignature;
import roslab.types.builtins.PortName;
import roslab.types.builtins.PortType;
import roslab.types.builtins.PortTypes;
import roslab.types.builtins.TaskDescriptor;
import roslab.utils.FileIO;
import roslab.utils.Pair;

public class WorkspaceSpec { //*.wksp.xml
	
	public String workspaceName;
	public Map<String, ModuleSpec> moduleTypes; //<compTypeName:Str, ModuleSpec>
	public Map<String, ConfigurationSpec> configurations; //<appConfigName:Str, ConfigurationSpec>
	
	public WorkspaceSpec(String name){
		this.workspaceName = name;
		this.moduleTypes = generateBuiltIns();
		this.configurations = new HashMap<String, ConfigurationSpec>();
	}
	
	public List<ModuleSpec> getCompTypesByRole(String role){
		List<ModuleSpec> ret = new ArrayList<ModuleSpec>();
		for(ModuleSpec mspec : moduleTypes.values()){
			if(mspec.role.equals(role)) ret.add(mspec);
		}
		return ret;
	}
	
	public void deleteComponentType(String typeName){
		if(moduleTypes.containsKey(typeName)){
			Set<String> configurationNames = configurations.keySet();
			//search the scenarios and see if the type has been instantiated
			for(String configurationName : configurationNames){
				ConfigurationSpec sspec = configurations.get(configurationName);
				Set<String> localCompNames = sspec.configurationComponents.keySet();
				List<String> remComps = new ArrayList<String>();
				for(String localName : localCompNames){
					ModuleSpec mspec
						= sspec.configurationComponents.get(localName).fst;
					if(mspec.type.equals(typeName)){
						//found an object instance, now need to find all connections
						//involving it
						Map<String, Channel> channels = sspec.channels;
						Set<String> chanNames = channels.keySet();
						List<String> remChans = new ArrayList<String>();
						for(String chanName : chanNames){
							Channel chan = channels.get(chanName);
							if(chan.getPubComp().fst.equals(localName) || 
									chan.getSubComp().fst.equals(localName)){
								//channels.remove(chanName);
								remChans.add(chanName);
							}
						}
						for(String chanName : remChans){
							channels.remove(chanName);
						}
						//sspec.scenarioComponents.remove(localName);
						remComps.add(localName);
					}
				}
				for(String remComp : remComps){
					sspec.configurationComponents.remove(remComp);
				}
			}
			moduleTypes.remove(typeName);
		}
	}
	
	public List<Pair<String, String>> getCompInstancesPerType(String typeName){
		ArrayList<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();
		for(String configurationName : configurations.keySet()){
			ConfigurationSpec sspec = configurations.get(configurationName);
			Set<String> localCompNames = sspec.configurationComponents.keySet();
			for(String localName : localCompNames){
				ModuleSpec mspec
					= sspec.configurationComponents.get(localName).fst;
				if(mspec.type.equals(typeName)){
					ret.add(new Pair<String, String>(configurationName, localName));
				}
			}
		}
		return ret;
	}
	
	public void deleteComponentTypeSubPort(String componentType, String portName){
		if(moduleTypes.containsKey(componentType)){
			Set<String> configurationNames = configurations.keySet();
			for(String configurationName : configurationNames){
				ConfigurationSpec sspec = configurations.get(configurationName);
				Set<String> localCompNames = sspec.configurationComponents.keySet();
				for(String localName : localCompNames){
					ModuleSpec mspec
						= sspec.configurationComponents.get(localName).fst;
					if(mspec.type.equals(componentType)){
						Map<String, Channel> channels = sspec.channels;
						Set<String> chanNames = channels.keySet();
						List<String> remChan = new ArrayList<String>();
						for(String chanName : chanNames){
							Channel chan = channels.get(chanName);
							if(chan.getSubName().equals(portName)){
								remChan.add(chanName);
							}
						}
						for(String chanName : remChan){
							channels.remove(chanName);
						}
					}
				}
			}
			ModuleSpec mspec = moduleTypes.get(componentType);
			delPortNameInCsig(mspec.sig, portName, false);		
		}
	}
	
	public void deleteComponentTypePubPort(String componentType, String portName){
		if(moduleTypes.containsKey(componentType)){
			Set<String> configurationNames = configurations.keySet();
			for(String configurationName : configurationNames){
				ConfigurationSpec sspec = configurations.get(configurationName);
				Set<String> localCompNames = sspec.configurationComponents.keySet();
				for(String localName : localCompNames){
					ModuleSpec mspec = sspec.configurationComponents.get(localName).fst;
					if(mspec.type.equals(componentType)){
						Map<String, Channel> channels = sspec.channels;
						Set<String> chanNames = channels.keySet();
						List<String> remChan = new ArrayList<String>();
						for(String chanName : chanNames){
							Channel chan = channels.get(chanName);
							if(chan.getPubName().equals(portName)){
								remChan.add(chanName);
							}
						}
						for(String chanName : remChan){
							channels.remove(chanName);
						}
					}
				}
			}
			ModuleSpec mspec = moduleTypes.get(componentType);
			delPortNameInCsig(mspec.sig, portName, true);
		}
	}
	
	private void delPortNameInCsig(ComponentSignature csig, String portName, boolean pub){
		if(pub){
			List<PortName> pubNames = csig.getSendPortNames();
			List<PortType> pubTypes = csig.getSendPortTypes();
			for(int i = 0; i < pubNames.size(); i++){
				if(pubNames.get(i).getName().equals(portName)){
					pubNames.remove(i);
					pubTypes.remove(i);
					break;
				}
			}
		}
		else{
			List<PortName> subNames = csig.getRecvPortNames();
			List<PortType> subTypes = csig.getRecvPortTypes();
			for(int i = 0; i < subNames.size(); i++){
				if(subNames.get(i).getName().equals(portName)){
					subNames.remove(i);
					subTypes.remove(i);
					break;
				}
			}
		}
	}
	
	private static Map<String, ModuleSpec> generateBuiltIns(){
		Map<String, ModuleSpec> ret = null;
				
		try {
			ret = FileIO.loadModules();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return ret;
	}

}
