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
import roslab.utils.Pair;

public class WorkspaceSpec { //*.wksp.xml
	
	public String workspaceName;
	public Map<String, ModuleSpec> moduleTypes; //<compTypeName:Str, ModuleSpec>
	public Map<String, ConfigurationSpec> configurations; //<appConfigName:Str, ConfigurationSpec>
	
	public WorkspaceSpec(String name){
		this.workspaceName = name;
		this.moduleTypes = generateBuiltIns();//new HashMap<String, ModuleSpec>();
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
	
	private static HashMap<String, ModuleSpec> generateBuiltIns(){
		HashMap<String, ModuleSpec> ret = new HashMap<String, ModuleSpec>();
		
		
		String velType = "Velocity";
		LinkedList<PortName> vel_recvPortNames = new LinkedList<PortName>();
		vel_recvPortNames.add(new PortName("/cmd_vel"));
		LinkedList<PortType> vel_recvPortTypes = new LinkedList<PortType>();
		PortType velTypeT = new PortType(PortTypes.TWIST_TYPE_NAME);
		velTypeT.rosType = "geometry_msgs::Twist";
		vel_recvPortTypes.add(velTypeT);
	    ComponentSignature velCompSig = new ComponentSignature(velType, new LinkedList<PortName>(), vel_recvPortNames,
	    		new LinkedList<PortType>(), vel_recvPortTypes, new LinkedList<TaskDescriptor>());
		ModuleSpec velocityCmd = new ModuleSpec(velType, "ROS Service", velCompSig);
		ret.put(velType, velocityCmd);
		
		
		String odomType = "Odometry";
		LinkedList<PortName> od_sendPortNames = new LinkedList<PortName>();
		LinkedList<PortType> od_sendPortTypes = new LinkedList<PortType>();
		od_sendPortNames.add(new PortName("/odom"));
		PortType odomTypeT = new PortType(PortTypes.ODOM_TYPE_NAME);
		odomTypeT.rosType = "nav_msgs::Odometry";
		od_sendPortTypes.add(odomTypeT);
		ComponentSignature odomCompSig = new ComponentSignature(odomType, od_sendPortNames, new LinkedList<PortName>(),
				 od_sendPortTypes, new LinkedList<PortType>(), new LinkedList<TaskDescriptor>());
		ModuleSpec odom = new ModuleSpec(odomType, "ROS Service", odomCompSig);
		ret.put(odomType, odom);
		
		
		String imuType = "IMU";
		LinkedList<PortName> imu_sendPortNames = new LinkedList<PortName>();
		LinkedList<PortType> imu_sendPortTypes = new LinkedList<PortType>();
		imu_sendPortNames.add(new PortName("/imu_data"));
		PortType imuTypeT = new PortType(PortTypes.ORIENTATION_TYPE_NAME);
		imuTypeT.rosType = "sensor_msgs::Imu";
		imu_sendPortTypes.add(imuTypeT);
		ComponentSignature imuCompSig = new ComponentSignature(imuType, imu_sendPortNames, new LinkedList<PortName>(),
				 imu_sendPortTypes, new LinkedList<PortType>(), new LinkedList<TaskDescriptor>());
		ModuleSpec imu = new ModuleSpec(imuType, "ROS Service", imuCompSig);
		ret.put(imuType, imu);

		
		String gpsType = "GPS";
		LinkedList<PortName> gps_sendPortNames = new LinkedList<PortName>();
		LinkedList<PortType> gps_sendPortTypes = new LinkedList<PortType>();
		gps_sendPortNames.add(new PortName("/gps"));
		PortType gpsTypeT = new PortType(PortTypes.NAVIGATION_SATELLITE_FIX_TYPE_NAME);
		gpsTypeT.rosType = "sensor_msgs::Gps";
		gps_sendPortTypes.add(gpsTypeT);
		ComponentSignature gpsCompSig = new ComponentSignature(gpsType, gps_sendPortNames, new LinkedList<PortName>(),
				 gps_sendPortTypes, new LinkedList<PortType>(), new LinkedList<TaskDescriptor>());
		ModuleSpec gps = new ModuleSpec(gpsType, "ROS Service", gpsCompSig);
		ret.put(gpsType, gps);
		
		
		String quadType = "Quadrotor";
		LinkedList<PortName> quad_recvPortNames = new LinkedList<PortName>();
		quad_recvPortNames.add(new PortName("/trpy_cmd"));
		LinkedList<PortType> quad_recvPortTypes = new LinkedList<PortType>();
		PortType quadTypeTrec = new PortType(PortTypes.TRPY_TYPE_NAME);
		quadTypeTrec.rosType = "quadrotor_msgs::TRPYCommand";
		quad_recvPortTypes.add(quadTypeTrec);
		LinkedList<PortName> quad_sendPortNames = new LinkedList<PortName>();
		LinkedList<PortType> quad_sendPortTypes = new LinkedList<PortType>();
		quad_sendPortNames.add(new PortName("/est_trpy"));
		PortType quadTypeTsend = new PortType(PortTypes.EST_TRPY_TYPE_NAME);
		quadTypeTsend.rosType = "quadrotor_msgs::TRPYCommand";
		quad_sendPortTypes.add(quadTypeTsend);
		ComponentSignature quadCompSig = new ComponentSignature(quadType, quad_sendPortNames, quad_recvPortNames,
				 quad_sendPortTypes, quad_recvPortTypes, new LinkedList<TaskDescriptor>());
		ModuleSpec quad = new ModuleSpec(quadType, "ROS Service", quadCompSig);
		ret.put(quadType, quad);
	
		
		String joystickType = "Joystick";
		LinkedList<PortName> joystick_sendPortNames = new LinkedList<PortName>();
		LinkedList<PortType> joystick_sendPortTypes = new LinkedList<PortType>();
		joystick_sendPortNames.add(new PortName("/joy"));
		PortType joystickTypeTsend = new PortType(PortTypes.JOYSTICK_TYPE_NAME);
		joystickTypeTsend.rosType = "sensor_msgs::Joy";
		joystick_sendPortTypes.add(joystickTypeTsend);
		ComponentSignature joystickCompSig = new ComponentSignature(joystickType, joystick_sendPortNames, new LinkedList<PortName>(),
				 joystick_sendPortTypes, new LinkedList<PortType>(), new LinkedList<TaskDescriptor>());
		ModuleSpec joystick = new ModuleSpec(joystickType, "ROS Service", joystickCompSig);
		ret.put(joystickType, joystick);


        String base_velType = "Landshark";
        LinkedList<PortName> base_vel_recvPortNames = new LinkedList<PortName>();
        base_vel_recvPortNames.add(new PortName("/landshark_control/base_velocity"));
        LinkedList<PortType> base_vel_recvPortTypes = new LinkedList<PortType>();
        PortType base_velTypeT = new PortType(PortTypes.TWISTSTAMPED_TYPE_NAME);
        base_velTypeT.rosType = "geometry_msgs::TwistStamped";
        base_vel_recvPortTypes.add(base_velTypeT);
        ComponentSignature base_velCompSig = new ComponentSignature(base_velType, new LinkedList<PortName>(), base_vel_recvPortNames,
                new LinkedList<PortType>(), base_vel_recvPortTypes, new LinkedList<TaskDescriptor>());
        ModuleSpec base_velocityCmd = new ModuleSpec(base_velType, "ROS Service", base_velCompSig);
        ret.put(base_velType, base_velocityCmd);


        String insectType = "Insect";	
        LinkedList<PortName> insect_recvPortNames = new LinkedList<PortName>();
        insect_recvPortNames.add(new PortName("/insect_cmd"));
        LinkedList<PortType> insect_recvPortTypes = new LinkedList<PortType>();
        PortType insectTypeT = new PortType(PortTypes.UInt8_TYPE_NAME);
        insectTypeT.rosType = "std_msgs::UInt8";
        insect_recvPortTypes.add(insectTypeT);
        ComponentSignature insectCompSig = new ComponentSignature(insectType, new LinkedList<PortName>(), insect_recvPortNames,
                new LinkedList<PortType>(), insect_recvPortTypes, new LinkedList<TaskDescriptor>());
        ModuleSpec insectCmd = new ModuleSpec(insectType, "ROS Service", insectCompSig);
        ret.put(insectType, insectCmd);


        String guiType = "GUI";
        LinkedList<PortName> gui_recvPortNames = new LinkedList<PortName>();
        gui_recvPortNames.add(new PortName("joy"));
        gui_recvPortNames.add(new PortName("cmd_vel"));
        LinkedList<PortType> gui_recvPortTypes = new LinkedList<PortType>();
        PortType guiTypeTrec1 = new PortType(PortTypes.JOYSTICK_TYPE_NAME);
        guiTypeTrec1.rosType = "Joy";
        gui_recvPortTypes.add(guiTypeTrec1);
        PortType guiTypeTrec2 = new PortType(PortTypes.TWIST_TYPE_NAME);
        guiTypeTrec2.rosType = "Twist";
        gui_recvPortTypes.add(guiTypeTrec2);
        ComponentSignature guiCompSig = new ComponentSignature(guiType, new LinkedList<PortName>(), gui_recvPortNames,
                new LinkedList<PortType>(), gui_recvPortTypes, new LinkedList<TaskDescriptor>());
        ModuleSpec gui = new ModuleSpec(guiType, "ROS Service", guiCompSig);
        ret.put(guiType, gui);


        return ret;
	}

}
