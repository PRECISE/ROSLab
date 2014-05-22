/**
 * 
 */
package roslab.model.electronics;

import java.util.List;
import java.util.Map;

import roslab.model.general.Feature;

/**
 * @author Peter Gebhard
 *
 */
public class Pin extends Feature {
	
	List<PinService> services;
	PinService assignedService;
	String portName;
	int pinIn;

	/**
	 * @param name
	 * @param parent
	 * @param annotations
	 */
	public Pin(String name, Circuit parent, Map<String, String> annotations, List<PinService> services) {
		super(name, parent, annotations);
		this.services = services;
	}
	
	/**
	 * @param name
	 * @param parent
	 * @param annotations
	 * @param portName
	 * @param pinIn
	 */
	public Pin(String name, Circuit parent, Map<String, String> annotations, List<PinService> services, String portName, int pinIn) {
		super(name, parent, annotations);
		this.services = services;
		this.portName = portName;
		this.pinIn = pinIn;
	}

	/**
	 * @return the services
	 */
	public List<PinService> getServices() {
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(List<PinService> services) {
		this.services = services;
	}

	/**
	 * @return the assignedService
	 */
	public PinService getAssignedService() {
		return assignedService;
	}

	/**
	 * @param assignedService the assignedService to set
	 * @throws IllegalArgumentException 
	 */
	public void setAssignedService(PinService assignedService) throws IllegalArgumentException {
		if (!this.services.contains(assignedService)) {
			throw new IllegalArgumentException("Cannot set a pin's assigned service to one which is not available on the pin.");
		}
		this.assignedService = assignedService;
	}

	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @param portName the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/**
	 * @return the pinIn
	 */
	public int getPinIn() {
		return pinIn;
	}

	/**
	 * @param pinIn the pinIn to set
	 */
	public void setPinIn(int pinIn) {
		this.pinIn = pinIn;
	}

}
