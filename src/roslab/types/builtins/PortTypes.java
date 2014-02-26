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
package roslab.types.builtins;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import roslab.artifacts.PortSpec;


public class PortTypes {
	
	public static final String INTEGER_TYPE_NAME = "Integer"; 
	public static final String FLOAT_TYPE_NAME = "Float";
	public static final String DOUBLE_TYPE_NAME = "Double"; 
	public static final String STRING_TYPE_NAME = "String";
	public static final String ODOM_TYPE_NAME = "Odometry";
	public static final String TWIST_TYPE_NAME = "Twist";
	public static final String TWISTSTAMPED_TYPE_NAME = "TwistStamped";
	public static final String ORIENTATION_TYPE_NAME = "Orientation";
	public static final String NAVIGATION_SATELLITE_FIX_TYPE_NAME = "NavSatFix";
	public static final String EST_TRPY_TYPE_NAME = "EST_TRPY";
	public static final String TRPY_TYPE_NAME = "TRPY";
	public static final String JOYSTICK_TYPE_NAME = "Joystick";
	public static final String UInt8_TYPE_NAME = "UInt8";

	static Set<String> builtins;
	static Map<String, PortSpec> builtPortTypesMap;
	
	static PortSpec integerType;
	static PortSpec floatType;
	static PortSpec doubleType;
	static PortSpec stringType;
	static PortSpec odomType;
	static PortSpec twistType;
	static PortSpec orientationType;
	static PortSpec navsatfixType;
	static PortSpec est_trpyType;
	static PortSpec trpyType;
	static PortSpec joystickType;
	static PortSpec twistStampedType;
	static PortSpec uint8Type;
		
	public static boolean isBuiltIn(String s){
		if(builtins == null){
			builtins = new HashSet<String>();
			builtins.add(INTEGER_TYPE_NAME);
			builtins.add(FLOAT_TYPE_NAME);
			builtins.add(DOUBLE_TYPE_NAME);
			builtins.add(STRING_TYPE_NAME);
			builtins.add(UInt8_TYPE_NAME);
		}
		return builtins.contains(s);
	}
	
	public static Map<String, PortSpec> getBuiltInPortMap(){
		if(builtPortTypesMap == null){
			builtPortTypesMap = new HashMap<String, PortSpec>();
			builtPortTypesMap.put(INTEGER_TYPE_NAME, getIntegerType());
			builtPortTypesMap.put(FLOAT_TYPE_NAME, getFloatType());
			builtPortTypesMap.put(DOUBLE_TYPE_NAME, getDoubleType());
			builtPortTypesMap.put(STRING_TYPE_NAME, getStringType());
			builtPortTypesMap.put(ODOM_TYPE_NAME, getOdomType());
			builtPortTypesMap.put(TWIST_TYPE_NAME, getTwistType());
			builtPortTypesMap.put(ORIENTATION_TYPE_NAME, getIMUType());
			builtPortTypesMap.put(NAVIGATION_SATELLITE_FIX_TYPE_NAME, getGPSType());
			builtPortTypesMap.put(EST_TRPY_TYPE_NAME, getESTTRPYType());
			builtPortTypesMap.put(TRPY_TYPE_NAME, getTRPYType());
			builtPortTypesMap.put(JOYSTICK_TYPE_NAME, getJoystickType());
			builtPortTypesMap.put(TWISTSTAMPED_TYPE_NAME, getTwistStampedType());
			builtPortTypesMap.put(UInt8_TYPE_NAME, getUInt8Type());

		}
		return builtPortTypesMap;
	}
	
	public static PortSpec getIntegerType(){
		if(integerType == null){
			integerType = new PortSpec(INTEGER_TYPE_NAME, null);
		}
		return integerType;
	}
	
	public static PortSpec getFloatType(){
		if(floatType == null){
			floatType = new PortSpec(FLOAT_TYPE_NAME, null);
		}
		return floatType;
	}
	
	public static PortSpec getDoubleType(){
		if(doubleType == null){
			doubleType = new PortSpec(DOUBLE_TYPE_NAME, null);
		}
		return doubleType;
	}
	
	public static PortSpec getStringType(){
		if(stringType == null){
			stringType = new PortSpec(STRING_TYPE_NAME, null);
		}
		return stringType;
	}
	
	public static PortSpec getUInt8Type(){
		if(uint8Type == null){
			uint8Type = new PortSpec(UInt8_TYPE_NAME, null);
			uint8Type.rosType = "std_msgs::UInt8";
			uint8Type.includeDeps.add("std_msgs/UInt8.h");

		}
		return uint8Type;
	}
	
	public static PortSpec getOdomType(){
		if(odomType == null){
			odomType = new PortSpec(ODOM_TYPE_NAME, null);
			odomType.rosType = "nav_msgs::Odometry";
			odomType.includeDeps.add("nav_msgs/Odometry.h");
		}
		return odomType;
	}
	 
	public static PortSpec getTwistType(){
		if(twistType == null){
			twistType = new PortSpec(TWIST_TYPE_NAME, null);
			twistType.rosType = "geometry_msgs::Twist";
			twistType.includeDeps.add("geometry_msgs/Twist.h");
		}
		return twistType;
	}

	public static PortSpec getTwistStampedType(){
		if(twistStampedType == null){
			twistStampedType = new PortSpec(TWISTSTAMPED_TYPE_NAME, null);
			twistStampedType.rosType = "geometry_msgs::TwistStamped";
			twistStampedType.includeDeps.add("geometry_msgs/TwistStamped.h");
		}
		return twistStampedType;
	}

	
	public static PortSpec getIMUType(){
		if(orientationType == null){
			orientationType = new PortSpec(ORIENTATION_TYPE_NAME, null);
			orientationType.rosType = "sensor_msgs::Imu";
			orientationType.includeDeps.add("sensor_msgs/Imu.h");
		}
		return orientationType;
	}

	
	public static PortSpec getGPSType(){
		if( navsatfixType == null){
			navsatfixType = new PortSpec(NAVIGATION_SATELLITE_FIX_TYPE_NAME, null);
			navsatfixType.rosType = "sensor_msgs::Gps";
			navsatfixType.includeDeps.add("sensor_msgs/Gps.h");
		}
		return navsatfixType;
	}
	
	public static PortSpec getESTTRPYType(){
		if( est_trpyType == null){
			est_trpyType = new PortSpec(EST_TRPY_TYPE_NAME, null);
			est_trpyType.rosType = "/est_trpy";
			est_trpyType.includeDeps.add("/est_trpy");
		}
		return est_trpyType;
	}
	
	public static PortSpec getTRPYType(){
		if( trpyType == null){
			trpyType = new PortSpec(TRPY_TYPE_NAME, null);
			trpyType.rosType = "quadrotor_msgs::TRPYCommand";
			trpyType.includeDeps.add("quadrotor_msgs/TRPYCommand.h");
		}
		return trpyType;
	}
	public static PortSpec getJoystickType(){
		if( joystickType == null){
			joystickType = new PortSpec(JOYSTICK_TYPE_NAME, null);
			joystickType.rosType = "sensor_msgs::Joy";
			joystickType.includeDeps.add("sensor_msgs/Joy.h");
		}
		return joystickType;
	}
}
