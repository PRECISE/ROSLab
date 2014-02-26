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

public class Pair<T1, T2> {
	
	public T1 fst;
	public T2 snd;
	
	public Pair(T1 fst, T2 snd) {
		super();
		this.fst = fst;
		this.snd = snd;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Pair){
			@SuppressWarnings("unchecked")
			Pair<String, String> p = (Pair<String, String>) o;
			return (this.fst.equals(p.fst) && this.snd.equals(p.snd));
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return fst.toString().hashCode() + snd.toString().hashCode();
	}
}
