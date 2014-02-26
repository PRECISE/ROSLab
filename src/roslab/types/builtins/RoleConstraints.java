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
import java.util.Map;

import roslab.types.constraints.ComponentRoleConstraint;
import roslab.types.constraints.Constants.Arity;


public class RoleConstraints {
	
	private static Map<String, ComponentRoleConstraint> kindConstraints;
	
	static{
		kindConstraints = new HashMap<String, ComponentRoleConstraint>();
		/*kindConstraints.put(CompRoles.APPPANEL_NAME,
				new ComponentRoleConstraint(CompRoles.APPPANEL_NAME, Arity.NONE, Arity.MANY));
		kindConstraints.put(CompRoles.DEVICE_NAME, 
				new ComponentRoleConstraint(CompRoles.DEVICE_NAME, Arity.MANY, Arity.MANY));
		kindConstraints.put(CompRoles.LOGIC_NAME, 
				new ComponentRoleConstraint(CompRoles.LOGIC_NAME, Arity.MANY, Arity.MANY));*/
	}
	
	public static ComponentRoleConstraint getConstraint(String kindName){
		return kindConstraints.get(kindName);
	}

}
