package roslab.types.builtins;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import roslab.artifacts.PortSpec;

public class PortType {
	private String portType = null;
	public String rosType;
	public Set<String> includeDeps = new HashSet<String>();
	
	public PortType(String portType){
		this.portType = portType;
		Map<String, PortSpec> map =  PortTypes.getBuiltInPortMap();
		PortSpec spec = map.get(portType);
		if(spec != null){
			this.rosType = spec.rosType; //hack
			this.includeDeps.addAll(spec.includeDeps);
		}
	}
	
	public String getType(){
		return this.portType;
	}
	
	public void setType(String portType){
		this.portType = portType;
	}
	
	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof PortType))
			return false;
		final PortType type = (PortType) obj;
		return this.portType.equals(type.getType());
	}
	
	@Override
	public String toString(){
		return this.portType;
	}
}
