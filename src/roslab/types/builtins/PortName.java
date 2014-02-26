package roslab.types.builtins;

public class PortName {
	private String portName = null;
	
	public PortName(String portName){
		this.portName = portName;
	}
	
	public String getName(){
		return this.portName;
	}
	
	public void setName(String portName){
		this.portName = portName;
	}
	
	@Override
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof PortName))
			return false;
		final PortName port = (PortName) obj;
		return this.portName.equals(port.getName());
	}
	
	@Override
	public String toString(){
		return this.portName;
	}
}
