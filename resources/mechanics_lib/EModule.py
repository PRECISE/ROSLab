
from api.EComponent import EComponent
from EDevice import EDevice
import itertools

class EModule(EComponent):
    """
    A class for representing an electronic module (a micronctroller with attached devices).
    A module includes an ATtiny chip and some L{EDevice}s.
    A module has 3 available pins.
    It stores the type of each ATtiny pin needed to support the given EDevices.
    An EModule also points to the EModule which comes after it in the chain.

    @cvar PIN_TYPES: The types that an ATtiny pin may be set to (N_C is not connected).
    @cvar pwmPossiblePins: Indicates the pins of the module which support PWM outputs (note SERVO type uses a PWM output).
    @cvar analogInPossiblePins: Indicates the pins of the module which support analog inputs.

    @ivar eDevices: A list of EDevices attached to the module.
    @ivar eDevicesPins: A list where the ith element is a list of pins used by the ith attached EDevice (indexed into L{eDevices})
    @ivar pinTypes: A list where the ith element indicates how to configure the ith pin of the module (see L{PIN_TYPES}).
    """

    # Define allowable pin types
    PIN_TYPES = ["D_I", "D_O", "A_I", "A_O", "N_C", "SERVO"]
    # Define allowable pins for PWM outpus and Analog Inputs
    pwmPossiblePins = [True, True, True]
    analogInPossiblePins = [False, False, True]
    
    def __init__(self, name = None):
        """
        Initialize the EModule.
        
        @param name: The name of the EModule (will be a default if none is given).
            Must be at least one character long.
        @type name: String
        """
        EComponent.__init__(self, name)
        self.clearEDevices()
        self.clearNextEModule()
        self.clearPinTypes()

    def clearEDevices(self):
        """
        Deletes all devices from the module.
        """
        self.eDevices = []
        self.eDevicesPins = []
        self.pinTypes = ["N_C", "N_C", "N_C"]

    def clearPinTypes(self):
        """
        Same as L{clearEDevices}.
        """
        self.clearEDevices()

    def clearNextEModule(self):
        """
        Deletes the next module (this module becomes the last in its chain).
        """
        self.nextEModule = None
        
    def setNextEModule(self, inNextEModule):
        """
        Sets the next EModule in the chain (the one downstream of this one).
        Overrides current next module if one exists.

        @param inNextEModule: The next module to attach.
        @type inNextEModule: EModule

        @return: False if inNextEModule is not of type EModule, True otherwise.
        @rtype: boolean
        """
        if not isinstance(inNextEModule, EModule):
            return False
        self.nextEModule = inNextEModule
        return True

    def getNextEModule(self):
        """
        Gets the next EModule in the chain (the one downstream of this one).

        @return: The downstream EModule, or None if there is none attached.
        @rtype: EModule or None
        """
        return self.nextEModule

    def appendToChain(self, inEModule):
        """
        Appends inEModule to the end of the chain to which this EModule belongs.

        @param inEModule: The module to append.
        @type inEModule: EModule

        @return: False if inEModule is not a valid EModule, True otherwise.
        @rtype: boolean
        """
        if not isinstance(inEModule, EModule):
            return False

        lastModule = self
        while lastModule.getNextEModule() is not None:
            lastModule = lastModule.getNextEModule()

        lastModule.setNextEModule(inEModule)
        return True

    def setEDevicesInOrder(self, eDevices):
        """
        Sets the attached L{EDevice}s to be the set given (in order).
        If eDevices is not a list or tuple or if any element of eDevices is
        not an L{EDevice}, this module will not be altered at all.
        
        @param eDevices: The EDevices to add to the module.
        @type eDevices: List or Tuple of L{EDevice}s

        @return: False if any items in eDevices is not an EDevice
            or if it is impossible to add the given EDevices in order, True otherwise.
            May be impossible if too many pins are required
            May be impossible if too many PWM/Analog pins are required (see L{pwmPossiblePins} and L{analogInPossiblePins})
            May be impossible due to the ordering of devices
        @rtype: boolean
        """
        # Make sure eDevices is valid
        if not isinstance(eDevices, (list, tuple)):
            return False
        for nextDev in eDevices:
            if not isinstance(nextDev, EDevice):
                return False
        #  See if it is possible to add them
        temp = EModule()
        for nextDev in eDevices:
            if not temp.addEDevice(nextDev):
                return False
            
        # Seems valid... make the assignments
        self.clearEDevices()
        for nextDev in eDevices:
            self.addEDevice(nextDev)
        return True        
    
    def addEDevice(self, eDevice):
        """
        Adds the given L{EDevice} to the module using currently available pins on the module.
        If not enough pins are open or open pins do not support the needed types, does nothing.
        Will try different orderings of the EDevice's pins and spread them out if necessary.
        Does not try to rearrange existing EDevices to accomodate the new one.
        If rearranging is desired, use L{setEDevices}.

        @param eDevice: The EDevice to add.
        @type eDevice: L{EDevice}

        @return: False if it is impossible to add or if eDevice is not an L{EDevice}, True otherwise.
            It may be impossible if not enough pins are open.
            It may be impossible if not enough PWM or analog pins are open (see L{pwmPossiblePins} and L{analogInPossiblePins})
        @rtype: boolean
        """
        if not isinstance(eDevice, EDevice):
            return False
        neededPinTypes = eDevice.getPinTypes()
        # If not enough pins are open, return false
        if self.pinTypes.count("N_C") < len(neededPinTypes):
            return False
        # If not enough A_I pins are open, return false
        if EModule.analogInPossiblePins.count(True) - self.pinTypes.count("A_I") < neededPinTypes.count("A_I"):
            return False
        # If not enough A_O pins are open, return false
        if EModule.pwmPossiblePins.count(True) - self.pinTypes.count("A_O") - self.pinTypes.count("SERVO") < neededPinTypes.count("A_O") + neededPinTypes.count("SERVO"):
            return False
        
        # Try to map the required pins to free pins on this module
        # For now, uses brute force - gets all permutations of the needed pins and tries to assign them in that order
        
        # First get a list of the free pins
        freePins = range(3)
        for nextEDevicePins in self.eDevicesPins:
            # Check if this device uses multiple pins
            if isinstance(nextEDevicePins, (list, tuple)):
                for nextPin in nextEDevicePins:
                    freePins.remove(nextPin)
            else:
                freePins.remove(nextEDevicePins)
        # Add "N_C" to list of needed pins until the list is same length as freePins
        for i in range(len(freePins) - len(eDevice.getPinTypes())):
            neededPinTypes.append("N_C")
        # Try all permutations of needed pins 
        haveMatching = True   
        for nextPermutation in list(itertools.permutations(neededPinTypes)):
            haveMatching = True
            for i in range(len(freePins)):
                # See if ith open pin can map to ith needed pin
                if nextPermutation[i] == "A_I" and not EModule.analogInPossiblePins[freePins[i]]:
                    haveMatching = False
                if (nextPermutation[i] == "A_O" or nextPermutation[i] == "SERVO") and not EModule.pwmPossiblePins[freePins[i]]:
                    haveMatching = False
            # Make the assignment if it is a valid matching
            if haveMatching:
                self.eDevices.append(eDevice)
                self.eDevicesPins.append([])
                for i in range(len(freePins)):
                    self.pinTypes[freePins[i]] = nextPermutation[i]
                    if nextPermutation[i] is not "N_C":
                        self.eDevicesPins[len(self.eDevicesPins)-1].append(freePins[i])
                break
            
        return haveMatching                

    def setEDevices(self, inEDevices):
        """
        Sets the L{EDevice}s on this module to the given ones.
        Deletes any EDevices that were on this module before.
        Tries every possible arrangement of pin assignments to fit the given EDevices.
        If it was impossible, this module remains unchanged.

        @param inEDevices: The L{EDevice}s to attach to the module.
        @type inEDevices: List or Tuple of L{EDevice}s or a single L{EDevice}

        @return: False if it was impossible to put the given EDevices on the module or if inEDevices is invalid, True otherwise.
        @rtype: boolean
        """
        if not isinstance(inEDevices, (list, tuple)):
            inEDevices = [inEDevices]
        for nextDev in inEDevices:
            if not isinstance(nextDev, EDevice):
                return False
            
        # Try all permutations of desired EDevices
        temp = EModule()
        workingOrder = None
        for nextOrder in list(itertools.permutations(inEDevices)):
            haveMatching = True
            temp.clearEDevices()
            for nextEDevice in nextOrder:
                if not temp.addEDevice(nextEDevice):
                    haveMatching = False
            if haveMatching:
                workingOrder = nextOrder[:]
                break
            
        # If we found a good order, use it
        if workingOrder is not None:
            self.clearEDevices()
            for nextEDevice in workingOrder:
                self.addEDevice(nextEDevice)
            return True
        return False

    def attach(self, toAttach, moveOtherDevices=True):
        """
        Attaches the given EDevice to the module or appends the given EModule to the chain.
        
        If toAttach is an EModule, moveOtherDevices is ignored.  Otherwise,
          - If moveOtherDevices is False, devices already connected to the module will not be moved to accomodate new device.
          - If moveOtherDevices is True, then existing devices may be moved around to fit new device if necessary.

        If adding an EDevice and it is impossible, current module remains unchanged.
        
        @param toAttach: The EComponent to attach to the module.
        @type toAttach:  L{EModule} or L{EDevice}

        @param moveOtherDevices: Whether or not existing devices can be moved to accomodate new device
        @type moveOtherDevices: boolean

        @return: False if it was impossible to put the given EDevice on the module or if toAttach is invalid, True otherwise.
        @rtype: boolean
        """
        if isinstance(toAttach, EDevice):
            if moveOtherDevices:
                desiredDevices = self.getEDevices()
                desiredDevices.append(toAttach)
                return self.setEDevices(desiredDevices)
            else:
                return self.addEDevice(toAttach)
        if isinstance(toAttach, EModule):
            return self.appendToChain(toAttach)
        
        return False # toAttach is not an EDevice or EModule
        

    def getEDevices(self):
        """
        Get the attached L{EDevice}s

        @return: The attached EDevices.
        @rtype: List of L{EDevice}s
        """
        return self.eDevices[:]

    def getEDevicesPins(self):
        """
        Get a which pin numbers are attached to which devices.

        @return: A list where entry i indicates the pins to which the ith device is attached (may use in conjunction with L{getEDevices}).
        @rtype: List of Lists of Integers
        """
        return self.eDevicesPins[:]

    def getPinDescriptions(self):
        """
        Gets a description of what is attached to each pin of the module.

        @return: The name of the EDevice (and which of its pins) attached to each pin of the module.
            A 3-element list where the ith element is a String indicating the name and pin number of the EDevice attached to the module's ith pin.
            The ith element is None if nothing is attached to the module's ith pin.  
        @rtype: List whose elements are Strings (or None)
        """
        res = [None]*3
        for i in range(len(self.eDevices)):
            if len(self.eDevicesPins[i]) > 1:
                for j in self.eDevicesPins[i]:
                    res[j] = self.eDevices[i].getName() + " (pin " + str(self.eDevicesPins[i].index(j)) + ")" + " (" + self.getName() + ")"
            else:
                res[self.eDevicesPins[i][0]] = self.eDevices[i].getName() + " (" + self.getName() + ")"
        return res

    def getPinDescriptionsForChain(self):
        """
        Get a description of what is attached to every pin of every chip in the chain that begins with this module.
        Basically, get a list where the ith item is the result of calling L{getPinDescriptions} on the ith module in the chain.
        The first element in the list corresponds to this module.
        
        @return: The name of the EDevice (and which of its pins) attached to each pin of each module in the chain that starts with this module.
        A list where the jth element is a 3-element list whose ith element is a String
        indicating the name and pin number of the EDevice attached to the jth module's ith pin.

        An element is None if nothing is attached to the jth module's ith pin.

        The first element corresponds to this module.
        @rtype: List of Lists of Strings
        """
        if self.getNextEModule() is None:
            return [self.getPinDescriptions()]
        else:
            res = self.getNextEModule().getPinDescriptionsForChain()
            res.insert(0, self.getPinDescriptions())
            return res
        
    def setPinType(self, pinNum, pinType):
        """
        Manually sets the type of the given pin.
        Use this if not using an eDevice, but rather want to set pin types manually.
        IMPORTANT: Calling this function, if it is successfull, will delete all currently attached EDevices

        @param pinNum: The pin index to set, in the range 0-2
        @type pinNum: Integer

        @param pinType: The type to which the pin should be forced.
        @type pinType: String, must be an element of L{PIN_TYPES}
        
        @return: False if assignment cannot be done, True otherwise.
            May fail if pinNum is out of range or pinType is invalid
            May fail if desired pin does not support desired type (see L{pwmPossiblePins} and L{analogInPossiblePins})
        @rtype: boolean
        """
        if pinType not in EModule.PIN_TYPES:
            return False
        if pinNum not in range(3):
            return False

        # Check that the desired pin supports the desired type
        #   Note any pin can be a digital input/output
        if pinType == "A_I" and not EModule.analogInPossiblePins[pinNum]:
            return False
        if (pinType == "A_O" or pinType == "SERVO") and not EModule.pwmPossiblePins[pinNum]:
            return False

        # Everything seems to be in order... make the assignment
        self.clearEDevices()
        self.pinTypes[pinNum] = pinType
        return True

    def setPinTypes(self, inPinTypes):
        """
        Manually sets all three pin types at once
        Use this if not using EDevices, but rather want to set pin types manually
        IMPORTANT: Calling this function, if it is successfull, will delete all currently attached EDevices and pin types

        @param inPinTypes: The desired pin types
        @type inPinTypes: A list of length 3 where each element is in L{PIN_TYPES}
        
        @return: False if inPinTypes is not a list of length 3 or if any element is not in L{PIN_TYPES}, True otherwise.
        @rtype: boolean
        """
        # Check that inPinTypes is a list of length three
        if not isinstance(inPinTypes, (list, tuple)):
            return False
        if len(inPinTypes) != 3:
            return False
        
        # Make sure every element is an acceptable pin type
        if sum(map(lambda x: (x in EModule.PIN_TYPES), inPinTypes)) != 3:
            return False
        # Make sure desired pins can support desired types
        if sum([a and b for a,b in zip(map(lambda x: x == "A_I", inPinTypes), EModule.analogInPossiblePins)]) < inPinTypes.count("A_I"):
            return False
        if sum([a and b for a,b in zip(map(lambda x: x == "A_O" or x == "SERVO", inPinTypes), EModule.pwmPossiblePins)]) < inPinTypes.count("A_O") + inPinTypes.count("SERVO"):
            return False

        # Everything seems to be in order... make the assignments
        self.clearEDevices()
        for i in range(3):
            self.pinTypes[i] = inPinTypes[i]
            
        return True

    def getPinTypes(self):
        """
        Get a list of pin types for this module.

        @return: A 3-element list indicating the type of each module pin.
        Each element is in L{PIN_TYPES}.
        @rtype: List of Strings
        """
        return self.pinTypes[:]

    def getPinTypesForChain(self):
        """
        Get a list of pin types for the entire chain that starts with this module.

        @return: A list whose jth element is a list of pin types for the jth module in the chain starting with this module.
            The first element corresponds to this module, last element to last module on chain.
            Each type is in L{PIN_TYPES}.
        @rtype: List of List of Strings
        """
        if self.getNextEModule() is None:
            return [self.getPinTypes()]
        else:
            res = self.getNextEModule().getPinTypesForChain()
            res.insert(0, self.getPinTypes())
            return res

    def getNumChipsOnChain(self):
        """
        Get the number of chips on the chain that starts with this module.

        @return: The number of chips (including this chip) in the chain that starts with this module
        @rtype: Integer
        """
        numChips = 1
        nextEModule = self.getNextEModule()
        while nextEModule is not None:
            numChips += 1
            nextEModule = nextEModule.getNextEModule()
        return numChips
  
    def toString(self):
        """
        Get a printable string describing this module.

        @return: A string which describes the module by including its name,
        its pin types, the names of attached EDevices and which pins they use, and the name of the next module (if any).
        @rtype: String
        """
        res = "EModule '%s'" %self.name
        
        for i in range(len(self.eDevices)):
            res += "\n\t'%s'" %self.eDevices[i].getName()
            res += " is attached to pin(s) %s" %str(self.eDevicesPins[i])
            res += " ("
            for p in range(len(self.eDevicesPins[i])):
                res += self.pinTypes[self.eDevicesPins[i][p]]
                if p < len(self.eDevicesPins[i])-1:
                    res += ", "
            res += ")"
        res += "\n\tNext EModule in chain: "
        if self.nextEModule is not None:
            res += "<" + self.nextEModule.getName() + ">"
        else:
            res += "<None>"
        return res    

    def clone(self, inName = None, postFix = "", cloneChain = True):
        """
        Get a (pseudo) clone of this EModule.
        Clone will have same pin types and same (cloned) EDevices as this module.
        Depending on how this module was created, the EDevices may be in a different order.
        If there are no EDevices on this module, the pin types will be manually copied to the clone.

        @param cloneChain: Indicate whether the entire chain should be cloned or only this module.
        If cloneChain is True, then the returned clone will point to a clone of the next module, and so on down the chain.
        If cloneChain is False, then the returned clone will point to the exact same object as this module does.
        @type cloneChain: boolean

        @param inName: A new name for the clone (optional).
        @type inName: String

        @param postFix: A postfix to append to the clone's name (optional).
        If given, the postfix will also be used for all cloned EDevices and, if cloning whole chain, the cloned descendant modules as well.
        A space will be added before the postfix.
        @type postFix: String
        """
        res = EModule()
        if postFix != "":
            postFix = " " + postFix
        # Name the clone
        if inName is None:
            res.setName(self.name + postFix)
        else:
            res.setName(inName + postFix)
        # Get EDevices to add to the clone, with the given postfix in their name
        eDevicesToAdd = []
        for nextDevice in self.getEDevices():
            eDevicesToAdd.append(nextDevice.clone(nextDevice.getName() + postFix))
        # Add the ECopmonents or, if there are none, set pin types
        if len(self.eDevices) > 0:
            res.setEDevices(eDevicesToAdd)
        else:
            res.setPinTypes(self.getPinTypes())
            
        # Clone the rest of the chain if desired
        if self.getNextEModule() is not None:
            if cloneChain:
                res.setNextEModule(self.getNextEModule().clone(None, postFix))
            else:
                res.setNextEModule(self.getNextEModule())
        return res

    def copyComponent(self, toCopy):
        """
        Makes this module a copy of the given EModule.

        @param toCopy: The module to copy.
        @type toCopy: L{EModule}

        @return: Whether or not it was successful (whether toCopy was valid)
        @rtype: boolean
        """
        if not isinstance(toCopy, EModule):
            return False
        self.__init__(None)
        self.setName(toCopy.getName())

        # Get EDevices to add
        eDevicesToAdd = []
        for nextDevice in toCopy.getEDevices():
            eDevicesToAdd.append(nextDevice)
        # Add the ECopmonents or, if there are none, set pin types
        if len(eDevicesToAdd) > 0:
            self.setEDevices(eDevicesToAdd)
        else:
            self.setPinTypes(toCopy.getPinTypes())
            
        # Point to the same next module (don't clone whole chain)
        if toCopy.getNextEModule() is not None:
            self.setNextEModule(toCopy.getNextEModule())
        return True
    
if __name__ == '__main__':

    LED = EDevice("LED", "D_O")
    y = EModule("Y Mod")
    y.addEDevice(LED)
    y.addEDevice(EDevice("LED1", "D_O"))

    x = EModule("X Mod")
    x.addEDevice(LED)
    x.addEDevice(EDevice("Servo", ["SERVO", "D_O"]))

    z = EModule("Z Mod")
    z.setEDevices([LED, EDevice("Sensor", ["A_I", "A_I"])])
    
    y.setNextEModule(x)
    y.appendToChain(z)

    print(y.toString())
    print(y.clone(None," (2)").toString())
    print(x.toString())
    print(z.toString())
    
    print(y.getPinTypes())
    print(y.getPinTypesForChain())

    print(y.getPinDescriptions())
    print(y.getPinDescriptionsForChain())
    
    print(y.getNumChipsOnChain())

    a = EModule("A")
    a.setPinType(0, "D_O")
    b = EModule("B")
    b.setPinType(1, "D_O")
    c = EModule("C")
    c.setPinType(2, "D_O")
    d = EModule("D")
    d.setPinType(0, "A_O")

    a.appendToChain(b)
    a.appendToChain(c)
    a.appendToChain(d)

    print a.getPinTypesForChain()
    
    





