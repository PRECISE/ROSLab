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
package roslab.types.constraints;

import roslab.artifacts.ModuleSpec;
public class ConstructionConstraints {
	
	public static boolean canAddNewPublishPort(ModuleSpec spec){
		/*ComponentRoleConstraint cks = MdcfRoleConstraints.getConstraint(spec.role);
		if(cks.pubArity == Arity.MANY){
			return true;
		}
		else if(cks.pubArity == Arity.ONE && 
				spec.sig.getSendPortNames().size() == 0){
			return true;
		}
		else{
			return false;
		}*/
		return true;
	}
	
	public static boolean canAddNewSubscribePort(ModuleSpec spec){
//		ComponentRoleConstraint cks = MdcfRoleConstraints.getConstraint(spec.role);
//		if(cks.subArity == Arity.MANY){
//			return true;
//		}
//		else if(cks.subArity == Arity.ONE && 
//				spec.sig.getRecvPortNames().size() == 0){
//			return true;
//		}
//		else{
//			return false;
//		}
		return true;
	}

}
