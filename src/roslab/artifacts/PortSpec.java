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

import java.util.HashSet;
import java.util.Set;

public class PortSpec {
	
	public static final String ExceptionOutPortName = "exception";
	public static final String ExceptionInPortName = "handler";
	public String rosType = "";
	public Set<String> includeDeps = new HashSet<String>();
	
	public String typeName;
	public PortSpec inheritsFrom;
	
	public PortSpec(String typeName, PortSpec inheritsFrom) {
		super();
		this.typeName = typeName;
		this.inheritsFrom = inheritsFrom;
	}
	
	@Override
	public String toString(){
		return this.typeName;
	}
	
	
	public PortSpec copy(){
		PortSpec spec = new PortSpec(typeName, null);
		spec.inheritsFrom = this.inheritsFrom;
		return spec;
	}
	

}
