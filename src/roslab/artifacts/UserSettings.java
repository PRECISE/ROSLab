/**
 * Copyright 2009-2011, Kansas State University, 
 * PRECISE Center at the University of Pennsylvania,
 * Yu Jin Kim, and Andrew King
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class UserSettings {
	public String currentWorkspaceFileName;
	public Map<String, File> configToAppArchiveLocations;
	public ArrayList<File> buildClasspath;
	public File appDevPath;
	public String appArchiveStoragePath;
	
	public void populateFromContext(){
		this.currentWorkspaceFileName = WorkspaceContext.currentWorkspaceFileName;
		this.configToAppArchiveLocations = WorkspaceContext.configToAppArchiveLocations;
		this.buildClasspath = WorkspaceContext.buildClasspath;
		this.appDevPath = WorkspaceContext.appDevPath;
		this.appArchiveStoragePath = WorkspaceContext.appArchiveStoragePath;
	}
	
	public void loadbackToContext(){
		//Checks the validity of the data and put it back to the context
		if(this.currentWorkspaceFileName != null){
			File file = new File(this.currentWorkspaceFileName);
			if(file.exists()){
				WorkspaceContext.currentWorkspaceFileName = this.currentWorkspaceFileName;
			}
		}
		
		Set<String> configs = this.configToAppArchiveLocations.keySet();
		for(String appConfigName : configs){
			File file = this.configToAppArchiveLocations.get(appConfigName);
			if(!file.exists()){
				this.configToAppArchiveLocations.remove(appConfigName);
			}
		}
		WorkspaceContext.configToAppArchiveLocations = this.configToAppArchiveLocations;
		
		ArrayList<File> newBuildClassPath = new ArrayList<File>();
		for(Iterator<File> files = this.buildClasspath.iterator(); files.hasNext(); ){
			File thisfile = files.next();
			if(thisfile.exists()){
				newBuildClassPath.add(thisfile);
			}
		}
		WorkspaceContext.buildClasspath = newBuildClassPath;
		
		if((this.appDevPath != null) && (this.appDevPath.exists())){
			WorkspaceContext.appDevPath = this.appDevPath;
		}
		
		if(this.appArchiveStoragePath != null){
			File file = new File(this.appArchiveStoragePath);
			if(file.exists()){
				WorkspaceContext.appArchiveStoragePath = this.appArchiveStoragePath;
			}
		}
	}
}
