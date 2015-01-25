/**
 *
 */
package roslab.model.electronics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import roslab.model.general.Feature;

/**
 * @author Peter Gebhard
 */
public class Pin extends Feature {

    List<PinService> services = new ArrayList<PinService>();
    PinService assignedService = null;
    String portName = "";
    int pinIn = 0;
    String net = null;

    /**
     * @param name
     * @param parent
     * @param annotations
     * @param services
     */
    public Pin(String name, Circuit parent, Map<String, String> annotations, List<PinService> services) {
        super(name, parent, annotations);
        if (services != null) {
            this.services = services;
        }
    }

    /**
     * @param name
     * @param parent
     * @param annotations
     * @param services
     */
    public Pin(String name, Circuit parent) {
        super(name, parent);
    }

    /**
     * @param name
     * @param parent
     * @param annotations
     * @param services
     * @param portName
     * @param pinIn
     */
    public Pin(String name, Circuit parent, Map<String, String> annotations, List<PinService> services, PinService assignedService, String portName,
            int pinIn, String net) {
        super(name, parent, annotations);
        if (services != null) {
            this.services = services;
        }
        if (assignedService != null) {
            this.assignedService = assignedService;
        }
        if (portName == null) {
            throw new IllegalArgumentException("Bad port name input.");
        }
        else {
            this.portName = portName;
        }
        this.pinIn = pinIn;
        this.net = net;
    }

    /**
     * @return the services
     */
    public List<PinService> getServices() {
        return services;
    }

    /**
     * @param services
     *            the services to set
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
     * @param assignedService
     *            the assignedService to set
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
     * @param portName
     *            the portName to set
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
     * @param pinIn
     *            the pinIn to set
     */
    public void setPinIn(int pinIn) {
        this.pinIn = pinIn;
    }

    /**
     * @return the net
     */
    public String getNet() {
        return net;
    }

    /**
     * @param net
     *            the net to set
     */
    public void setNet(String net) {
        this.net = net;
    }

    public boolean canConnect(Pin p) {
        // Check that input pin does not already have an assigned service that
        // does not match this pin's assigned service. If so, they cannot be
        // connected.
        if (p.assignedService != null && !p.assignedService.name.equals(this.assignedService.name)) {
            return false;
        }

        // Check if any of the input pin's services match this pin's assigned
        // service. If so, a connection can be made.
        for (PinService ps : p.getServices()) {
            if (ps.name.equals(this.assignedService.name)) {
                return true;
            }
        }

        // If none of the input pin's services match this pin's assigned
        // service, no connection can be made.
        return false;
    }

    /**
     * This method attempts to connect one pin to another by finding a matching
     * service (based on the service name). If the connection is possible, a
     * Wire object representing the connection is returned.
     *
     * @param p
     *            the Pin to connect to this Pin instance
     * @return the Wire object representing the connected pins; null if the
     *         connection cannot be made
     */
    public Wire connect(Pin p) {
        if (canConnect(p)) {
            for (PinService ps : p.getServices()) {
                if (ps.name == this.assignedService.name) {
                    p.assignedService = ps;
                    return new Wire(this.name + "--" + p.name, this, p);
                }
            }
        }
        return null;
    }

    /**
     * @param pin
     *            the pin string to be parsed
     * @param parent
     *            the Pin's parent circuit
     * @return the Pin object constructed from the parsed pin string
     */
    public static Pin getPinFromString(String pin, Circuit parent) {
        // Example:
        // GPIO,#,+,IO/PWM,1,+,IO,TIMER,15,1/MISO,#,+,IO,SPI,2,5/PWM_N,2,+,O,TIMER,1,6.B,14
        String[] pinArray = { pin };

        // Split on period character if the string contains them.
        if (pin.contains(".")) {
            pinArray = pin.split("\\.");
        }

        // The input pin string should never have more than one period.
        if (pinArray.length > 2) {
            throw new IllegalArgumentException("Bad input pin string - too many periods");
        }

        Pin result = null;

        if (pinArray.length == 2) {
            if (pinArray[1].contains(",")) {
                String[] portArray = pinArray[1].split("\\,");
                result = new Pin(portArray[0] + portArray[1], parent);
                result.setPortName(portArray[0]);
                result.setPinIn(Integer.parseInt(portArray[1]));
            }
            else {
                result = new Pin(pinArray[1], parent);
                result.setPortName(pinArray[1]);
            }
        }
        else {
            result = new Pin(pinArray[0], parent);
        }

        for (String serviceStr : pinArray[0].split("/")) {
            StringTokenizer st = new StringTokenizer(serviceStr, ",");

            String service = null;
            int serviceNum = -1;
            char oneToMany = '#';
            String io = null;
            String superService = null;
            int superServiceNum = -1;
            int af = -1;

            if (st.hasMoreTokens()) {
                service = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                String temp = st.nextToken();
                if (!temp.equals("#")) {
                    serviceNum = Integer.valueOf(temp);
                }
            }
            if (st.hasMoreTokens()) {
                oneToMany = st.nextToken().charAt(0);
            }
            if (st.hasMoreTokens()) {
                io = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                superService = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                String temp = st.nextToken();
                if (!temp.equals("#")) {
                    superServiceNum = Integer.valueOf(temp);
                }
            }
            if (st.hasMoreTokens()) {
                String temp = st.nextToken();
                if (!temp.equals("#")) {
                    af = Integer.valueOf(temp);
                }
            }

            result.getServices().add(new PinService(service, serviceNum, oneToMany, io, superService, superServiceNum, af));
        }

        // If the pin only has one service, make that the assigned service.
        if (result.getServices().size() == 1) {
            result.assignedService = result.getServices().get(0);
        }

        return result;
    }

    public Pin clone(String name, Circuit parent) {
        return new Pin(name, parent, this.getAnnotationsCopy(), this.getServicesCopy(), assignedService, portName, pinIn, net);
    }

    private List<PinService> getServicesCopy() {
        List<PinService> copy = new ArrayList<PinService>();
        for (PinService ps : services) {
            copy.add(ps.clone());
        }
        return copy;
    }

}
