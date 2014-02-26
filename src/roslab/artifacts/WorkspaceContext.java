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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class WorkspaceContext {
	
	public static String currentWorkspaceFileName; //the absolute path where the workspace is saved
	public static WorkspaceSpec currentWorkspace;
	//public static Map<String, ConfigurationEditorPanel> currentWorkspaceVisuals; //<appConfiguration:Str, ConfigurationEditorPanel>
	public static Map<String, File> configToAppArchiveLocations; //<appConfiguration:Str, locationOfAppConfiguration>
	public static ArrayList<File> buildClasspath; //compile paths
	public static File appDevPath; //application development project path
	public static String appArchiveStoragePath; // the relative path of the app archive storage path(mdcf2-apps) from currently set app development path 
	
	static {
		currentWorkspace = new WorkspaceSpec("Untitled");
		
		//currentWorkspaceVisuals = new HashMap<String, ConfigurationEditorPanel>();
		configToAppArchiveLocations = new HashMap<String, File>();
		buildClasspath = new ArrayList<File>();
	}

}
