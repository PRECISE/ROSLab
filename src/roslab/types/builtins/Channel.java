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

import roslab.artifacts.ModuleSpec;
import roslab.utils.Pair;

public class Channel {
	
	/**
	 * This is the name of the channel, as managed by the JMS
	 */
	String chanName;
	
	/**
	 * This is the index of the publisher name in the ComponentSignature of the owner of this port
	 */
	PortName pubName;
	
	/**
	 * This is the index of the subscriber name in the ComponentSignature of the owner of this port
	 */
	PortName subName;
	
	/**
	 * This is the component that publishes information on this channel
	 */
	Pair<String, ModuleSpec> pubComp;
	
	/**
	 * This is the component which subscribes to this channel
	 */
	Pair<String, ModuleSpec> subComp;
	
	public Channel(){
	}

	public String getChanName() {
		return chanName;
	}

	public void setChanName(String chanName) {
		this.chanName = chanName;
	}

	public String getPubName() {
		return this.pubName.getName();
	}

	public void setPubName(PortName pubName) {
		this.pubName = pubName;
	}

	public String getSubName() {
		return this.subName.getName();
	}

	public void setSubName(PortName subName) {
		this.subName = subName;
	}

	public Pair<String, ModuleSpec> getPubComp() {
		return pubComp;
	}

	public void setPubComp(Pair<String, ModuleSpec> pubComp) {
		this.pubComp = pubComp;
	}

	public Pair<String, ModuleSpec> getSubComp() {
		return subComp;
	}

	public void setSubComp(Pair<String, ModuleSpec> subComp) {
		this.subComp = subComp;
	}
	
	@Override
	public String toString(){
		String ret = "";
		ret += pubComp.fst + "." + pubName + "->" 
			+ subComp.fst + "." + subName;
		return ret;
	}
	
	public String getPubIdent(){
		return pubComp.fst + "." + pubName;
	}
	
	public String getSubIdent(){
		return subComp.fst + "." + subName;
	}
}
