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
package roslab.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import roslab.artifacts.UserSettings;
import roslab.artifacts.WorkspaceSpec;
import roslab.gui.misc.FileConstants;


//import mdcf.core.app.AppSpec;
//import mdcf.core.ctypes.ComponentSignature;



import com.thoughtworks.xstream.XStream;

public class FileIO {
	
	public static final XStream xstream = new XStream();
	
	public static String loadText(File file) throws IOException{
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = reader.readLine()) != null){
			buffer.append(line).append(System.getProperty("line.separator"));
		}
		reader.close();
		return buffer.toString();
	}
	
	public static String loadText(String fName) throws IOException{
		File file = new File(fName);
		return loadText(file);
	}
	
//	public static ComponentSignature loadSignatureFromFile(File f) {
//		ComponentSignature ret = null;
//		try {
//			ComponentSignature comp = null;
//			String fileText = FileIO.loadText(f);
//			comp = (ComponentSignature)xstream.fromXML(fileText);
//			
//			ArrayList<PortName> sendPortNames = new ArrayList<PortName>();
//			ArrayList<PortName> recvPortNames = new ArrayList<PortName>();
//			ArrayList<PortType> sendPortTypes = new ArrayList<PortType>();
//			ArrayList<PortType> recvPortTypes = new ArrayList<PortType>();
//			ArrayList<TaskDescriptor> taskDescriptors = new ArrayList<TaskDescriptor>();
//			
//			for(int index = 0; index < comp.getSendPortNames().size(); index++){
//				String portName = comp.getSendPortNames().get(index);
//				sendPortNames.add(new PortName(portName));
//				String portType = comp.getSendPortTypes().get(index);
//				sendPortTypes.add(new PortType(portType));
//			}
//			
//			for(int index = 0; index < comp.getRecvPortNames().size(); index++){
//				String portName = comp.getRecvPortNames().get(index);
//				recvPortNames.add(new PortName(portName));
//				String portType = comp.getRecvPortTypes().get(index);
//				recvPortTypes.add(new PortType(portType));
//			}
//			
//			ret = new ComponentSignature(comp.getType(),
//					sendPortNames, recvPortNames, sendPortTypes, recvPortTypes, taskDescriptors);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		catch (ClassCastException e){
//			e.printStackTrace();
//		}
//		return ret;
//	}
	
//	public static AppSpec loadAppConfigurationFromFile(File f) {
//		AppSpec ret = null;
//		try {
//			String fileText = FileIO.loadText(f);
//			ret = (AppSpec) xstream.fromXML(fileText);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassCastException e) {
//			e.printStackTrace();
//		}
//		return ret;
//	}

	public static WorkspaceSpec loadWorkspace(String fName) throws IOException{
		return loadWorkspace(new File(fName));
	}
	
	public static WorkspaceSpec loadWorkspace(File file) throws IOException{
		String objectTxt = loadText(file);
		Object obj = xstream.fromXML(objectTxt);
		if(obj instanceof WorkspaceSpec){
			return (WorkspaceSpec)obj;
		}
		return null;
	}
	
	
	public static void saveText(File file, String text) throws IOException{
		FileWriter writer = new FileWriter(file);
		writer.write(text);
		writer.flush();
		writer.close();
	}
	
	public static void saveText(String fName, String text) throws IOException{
		File file = new File(fName);
		saveText(file, text);
	}
	
	public static void saveObject(File file, Object data) throws IOException{
		FileWriter writer = new FileWriter(file);
		xstream.toXML(data, writer);
	}
	
	public static void saveObject(String fName, Object data) throws IOException{
		File file = new File(fName);
		saveObject(file, data);
	}
	
	public static void saveUserSettings() {
		UserSettings settings = new UserSettings();
		settings.populateFromContext();
		try {
			saveObject(FileConstants.USERSETTINGFILE, settings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadUserSettings() {
		UserSettings settings = null;
		File file = new File(FileConstants.USERSETTINGFILE);
		if(!file.exists()){
			return;
		}
		try {
			String fileText = FileIO.loadText(FileConstants.USERSETTINGFILE);
			settings = (UserSettings)xstream.fromXML(fileText);
			settings.loadbackToContext();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassCastException e){
			e.printStackTrace();
		}
	}
	
	
}
