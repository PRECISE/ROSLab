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
package roslab.model;

import java.util.List;

public class Module {
	
	protected String name;
	protected String role;
	List<Port> inPorts;
	List<Port> outPorts;
	
	public Module(String name, String role, List<Port> inPorts, List<Port> outPorts) {
		this.name = name;
		this.role = role;
		this.inPorts = inPorts;
		this.outPorts = outPorts;
	}

	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public List<Port> getInPorts() {
		return inPorts;
	}

	public List<Port> getOutPorts() {
		return outPorts;
	}
	
	public void addOutPort(final Port p){
		if(p != null) outPorts.add(p);
	}
	
	public void addInPort(final Port p){
		if(p != null) inPorts.add(p);
	}
	
	public void removeOutPort(final Port p){
		if(p != null) outPorts.remove(p);
	}
	
	public void removeInPort(final Port p){
		if(p != null) inPorts.remove(p);
	}
}
