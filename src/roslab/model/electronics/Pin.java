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
    boolean required = false;
    int destinationSet = -1;
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
     * @param required
     * @param net
     */
    public Pin(String name, Circuit parent, Map<String, String> annotations, List<PinService> services, PinService assignedService, String portName,
            int pinIn, boolean required, String net) {
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
        this.required = required;
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

    /**
     * @return the required flag
     */
    public boolean getRequired() {
        return required;
    }

    /**
     * @param required
     *            the required flag to set
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean canConnect(Pin p) {
        // Check that input pin does not already have an assigned service that
        // does not match this pin's assigned service. If so, they cannot be
        // connected.
        if (this.assignedService == null || (p.assignedService != null && !p.assignedService.name.equals(this.assignedService.name))) {
            return false;
        }

        boolean result = true;

        // Check if any of the input pin's services match this pin's assigned
        // service. If so, a connection can be made.
        boolean serviceCheck = false;
        for (PinService ps : p.getServices()) {
            if (ps.name.equals(this.assignedService.name)) {
                boolean ioCheck = false;
                if (!this.assignedService.io.equals("#")
                        && ((this.assignedService.io.equals("I") && ps.io.equals("O")) || (this.assignedService.io.equals("O") && ps.io.equals("I")))) {
                    ioCheck = true;
                }
                if (this.assignedService.io.equals("#")) {
                    ioCheck = true;
                }
                serviceCheck = serviceCheck && ioCheck;

                // TODO Modify this to make sure pins in the same SuperService
                // will connect to the same SuperService on the destination
                // circuit
                boolean superServiceCheck = false;
                if (ps.superServiceName.equals(this.assignedService.superServiceName)) {
                    superServiceCheck = true;
                }
                serviceCheck = serviceCheck && superServiceCheck;
            }
        }
        result = result && serviceCheck;

        // If none of the input pin's services match this pin's assigned
        // service, no connection can be made.
        return result;
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
                if (ps.name.equals(this.assignedService.name)) {
                    p.assignedService = ps;
                    return new Wire(this.parent.getName() + "." + ps.name + "--" + p.parent.getName() + "." + p.assignedService.name, this, p);
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
        // GPIO,#,+,IO/PWM,1,+,IO,TIMER,15,1/MISO,#,+,IO,SPI,2,5/PWM_N,2,+,O,TIMER,1,6.B,14@1
        String[] pinArray = { pin };
        int destSet = -1;

        // Split on 'at' character to get the destination set value.
        if (pinArray[0].contains("@")) {
            pinArray = pin.split("\\@");
            destSet = Integer.parseInt(pinArray[1]);
        }

        // Split on period character if the string contains them.
        if (pinArray[0].contains(".")) {
            pinArray = pinArray[0].split("\\.");
        }

        // The input pin string should never have more than two periods.
        if (pinArray.length > 2) {
            throw new IllegalArgumentException("Bad input pin string - too many periods");
        }

        Pin result = null;

        if (pinArray.length > 1) {
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

        result.destinationSet = destSet;

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
        return new Pin(name, parent, this.getAnnotationsCopy(), this.getServicesCopy(), assignedService, portName, pinIn, required, net);
    }

    private List<PinService> getServicesCopy() {
        List<PinService> copy = new ArrayList<PinService>();
        for (PinService ps : services) {
            copy.add(ps.clone());
        }
        return copy;
    }

    public PinService getServiceByName(String name) {
        for (PinService p : services) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }
}
