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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roslab.types.builtins.Channel;
import roslab.types.builtins.PortName;
import roslab.types.builtins.PortType;
import roslab.utils.Pair;

public class ConfigurationSpec { //*.cfg.xml
	public final String configurationName;
	public final Map<String, Pair<ModuleSpec, ComponentLocation>> configurationComponents; //<instanceName:Str, ..>
	public final Map<String, Channel> channels; //<channelName:Str, MdcfChannel>

	public ConfigurationSpec(final String configurationName){
		super();
		this.configurationName = configurationName;
		this.configurationComponents = 
			new HashMap<String, Pair<ModuleSpec, ComponentLocation>>();
		this.channels = new HashMap<String, Channel>();
	}
	
	public void addComponent(final ModuleSpec spec, final String localName, final int x, final int y){
		this.configurationComponents.put(localName,
                new Pair<ModuleSpec,
                        ComponentLocation>(spec, new ComponentLocation(x, y)));
	}
	
	public void addComponent(final ModuleSpec spec, final String localName){
		this.addComponent(spec, localName, 300, 300);
	}
	
	public void removeComponent(final String localName){
		this.configurationComponents.remove(localName);
	}
	
	public void moveComponent(final String localName, final int newX, final int newY){
		Pair<ModuleSpec, ComponentLocation> old = 
			this.configurationComponents.get(localName);
		this.configurationComponents.remove(localName);
		this.configurationComponents.put(localName, new Pair<ModuleSpec, ComponentLocation>
									(old.fst, new ComponentLocation(newX, newY)));
	}
	
	public void addChannel(final Channel newChan){
		this.channels.put(newChan.getChanName(), newChan);
	}
	
	public void removeChannel(final Channel chan){
		this.channels.remove(chan.getChanName());
	}
	
	public void removeChannel(final String key){
		Set<String> chanNames = this.channels.keySet();
		for(Iterator<String> chanNameIter = chanNames.iterator(); chanNameIter.hasNext(); ){
			String channName = chanNameIter.next();
			Channel chan = this.channels.get(channName);
			if(chan.toString().equals(key)){
				this.channels.remove(channName);
				return;
			}
		}
	}
	
	public List<PortInstance> getPublishInstances(){
		ArrayList<PortInstance> ret = new ArrayList<PortInstance>();
		for( String key : configurationComponents.keySet()){
			Pair<ModuleSpec, ComponentLocation> instance = configurationComponents.get(key);
			ModuleSpec mspec = instance.fst;
			List<PortName> portNames = mspec.sig.getSendPortNames();
			List<PortType> portTypes = mspec.sig.getSendPortTypes();
			for(int i = 0; i < portNames.size(); i++){
				ret.add(new PortInstance(key, portNames.get(i).getName(), 
						portTypes.get(i).getType(), mspec));
			}
		}
		return ret;
	}
	
	public List<PortInstance> getSubscribeInstances(){
		ArrayList<PortInstance> ret = new ArrayList<PortInstance>();
		for( String key : configurationComponents.keySet()){
			Pair<ModuleSpec, ComponentLocation> instance = configurationComponents.get(key);
			ModuleSpec mspec = instance.fst;
			List<PortName> portNames = mspec.sig.getRecvPortNames();
			List<PortType> portTypes = mspec.sig.getRecvPortTypes();
			for(int i = 0; i < portNames.size(); i++){
				ret.add(new PortInstance(key, portNames.get(i).getName(), 
						portTypes.get(i).getType(), mspec));
			}
		}
		return ret;
	}
	
	public List<PortInstance> getPublishInstancesByType(final String type){
		ArrayList<PortInstance> ret = new ArrayList<PortInstance>();
		for( String key : configurationComponents.keySet()){
			Pair<ModuleSpec, ComponentLocation> instance = configurationComponents.get(key);
			ModuleSpec mspec = instance.fst;
			List<PortName> portNames = mspec.sig.getSendPortNames();
			List<PortType> portTypes = mspec.sig.getSendPortTypes();
			for(int i = 0; i < portNames.size(); i++){
				if(type.equals(portTypes.get(i).getType()))
					ret.add(new PortInstance(key, portNames.get(i).getName(), 
							portTypes.get(i).getType(), mspec));
			}
		}
		return ret;
	}
	
	public List<PortInstance> getSubscribeInstancesByType(final String type){
		ArrayList<PortInstance> ret = new ArrayList<PortInstance>();
		for( String key : configurationComponents.keySet()){
			Pair<ModuleSpec, ComponentLocation> instance = configurationComponents.get(key);
			ModuleSpec mspec = instance.fst;
			List<PortName> portNames = mspec.sig.getRecvPortNames();
			List<PortType> portTypes = mspec.sig.getRecvPortTypes();
			for(int i = 0; i < portNames.size(); i++){
				if(type.equals(portTypes.get(i).getType()))
					ret.add(new PortInstance(key, portNames.get(i).getName(), 
							portTypes.get(i).getType(), mspec));
			}
		}
		return ret;
	}
	
	public void removeComponentInstance(final String localName){
		final Set<String> chanNames = channels.keySet();
		final List<String> remChans = new ArrayList<String>();
		for(String chanName : chanNames){
			Channel chan = channels.get(chanName);
			if(chan.getPubComp().fst.equals(localName) || 
					chan.getSubComp().fst.equals(localName)){
				remChans.add(chanName);
			}
		}
		for(String chanName : remChans){
			channels.remove(chanName);
		}
		configurationComponents.remove(localName);
	}
}
